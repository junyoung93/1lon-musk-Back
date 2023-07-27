package com.clone.clone.user.controller;

import com.clone.clone.user.dto.*;
import com.clone.clone.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @ResponseBody
    @PostMapping("api/user/signup")
    public SignResponseDto signup(@RequestBody @Valid SignupRequestDto signupRequestDto, HttpServletResponse response) {
        return userService.signup(signupRequestDto, response);
    }

    //로그인
    @GetMapping("/refreshToken")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        userService.SignRefreshToken(request, response);
        return ResponseEntity.ok().build();
    }

    // token 별 별 email, nickname
    @GetMapping("api/user/token")
    public UserResponseDto getTokenInfo(HttpServletRequest request, HttpServletResponse response) {
        return userService.getTokenInfo(request, response);
    }

    @GetMapping("api/pwd/forgot")
    public PwdForgotResponseDto pwdForgot(@RequestBody PwdForgotRequestDto pwdForgotRequestDto){
        return userService.pwdForgot(pwdForgotRequestDto);
    }

    @GetMapping("api/pwd/newPassword")
    public PwdForgotResponseDto pwdForgot(
            @RequestParam String token,
            @RequestBody PwdResetRequestDto pwdResetRequestDto
    ){
        return userService.pwdReset(token, pwdResetRequestDto);
    }

    @PostMapping("api/user/logout")
    public LogoutResponseDto logout(HttpServletRequest request, HttpServletResponse response){
       userService.logout(request,response);
       return new LogoutResponseDto(HttpStatus.OK.value());
    }
}