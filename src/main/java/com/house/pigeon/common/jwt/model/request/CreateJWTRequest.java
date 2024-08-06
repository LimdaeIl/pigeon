package com.house.pigeon.common.jwt.model.request;

import com.house.pigeon.role.model.RoleType;
import lombok.Builder;

import java.util.List;

public record CreateJWTRequest(
        Long memberId,
        String email,
        List<RoleType> roleTypes
) {
    @Builder
    public CreateJWTRequest {
    }
}
