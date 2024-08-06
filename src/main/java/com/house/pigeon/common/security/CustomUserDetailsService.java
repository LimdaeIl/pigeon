package com.house.pigeon.common.security;

import com.house.pigeon.member.model.Member;
import com.house.pigeon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // CustomUserDetailsService: 스프링 시큐리티와 연동하여 사용자 인증 및 권한 관리를 처리합니다.
    // 시큐리티로 로그인이 될때, 시큐리티가 loadUserByUsername() 실행해서 username 을 체크!!
    // 없으면 오류
    // 있으면 정상적으로 시큐리티 컨텍스트 내부 세션에 로그인된 세션이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new InternalAuthenticationServiceException("인증 실패"));
        // UsernameNotFoundException 으로 처리할지 고민해보자.

        return new CustomUserDetails(member);
    }
}
