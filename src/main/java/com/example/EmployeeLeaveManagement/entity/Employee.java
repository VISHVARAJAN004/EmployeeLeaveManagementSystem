package com.example.EmployeeLeaveManagement.entity;

import com.example.EmployeeLeaveManagement.enums.EmployeeStatus;
import com.example.EmployeeLeaveManagement.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;


/**
 * Entity representing an Employee in the HRMS system.
 * <p>This entity is mapped to the database and stores the basic
 * information of an employee, including their role and status.</p>
 */
@Entity
@Data
public class Employee {

    /** Unique identifier of the employee, auto-generated */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** Full name of the employee */
    private String name;


    /** Email address of the employee, must be unique */
    @Column(unique=true)
    private String email;

    /** Date of birth of the employee */
    private LocalDate dateOfBirth;

    /** Role of the employee (Employee or Manager) */
    @Enumerated(EnumType.STRING)
    private Role role;

    /** Status of the employee (Active or Inactive) */
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
}
