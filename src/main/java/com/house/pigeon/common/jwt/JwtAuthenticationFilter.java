package com.house.pigeon.common.jwt;

import com.house.pigeon.common.exception.CustomJwtException;
import com.house.pigeon.common.jwt.model.request.CreateJWTRequest;
import com.house.pigeon.common.security.CustomUserDetails;
import com.house.pigeon.role.model.RoleType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTProvider jwtTokenProvider;
    private final UserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getTokenFromRequest(request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY())) {
            // Access Token이 유효한 경우,
            authenticateWithToken(accessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY(), request);
        } else {
            // Access Token이 유효하지 않은 경우, Refresh Token을 검사
            String refreshToken = getRefreshTokenFromRequest(request);
            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken, jwtTokenProvider.getREFRESH_TOKEN_SECRET_KEY())) {
                // Refresh Token이 유효한 경우, 새로운 Access Token 발급
                String email = jwtTokenProvider.getEmailFromToken(refreshToken, jwtTokenProvider.getREFRESH_TOKEN_SECRET_KEY());

                // 새로운 Access Token 발급 로직을 서비스에서 호출
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                if (userDetails instanceof CustomUserDetails customUserDetails) {
                    List<RoleType> roleTypes = customUserDetails.getAuthorities().stream()
                            .map(authority -> RoleType.valueOf(authority.getAuthority()))
                            .toList();

                    CreateJWTRequest jwtRequest = new CreateJWTRequest(customUserDetails.getId(), email, roleTypes);
                    String newAccessToken = jwtTokenProvider.createAccessToken(jwtRequest);

                    response.setHeader("Authorization", "Bearer " + newAccessToken);
                    authenticateWithToken(newAccessToken, jwtTokenProvider.getACCESS_TOKEN_SECRET_KEY(), request);
                } else {
                    throw new CustomJwtException("Invalid UserDetails implementation");
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateWithToken(String token, byte[] secretKey, HttpServletRequest request) {
        String email = jwtTokenProvider.getEmailFromToken(token, secretKey);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        log.info("getTokenFromRequest: {}", bearerToken);
        return null;
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("refresh-token");
    }
}
