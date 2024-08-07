package com.house.pigeon.common.jwt.model;

import lombok.Builder;

public record JWTStorageDTO(
        Long memberId,
        String refreshToken
) {

    @Builder
    public JWTStorageDTO {
    }


    public static JWTStorageDTO from(TokenStorage tokenStorage) {
        return new JWTStorageDTO(
                tokenStorage.getId(),
                tokenStorage.getToken());
    }
}
