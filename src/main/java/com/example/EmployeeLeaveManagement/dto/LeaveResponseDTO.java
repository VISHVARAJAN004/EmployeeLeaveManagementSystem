package com.example.EmployeeLeaveManagement.dto;

import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import com.example.EmployeeLeaveManagement.enums.LeaveTypeEnum;
import lombok.Data;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing the response details of a leave request.
 * <p>
 * This DTO is used to send leave request information to clients.
 * All fields are read-only and reflect the current state of the leave request.
 * </p>
 */
@Data
public class LeaveResponseDTO {

    /**
     * Unique identifier of the leave request.
     */
    private Long leaveId;

    /**
     * ID of the employee who submitted the leave request.
     */
    private Long employeeId;

    /**
     * Type of leave requested (e.g., Casual, Sick, Birthday).
     */
    private LeaveTypeEnum leaveType;

    /**
     * Start date of the leave.
     */
    private LocalDate startDate;

    /**
     * End date of the leave.
     */
    private LocalDate endDate;

    /**
     * Total number of leave days.
     */
    private int numberOfDays;

    /**
     * Current status of the leave request (e.g., PENDING, APPROVED, REJECTED).
     */
    private LeaveStatus status;
}
