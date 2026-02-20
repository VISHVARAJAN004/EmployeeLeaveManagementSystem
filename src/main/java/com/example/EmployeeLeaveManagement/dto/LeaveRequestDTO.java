package com.example.EmployeeLeaveManagement.dto;

import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a leave request.
 * <p>
 * This DTO is used for submitting leave requests as well as returning
 * leave request details to clients. Certain fields are read-only and
 * automatically managed by the system, such as leaveId, numberOfDays, and status.
 * </p>
 */
@Data
public class LeaveRequestDTO {

    /**
     * Unique identifier of the leave request.
     * <p>Read-only: Automatically assigned by the system.</p>
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long leaveId;

    /**
     * ID of the employee requesting leave.
     * <p>Cannot be null.</p>
     */
    @NotNull(message="Employee ID must not be null")
    private Long employeeId;

    /**
     * Name of the leave type (e.g., Casual, Sick, Birthday).
     * <p>Cannot be null.</p>
     */
    @NotNull(message="Leave type name must not be null")
    private String leaveTypeName;

    /**
     * Start date of the leave.
     * <p>Cannot be null.</p>
     */
    @NotNull(message="Start date must not be null")
    private LocalDate startDate;

    /**
     * End date of the leave.
     * <p>Cannot be null.</p>
     */
    @NotNull(message="End date must not be null")
    private LocalDate endDate;

    /**
     * Total number of leave days.
     * <p>Read-only: Automatically calculated by the system.</p>
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int numberOfDays;

    /**
     * Note or reason provided by the employee for the leave.
     * <p>Cannot be null.</p>
     */
    @NotNull(message ="Leave note must not be null")
    private String leaveNote;

    /**
     * Current status of the leave request.
     * <p>Read-only: Managed by the system (e.g., PENDING, APPROVED, REJECTED).</p>
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LeaveStatus status;
}
