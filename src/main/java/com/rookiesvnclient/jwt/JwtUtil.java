package com.rookiesvnclient.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🔥 이 키는 실제 서비스에선 환경변수로 빼야 한다.
    private static final String SECRET_KEY = "12345678901234567890123456789012"; // 32바이트 이상 (HS256용)

    // 토큰 유효시간 (예: 30분)
    private static final long EXPIRATION_TIME = 1000 * 60 * 30; // 30분

    private final Key key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // JWT 토큰 생성
    public String generateToken(String username) {
        System.out.println("[JwtUtil] generateToken 호출됨");
        return Jwts.builder()
                .setSubject(username) // 보통 username (ID)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 토큰 검증 및 사용자 ID 추출
    public String validateAndExtractUsername(String token) {
        System.out.println("[JwtUtil] validateAndExtractUsername 호출됨");
        try {
            System.out.println("[JwtUtil] validateAndExtractUsername try 블록 진입");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("[JwtUtil] validateAndExtractUsername catch 블록 진입");
            throw new RuntimeException("Invalid JWT token");
        }
    }
}
