package com.clone.clone.user.requestdto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PwdForgotRequestDto {
    private String email;

}
