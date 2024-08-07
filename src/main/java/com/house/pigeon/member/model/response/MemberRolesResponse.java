package com.house.pigeon.member.model.response;

import com.house.pigeon.role.model.RoleType;
import lombok.Builder;

import java.util.List;

public record MemberRolesResponse(
        List<RoleType> roleTypes
) {
    @Builder
    public MemberRolesResponse {
    }
}
