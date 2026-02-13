package com.example.EmployeeLeaveManagement.dto;

import com.example.EmployeeLeaveManagement.entity.Employee;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;


/**
 * Data Transfer Object (DTO) representing an Employee response.
 * <p>This DTO is used to send employee details to the client, including
 * their leave balances for Casual, Sick, and Birthday leaves.</p>
 */
@Data
public class EmployeeResponseDTO {

    /** Unique identifier of the employee */
    private long id;

    /** Name of the employee */
    private String name;

    /** Email of the employee */
    private String email;

    /** Date of birth of the employee, formatted as yyyy-MM-dd */
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateOfBirth;

    /** Role of the employee (e.g., Employee, Manager) */
    private String role;

    /** Remaining casual leave days */
    private int casualLeave;

    /** Remaining sick leave days */
    private int sickLeave;

    /** Remaining birthday leave days */
    private int birtdayLeave;

    /**
     * Constructor to create EmployeeResponseDTO from an Employee entity and leave balances.
     *
     * @param emp Employee entity
     * @param casualLeave Remaining casual leave days
     * @param sickLeave Remaining sick leave days
     * @param birtdayLeave Remaining birthday leave days
     */
    public EmployeeResponseDTO(Employee emp,int casualLeave,int sickLeave,int birtdayLeave){
        this.id=emp.getId();
        this.name=emp.getName();
        this.email=emp.getEmail();
        this.dateOfBirth=emp.getDateOfBirth();
        this.role=emp.getRole().name();
        this.casualLeave=casualLeave;
        this.sickLeave=sickLeave;
        this.birtdayLeave=birtdayLeave;
    }
}
