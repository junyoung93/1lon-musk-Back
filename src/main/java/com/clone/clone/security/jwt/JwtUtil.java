package com.clone.clone.security.jwt;

import com.clone.clone.exception.CustomException;
import com.clone.clone.exception.ErrorCode;
import com.clone.clone.security.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    // Token 식별자. 말그대로 JWT를 식별하는 접두사입니다
    public static final String BEARER_PREFIX = "Bearer ";  //bearer 규칙(공백 필수)
    public static final String COOKIE_PREFIX = "Bearer%20";
    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");
    private final UserDetailsServiceImpl userDetailsService;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    // 토큰 만료시간
    long sec = 1000L;
    long minute = 60 * 1000L;
    private final long ACCESSTOKEN_TIME = 20 * minute;
    long hour = 60 * minute;
    private final long REFRESHTOKEN_TIME = hour;


    @Value("${jwt.secret.key}")

    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);  //Hash-based Message Authentication
    }

    //토큰 생성
    public String createToken(String email) {
        Date date = new Date();    //현재 날짜 시간
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .setExpiration(new Date(date.getTime() + ACCESSTOKEN_TIME))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm)
                        .compact();     //마무리
    }

    // refresh 토큰 생성
    public String createRefreshToken() {
        Date date = new Date();    //현재 날짜 시간
        return BEARER_PREFIX +
                Jwts.builder()
                        .setExpiration(new Date(date.getTime() + REFRESHTOKEN_TIME))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm)
                        .compact();     //마무리
    }

    // RefreshToken을 토큰 쿠키에 저장
    public void addRefreshTokenCookie(String token, HttpServletResponse response) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");
            Cookie cookie = new Cookie("RefreshToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            cookie.setAttribute("SameSite", "None");
            response.addCookie(cookie);
            //쿠키 유효 경로 설정

        } catch (UnsupportedEncodingException e) {
            new CustomException(ErrorCode.TOKEN_ERROR);
        }
    }

    //AccessToken을 쿠키에 저장
    public void addAccessTokenCookie(String token, HttpServletResponse response) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");
            Cookie cookie = new Cookie("AccessToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            cookie.setAttribute("SameSite", "None");
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            new CustomException(ErrorCode.TOKEN_ERROR);
        }
    }

    // JWT 토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(COOKIE_PREFIX)) {
            return tokenValue.substring(COOKIE_PREFIX.length());
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            new CustomException(ErrorCode.NOT_FOUND_USER);
        } catch (ExpiredJwtException e) {
            new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    //쿠키에서 토큰을 추출
    public String getTokenFromCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("AccessToken")) { // 쿠키 이름에 따라 변경
                    log.info(cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // cookie에서 refreshToken을 추출
    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("RefreshToken")) {
                    log.info(cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

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
