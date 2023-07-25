package com.clone.clone.user.service;

import com.clone.clone.exception.CustomException;
import com.clone.clone.exception.ErrorCode;
import com.clone.clone.security.jwt.JwtUtil;
import com.clone.clone.user.dto.SignResponseDto;
import com.clone.clone.user.dto.SignupRequestDto;
import com.clone.clone.user.dto.UserResponseDto;
import com.clone.clone.user.entity.User;
import com.clone.clone.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;


    //사용자로부터 회원 가입 요청 정보를 담은 DTO를 인자로 받아 처리합니다.
    @Transactional
    public SignResponseDto signup(@Valid SignupRequestDto requestDto, HttpServletResponse response) {
        //정보 가져옴
        String nickname = requestDto.getNickname();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String email = requestDto.getEmail();
        boolean marketing = requestDto.isMarketing();

        // email 중복 유효성 검사
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new CustomException(ErrorCode.DIFFERENT_FORMAT);
        }

        if (requestDto.getEmail().isEmpty() || requestDto.getPassword().isEmpty()) {
            throw new CustomException(ErrorCode.NULLABLE);
        }

        // 마켓팅 수집 동의 여부 약관 유효성 검사
        if (!marketing) {
            throw new CustomException(ErrorCode.DIFFERENT_FORMAT);
        }
        log.info("마켓팅 동의 수집 여부 확인 (=true)");

        //user repo에 저장
        User user = new User(nickname, password, email, marketing);
        userRepository.save(user);

        String token = jwtUtil.createToken(email);
        jwtUtil.addAccessTokenCookie(token, response);

        if(token==null || token.isEmpty()){
            throw new CustomException(ErrorCode.TOKEN_ERROR);
        }

        String refreshToken = jwtUtil.createRefreshToken();
        jwtUtil.addRefreshTokenCookie(refreshToken, response);

        //Jackson에 의해 자동으로 json형태로 나감.
        return new SignResponseDto(HttpStatus.OK.value());
    }

    //refresh token
    @Transactional
    public void SignRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String refreshToken = jwtUtil.getRefreshTokenFromCookies(request);
        String tokenValue = jwtUtil.getTokenFromCookie(request);
        refreshToken = jwtUtil.substringToken(refreshToken);
        tokenValue = jwtUtil.substringToken(tokenValue);


        if (StringUtils.hasText(tokenValue)) {
            if (!jwtUtil.validateToken(tokenValue)) {
                if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                    String username = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();
                    String newAccessToken = jwtUtil.createToken(username);
                    newAccessToken = URLEncoder.encode(newAccessToken, "utf-8")
                            .replaceAll("\\+", "%20");
                    Cookie cookie = new Cookie("AccessToken", newAccessToken);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
            try {
                jwtUtil.setAuthentication(info.getSubject());
            } catch (Exception e) {
                throw new CustomException(ErrorCode.EXPIRED_TOKEN);
            }
        }
    }

    @Transactional
    public UserResponseDto getTokenInfo(HttpServletRequest request, HttpServletResponse response) {
        String tokenValue = jwtUtil.getTokenFromCookie(request);
        tokenValue = jwtUtil.substringToken(tokenValue);


        if (jwtUtil.validateToken(tokenValue)) {
            Claims claims = jwtUtil.getUserInfoFromToken(tokenValue);
            String nickname = claims.getSubject();

            Optional<User> optionalUser = userRepository.findByEmail(nickname);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                UserResponseDto userResponseDto = new UserResponseDto(user.getNickname(), user.getEmail());
                return userResponseDto;
            }
        } else {
            new CustomException(ErrorCode.NOT_FOUND_USER);
        }

        return null;
    }
}
