package com.house.pigeon.member.model;

import com.house.pigeon.role.model.Role;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_role_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Builder
    public MemberRole(Member member, Role role) {
        this.member = member;
        this.role = role;
    }

    public void setMember(Member member) {
        // 기존 member와의 관계를 제거
        if (this.member != null) {
            this.member.getMemberRoles().remove(this);
        }
        this.member = member;
        if (member != null && !member.getMemberRoles().contains(this)) {
            member.getMemberRoles().add(this);
        }
    }

    public void setRole(Role role) {
        // 기존 role과의 관계를 제거
        if (this.role != null) {
            this.role.getMemberRoles().remove(this);
        }
        this.role = role;
        if (role != null && !role.getMemberRoles().contains(this)) {
            role.getMemberRoles().add(this);
        }
    }
}