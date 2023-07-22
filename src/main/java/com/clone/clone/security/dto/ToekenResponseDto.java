package com.clone.clone.security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToekenResponseDto {
    private String token;

    //토큰을 body에 반환하기 위해 만들었음. 보안에 의심이 감
    public ToekenResponseDto(String token){
        this.token=token;
    }

}
