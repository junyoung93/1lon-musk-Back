package com.clone.clone.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignResponseDto {
    private int status;

    public SignResponseDto(int status) {
        this.status = status;
    }
}
