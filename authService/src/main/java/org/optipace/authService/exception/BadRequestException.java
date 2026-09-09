package org.optipace.authService.exception;

public class BadRequestException extends  RuntimeException{

    public BadRequestException(String message){
        super(message);
    }
}
