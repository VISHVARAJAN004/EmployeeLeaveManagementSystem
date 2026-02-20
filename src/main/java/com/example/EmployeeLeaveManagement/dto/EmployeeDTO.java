package com.example.EmployeeLeaveManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

/**
 * Data Transfer Object for Employee information.
 * <p>
 * Used to transfer employee data from client requests to the backend.
 * Includes validation constraints for all fields.
 * </p>
 */
@Data
public class EmployeeDTO {

    /**
     * Name of the employee.
     * <p>
     * Must not be blank and length must be between 3 and 30 characters.
     * </p>
     */
    @NotBlank(message="Name must not be blank")
    @Size(min=3,max=30,message="Name must be between 3 and 30 characters")
    private String name;

    /**
     * Email of the employee.
     * <p>
     * Must not be blank and must follow a valid email format.
     * </p>
     */
    @NotBlank(message="Email must not be blank")
    @Email(message="Email should be valid")
    private String email;

    /**
     * Date of birth of the employee.
     * <p>
     * Cannot be null.
     * </p>
     */
    @NotNull(message="Date of birth must not be null")
    private LocalDate dateOfBirth;
}
