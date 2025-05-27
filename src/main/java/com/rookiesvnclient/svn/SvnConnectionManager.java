package com.rookiesvnclient.svn;

import com.rookiesvnclient.config.SvnProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SVN 연결 정보를 관리하고 SVNRepository 인스턴스를 생성합니다.
 */
@Component
@RequiredArgsConstructor
public class SvnConnectionManager {

    private final SvnProperties svnProperties;

    /**
     * 설정된 서버 이름과 사용자 인증 정보로 SVNRepository를 생성합니다.
     *
     * @param serverName 서버 식별자 (예: "A", "B")
     * @param username   사용자 ID
     * @param password   사용자 비밀번호
     * @return 인증이 완료된 SVNRepository 인스턴스
     * @throws SVNException 연결 실패 또는 인증 실패 시 예외 발생
     */
    public SVNRepository getRepository(String serverName, String username, String password) throws SVNException {
        Map<String, String> serverMap = svnProperties.getServers();
        String url = serverMap.get(serverName);
        if (url == null) {
            throw new IllegalArgumentException("알 수 없는 SVN 서버 이름: " + serverName);
        } else {
            System.out.println(url + " 연결 성공 했음요!");
        }

        DAVRepositoryFactory.setup();
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
        repository.setAuthenticationManager(authManager);
        return repository;
    }

    /**
     * 설정된 모든 서버 이름을 반환합니다.
     *
     * @return 서버 식별자 목록
     */
    public List<String> getAllServerNames() {
        System.out.println("[SvnConnectionManager] getAllServerNames 호출됨");
        return new ArrayList<>(svnProperties.getServers().keySet());
    }

    /**
     * 전체 서버 URL 맵을 반환합니다.
     *
     * @return 서버 URL 맵 (읽기 전용)
     */
    public Map<String, String> getServerUrlMap() {
        return Map.copyOf(svnProperties.getServers());
    }

    /**
     * 서버명-URL 맵 반환
     */
    public Map<String, String> getAllServerUrls() {
        return svnProperties.getServers();
    }

    /**
     * base URL + subPath로 SVNRepository 반환
     */
    public SVNRepository getRepositoryWithSubPath(String serverName, String username, String password, String subPath) throws SVNException {
        Map<String, String> serverMap = svnProperties.getServers();
        String baseUrl = serverMap.get(serverName);
        if (baseUrl == null) {
            throw new IllegalArgumentException("알 수 없는 SVN 서버 이름: " + serverName);
        }
        String fullUrl = baseUrl.endsWith("/") ? baseUrl + subPath : baseUrl + "/" + subPath;
        DAVRepositoryFactory.setup();
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(fullUrl));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
        repository.setAuthenticationManager(authManager);
        return repository;
    }
}
