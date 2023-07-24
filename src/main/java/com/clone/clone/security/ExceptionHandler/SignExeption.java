package com.clone.clone.security.ExceptionHandler;

public class SignExeption extends RuntimeException{
    private String error;
    public SignExeption(String message, String error){
        super(message);
        this.error = error;
    }

    public String getError(){
        return this.error;
    }


}
