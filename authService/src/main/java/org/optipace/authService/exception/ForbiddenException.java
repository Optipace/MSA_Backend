package org.optipace.authService.exception;

public class ForbiddenException  extends  RuntimeException{

    public ForbiddenException(String message){
        super(message);
    }
}
