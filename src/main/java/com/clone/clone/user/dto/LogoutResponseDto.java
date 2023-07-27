package com.clone.clone.user.dto;

import lombok.Getter;

@Getter
public class LogoutResponseDto {
    private int status;

    public LogoutResponseDto(int status) {
        this.status=status;
    }
}
