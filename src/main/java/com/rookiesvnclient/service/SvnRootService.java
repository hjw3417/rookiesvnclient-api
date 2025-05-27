package com.rookiesvnclient.service;

import com.rookiesvnclient.dto.svn.SvnRootResponseDto;
import com.rookiesvnclient.svn.SvnConnectionManager;
import com.rookiesvnclient.cache.SvnCredentialCacheService;
import com.rookiesvnclient.dto.svn.SvnCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.*;

/**
 * 모든 SVN 서버의 루트 디렉토리 파일/폴더 이름을 조회하는 서비스
 */
@Service
@RequiredArgsConstructor
public class SvnRootService {
    private final SvnConnectionManager svnConnectionManager;
    private final SvnCredentialCacheService credentialCache;

    /**
     * 모든 서버의 루트 디렉토리 파일/폴더 이름을 조회
     * @param username JWT에서 추출한 사용자명
     * @return 서버명별 결과 DTO Map
     */
    public Map<String, SvnRootResponseDto> getAllServerRoots(String username) {
        System.out.println("getAllServerRoots Service 진입!");

        Map<String, SvnRootResponseDto> result = new LinkedHashMap<>();
        List<String> servers = svnConnectionManager.getAllServerNames();
        Optional<SvnCredentials> credsOpt = credentialCache.get(username);
        if (credsOpt.isEmpty()) {
            // 모든 서버 실패 처리
            for (String server : servers) {
                System.out.println("server: " + server);
                result.put(server, new SvnRootResponseDto(false, null, "인증 정보 없음"));
            }
            return result;
        }
        SvnCredentials creds = credsOpt.get();

        for (String server : servers) {
            try {
                SVNRepository repository = svnConnectionManager.getRepository(server, creds.username(), creds.password());
                Collection<SVNDirEntry> entries = repository.getDir("", -1, null, (Collection<?>) null);
                List<String> names = new ArrayList<>();
                for (SVNDirEntry entry : entries) {
                    System.out.println("entry: " + entry.getName());
                    names.add(entry.getName());
                }
                System.out.println(names.size());

                result.put(server, new SvnRootResponseDto(true, names, null));
            } catch (SVNException e) {
                result.put(server, new SvnRootResponseDto(false, null, "접속 실패: " + e.getMessage()));
            } catch (Exception e) {
                result.put(server, new SvnRootResponseDto(false, null, "알 수 없는 오류: " + e.getMessage()));
            }
        }
        System.out.println(result.size());
        return result;
    }

    /**
     * 모든 서버명 리스트 반환
     */
    public List<String> getAllServerNames() {
        System.out.println("[SvnRootService] getAllServerNames 호출됨");
        return svnConnectionManager.getAllServerNames();
    }
} 