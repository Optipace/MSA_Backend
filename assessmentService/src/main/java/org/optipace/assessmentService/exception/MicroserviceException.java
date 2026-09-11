package org.optipace.assessmentService.exception;

import lombok.Getter;

@Getter
public class MicroserviceException extends RuntimeException {

    private final int statusCode;

    public MicroserviceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
