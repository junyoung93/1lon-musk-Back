package com.clone.clone.security.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SignupRequestDto {

    private String username;
    private String password;
    private String email;
    private boolean marketing;  //마케팅 약관
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
}
