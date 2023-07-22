package com.clone.clone.security.jwt;

import com.clone.clone.security.dto.ToekenResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

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

    public static final String AUTHORIZATION_HEADER = "Authorization";

    // 사용자 권한 값의 KEY / 권한 정보를 토크에 저장할 때 사용하는 키
    public static final String AUTHORIZATION_KEY = "auth";

    // Token 식별자. 말그대로 JWT를 식별하는 접두사입니다
    public static final String BEARER_PREFIX = "Bearer ";  //bearer 규칙(공백 필수)

    // 토큰 만료시간
    long minute = 60*1000L;
    long hour = 60*minute;
    private final long TOKEN_TIME = hour;       //1시간


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
        Date date = new Date();	//현재 날짜 시간
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm)
                        .compact();     //마무리
    }


    //토큰을 body에 저장
    public ResponseEntity<ToekenResponseDto> addJwtBody(String token, HttpServletResponse response){
        try {
            token= URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

            //body에 보낼 ToekenResponse 객체 생성
            ToekenResponseDto toekenResponseDto = new ToekenResponseDto(token);

            // Response 객체에 body 추가
            return ResponseEntity.ok(toekenResponseDto);

        } catch (UnsupportedEncodingException e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // JWT 토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }


    public boolean validateToken(String token) {
        try {
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
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }


}