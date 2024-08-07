package com.house.pigeon.common.jwt.service;

import com.house.pigeon.common.exception.CustomExpiredTokenException;
import com.house.pigeon.common.exception.CustomJwtException;
import com.house.pigeon.common.jwt.JWTProvider;
import com.house.pigeon.common.jwt.model.JWTStorageDTO;
import com.house.pigeon.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class JWTStorageService {

    private final RedisUtil redisUtil;
    private final JWTProvider jwtProvider;

    public void storeRefreshToken(Long memberId, String refreshToken) {
        Date expirationTimeFromToken = jwtProvider.getExpirationTimeFromToken(refreshToken, jwtProvider.getREFRESH_TOKEN_SECRET_KEY());

        long currentTimeInMillis = System.currentTimeMillis();
        long expirationTimeInMillis = expirationTimeFromToken.getTime();
        long durationInSeconds = (expirationTimeInMillis - currentTimeInMillis) / 1000;

        if (!(durationInSeconds > 0)) {
            throw new CustomExpiredTokenException("리프레시 토큰이 만료되었습니다.");
        }

        redisUtil.setDataExpire(refreshToken, Long.toString(memberId), durationInSeconds);
    }

    public JWTStorageDTO findRefreshToken(String refreshToken) {
        String getMemberIdByRefreshToken = redisUtil.getData(refreshToken);

        if (getMemberIdByRefreshToken == null || getMemberIdByRefreshToken.isBlank()) {
            throw new CustomJwtException("리프레시 토큰이 존재하지 않습니다.");
        }

        return JWTStorageDTO.builder()
                .memberId(Long.valueOf(getMemberIdByRefreshToken))
                .refreshToken(refreshToken)
                .build();
    }

    public void deleteRefreshToken(String refreshToken) {
        redisUtil.deleteData(refreshToken);
    }
}
