package com.clone.clone.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {
    private String accessToken;

    //토큰을 body에 반환하기 위해 만들었음. 보안에 의심이 감
    public TokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

}
