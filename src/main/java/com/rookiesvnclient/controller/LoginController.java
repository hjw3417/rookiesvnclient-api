package com.rookiesvnclient.controller;

import com.rookiesvnclient.dto.auth.LoginRequestDto;
import com.rookiesvnclient.jwt.JwtUtil;
import com.rookiesvnclient.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tmatesoft.svn.core.SVNException;

import java.util.HashMap;
import java.util.Map;

/**
 * SVN 로그인 요청을 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        System.out.println("login 진입!");

        try {
            // 모든 서버 인증 성공 시 캐시에 저장
            loginService.authenticateAndCache(request.getUsername(), request.getPassword());

            String token = jwtUtil.generateToken(request.getUsername());

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("username", request.getUsername());
            return ResponseEntity.ok(result);

        } catch (SVNException e) {
            System.out.println("[LoginController] login 실패: " + e.getMessage());
            return ResponseEntity.status(401).body("SVN 인증 실패");
        } catch (Exception e) {
            System.out.println("[LoginController] login 실패: " + e.getMessage());
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }
}
