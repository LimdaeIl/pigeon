package com.house.pigeon.member.repository;

import com.house.pigeon.member.model.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {
}
