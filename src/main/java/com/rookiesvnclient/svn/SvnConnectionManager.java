package com.rookiesvnclient.svn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * SVN 연결 정보를 관리하고 SVNRepository 인스턴스를 생성합니다.
 */
@Component
public class SvnConnectionManager {

    private final String url;
    private final String username;
    private final String password;

    public SvnConnectionManager(
            @Value("${svn.url}") String url,
            @Value("${svn.username}") String username,
            @Value("${svn.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * 설정된 인증 정보를 바탕으로 SVNRepository 인스턴스를 생성하여 반환합니다.
     *
     * @return SVNRepository 객체 (연결 및 인증 완료됨)
     * @throws SVNException 연결 실패 시 예외
     */
    public SVNRepository getRepository() throws SVNException {
        DAVRepositoryFactory.setup(); // 프로토콜 초기화

        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
        repository.setAuthenticationManager(authManager);

        return repository;
    }
}
