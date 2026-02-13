package com.example.EmployeeLeaveManagement.dto;

import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import com.example.EmployeeLeaveManagement.enums.LeaveTypeEnum;
import lombok.Data;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a leave request response.
 * <p>This DTO is used to return leave request details to the client,
 * typically after applying, approving, rejecting, or fetching leave history.</p>
 */
@Data
public class LeaveResponseDTO {

    /** Unique identifier of the leave request */
    private Long leaveId;

    /** ID of the employee who submitted the leave request */
    private Long employeeId;

    /** Type of leave requested (Casual, Sick, Birthday) */
    private LeaveTypeEnum leaveType;

    /** Start date of the leave */
    private LocalDate startDate;

    /** End date of the leave */
    private LocalDate endDate;

    /** Total number of leave days */
    private int numberOfDays;

    /** Current status of the leave request (Pending, Approved, Rejected) */
    private LeaveStatus status;
}
