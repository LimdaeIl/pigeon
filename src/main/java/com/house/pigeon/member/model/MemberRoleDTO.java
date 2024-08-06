package com.house.pigeon.member.model;

import com.house.pigeon.role.model.Role;
import com.house.pigeon.role.model.RoleDTO;

public record MemberRoleDTO(
        Long id,
        Long memberId,
        Long roleId,
        RoleDTO role
) {

    public static MemberRoleDTO of(Long memberId, Long roleId, RoleDTO role) {
        return new MemberRoleDTO(null, memberId, roleId, role);
    }

    public static MemberRoleDTO of(Long id, Long memberId, Long roleId, RoleDTO role) {
        return new MemberRoleDTO(id, memberId, roleId, role);
    }

    public static MemberRoleDTO from(MemberRole memberRole) {
        return new MemberRoleDTO(
                memberRole.getId(),
                memberRole.getMember().getId(),
                memberRole.getRole().getId(),
                RoleDTO.from(memberRole.getRole())
        );
    }

    public MemberRole toEntity() {
        Member memberEntity = Member.builder().id(this.memberId).build();
        Role roleEntity = this.role.toEntity();
        return MemberRole.builder()
                .member(memberEntity)
                .role(roleEntity)
                .build();
    }

}
