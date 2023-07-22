package com.clone.clone.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 이메일과 패스워드로만 로그인
public class LoginRequestDto {
    private String email;
    private String password;
}
