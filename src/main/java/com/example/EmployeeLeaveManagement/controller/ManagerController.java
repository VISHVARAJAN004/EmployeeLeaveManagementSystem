package com.example.EmployeeLeaveManagement.controller;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import com.example.EmployeeLeaveManagement.service.LeaveRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller for manager-specific leave operations.
 * <p>This controller provides endpoints for managers to:</p>
 * <ul>
 *     <li>Approve leave requests</li>
 *     <li>Reject leave requests</li>
 *     <li>View all pending leave requests</li>
 * </ul>
 *
 * <p>All business logic is delegated to {@link LeaveRequestService}.</p>
 */
@RestController
@RequestMapping("/api/manager")
public class ManagerController{

    private final LeaveRequestService leaveRequestService;

    /**
     * Constructor for ManagerController.
     * @param leaveRequestService Service layer handling leave request operations
     */
    public ManagerController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    /**
     * Approve a pending leave request.
     * <p>Updates the status of the leave request to Approved and deducts the leave days
     * from the employee's leave balance if sufficient.</p>
     * @param leaveRequestId ID of the leave request to approve
     * @return ResponseEntity containing LeaveRequestDTO with updated status
     * @throws com.example.EmployeeLeaveManagement.exception.CustomException if the leave request
     *         does not exist, is already processed, or has insufficient leave balance
     */
    @PatchMapping("/approve/{leaveRequestId}")
    public ResponseEntity<LeaveRequestDTO> approveLeave(@PathVariable Long leaveRequestId){
        LeaveRequestDTO dto=leaveRequestService.approveLeaveByManager(leaveRequestId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Reject a pending leave request.
     * <p>Updates the status of the leave request to Rejected. No changes are made
     * to the employee's leave balance.</p>
     * @param leaveRequestId ID of the leave request to reject
     * @return ResponseEntity containing LeaveRequestDTO with updated status
     * @throws com.example.EmployeeLeaveManagement.exception.CustomException if the leave request
     *         does not exist or is already processed
     */
    @PatchMapping("/reject/{leaveRequestId}")
    public ResponseEntity<LeaveRequestDTO> rejectLeave(@PathVariable Long leaveRequestId){
        LeaveRequestDTO dto=leaveRequestService.rejectLeaveByManager(leaveRequestId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Retrieve all pending leave requests.
     * <p>This endpoint allows managers to view all leave requests with status Pending.</p>
     * @return ResponseEntity containing a list of LeaveRequestDTO with Pending status
     */
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequestDTO>> getPendingLeaves(){
        List<LeaveRequestDTO> pending=leaveRequestService.getPendingLeaves();
        return ResponseEntity.ok(pending);
    }
}
