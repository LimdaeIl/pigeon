package com.house.pigeon.role.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleType {
    MEMBER("회원"),
    MANAGER("관리자"),
    ADMIN("운영자");

    private final String name;
}
