package com.example.EmployeeLeaveManagement.service;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import org.springframework.data.domain.Page;

/**
 * Service interface for managing employee leave requests.
 * <p>
 * Provides methods for applying leaves, fetching leave history,
 * and retrieving pending leave requests.
 * </p>
 */
public interface LeaveRequestService {

    /**
     * Submits a new leave request for an employee.
     *
     * @param dto Data Transfer Object containing leave request details
     * @return LeaveRequestDTO representing the created leave request
     */
    LeaveRequestDTO applyLeave(LeaveRequestDTO dto);

    /**
     * Retrieves paginated leave history for a specific employee.
     *
     * @param employeeId ID of the employee
     * @param page Page number (0-based)
     * @param size Number of records per page
     * @return Page of LeaveRequestDTO containing leave history
     */
    Page<LeaveRequestDTO> getLeaveHistory(Long employeeId,int page,int size);

    /**
     * Retrieves a paginated list of all pending leave requests.
     *
     * @param page Page number (0-based)
     * @param size Number of records per page
     * @return Page of LeaveRequestDTO representing pending leaves
     */
    Page<LeaveRequestDTO> getPendingLeaves(int page,int size);
}
