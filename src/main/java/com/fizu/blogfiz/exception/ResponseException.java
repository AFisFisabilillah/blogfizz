package com.fizu.blogfiz.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseException extends RuntimeException {
    private HttpStatus status;
    private String message;
    public ResponseException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
