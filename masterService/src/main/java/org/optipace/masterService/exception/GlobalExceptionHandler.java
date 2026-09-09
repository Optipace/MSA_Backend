package org.optipace.masterService.exception;


import lombok.extern.slf4j.Slf4j;
import org.optipace.masterService.dto.response.Response;
import org.optipace.masterService.dto.response.SingleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access Denied: {}", ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied: You do not have permission to access this resource"
        );
        return new ResponseEntity<>(err, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        log.warn("Endpoint not found: {}", ex.getResourcePath());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "The requested endpoint '/" + ex.getResourcePath() + "' does not exist"
        );
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {

//        log.error("Bad Request exception", ex);
        log.error("Bad Request exception: {}", ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorisedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorisation(UnauthorisedException ex) {
//        log.error("Unauthorised exception", ex);
        log.error("Unauthorised exception: {}", ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(err, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
//        log.error("forbidden exception", ex);
        log.error("forbidden exception: {}", ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(err, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
//        log.error("Not found exception", ex);
        log.error("Not found exception: {}", ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();

        // Collect all field errors into a readable format
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        // Join the errors into a single string
        String combinedErrorMessage = "Validation failed - " + String.join(", ", errors);

        log.warn("Validation exception: {}", combinedErrorMessage);

        // Use the standard ErrorResponse matching your other handlers
        ErrorResponse err = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                combinedErrorMessage
        );

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<SingleResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        Map<String, String> validationErrors = new HashMap<>();
//
//        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
//            validationErrors.put(error.getField(), error.getDefaultMessage());
//        }
//
//        Response responseMeta = new Response();
//        responseMeta.setCode(HttpStatus.BAD_REQUEST.value());
//        responseMeta.setMessage("Input validation failed");
//
//        log.warn("Validation exception: {}", ex.getMessage());
//        SingleResponse<Map<String, String>> singleResponse = new SingleResponse<>();
//        singleResponse.setData(validationErrors);
//        singleResponse.setResponse(responseMeta);
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(singleResponse);
//    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed JSON or invalid data type: {}", ex.getMessage());

        ErrorResponse err = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage() // "Malformed JSON request or invalid data format provided. Please check your input types."
        );

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MicroserviceException.class)
    public ResponseEntity<SingleResponse<?>> handleMicroserviceIntegrationException(MicroserviceException ex) {
        // Return the exact status code that the authService originally threw
        return ResponseEntity.status(ex.getStatusCode())
                .body(new SingleResponse<>(
                        null,
                        new Response(ex.getStatusCode(), ex.getMessage()) // Assuming you have a Response object for errors
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {

//        log.error("Unexpected application error", ex);
        log.error("Unexpected application error: {}", ex.getMessage());
        ErrorResponse err = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error"
        );

        return new ResponseEntity<>(err,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
