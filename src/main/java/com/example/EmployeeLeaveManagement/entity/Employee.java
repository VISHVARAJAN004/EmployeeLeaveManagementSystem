package com.example.EmployeeLeaveManagement.entity;

import com.example.EmployeeLeaveManagement.enums.EmployeeStatus;
import com.example.EmployeeLeaveManagement.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * Entity representing an Employee in the system.
 * <p>
 * This entity stores personal information, role, and status of an employee.
 * It is mapped to the database table automatically by JPA/Hibernate.
 * </p>
 */
@Entity
@Data
public class Employee {

    /**
     * Unique identifier for the employee.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Name of the employee.
     */
    private String name;

    /**
     * Email of the employee.
     * Must be unique across all employees.
     */
    @Column(unique=true)
    private String email;

    /**
     * Date of birth of the employee.
     */
    private LocalDate dateOfBirth;

    /**
     * Role of the employee in the system (e.g., EMPLOYEE, MANAGER).
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Employment status of the employee (e.g., ACTIVE, INACTIVE).
     */
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
}
