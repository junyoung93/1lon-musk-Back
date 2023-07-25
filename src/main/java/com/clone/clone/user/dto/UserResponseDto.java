package com.clone.clone.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String nickname;
    private String email;

    public UserResponseDto(String nickname, String email) {
        this.nickname = nickname;
        this.email = email;
    }
}
