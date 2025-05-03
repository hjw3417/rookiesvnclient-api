package com.rookiesvnclient.controller;

import com.rookiesvnclient.dto.LoginRequestDto;
import com.rookiesvnclient.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final JwtUtil jwtUtil;
    @Value("${svn.url}")
    private String svnUrl;
    // 🔥 SVN 서버 URL (추후 Config로 뺄 수 있음)
//    private String SVN_URL =  svnUrl;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto request) throws SVNException {
        // SVN 서버 접속 시도
        DAVRepositoryFactory.setup();
        SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
                request.getUsername(),
                request.getPassword()
        );
        repository.setAuthenticationManager(authManager);

        // 리비전 조회 시도 (실제 SVN 인증 확인)
        repository.getLatestRevision();

        // 접속 성공하면 JWT 발급
        return jwtUtil.generateToken(request.getUsername());
    }
}
