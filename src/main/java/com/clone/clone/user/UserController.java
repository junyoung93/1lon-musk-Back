package com.clone.clone.user;

import com.clone.clone.security.dto.LoginRequestDto;
import com.clone.clone.security.dto.SignupRequestDto;
import com.clone.clone.user.requestdto.PwdForgotRequestDto;
import com.clone.clone.user.requestdto.PwdResetRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ResponseBody
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse res){
        return userService.signin(loginRequestDto,res);
    }

    @GetMapping("/pwd/forgot")
    public ResponseEntity<?> pwdForgot(@RequestBody PwdForgotRequestDto pwdForgotRequestDto){
        return userService.pwdForgot(pwdForgotRequestDto);
    }

    @GetMapping("/pwd/newPassword")
    public ResponseEntity<?> pwdForgot(@RequestParam String token, @RequestBody PwdResetRequestDto pwdResetRequestDto){
        return userService.pwdReset(token, pwdResetRequestDto.getPassword());
    }

}