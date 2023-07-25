package com.clone.clone.user.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StatusMessageDto{

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;


    public StatusMessageDto(String error, int status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

}
