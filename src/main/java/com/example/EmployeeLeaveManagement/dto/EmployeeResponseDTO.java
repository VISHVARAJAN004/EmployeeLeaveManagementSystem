package com.example.EmployeeLeaveManagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;

/**
 * Data Transfer Object for sending Employee details in responses.
 * <p>
 * Contains all relevant employee information including personal details,
 * role, and leave balances.
 * </p>
 */
@Data
public class EmployeeResponseDTO {

    /**
     * Unique identifier of the employee.
     */
    private long id;

    /**
     * Name of the employee.
     */
    private String name;

    /**
     * Email of the employee.
     */
    private String email;

    /**
     * Date of birth of the employee.
     * <p>
     * Serialized in JSON using the format "yyyy-MM-dd".
     * </p>
     */
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateOfBirth;

    /**
     * Role of the employee (e.g., EMPLOYEE, MANAGER).
     */
    private String role;

    /**
     * Number of casual leave days available for the employee.
     */
    private int casualLeave;

    /**
     * Number of sick leave days available for the employee.
     */
    private int sickLeave;

    /**
     * Number of birthday leave days available for the employee.
     */
    private int birthdayLeave;

}
