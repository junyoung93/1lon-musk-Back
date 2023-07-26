package com.clone.clone.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PwdForgotResponseDto {
    private int status;

    public PwdForgotResponseDto(int status) {
        this.status = status;
    }
}
