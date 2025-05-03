package com.rookiesvnclient.service;

import com.rookiesvnclient.dto.SvnFileTreeNode;
import com.rookiesvnclient.dto.SvnLogRequestDto;
import com.rookiesvnclient.dto.SvnLogResponseDto;
import com.rookiesvnclient.svn.SvnConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SvnReaderService {

    private final SvnConnectionManager svnConnectionManager;

    /**
     * SVN 서버 연결 테스트 및 최신 리비전 출력
     */
    public void testConnection() throws SVNException {
        SVNRepository repository = svnConnectionManager.getRepository();
        long latestRevision = repository.getLatestRevision();
        System.out.println("✅ SVN 연결 성공, 최신 리비전: " + latestRevision);
    }

    /**
     * 주어진 경로 목록에 대해 최신 로그를 개수 제한으로 조회
     */
    public void fetchLogs(Collection<String> folderPaths, int limitPerFolder) throws SVNException {
        SVNRepository repository = svnConnectionManager.getRepository();

        for (String folderPath : folderPaths) {
            System.out.println("======= [" + folderPath + "] 폴더 로그 조회 시작 =======");

            Collection<SVNLogEntry> logEntries = repository.log(
                    new String[]{folderPath},
                    null,
                    repository.getLatestRevision(),
                    0,
                    true,
                    true
            );

            int count = 0;
            for (SVNLogEntry logEntry : logEntries) {
                if (count++ >= limitPerFolder) break;
                System.out.println("------------------------------------------------");
                System.out.println("폴더: " + folderPath);
                System.out.println("리비전: " + logEntry.getRevision());
                System.out.println("작성자: " + logEntry.getAuthor());
                System.out.println("메시지: " + logEntry.getMessage());
                System.out.println("커밋 시간: " + logEntry.getDate());
            }
        }
    }

    /**
     * DTO로 전달된 경로와 개수 제한 기반으로 SVN 로그 조회
     */
    public List<SvnLogResponseDto> fetchLogs(SvnLogRequestDto requestDto) throws SVNException {
        SVNRepository repository = svnConnectionManager.getRepository();
        List<SvnLogResponseDto> result = new ArrayList<>();

        for (String path : requestDto.getPaths()) {
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
                if (count++ >= requestDto.getLimit()) break;

                SvnLogResponseDto dto = new SvnLogResponseDto(
                        path,
                        entry.getRevision(),
                        entry.getAuthor(),
                        entry.getMessage(),
                        entry.getDate()
                );
                result.add(dto);
            }
        }

        return result;
    }


    /**
     * SVN 디렉토리 트리를 재귀적으로 조회하여 트리 구조로 반환합니다.
     *
     * @param basePath 탐색을 시작할 상위 경로 (예: "", "SMMES")
     * @return SVN 디렉토리 구조를 담은 루트 노드
     * @throws SVNException SVN 서버 접근 중 오류 발생 시
     */
    public SvnFileTreeNode buildDirectoryTree(String basePath) throws SVNException {
        SVNRepository repository = svnConnectionManager.getRepository();
        SvnFileTreeNode root = new SvnFileTreeNode(basePath, true); // 최상위도 디렉토리 간주
        listEntriesRecursively(repository, basePath, root);
        return root;
    }

    /**
     * 내부적으로 SVN 디렉토리의 하위 항목들을 재귀적으로 탐색하여 트리에 추가합니다.
     *
     * @param repository 연결된 SVNRepository 객체
     * @param path       현재 탐색 중인 경로
     * @param parent     부모 노드 객체
     * @throws SVNException SVN 서버 접근 중 오류 발생 시
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


}
