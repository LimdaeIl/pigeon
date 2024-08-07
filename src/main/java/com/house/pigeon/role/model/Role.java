package com.house.pigeon.role.model;

import com.house.pigeon.member.model.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @OneToMany(mappedBy = "role")
    private final List<MemberRole> memberRoles = new ArrayList<>();

    @Builder
    public Role(RoleType roleType) {
        this.roleType = roleType;
    }

    public void addMemberRole(MemberRole memberRole) {
        if (!memberRoles.contains(memberRole)) {
            memberRoles.add(memberRole);
            memberRole.setRole(this);
        }
    }

    public void removeMemberRole(MemberRole memberRole) {
        if (memberRoles.contains(memberRole)) {
            memberRoles.remove(memberRole);
            memberRole.setRole(null);
        }
    }
}