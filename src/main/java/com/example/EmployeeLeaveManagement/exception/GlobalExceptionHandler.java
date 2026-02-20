package com.example.EmployeeLeaveManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Employee Leave Management system (HRMS).
 * <p>
 * Captures exceptions thrown across all controllers and services, returning
 * structured JSON error responses with appropriate HTTP status codes.
 * </p>
 *
 * <p>Handles the following exceptions:</p>
 * <ul>
 *     <li>{@link CustomException} - Business rule violations and application-specific errors.</li>
 *     <li>{@link MethodArgumentNotValidException} - Validation failures in request payloads.</li>
 *     <li>{@link HttpMessageNotReadableException} - Malformed or invalid JSON requests.</li>
 *     <li>{@link NoHandlerFoundException} - Requests to undefined endpoints.</li>
 *     <li>{@link Exception} - Any other unhandled runtime exceptions.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException ex){
        Map<String,Object> error=new HashMap<>();
        error.put("timestamp",LocalDateTime.now());
        error.put("status",HttpStatus.BAD_REQUEST.value());
        error.put("error",ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidException(MethodArgumentNotValidException ex){
        Map<String,Object> error=new HashMap<>();
        error.put("timestamp",LocalDateTime.now());
        error.put("status",HttpStatus.BAD_REQUEST.value());
        String message=ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage():"Validation failed";
        error.put("error",message);
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleInvalidJson(HttpMessageNotReadableException ex){
        Map<String,Object> error=new HashMap<>();
        error.put("timestamp",LocalDateTime.now());
        error.put("status",HttpStatus.BAD_REQUEST.value());
        error.put("error","Malformed JSON request");
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNotFound(NoHandlerFoundException ex){
        Map<String,Object> error =new HashMap<>();
        error.put("timestamp",LocalDateTime.now());
        error.put("status",HttpStatus.NOT_FOUND.value());
        error.put("error","Endpoint not found: "+ex.getRequestURL());
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

}
