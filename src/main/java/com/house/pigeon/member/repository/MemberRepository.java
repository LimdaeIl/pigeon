package com.house.pigeon.member.repository;

import com.house.pigeon.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);

    Boolean existsByPhone(String phone);
}
