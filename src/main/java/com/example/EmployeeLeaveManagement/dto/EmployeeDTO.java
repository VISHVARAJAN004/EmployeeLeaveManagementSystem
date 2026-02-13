package com.example.EmployeeLeaveManagement.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing an Employee input.
 * <p>This DTO is used to transfer employee information from the client
 * to the server when creating a new employee. It contains validation
 * constraints to ensure data integrity.</p>
 */
@Data
public class EmployeeDTO {

    /**
     * Name of the employee.
     * <p>Cannot be blank and must be between 3 and 30 characters.</p>
     */
    @NotBlank(message="Name must not be blank")
    @Size(min=3,max=30,message="Name must be between 3 and 30 characters")
    private String name;

    /**
     * Email of the employee.
     * <p>Cannot be blank and must be a valid email format.</p>
     */
    @NotBlank(message="Email must not be blank")
    @Email(message="Email should be valid")
    private String email;

    /**
     * Date of birth of the employee.
     * <p>Cannot be null.</p>
     */
    @NotNull(message="Date of birth must not be null")
    private LocalDate dateOfBirth;
}
