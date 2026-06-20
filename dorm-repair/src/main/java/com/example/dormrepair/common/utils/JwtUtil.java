package com.example.dormrepair.common.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * #1 阶段为骨架，#2 阶段完善
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration; // 默认24小时（毫秒）

    /**
     * 生成 JWT Token
     * 包含 claims：userId、username、role
     */
    public String generateToken(Integer userId, String username, String role) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);

        log.debug("生成JWT token: userId={}, username={}, role={}", userId, username, role);

        String token = Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();

        log.debug("JWT token 生成成功: {}", token.substring(0, Math.min(20, token.length())) + "...");
        return token;
    }

    /**
     * 从 Token 中获取用户名（subject）
     */
    public String getUsernameFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            String username = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            log.debug("从token解析用户名: {}", username);
            return username;
        } catch (Exception e) {
            log.warn("解析token用户名失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 Token 中获取用户ID（自定义 claim）
     */
    public Integer getUserIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Integer userId = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("userId", Integer.class);
            log.debug("从token解析用户ID: {}", userId);
            return userId;
        } catch (Exception e) {
            log.warn("解析token用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 Token 中获取角色（自定义 claim）
     */
    public String getRoleFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            String role = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("role", String.class);
            log.debug("从token解析角色: {}", role);
            return role;
        } catch (Exception e) {
            log.warn("解析token角色失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 验证 Token 是否有效（签名正确 + 未过期）
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
