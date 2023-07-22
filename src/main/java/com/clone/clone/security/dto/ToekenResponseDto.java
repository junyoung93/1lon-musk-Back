package com.clone.clone.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToekenResponseDto {
    private String token;

    public ToekenResponseDto(String token){
        this.token=token;
    }

}
