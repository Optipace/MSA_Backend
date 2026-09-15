package org.optipace.assessmentService.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    public int status;
    public String message;
}
