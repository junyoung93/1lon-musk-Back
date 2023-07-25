package com.clone.clone.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //USER
    DIFFERENT_FORMAT(HttpStatus.BAD_REQUEST.value(), "E-mail and password have different formats."),
    NULLABLE(HttpStatus.BAD_REQUEST.value(), "Nullable=false"),
    TOKEN_ERROR(HttpStatus.BAD_REQUEST.value(), "Unable to issue access tokens"),

    INVAILD_EMAIL_PASSWORD(HttpStatus.BAD_REQUEST.value(), "Wrong email or password format"),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST.value(), "User not found"),
    // throw new customE(NOT_FOUND_USER)
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Authentication token expired"),
    OUT_OF_RANGE(HttpStatus.UNAUTHORIZED.value(), "Out of range."),
    //main
    PAGE_NOT_EXITST(HttpStatus.UNAUTHORIZED.value(), "page does not exist."),
    CATEGORY_NOT_EXITST(HttpStatus.UNAUTHORIZED.value(), "category does not exist."),
    TOKEN_NOT_EXITST(HttpStatus.UNAUTHORIZED.value(), "Token does not exist."),
    ;

    private final int httpStatus;
    private final String message;

}