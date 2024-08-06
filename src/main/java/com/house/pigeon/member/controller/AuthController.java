package com.house.pigeon.member.controller;

import com.house.pigeon.common.jwt.model.request.ReissueRefreshTokenRequest;
import com.house.pigeon.common.response.HttpResponse;
import com.house.pigeon.member.model.EmailCheckDTO;
import com.house.pigeon.member.model.MemberDTO;
import com.house.pigeon.member.model.request.CreateMemberRequest;
import com.house.pigeon.member.model.request.EmailVerificationRequest;
import com.house.pigeon.member.model.request.LoginMemberRequest;
import com.house.pigeon.member.model.request.LogoutMemberRequest;
import com.house.pigeon.member.model.response.LoginMemberResponse;
import com.house.pigeon.member.service.MailService;
import com.house.pigeon.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final MemberService memberService;
    private final MailService mailService;

    @PostMapping
    public ResponseEntity<HttpResponse<MemberDTO>> saveMember(@RequestBody @Valid CreateMemberRequest request, BindingResult result) {
        MemberDTO memberDTO = memberService.saveMember(request);
        return new ResponseEntity<>(new HttpResponse<>(1, "회원가입에 성공했습니다.", memberDTO), HttpStatus.CREATED);
    }

    // 회원 로그인
    @PostMapping("/login")
    public ResponseEntity<HttpResponse<LoginMemberResponse>> login(@RequestBody @Valid LoginMemberRequest request, BindingResult result) {
        LoginMemberResponse loginMemberResponse = memberService.login(request);
        return new ResponseEntity<>(new HttpResponse<>(1, "로그인에 성공했습니다.", loginMemberResponse), HttpStatus.OK);
    }

    // 회원 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<HttpResponse<Void>> logout(@RequestBody LogoutMemberRequest request) {
        memberService.logout(request);
        return new ResponseEntity<>(new HttpResponse<>(1, "로그아웃에 성공했습니다.", null), HttpStatus.OK);
    }

    // JWT 토큰 재발급
    @PostMapping("/token-reissue")
    public ResponseEntity<HttpResponse<LoginMemberResponse>> refreshTokenReissue(@RequestBody ReissueRefreshTokenRequest request, BindingResult result) {
        LoginMemberResponse loginMemberResponse = memberService.refreshTokenReissue(request);
        return new ResponseEntity<>(new HttpResponse<>(1, "토큰을 재발급 합니다.", loginMemberResponse), HttpStatus.OK);
    }


    @GetMapping("/{memberId}")
    public ResponseEntity<HttpResponse<MemberDTO>> getMember(@PathVariable(name = "memberId") Long memberId) {
        MemberDTO memberDTO = memberService.getMember(memberId);
        return new ResponseEntity<>(new HttpResponse<>(1, "회원 조회에 성공했습니다.", memberDTO), HttpStatus.CREATED);
    }

    @PostMapping("/mail/send")
    public ResponseEntity<HttpResponse<Void>> sendEmail(@RequestBody @Valid EmailVerificationRequest request, BindingResult result) {
        mailService.sendVerificationEmail(request.email());
        return new ResponseEntity<>(new HttpResponse<>(1, "유효한 이메일입니다.", null), HttpStatus.OK);
    }

    @PostMapping("/mail/verify")
    public ResponseEntity<HttpResponse<Void>> authCheck(@RequestBody @Valid EmailCheckDTO emailCheckDTO) {
        mailService.verifyAuthNumber(emailCheckDTO);
        return new ResponseEntity<>(new HttpResponse<>(1, "이메일 인증 번호가 유효합니다.", null), HttpStatus.OK);
    }
}
