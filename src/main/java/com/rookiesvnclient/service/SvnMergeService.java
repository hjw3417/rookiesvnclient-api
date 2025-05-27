package com.rookiesvnclient.service;

import com.rookiesvnclient.cache.SvnCredentialCacheService;
import com.rookiesvnclient.dto.svn.MergedChildrenRequest;
import com.rookiesvnclient.dto.svn.MergedChildDto;
import com.rookiesvnclient.dto.svn.SvnCredentials;
import com.rookiesvnclient.svn.SvnConnectionManager;
import com.rookiesvnclient.dto.svn.MergedChildrenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class SvnMergeService {
    private final SvnConnectionManager svnConnectionManager;
    private final SvnCredentialCacheService credentialCache;
    private static final int THREAD_POOL_SIZE = 8;
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public List<MergedChildrenResponse> getMergedChildren(String username, MergedChildrenRequest request) {
        System.out.println("[SvnMergeService] 호출됨"); 
        Optional<SvnCredentials> credsOpt = credentialCache.get(username);
        if (credsOpt.isEmpty()) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }
        SvnCredentials creds = credsOpt.get();
        List<String> servers = new ArrayList<>(svnConnectionManager.getAllServerNames());
        Map<String, String> serverUrlMap = svnConnectionManager.getAllServerUrls();
        List<MergedChildrenResponse> result = new ArrayList<>();

        for (String rootPath : Optional.ofNullable(request.getRoots()).orElse(List.of())) {
            Map<String, MergedChildDto> merged = new ConcurrentHashMap<>();
            List<Future<?>> futures = new ArrayList<>();
            for (String server : servers) {
                futures.add(executor.submit(() -> {
                    try {
                        String baseUrl = serverUrlMap.get(server);
                        SVNRepository repository = svnConnectionManager.getRepositoryWithSubPath(
                                server, creds.username(), creds.password(), rootPath
                        );
                        String targetPath = request.getPath() == null ? "" : request.getPath();
                        Collection<SVNDirEntry> entries = repository.getDir(targetPath, -1, null, (Collection<?>) null);
                        for (SVNDirEntry entry : entries) {
                            String name = entry.getName();
                            String type = entry.getKind() == SVNNodeKind.DIR ? "directory" : "file";
                            merged.compute(name, (k, v) -> {
                                if (v == null) {
                                    List<String> serversList = new ArrayList<>();
                                    serversList.add(baseUrl);
                                    return new MergedChildDto(name, type, serversList);
                                } else {
                                    if (!v.getServers().contains(baseUrl)) {
                                        v.getServers().add(baseUrl);
                                    }
                                    return v;
                                }
                            });
                        }
                    } catch (SVNException e) {
                        // 서버별 실패는 무시
                    }
                }));
            }
            for (Future<?> f : futures) {
                try { f.get(); } catch (Exception ignored) {}
            }
            List<MergedChildDto> children = new ArrayList<>(merged.values());
            children.sort(Comparator.comparing(MergedChildDto::getType).reversed().thenComparing(MergedChildDto::getName));
            result.add(new MergedChildrenResponse(rootPath, children));
        }
        System.out.println("[SvnMergeService] 최종 반환값: " + result);
        return result;
    }
} 