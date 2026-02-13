package com.example.EmployeeLeaveManagement.entity;

import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import com.example.EmployeeLeaveManagement.enums.LeaveTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

/**
 * Entity representing a leave request submitted by an employee.
 *
 * <p>This entity stores the leave type, duration, reason, and current status.
 * Each leave request is linked to an employee.</p>
 */
@Entity
@Table(name="leave_requests")
@Data
public class LeaveRequest {

    /** Unique identifier of the leave request */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** Employee who submitted the leave request */
    @ManyToOne
    @JoinColumn(name="employee_id",nullable=false)
    private Employee employee;

    /** Type of leave (Casual, Sick, Birthday) */
    @Enumerated(EnumType.STRING)
    private LeaveTypeEnum leaveType;

    /** Start date of the leave */
    @NotNull(message = "Start date must not be null")
    private LocalDate startDate;

    /** End date of the leave */
    @NotNull(message="End date must not be null")
    private LocalDate endDate;

    /** Total number of leave days requested */
    private int totalDays;

    /** Reason or note for the leave, maximum length 500 characters */
    @Column(length = 500)
    private String leaveNote;

    /** Current status of the leave request (Pending, Approved, Rejected) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status;
}
