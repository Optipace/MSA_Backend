package org.optipace.assessmentService.exception;

public class UnauthorisedException extends  RuntimeException{

    public UnauthorisedException(String message){
        super(message);
    }
}
