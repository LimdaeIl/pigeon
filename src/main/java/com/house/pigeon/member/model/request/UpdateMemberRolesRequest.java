package com.house.pigeon.member.model.request;

import com.house.pigeon.role.model.RoleType;

import java.util.List;

public record UpdateMemberRolesRequest(
        List<RoleType> roleTypes
) {
}
