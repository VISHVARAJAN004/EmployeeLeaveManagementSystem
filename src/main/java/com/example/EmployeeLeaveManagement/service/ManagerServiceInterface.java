package com.example.EmployeeLeaveManagement.service;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import java.util.List;

/**
 * Service interface for manager-specific leave operations.
 * <p>
 * Allows managers to approve or reject leave requests and
 * fetch pending leave requests.
 * </p>
 */
public interface ManagerServiceInterface {

    /**
     * Approves a pending leave request.
     *
     * @param leaveRequestId ID of the leave request to approve
     * @return LeaveRequestDTO representing the approved leave
     */
    LeaveRequestDTO approveLeave(Long leaveRequestId);

    /**
     * Rejects a pending leave request.
     *
     * @param leaveRequestId ID of the leave request to reject
     * @return LeaveRequestDTO representing the rejected leave
     */
    LeaveRequestDTO rejectLeave(Long leaveRequestId);

    /**
     * Retrieves a list of pending leave requests.
     *
     * @param page Page number (0-based)
     * @param size Number of records per page
     * @return List of LeaveRequestDTO representing pending leaves
     */
    List<LeaveRequestDTO> getPendingLeaves(int page,int size);

    void deleteEmployee(Long employeeId);
}
