package com.house.pigeon.member.model.response;

import lombok.Builder;

public record LoginMemberResponse(
        Long memberId,
        String accessToken,
        String refreshToken
) {
    @Builder
    public LoginMemberResponse {
    }
}
