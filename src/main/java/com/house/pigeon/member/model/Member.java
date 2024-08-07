package com.house.pigeon.member.model;

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
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", unique = true, nullable = false, updatable = false)
    private Long id;

    private String email;

    private String password;

    private String phone;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRole> memberRoles = new ArrayList<>();

    @Builder
    public Member(Long id, String email, String password, String phone, List<MemberRole> memberRoles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.memberRoles = memberRoles != null ? memberRoles : new ArrayList<>();
    }

    public void addMemberRole(MemberRole memberRole) {
        if (!memberRoles.contains(memberRole)) {
            memberRoles.add(memberRole);
            memberRole.setMember(this);
        }
    }

    public void removeMemberRole(MemberRole memberRole) {
        if (memberRoles.contains(memberRole)) {
            memberRoles.remove(memberRole);
            memberRole.setMember(null);
        }
    }
}
