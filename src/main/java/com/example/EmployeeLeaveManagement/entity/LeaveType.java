package com.example.EmployeeLeaveManagement.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a type of leave in the system.
 * <p>
 * Each leave type has a unique name and a default number of leave days
 * allocated to employees.
 * </p>
 */
@Entity
@Table(name="leave_type")
@Data
public class LeaveType {

    /** Unique identifier for the leave type */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Name of the leave type (e.g., Casual, Sick, Birthday) */
    @Column(unique=true,nullable=false)
    private String name;

    /** Default number of days allocated for this leave type */
    @Column(nullable = false)
    private int defaultDays;
}
