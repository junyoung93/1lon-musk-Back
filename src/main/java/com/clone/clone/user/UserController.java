package com.clone.clone.user;

import com.clone.clone.security.dto.LoginRequestDto;
import com.clone.clone.security.dto.SignResponseDto;
import com.clone.clone.security.dto.SignupRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")

public class UserController {

    private final UserService userService;


    @ResponseBody
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    @PostMapping("/signin")
    public SignResponseDto signin(@RequestBody LoginRequestDto loginRequestDto){
        return userService.signin(loginRequestDto);
    }



}