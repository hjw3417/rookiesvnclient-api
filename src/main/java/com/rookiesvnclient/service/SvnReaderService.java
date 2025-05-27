package com.rookiesvnclient.service;

import com.rookiesvnclient.cache.SvnCredentialCacheService;
import com.rookiesvnclient.dto.svn.SvnCredentials;
import com.rookiesvnclient.dto.svn.SvnFileTreeNode;
import com.rookiesvnclient.dto.svn.SvnLogRequestDto;
import com.rookiesvnclient.dto.svn.SvnLogResponseDto;
import com.rookiesvnclient.dto.svn.SvnLogEntryDto;
import com.rookiesvnclient.svn.SvnConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.*;

/**
 * SVN 저장소의 디렉토리 및 로그 정보를 조회하는 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class SvnReaderService {

    private final SvnConnectionManager svnConnectionManager;
    private final SvnCredentialCacheService credentialCache;

    /**
     * 지정된 서버에서 루트('/') 하위 최상위 디렉토리를 조회합니다.
     *
     * @param serverName 서버 식별자 (예: "A", "B")
     * @param username 사용자 ID (JWT에서 추출된 값)
     * @return 디렉토리 이름 목록
     * @throws SVNException SVN 접근 실패 시
     */
    public List<String> getTopLevelDirectories(String serverName, String username) throws SVNException {
        System.out.println("[SvnReaderService] getTopLevelDirectories 호출됨");
        SvnCredentials creds = credentialCache.get(username)
                .orElseThrow(() -> new IllegalStateException("인증 정보가 캐시에 없습니다."));

        SVNRepository repository = svnConnectionManager.getRepository(serverName, creds.username(), creds.password());
        List<String> roots = new ArrayList<>();

        Collection<SVNDirEntry> entries = repository.getDir("", -1, null, (Collection<?>) null);
        for (SVNDirEntry entry : entries) {
            if (entry.getKind() == SVNNodeKind.DIR) {
                roots.add(entry.getName());
                System.out.println("entry.getName(): " + entry.getName());
            }
        }

        return roots;
    }

    /**
     * 지정된 서버와 경로에서 직하위 파일/폴더 이름 리스트 반환
     * @param server 서버명
     * @param username 사용자명
     * @param basePath 기준 경로 (""이면 루트)
     * @return 파일/폴더 이름 리스트
     */
    public List<String> getTopLevelDirectories(String server, String username, String basePath) throws SVNException {
        SvnCredentials creds = credentialCache.get(username)
                .orElseThrow(() -> new IllegalStateException("인증 정보가 캐시에 없습니다."));
        SVNRepository repository = svnConnectionManager.getRepository(server, creds.username(), creds.password());
        List<String> roots = new ArrayList<>();
        Collection<SVNDirEntry> entries = repository.getDir(basePath, -1, null, (Collection<?>) null);
        for (SVNDirEntry entry : entries) {
            roots.add(entry.getName());
        }
        return roots;
    }

    /**
     * 지정된 서버와 사용자 인증 정보로 SVN 디렉토리 트리를 조회합니다.
     *
     * @param serverName 서버 식별자
     * @param basePath 탐색 시작 경로
     * @param username 사용자 ID (JWT에서 추출된 값)
     * @return 트리 루트 노드
     * @throws SVNException SVN 조회 실패 시
     */
    public SvnFileTreeNode buildDirectoryTree(String serverName, String basePath, String username) throws SVNException {
        SvnCredentials creds = credentialCache.get(username)
                .orElseThrow(() -> new IllegalStateException("인증 정보가 캐시에 없습니다."));

        SVNRepository repository = svnConnectionManager.getRepository(serverName, creds.username(), creds.password());
        SvnFileTreeNode root = new SvnFileTreeNode(basePath, true);
        listEntriesRecursively(repository, basePath, root);
        return root;
    }

    /**
     * 내부적으로 재귀 탐색하여 디렉토리 트리를 구성합니다.
     */
    private void listEntriesRecursively(SVNRepository repository, String path, SvnFileTreeNode parent) throws SVNException {
        Collection<SVNDirEntry> entries = repository.getDir(path, -1, null, (Collection<?>) null);

        for (SVNDirEntry entry : entries) {
            String entryName = entry.getName();
            boolean isDir = entry.getKind() == SVNNodeKind.DIR;
            SvnFileTreeNode child = new SvnFileTreeNode(entryName, isDir);

            if (isDir) {
                String childPath = path.isEmpty() ? entryName : path + "/" + entryName;
                listEntriesRecursively(repository, childPath, child);
            }

            parent.getChildren().add(child);
        }
    }

    /**
     * 지정된 서버에서 경로 목록에 대한 최신 로그를 조회합니다.
     *
     * @param serverName 서버 식별자
     * @param requestDto 경로 및 제한 수 포함 DTO
     * @param username 사용자 ID (JWT에서 추출된 값)
     * @return 로그 DTO 리스트
     * @throws SVNException SVN 로그 조회 실패 시
     */
    public List<SvnLogEntryDto> fetchLogs(String serverName, SvnLogRequestDto requestDto, String username) throws SVNException {
        SvnCredentials creds = credentialCache.get(username)
                .orElseThrow(() -> new IllegalStateException("인증 정보가 캐시에 없습니다."));

        SVNRepository repository = svnConnectionManager.getRepository(serverName, creds.username(), creds.password());
        List<SvnLogEntryDto> result = new ArrayList<>();

        for (String path : requestDto.getPath()) {
            Collection<SVNLogEntry> logEntries = repository.log(
                    new String[]{path},
                    null,
                    repository.getLatestRevision(),
                    0,
                    true,
                    true
            );

            int count = 0;
            for (SVNLogEntry entry : logEntries) {
                if (requestDto.getLimit() != null && count++ >= requestDto.getLimit()) break;

                SvnLogEntryDto dto = new SvnLogEntryDto();
                dto.setRevision(entry.getRevision());
                dto.setAuthor(entry.getAuthor());
                dto.setMessage(entry.getMessage());
                dto.setDate(entry.getDate() != null ? entry.getDate().toString() : null);
                // SvnLogEntryDto에 path 필드가 필요하면 추가해야 합니다.
                // 현재 SvnLogEntryDto에는 path 필드가 없습니다.
                // 만약 각 로그 항목에 대해 path 정보가 필요하다면 SvnLogEntryDto에 필드를 추가하고 여기서 설정해야 합니다.
                result.add(dto);
            }
        }

        return result;
    }

    /**
     * 여러 root에서 특정 경로의 로그를 root별로 반환
     * @param rootNames 서버(root) 이름 리스트
     * @param path 조회할 경로
     * @param username 사용자명
     * @return root별 로그 리스트 Map
     */
    public Map<String, List<SvnLogEntryDto>> getLogsByRootsAndPath(List<String> rootNames, String path, String username) throws SVNException {
        System.out.println("[SvnReaderService] getLogsByRootsAndPath 호출됨");
        Map<String, List<SvnLogEntryDto>> result = new LinkedHashMap<>();
        for (String root : rootNames) {
            try {
                SvnCredentials creds = credentialCache.get(username)
                        .orElseThrow(() -> new IllegalStateException("인증 정보가 캐시에 없습니다."));
                SVNRepository repository = svnConnectionManager.getRepository(root, creds.username(), creds.password());
                // 경로 존재 여부 확인
                SVNNodeKind nodeKind = repository.checkPath(path, -1);
                if (nodeKind == SVNNodeKind.NONE) {
                    continue; // 해당 root에 경로 없음
                }
                // 로그 조회
                Collection<SVNLogEntry> logEntries = repository.log(
                        new String[]{path},
                        null,
                        repository.getLatestRevision(),
                        0,
                        true,
                        true
                );
                List<SvnLogEntryDto> logs = new ArrayList<>();
                for (SVNLogEntry entry : logEntries) {
                    SvnLogEntryDto dto = new SvnLogEntryDto();
                    dto.setRevision(entry.getRevision());
                    dto.setAuthor(entry.getAuthor());
                    dto.setDate(entry.getDate() != null ? entry.getDate().toString() : null);
                    dto.setMessage(entry.getMessage());
                    // 변경 파일 목록 등은 필요시 추가 구현
                    logs.add(dto);
                }
                result.put(root, logs);
            } catch (Exception e) {
                // 실패한 root는 결과에서 제외 (또는 필요시 빈 리스트/에러 메시지로 처리 가능)
                continue;
            }
        }
        return result;
    }
}
