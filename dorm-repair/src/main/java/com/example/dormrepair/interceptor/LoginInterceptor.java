package com.example.dormrepair.interceptor;

import com.example.dormrepair.common.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录拦截器
 * #1 阶段为骨架（全部放行），#2 阶段完善
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 放行 OPTIONS 预检请求（由浏览器自动发送）
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.debug("放行 OPTIONS 预检请求: {}", uri);
            return true;
        }

        log.debug("拦截请求: {}, method: {}", uri, method);

        // 排除登录和注册接口
        if (uri.equals("/api/auth/login") || uri.equals("/api/auth/register")) {
            log.debug("放行登录/注册接口: {}", uri);
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("缺少或非法 Authorization 头: {}", uri);
            writeUnauthorized(response, "未登录或 token 已过期");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            log.warn("token 验证失败: {}", uri);
            writeUnauthorized(response, "未登录或 token 已过期");
            return false;
        }

        // 解析 token，设置 request attribute
        Integer userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        request.setAttribute("userId", userId.longValue());
        request.setAttribute("username", username);
        request.setAttribute("role", role);

        log.debug("token 解析成功: userId={}, username={}, role={}", userId, username, role);
        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("code", 401);
        body.put("message", message);
        body.put("data", null);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(body));
    }
}
