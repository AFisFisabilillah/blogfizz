package com.fizu.blogfiz.controller;

import com.fizu.blogfiz.dto.ErrorResponse;
import com.fizu.blogfiz.exception.ResponseException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<ErrorResponse<Map<String, String>>> errorConstraint(ConstraintViolationException exception){
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        Map<String, String> response = new HashMap<>();

        for(ConstraintViolation<?> violation : violations){
            response.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        ErrorResponse<Map<String, String>> errorResponse = new ErrorResponse<>();
        errorResponse.setMessage("Bad Request data");
        errorResponse.setStatus("error");
        errorResponse.setErrors(response);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ResponseException.class)
    private ResponseEntity<ErrorResponse<String>> responseException(ResponseException exception){
        ErrorResponse<String> errorResponse = new ErrorResponse<>();
        errorResponse.setStatus("error");
        errorResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(exception.getStatus()).body(errorResponse);
    }
}
