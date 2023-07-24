package com.clone.clone.user;

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
