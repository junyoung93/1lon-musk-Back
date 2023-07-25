package com.clone.clone.security.filter;

import com.clone.clone.security.UserDetailsServiceImpl;
import com.clone.clone.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "AccessToken";
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = jwtUtil.getTokenFromCookie(request);
        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request);
        refreshToken=jwtUtil.substringToken(refreshToken);
        tokenValue=jwtUtil.substringToken(tokenValue);
        log.info(refreshToken);

        if (StringUtils.hasText(tokenValue)) {
            // JWT 토큰 substring
            log.info(tokenValue);
            if (!jwtUtil.validateToken(tokenValue)) {
                if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                    String username = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();
                    String newAccessToken = jwtUtil.createToken(username);
                    newAccessToken =  URLEncoder.encode(newAccessToken, "utf-8").replaceAll("\\+", "%20");
                    Cookie cookie = new Cookie(AUTHORIZATION_HEADER, newAccessToken);
                    log.info("refresh 토큰 발급");
                    log.info(newAccessToken);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}