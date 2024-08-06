package com.house.pigeon.member.service;

import com.house.pigeon.common.exception.CustomDataAlreadyExistsException;
import com.house.pigeon.common.exception.CustomDataNotFoundException;
import com.house.pigeon.common.exception.CustomForbiddenException;
import com.house.pigeon.common.exception.CustomJwtException;
import com.house.pigeon.common.jwt.JWTProvider;
import com.house.pigeon.common.jwt.model.TokenStorage;
import com.house.pigeon.common.jwt.model.TokenType;
import com.house.pigeon.common.jwt.model.request.CreateJWTRequest;
import com.house.pigeon.common.jwt.model.request.ReissueRefreshTokenRequest;
import com.house.pigeon.common.jwt.repository.TokenStorageRepository;
import com.house.pigeon.common.jwt.service.JWTStorageService;
import com.house.pigeon.common.security.CustomUserDetailsService;
import com.house.pigeon.member.model.Member;
import com.house.pigeon.member.model.MemberDTO;
import com.house.pigeon.member.model.MemberRole;
import com.house.pigeon.member.model.request.CreateMemberRequest;
import com.house.pigeon.member.model.request.LoginMemberRequest;
import com.house.pigeon.member.model.request.LogoutMemberRequest;
import com.house.pigeon.member.model.response.LoginMemberResponse;
import com.house.pigeon.member.repository.MemberRepository;
import com.house.pigeon.role.model.Role;
import com.house.pigeon.role.model.RoleType;
import com.house.pigeon.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final JWTProvider jwtProvider;

    private final JWTStorageService jwtStorageService;
    private final TokenStorageRepository tokenStorageRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    public void checkMemberByEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new CustomDataAlreadyExistsException("이메일: 이미 가입된 이메일입니다.");
        }
    }

    public void checkMemberByPhone(String phone) {
        if (memberRepository.existsByPhone(phone)) {
            throw new CustomDataAlreadyExistsException("휴대전화번호: 이미 등록된 휴대전화번호입니다.");
        }
    }

    @Transactional
    public MemberDTO saveMember(CreateMemberRequest request) {
        checkMemberByEmail(request.email());
        checkMemberByPhone(request.phone());

        Member member = Member.builder()
                .email(request.email())
                .password(bCryptPasswordEncoder.encode(request.password()))
                .phone(request.phone())
                .build();

        Role defaultRole = roleRepository.findByRoleType(RoleType.MEMBER)
                .orElseThrow(() -> new CustomDataNotFoundException("권한: MEMBER 권한이 등록되어 있지 않습니다."));

        MemberRole memberRole = MemberRole.builder()
                .member(member)
                .role(defaultRole)
                .build();

        member.getMemberRoles().add(memberRole);
        member = memberRepository.save(member);

        return MemberDTO.from(member);
    }

    // 회원 로그인
    @Transactional
    public LoginMemberResponse login(LoginMemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomDataNotFoundException("잘못된 아이디입니다."));

        if (!bCryptPasswordEncoder.matches(request.password(), member.getPassword())) {
            throw new CustomForbiddenException("잘못된 비밀번호입니다.");
        }

        List<RoleType> roleTypes = member.getMemberRoles().stream()
                .map(memberRole -> memberRole.getRole().getRoleType())
                .toList();

        CreateJWTRequest jwtTokenRequest = CreateJWTRequest.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .roleTypes(roleTypes)
                .build();

        String accessToken = jwtProvider.createAccessToken(jwtTokenRequest);
        String refreshToken = jwtProvider.createRefreshToken(jwtTokenRequest);

        jwtStorageService.addToken(refreshToken, TokenType.REFRESH_TOKEN);

        return LoginMemberResponse.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 회원 로그아웃
    @Transactional
    public void logout(LogoutMemberRequest request) {
//        if(tokenBlacklistRepository.existsByToken(request.refreshToken())) {
//            throw new CustomJwtException("블랙리스트에 등록된 토큰입니다.");
//        }

        TokenStorage tokenStorage = tokenStorageRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new CustomJwtException("존재하지 않는 토큰입니다."));

//        TokenBlacklist tokenBlacklist = TokenBlacklist.builder()
//                .token(request.refreshToken())
//                .build();

        tokenStorageRepository.delete(tokenStorage);
//        tokenBlacklistRepository.save(tokenBlacklist);
    }

    // 토큰 재발급
    @Transactional
    public LoginMemberResponse refreshTokenReissue(ReissueRefreshTokenRequest request) {
        Boolean isValidateToken = jwtProvider.validateToken(request.refreshToken(), jwtProvider.getREFRESH_TOKEN_SECRET_KEY());
        Boolean isRefreshTokenExpiringSoon = jwtProvider.isTokenExpiringSoon(request.refreshToken(), jwtProvider.getREFRESH_TOKEN_SECRET_KEY(), 60 * 24L);
        Long memberId = jwtProvider.getMemberIdFromToken(request.refreshToken(), jwtProvider.getREFRESH_TOKEN_SECRET_KEY());
        String email = jwtProvider.getEmailFromToken(request.refreshToken(), jwtProvider.getREFRESH_TOKEN_SECRET_KEY());

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        List<RoleType> roleTypes = userDetails.getAuthorities().stream()
                .map(authority -> RoleType.valueOf(authority.getAuthority()))
                .toList();

        CreateJWTRequest createJWTRequest = new CreateJWTRequest(memberId, email, roleTypes);

//        if (tokenBlacklistRepository.existsByToken(request.refreshToken())) {
//            throw new CustomJwtException("블랙리스트에 등록된 토큰입니다.");
//        }

        // 리프레시 토큰이 유효하고 만료 시간이 24 시간 이상 남은 경우: 액세스 토큰만 재발행
        if (isValidateToken && !isRefreshTokenExpiringSoon) {
            String newAccessToken = jwtProvider.createAccessToken(createJWTRequest);

            return LoginMemberResponse.builder()
                    .memberId(memberId)
                    .accessToken(newAccessToken)
                    .refreshToken(request.refreshToken())
                    .build();
        }

        // 리프레시 토큰이 만료되거나 만료 시간이 24시간 미만 남은 경우: 액세스 토큰, 리프레시 토큰 재발행
        String newAccessToken = jwtProvider.createAccessToken(createJWTRequest);
        String newRefreshToken = jwtProvider.createRefreshToken(createJWTRequest);
        return LoginMemberResponse.builder()
                .memberId(memberId)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional(readOnly = true)
    public MemberDTO getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomDataNotFoundException("회원: 존재하지 않는 회원입니다."));

        return MemberDTO.from(member);
    }
}