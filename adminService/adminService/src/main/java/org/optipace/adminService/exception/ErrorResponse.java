package org.optipace.adminService.exception;

import org.optipace.adminService.exception.ErrorResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    public int status;
    public String message;
}