package org.optipace.garmentService.exception;

public class UnauthorisedException extends  RuntimeException{

    public UnauthorisedException(String message){
        super(message);
    }
}
