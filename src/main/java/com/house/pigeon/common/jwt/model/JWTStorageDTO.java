package com.house.pigeon.common.jwt.model;

public record JWTStorageDTO(
        Long memberId,
        String refreshToken
) {

    public static JWTStorageDTO from(TokenStorage tokenStorage) {
        return new JWTStorageDTO(
                tokenStorage.getId(),
                tokenStorage.getToken());
    }
}
