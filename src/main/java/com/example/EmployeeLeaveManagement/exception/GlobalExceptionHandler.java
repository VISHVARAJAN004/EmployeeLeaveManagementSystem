package com.example.EmployeeLeaveManagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the HRMS system.
 * <p>This class handles exceptions thrown across all controllers and services,
 * returning structured JSON error responses with HTTP status codes.</p>
 * <p>It handles:</p>
 * <ul>
 *     <li>{@link CustomException} - for application-specific business rule violations.</li>
 *     <li>{@link MethodArgumentNotValidException} - for validation errors in request bodies.</li>
 *     <li>{@link Exception} - for any other unhandled exceptions.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * Handles CustomException thrown in the application.
     * @param ex the CustomException instance
     * @return a ResponseEntity containing timestamp, status, and error message
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException ex){
        Map<String,Object> error=new HashMap<>();
        error.put("timestamp",LocalDateTime.now());
        error.put("status",HttpStatus.BAD_REQUEST.value());
        error.put("error",ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }


    /**
     * Handles validation errors from @Valid annotated request bodies.
     * @param ex the MethodArgumentNotValidException instance
     * @return a ResponseEntity containing timestamp, status, and validation error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidException(MethodArgumentNotValidException ex){
        Map<String,Object> error=new HashMap<>();
        error.put("timestamp",LocalDateTime.now());
        error.put("status",HttpStatus.BAD_REQUEST.value());
        error.put("error",ex.getBindingResult().getFieldError().getDefaultMessage());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other uncaught exceptions.
     * @param ex the Exception instance
     * @return a ResponseEntity containing timestamp, status, and error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex){
        Map<String,Object> error=new HashMap<>();
        error.put("timestamp",LocalDateTime.now());
        error.put("status",HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error",ex.getMessage());
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
