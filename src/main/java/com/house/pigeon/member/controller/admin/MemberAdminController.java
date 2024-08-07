package com.house.pigeon.member.controller.admin;

import com.house.pigeon.common.response.HttpResponse;
import com.house.pigeon.member.model.request.UpdateMemberRolesRequest;
import com.house.pigeon.member.model.response.MemberRolesResponse;
import com.house.pigeon.member.service.MemberRoleService;
import com.house.pigeon.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@RestController
public class MemberAdminController {

    private final MemberRoleService memberRoleService;

    @GetMapping("/{memberId}/role")
    public ResponseEntity<HttpResponse<MemberRolesResponse>> getMemberRoles(@PathVariable Long memberId) {
        MemberRolesResponse memberRoles = memberRoleService.getMemberRoles(memberId);
        return new ResponseEntity<>(new HttpResponse<>(1, "회원권한 조회에 성공했습니다.", memberRoles), HttpStatus.OK);
    }

    @PutMapping("/{memberId}/role")
    public ResponseEntity<HttpResponse<MemberRolesResponse>> updateMemberRoles(
            @PathVariable Long memberId,
            @RequestBody UpdateMemberRolesRequest request) {
        MemberRolesResponse memberRoles = memberRoleService.updateMemberRoles(memberId, request);
        return new ResponseEntity<>(new HttpResponse<>(1, "회원권한 변경에 성공했습니다.", memberRoles), HttpStatus.OK);
    }
}
