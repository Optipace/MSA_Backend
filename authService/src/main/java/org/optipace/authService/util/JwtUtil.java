package org.optipace.authService.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.optipace.authService.entity.EmployeeLogin;
//import org.optipace.authService.entity.RefreshToken;
import org.optipace.authService.repository.EmployeeLoginRepository;
//import org.optipace.authService.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final EmployeeLoginRepository employeeLoginRepository;
//    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
//    @Value("${jwt.refresh.expiration}")
//    private Long refreshExpiration;

    public String generateToken(Long employeeId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        String jwtId = UUID.randomUUID().toString();
        return Jwts.builder()
                .setClaims(claims)
                .setId(jwtId)
                .setSubject(employeeId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


//    public String generateRefreshToken(Long employeeId) {
//        EmployeeLogin employeeLogin = employeeLoginRepository.findByEmployeeId(employeeId)
//                .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//        RefreshToken token = new RefreshToken();
//        token.setRefreshToken(UUID.randomUUID().toString());
//        token.setCreatedDatetime(LocalDateTime.now());
//        token.setEmployeeId(employeeLogin.getEmployeeId());
//        token.setExpireDatetime(LocalDateTime.now().plusSeconds(refreshExpiration));
//        refreshTokenRepository.save(token);
//        return token.getRefreshToken();
//
//    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
