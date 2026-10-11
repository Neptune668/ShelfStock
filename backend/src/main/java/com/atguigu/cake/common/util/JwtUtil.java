package com.atguigu.cake.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 工具类：负责 Token 的生成与解析
 */
@Component
public class JwtUtil {

    private static final String CLAIM_ADMIN_ID = "adminId";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.remember-expiration}")
    private Long rememberExpiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token（默认有效期，24 小时）
     */
    public String generateToken(Long adminId, String username) {
        return generateToken(adminId, username, expiration);
    }

    /**
     * 生成「记住我」Token（更长有效期，如 7 天），用于免登录
     */
    public String generateTokenWithRemember(Long adminId, String username) {
        return generateToken(adminId, username, rememberExpiration);
    }

    private String generateToken(Long adminId, String username, Long expireMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireMillis);
        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_ADMIN_ID, adminId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getKey())
                .compact();
    }

    /**
     * 解析 Token 返回声明信息；解析失败抛 JwtException
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
