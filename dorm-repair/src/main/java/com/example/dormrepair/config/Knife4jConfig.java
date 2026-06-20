package com.example.dormrepair.config;

import org.springframework.context.annotation.Configuration;

/**
 * Knife4j Swagger3 接口文档配置
 * 通过 application.yml 中 knife4j.enable=true 启用
 */
@Configuration
public class Knife4jConfig {
    // Knife4j 通过 spring-boot 自动配置启用
    // 访问 http://localhost:8080/doc.html 查看文档
}
