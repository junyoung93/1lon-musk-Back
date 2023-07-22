package com.clone.clone;

import com.clone.clone.security.dto.LoginRequestDto;
import com.clone.clone.security.dto.SignupRequestDto;
import com.clone.clone.security.jwt.JwtUtil;
import com.clone.clone.user.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SpringBootTest

class CloneApplicationTests {
	private final JwtUtil jwtUtil;
	private final UserService userService;

	CloneApplicationTests(JwtUtil jwtUtil, UserService userService) {
		this.jwtUtil = jwtUtil;
		this.userService = userService;
	}

	@Test
	@GetMapping("/create-jwt")
	public String createJwt(HttpServletResponse res) {
		// Jwt 생성
		String token = jwtUtil.createToken("Robbie");

		// Jwt 쿠키 저장
		jwtUtil.addJwtBody(token, res);

		return "createJwt : " + token;
	}

	@Test

	@GetMapping("/get-jwt")
	public String getJwt(@CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) {
		// JWT 토큰 substring
		String token = jwtUtil.substringToken(tokenValue);

		// 토큰 검증
		if(!jwtUtil.validateToken(token)){
			throw new IllegalArgumentException("Token Error");
		}

		// 토큰에서 사용자 정보 가져오기
		Claims info = jwtUtil.getUserInfoFromToken(token);
		// 사용자 username
		String username = info.getSubject();
		System.out.println("username = " + username);
		// 사용자 권한
		String authority = (String) info.get(JwtUtil.AUTHORIZATION_KEY);
		System.out.println("authority = " + authority);

		return "getJwt : " + username + ", " + authority;
	}

	@Test

	@ResponseBody
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
		return userService.signup(signupRequestDto);
	}

	@Test

	@PostMapping("/signin")
	public ResponseEntity<?> signin(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse res){
		return userService.signin(loginRequestDto,res);
	}



}
