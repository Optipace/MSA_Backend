package org.optipace.authService.exception;


import org.optipace.authService.dto.responseDto.Response;
import org.optipace.authService.dto.responseDto.SingleResponse;
import org.optipace.authService.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {

//        log.error("Unexpected application error", ex);
        log.error("Unexpected application error: {}",ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error"
        );

        return new ResponseEntity<>(err,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access Denied: {}", ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied: You do not have permission to access this resource"
        );
        return new ResponseEntity<>(err, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse>  handleBadRequest(BadRequestException ex){

//        log.error("Bad Request exception", ex);
        log.error("Bad Request exception: {}", ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(err,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorisedException.class)
    public ResponseEntity<ErrorResponse>  handleUnauthorisation(UnauthorisedException ex){
//        log.error("Unauthorised exception", ex);
        log.error("Unauthorised exception: {}", ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(err,HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse>  handleForbidden(ForbiddenException ex){
//        log.error("forbidden exception", ex);
        log.error("forbidden exception: {}", ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(err,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse>  handleNotFound(NotFoundException ex){
//        log.error("Not found exception", ex);
        log.error("Not found exception: {}", ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(err,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SingleResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();

        // Loop through all validation errors and map the field name to the error message
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        }

        // 1. Construct the nested Response object for metadata
        Response responseMeta = new Response();
        responseMeta.setCode(HttpStatus.BAD_REQUEST.value()); // Sets the code to 400
        responseMeta.setMessage("Input validation failed");

        // 2. Construct the outer SingleResponse object and attach the data
        log.warn("Validation exception: {}", ex.getMessage());
        SingleResponse<Map<String, String>> singleResponse = new SingleResponse<>();
        singleResponse.setData(validationErrors);
        singleResponse.setResponse(responseMeta);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(singleResponse);
    }


}
