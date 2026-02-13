package com.example.EmployeeLeaveManagement.service;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;

import java.util.List;

/**
 * Service interface to handle leave requests.
 * Provides methods to apply, approve, reject leaves, and fetch history.
 */

public interface LeaveRequestService {

    /**
     * Apply a leave for an employee.
     * @param dto LeaveRequestDTO containing leave details
     * @return LeaveRequestDTO with leave ID and status
     */
    LeaveRequestDTO applyLeave(LeaveRequestDTO dto);

    /**
     * Approve a pending leave request by manager.
     * @param leaveRequestId ID of leave request
     * @return LeaveRequestDTO with updated status
     */
    LeaveRequestDTO approveLeaveByManager(Long leaveRequestId);

    /**
     * Reject a pending leave request by manager.
     * @param leaveRequestId ID of leave request
     * @return LeaveRequestDTO with updated status
     */
    LeaveRequestDTO rejectLeaveByManager(Long leaveRequestId);

    /**
     * Get all leave history of an employee.
     * @param employeeId Employee ID
     * @return List of LeaveRequestDTO
     */
    List<LeaveRequestDTO> getLeaveHistory(Long employeeId);

    /**
     * Get all pending leave requests.
     * @return List of LeaveRequestDTO
     */
    List<LeaveRequestDTO> getPendingLeaves();
}
