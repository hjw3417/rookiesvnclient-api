package com.rookiesvnclient.service;

import com.rookiesvnclient.cache.SvnCredentialCacheService;
import com.rookiesvnclient.svn.SvnConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;

import java.util.List;

/**
 * 로그인 시 사용자 SVN 인증을 모든 서버에 대해 수행하고,
 * 성공하면 인증 정보를 캐시에 저장합니다.
 */
@Service
@RequiredArgsConstructor
public class LoginService {

    private final SvnConnectionManager svnConnectionManager;
    private final SvnCredentialCacheService credentialCache;

    /**
     * 모든 서버에 대해 인증을 시도하고 모두 성공 시 캐시에 자격정보 저장
     *
     * @param username 사용자 ID
     * @param password 사용자 PW
     * @throws SVNException 하나라도 실패 시 인증 실패
     */
    public void authenticateAndCache(String username, String password) throws SVNException {
        System.out.println("authenticateAndCache 진입!");        
        List<String> servers = svnConnectionManager.getAllServerNames();
        for (String server : servers) {
            svnConnectionManager.getRepository(server, username, password)
                    .getLatestRevision(); // 인증 성공 여부 확인
        }

        // 모든 서버 인증 통과 → 캐시에 등록
        credentialCache.put(username, password);
    }
}
