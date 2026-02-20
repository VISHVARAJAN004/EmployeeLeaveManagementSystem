package com.example.EmployeeLeaveManagement.entity;

import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

/**
 * Entity representing a leave request submitted by an employee.
 * <p>
 * Each leave request is associated with an employee and a leave type.
 * It stores the leave period, total days requested, reason, and the
 * current status (Pending, Approved, Rejected).
 * </p>
 */
@Entity
@Table(name="leave_requests")
@Data
public class LeaveRequest {

    /** Unique identifier for the leave request */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** Employee who submitted the leave request */
    @ManyToOne
    @JoinColumn(name="employee_id",nullable=false)
    private Employee employee;

    /** Leave type associated with the request */
    @ManyToOne
    @JoinColumn(name="leave_type_id",nullable=false)
    private LeaveType leaveType;

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

   /** Name of the leave type (stored for quick reference) */
    @Column(name="leave_type",nullable = false)
    private String leaveTypeName;

    /**
     * Constructor to create a leave request.
     *
     * @param employee Employee submitting the leave
     * @param leaveType Type of leave requested
     * @param startDate Start date of leave
     * @param endDate End date of leave
     * @param totalDays Total leave days
     * @param leaveNote Reason or note for the leave
     * @param status Status of the leave request
     */
    public LeaveRequest(Employee employee, LeaveType leaveType, LocalDate startDate, LocalDate endDate, int totalDays, String leaveNote, LeaveStatus status) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.leaveNote = leaveNote;
        this.status = status;
        this.leaveTypeName=leaveType.getName();
    }

    public LeaveRequest(){}
}
