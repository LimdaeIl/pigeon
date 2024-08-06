package com.house.pigeon.common.jwt;

import com.house.pigeon.common.exception.CustomJwtException;
import com.house.pigeon.common.jwt.model.request.CreateJWTRequest;
import com.house.pigeon.role.model.RoleType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Getter
@Component
public class JWTProvider {

    private final byte[] ACCESS_TOKEN_SECRET_KEY;
    private final byte[] REFRESH_TOKEN_SECRET_KEY;

    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 30 minutes
    public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7 days

    public JWTProvider(@Value("${jwt.key.access}") String accessSecret,
                       @Value("${jwt.key.refresh}") String refreshSecret) {
        this.ACCESS_TOKEN_SECRET_KEY = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.REFRESH_TOKEN_SECRET_KEY = refreshSecret.getBytes(StandardCharsets.UTF_8);
    }

    public String createAccessToken(CreateJWTRequest request) {
        return createToken(request, ACCESS_TOKEN_SECRET_KEY, ACCESS_TOKEN_EXPIRE_COUNT, "access-token");
    }

    public String createRefreshToken(CreateJWTRequest request) {
        return createToken(request, REFRESH_TOKEN_SECRET_KEY, REFRESH_TOKEN_EXPIRE_COUNT, "refresh-token");
    }

    private String createToken(CreateJWTRequest request, byte[] secretKey, Long expireCount, String tokenType) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime validityDate = now.plus(Duration.ofMillis(expireCount));

        String jti = UUID.randomUUID().toString();

        Claims claims = Jwts.claims().setSubject(request.email());
        claims.put("userId", request.memberId());
        claims.put("roles", request.roleTypes().stream()
                .map(RoleType::name)
                .toList());
        claims.put("tokenType", tokenType);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuer("pigeon")
                .setSubject(request.email())
                .setAudience("http://localhost:8080")
                .setExpiration(Date.from(validityDate.toInstant()))
                .setNotBefore(Date.from(now.toInstant()))
                .setIssuedAt(Date.from(now.toInstant()))
                .setId(jti)
                .signWith(getSigningKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }

    public Boolean isTokenExpiringSoon(String token, byte[] secretKey, Long timeInMinutes) {
        Date expiration = getExpirationTimeFromToken(token, secretKey);
        Date now = new Date();

        long diffInMinutes = (expiration.getTime() - now.getTime()) / (1000 * 60);
        return diffInMinutes < timeInMinutes;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver, byte[] secretKey) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claimsResolver.apply(claims);
    }

    public Date getExpirationTimeFromToken(String token, byte[] secretKey) {
        return getClaimFromToken(token, Claims::getExpiration, secretKey);
    }

    public Long getMemberIdFromToken(String token, byte[] secretKey) {
        return getClaimFromToken(token, claims -> claims.get("userId", Long.class), secretKey);
    }

    public List<RoleType> getMemberRolesFromToken(String token, byte[] secretKey) {
        return getClaimFromToken(token, claims -> {
            List<String> roles = claims.get("roles", List.class);
            if (roles == null) {
                throw new CustomJwtException("토큰에서 권한 정보를 찾을 수 없습니다.");
            }
            return roles.stream()
                    .map(RoleType::valueOf)
                    .toList();
        }, secretKey);
    }

    public String getEmailFromToken(String token, byte[] secretKey) {
        return getClaimFromToken(token, Claims::getSubject, secretKey);
    }

    public String getTokenTypeFromToken(String token, byte[] secretKey) {
        return getClaimFromToken(token, claims -> claims.get("tokenType", String.class), secretKey);
    }

    public Claims getAllClaimsFromToken(String token, byte[] secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean validateToken(String token, byte[] secretKey) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(secretKey))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | MissingClaimException e) {
            log.error("유효하지 않은 JWT: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("유효하지 않은 JWT 토큰: {}", e.getMessage());
        }
        return false;
    }
}