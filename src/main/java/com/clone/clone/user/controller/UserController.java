package com.clone.clone.user.controller;

import com.clone.clone.user.service.UserService;
import com.clone.clone.user.dto.SignupRequestDto;
import com.clone.clone.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")

public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    //회원가입
    @ResponseBody
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    //로그인
    @PostMapping("/refreshToken")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        jwtUtil.refreshToken(request, response);
        return ResponseEntity.ok().build();
    }
}