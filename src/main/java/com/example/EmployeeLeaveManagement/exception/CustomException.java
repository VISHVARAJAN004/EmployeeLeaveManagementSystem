package com.example.EmployeeLeaveManagement.exception;

/**
 * Custom runtime exception used in the HRMS system.
 * <p>This exception is thrown for business rule violations or
 * application-specific errors, such as invalid leave requests,
 * inactive employees, or insufficient leave balance.</p>
 */
public class CustomException extends RuntimeException{

    /**
     * Constructs a new CustomException with the specified detail message.
     * @param message the detail message describing the cause of the exception
     */
    public CustomException(String message){
        super(message);
    }
}
