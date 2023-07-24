package com.clone.clone.user;

import com.clone.clone.security.dto.StatusMessageDto;
import com.clone.clone.user.SignExeption;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


    @ControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(SignExeption.class)
        public ResponseEntity<StatusMessageDto> handleSignException(SignExeption e) {
            HttpStatus status;
            String errorCode;

            switch (e.getError()) {
                case "auth_001":
                    status = HttpStatus.UNAUTHORIZED;
                    errorCode = "auth_001";
                    break;
                case "auth_002":
                    status = HttpStatus.UNAUTHORIZED;
                    errorCode = "auth_002";
                    break;
                case "auth_003":
                    status = HttpStatus.BAD_REQUEST;
                    errorCode = "auth_003";
                    break;
                default:
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                    errorCode = "unknown_error";
                    break;
            }

            StatusMessageDto statusMessageDto = new StatusMessageDto(errorCode, status.value(), e.getMessage());
            return new ResponseEntity<>(statusMessageDto, status);
        }
    }


