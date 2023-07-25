package com.clone.clone.security.filter;

import com.clone.clone.security.ExceptionHandler.LoginFailHandler;
import com.clone.clone.security.impl.UserDetailsImpl;
import com.clone.clone.security.jwt.JwtUtil;
import com.clone.clone.user.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

// Jwt를 이용한 이증
@Slf4j(topic = "로그인 및 JWT 생성")  //로그명
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/signin");   //필터가 처리할 요청 URL을 "/signin"으로 설정
        setAuthenticationFailureHandler(new LoginFailHandler());
    }

    //로그인 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        //로그인 정보를 추출하여 인증을 시도
        // HTTP 요청의 본문을 InputStream으로부터 읽어와서 LoginRequestDto 타입의 객체로 변환
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            //인증과정!!!
            return getAuthenticationManager().authenticate( //AuthenticationManager의 authenticate 인증 시작
                    //AuthenticationManager는 ProviderManager을 구현하여 사용 되며
                    //이는 등록된 AuthenticationProvider를 순회합니다.
                    //UsernamePasswordAuthenticationToken객체를 생성
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    //성공한 경우 처리 -> body에 토큰을 담아 날림
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain
            , Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        String token = jwtUtil.createToken(username);
        jwtUtil.addAccessTokenCookie(token, response);

        String refreshToken = jwtUtil.createRefreshToken();
        jwtUtil.addRefreshTokenCookie(refreshToken, response);
    }
}
