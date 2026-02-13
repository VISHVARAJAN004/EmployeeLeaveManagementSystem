package com.example.EmployeeLeaveManagement.entity;

import com.example.EmployeeLeaveManagement.enums.LeaveTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Entity representing the leave balance for an employee.
 * <p>This entity tracks the remaining number of leave days of each type
 * (Casual, Sick, Birthday) for a specific employee.</p>
 */
@Entity
@Table(name="Leave_balance")
@Data
public class LeaveBalance {

    /** Unique identifier of the leave balance record (hidden in JSON responses) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    /** Employee to whom this leave balance belongs */
    @ManyToOne
    @JoinColumn(name="employee_id",nullable=false)
    private Employee employee;

    /** Type of leave (Casual, Sick, Birthday) */
    @Enumerated(EnumType.STRING)
    @NotNull(message="Leave type must not be null")
    private LeaveTypeEnum leaveType;

    /** Remaining number of days for this leave type */
    @NotNull(message="Remaining days must not be null")
    private Integer remainingDays;
}
