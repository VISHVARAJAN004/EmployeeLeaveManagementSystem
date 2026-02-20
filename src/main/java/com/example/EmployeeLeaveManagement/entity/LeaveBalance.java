package com.example.EmployeeLeaveManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Entity representing the leave balance of an employee for a specific leave type.
 * <p>
 * Tracks how many days of a particular leave type (e.g., Casual, Sick) an employee
 * has remaining. Each record is associated with one employee and one leave type.
 * </p>
 */
@Entity
@Table(name="Leave_balance")
@Data
public class LeaveBalance {

    /**
     * Unique identifier for the leave balance record.
     * Ignored during JSON serialization.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    /**
     * Employee associated with this leave balance.
     * Many leave balances can belong to the same employee.
     */
    @ManyToOne
    @JoinColumn(name="employee_id",nullable=false)
    private Employee employee;

    /**
     * Leave type associated with this balance (e.g., Casual, Sick, Birthday).
     */
    @ManyToOne
    @JoinColumn(name="leave_type_id",nullable = false)
    private LeaveType leaveType;

    /**
     * Number of remaining leave days for this leave type.
     * Cannot be null.
     */
    @NotNull(message="Remaining days must not be null")
    private Integer remainingDays;

    /**
     * Name of the leave type.
     * Stored for quick reference and denormalization purposes.
     */
    @Column(name="leave_type",nullable = false)
    private String leaveTypeName;

    /**
     * Constructor to create a leave balance record.
     *
     * @param employee Employee associated with the balance
     * @param leaveType Leave type associated with the balance
     * @param remainingDays Number of remaining leave days
     * @param leaveTypeName Name of the leave type
     */
    public LeaveBalance(Employee employee, LeaveType leaveType, Integer remainingDays, String leaveTypeName) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.remainingDays = remainingDays;
        this.leaveTypeName = leaveTypeName;
    }

    /**
     * Default constructor for JPA.
     */
    public LeaveBalance(){}
}
