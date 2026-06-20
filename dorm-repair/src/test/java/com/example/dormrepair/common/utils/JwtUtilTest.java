package com.example.dormrepair.common.utils;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试
 * 使用 ReflectionTestUtils 注入 @Value 字段，不启动 Spring 上下文
 */
public class JwtUtilTest {

    private JwtUtil getJwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();
        // 注入 @Value 字段，密钥长度 ≥32 字符（HS256 要求 256 bits）
        ReflectionTestUtils.setField(jwtUtil, "secret", "my-secret-key-2026-dorm-repair-2026-key-123456");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
        return jwtUtil;
    }

    @Test
    void generateToken_shouldReturnValidJwt() {
        JwtUtil jwtUtil = getJwtUtil();
        String token = jwtUtil.generateToken(1, "admin", "admin");
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT 三部分
    }

    @Test
    void generateToken_shouldContainClaims() {
        JwtUtil jwtUtil = getJwtUtil();
        String token = jwtUtil.generateToken(100, "student", "student");
        // 解析 claim
        Integer userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        assertEquals(100, userId);
        assertEquals("student", username);
        assertEquals("student", role);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        JwtUtil jwtUtil = getJwtUtil();
        String token = jwtUtil.generateToken(1, "admin", "admin");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateToken_shouldReturnFalseForTamperedToken() {
        JwtUtil jwtUtil = getJwtUtil();
        String token = jwtUtil.generateToken(1, "admin", "admin");
        // 篡改 signature
        String[] parts = token.split("\\.");
        String tampered = parts[0] + "." + parts[1] + ".tampered_signature";
        assertFalse(jwtUtil.validateToken(tampered));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        JwtUtil jwtUtil = getJwtUtil();
        assertFalse(jwtUtil.validateToken("invalid-token-string"));
        assertFalse(jwtUtil.validateToken(""));
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    void getUserIdFromToken_shouldReturnCorrectId() {
        JwtUtil jwtUtil = getJwtUtil();
        String token = jwtUtil.generateToken(42, "repair", "repair");
        assertEquals(42, jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void getUsernameFromToken_shouldReturnCorrectUsername() {
        JwtUtil jwtUtil = getJwtUtil();
        String token = jwtUtil.generateToken(1, "testuser", "student");
        assertEquals("testuser", jwtUtil.getUsernameFromToken(token));
    }

    @Test
    void getRoleFromToken_shouldReturnCorrectRole() {
        JwtUtil jwtUtil = getJwtUtil();
        String token = jwtUtil.generateToken(1, "admin", "admin");
        assertEquals("admin", jwtUtil.getRoleFromToken(token));
    }
}
