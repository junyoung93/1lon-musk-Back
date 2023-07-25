package com.clone.clone.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    /*
    * body에 토큰 전달
    (1) jwt 생성
    (2) 생성된 jwt를 body로 응답
    (3) 클라에서 응답 본문에서 jwt를 추출
    (4) Jwt를 body에 넣기
    (5) 백엔드에서 body response에서 jwt 추출
    (6) jwt 검증
    (7) jwt에서 사용자 정보를 가져오기
    */

    public static final String AUTHORIZATION_HEADER = "AccessToken";

    public static final String REFRESH_HEADER = "RefreshToken";
    // 사용자 권한 값의 KEY / 권한 정보를 토크에 저장할 때 사용하는 키
    public static final String AUTHORIZATION_KEY = "auth";

    // Token 식별자. 말그대로 JWT를 식별하는 접두사입니다
    public static final String BEARER_PREFIX = "Bearer ";  //bearer 규칙(공백 필수)
    public static final String COOKIE_PREFIX = "Bearer%20";

    // 토큰 만료시간
    long minute = 60*1000L;
    long hour = 60*minute;

    //accesToken 생명 주기
    private final long ACCESSTOKEN_TIME = 10*minute;

    //refreshToken 생명 주기
    private final long REFRESHTOKEN_TIME = hour;




    @Value("${jwt.secret.key}")

    private String secretKey;
    private Key key;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init(){
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);  //Hash-based Message Authentication
    }

    //토큰 생성
    public String createToken(String username) {
        log.info("access 토큰 생성");
        Date date = new Date();	//현재 날짜 시간
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .setExpiration(new Date(date.getTime() + ACCESSTOKEN_TIME))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm)
                        .compact();     //마무리
    }

    // refresh 토큰 생성
    public String createRefreshToken() {
        log.info("refresh 토큰 생성");
        Date date = new Date();	//현재 날짜 시간
        return BEARER_PREFIX +
                Jwts.builder()
//                        .setSubject(username)
                        .setExpiration(new Date(date.getTime() + REFRESHTOKEN_TIME))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm)
                        .compact();     //마무리
    }

    // refresh 토큰 쿠키에 저장
    public void addRefreshTokenCookie(String token, HttpServletResponse res){
        try {
            token =  URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

            //쿠키 생성
            Cookie cookie = new Cookie(REFRESH_HEADER, token);
            log.info("refresh 토큰 쿠키에 저장");

            //쿠키 유효 경로 설정
            cookie.setPath("/");
            res.addCookie(cookie);

        } catch (UnsupportedEncodingException e){
            log.error(e.getMessage());
        }
    }

    public void addAccessTokenCookie(String token, HttpServletResponse res){
        try {
            token =  URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

            //쿠키 생성
            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token);
            log.info("access 토큰 쿠키에 저장");

            //쿠키 유효 경로 설정
            cookie.setPath("/");
            res.addCookie(cookie);

        } catch (UnsupportedEncodingException e){
            log.error(e.getMessage());
        }
    }


    // JWT 토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(COOKIE_PREFIX)) {
            log.info("bearer 제거");
            return tokenValue.substring(COOKIE_PREFIX.length());
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }



    public boolean validateToken(String token) {
        try {
            log.info("유효성 검사 통과");
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        log.info("유저 정보 전달 완료");
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }


//    public String getTokenFromRequest(HttpServletRequest req) {
//        String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }

    public String getTokenFromCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("AccessToken")) { // 쿠키 이름에 따라 변경
                    return cookie.getValue();
                }
            }
        return null;
    }

    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("RefreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }



    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
//        String refreshToken = getRefreshTokenFromCookies(request);
//        if (refreshToken != null && validateToken(refreshToken)) {
//            String username = getUserInfoFromToken(refreshToken).getSubject();
//            String newAccessToken = createToken(username);
//
//            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, newAccessToken);
//            log.info("refresh 토큰 발급");
//
//            cookie.setPath("/");
//            response.addCookie(cookie);
//        } else {
//
//        }
    }




}
