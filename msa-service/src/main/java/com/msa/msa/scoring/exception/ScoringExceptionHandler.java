package com.msa.msa.scoring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ScoringExceptionHandler {

    @ExceptionHandler(AssessmentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAssessmentNotFound(
            AssessmentNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }
}