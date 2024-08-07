package com.house.pigeon.member.repository;

import com.house.pigeon.member.model.Member;
import com.house.pigeon.member.model.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {
    List<MemberRole> findByMemberId(Long memberId);
    List<MemberRole> findByMember(Member member);
}
