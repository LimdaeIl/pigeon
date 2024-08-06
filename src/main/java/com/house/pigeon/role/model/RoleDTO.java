package com.house.pigeon.role.model;

public record RoleDTO(
        Long id,
        RoleType roleType
) {
    public static RoleDTO of(RoleType roleType) {
        return new RoleDTO(null, roleType);
    }

    public static RoleDTO of(Long id, RoleType roleType) {
        return new RoleDTO(id, roleType);
    }

    public static RoleDTO from(Role role) {
        return new RoleDTO(
                role.getId(),
                role.getRoleType()
        );
    }

    public Role toEntity() {
        return Role.builder()
                .roleType(this.roleType)
                .build();
    }
}