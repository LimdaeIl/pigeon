package com.house.pigeon.member.service;

import com.house.pigeon.common.exception.CustomDataNotFoundException;
import com.house.pigeon.member.model.Member;
import com.house.pigeon.member.model.MemberRole;
import com.house.pigeon.member.model.request.UpdateMemberRolesRequest;
import com.house.pigeon.member.model.response.MemberRolesResponse;
import com.house.pigeon.member.repository.MemberRepository;
import com.house.pigeon.member.repository.MemberRoleRepository;
import com.house.pigeon.role.model.Role;
import com.house.pigeon.role.model.RoleType;
import com.house.pigeon.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberRoleService {

    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public MemberRolesResponse getMemberRoles(Long memberId) {
        // 회원 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomDataNotFoundException("회원: 회원이 존재하지 않습니다."));

        List<MemberRole> memberRoles = memberRoleRepository.findByMemberId(member.getId());
        List<RoleType> roleTypes = memberRoles.stream()
                .map(memberRole -> memberRole.getRole().getRoleType())
                .toList();

        return MemberRolesResponse.builder()
                .roleTypes(roleTypes)
                .build();
    }

    @Transactional
    public MemberRolesResponse updateMemberRoles(Long memberId, UpdateMemberRolesRequest request) {
        // 회원 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomDataNotFoundException("회원: 회원이 존재하지 않습니다."));

        // 기존 권한 제거
        List<MemberRole> existingRoles = memberRoleRepository.findByMember(member);
        for (MemberRole memberRole : existingRoles) {
            member.removeMemberRole(memberRole);
            memberRole.getRole().removeMemberRole(memberRole);
        }
        memberRoleRepository.deleteAll(existingRoles);

        // 새로운 권한 추가
        List<MemberRole> newMemberRoles = request.roleTypes().stream()
                .map(roleType -> {
                    Role role = roleRepository.findByRoleType(roleType)
                            .orElseThrow(() -> new CustomDataNotFoundException("권한: 해당 권한이 존재하지 않습니다."));
                    MemberRole memberRole = MemberRole.builder().member(member).role(role).build();
                    member.addMemberRole(memberRole);
                    role.addMemberRole(memberRole);
                    return memberRole;
                })
                .toList();

        memberRoleRepository.saveAll(newMemberRoles);

        // 새로운 권한 리스트 생성
        List<RoleType> roleTypes = newMemberRoles.stream()
                .map(memberRole -> memberRole.getRole().getRoleType())
                .toList();

        return MemberRolesResponse.builder()
                .roleTypes(roleTypes)
                .build();
    }
}
