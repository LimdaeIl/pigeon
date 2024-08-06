package com.house.pigeon.common.jwt.service;

import com.house.pigeon.common.exception.CustomJwtException;
import com.house.pigeon.common.jwt.model.JWTStorageDTO;
import com.house.pigeon.common.jwt.model.TokenStorage;
import com.house.pigeon.common.jwt.model.TokenType;
import com.house.pigeon.common.jwt.repository.TokenStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JWTStorageService {

    private final TokenStorageRepository tokenStorageRepository;

    public void addToken(String token, TokenType tokenType) {
        TokenStorage tokenStorage = TokenStorage.builder()
                .token(token)
                .tokenType(tokenType)
                .build();

        tokenStorageRepository.save(tokenStorage);
    }

    public JWTStorageDTO findToken(String token) {
        TokenStorage tokenStorage = tokenStorageRepository.findByToken(token)
                .orElseThrow(() -> new CustomJwtException("존재하지 않는 토큰입니다."));

        return JWTStorageDTO.from(tokenStorage);
    }
}
