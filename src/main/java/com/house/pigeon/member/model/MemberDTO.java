package com.house.pigeon.member.model;

import java.util.List;

public record MemberDTO(
        Long id,
        String email,
        String password,
        String phone,
        List<MemberRoleDTO> memberRoles
) {
    public static MemberDTO of(String email, String password, String phone) {
        return new MemberDTO(null, email, password, phone, null);
    }

    public static MemberDTO of(Long id, String email, String password, String phone, List<MemberRoleDTO> memberRoles) {
        return new MemberDTO(id, email, password, phone, memberRoles);
    }

    public static MemberDTO from(Member member) {
        List<MemberRoleDTO> memberRoleDTOs = member.getMemberRoles().stream()
                .map(MemberRoleDTO::from)
                .toList();

        return new MemberDTO(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getPhone(),
                memberRoleDTOs
        );
    }

    public Member toEntity() {
        List<MemberRole> memberRoleEntities = this.memberRoles != null
                ? this.memberRoles.stream()
                .map(MemberRoleDTO::toEntity)
                .toList()
                : null;

        return Member.builder()
                .email(this.email)
                .password(this.password)
                .phone(this.phone)
                .memberRoles(memberRoleEntities)
                .build();
    }
}