package com.example.EmployeeLeaveManagement.controller;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import com.example.EmployeeLeaveManagement.service.ManagerServiceInterface;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for Manager operations on Employee Leave Requests.
 * <p>
 * Provides endpoints for approving or rejecting leave requests
 * and retrieving all pending leave requests for managerial review.
 * </p>
 */
@RestController
@RequestMapping("/api/manager")
@SecurityRequirement(name="basicAuth")
public class ManagerController{

    private final ManagerServiceInterface managerService;

    public ManagerController(ManagerServiceInterface managerService){
        this.managerService=managerService;
    }

    /**
     * Approve a specific leave request.
     *
     * @param leaveRequestId the ID of the leave request to approve
     * @return the updated leave request after approval
     */
    @PatchMapping("/approve/{leaveRequestId}")
    public ResponseEntity<LeaveRequestDTO> approveLeave(@PathVariable Long leaveRequestId){
        LeaveRequestDTO response=managerService.approveLeave(leaveRequestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reject a specific leave request.
     *
     * @param leaveRequestId the ID of the leave request to reject
     * @return the updated leave request after rejection
     */
    @PatchMapping("/reject/{leaveRequestId}")
    public ResponseEntity<LeaveRequestDTO> rejectLeave(@PathVariable Long leaveRequestId){
        LeaveRequestDTO response=managerService.rejectLeave(leaveRequestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all pending leave requests with pagination.
     *
     * @param page the page number (default 0)
     * @param size the page size (default 10)
     * @return a list of pending leave requests
     */
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequestDTO>> getPendingLeaves(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size
    ){
        List<LeaveRequestDTO> pending =managerService.getPendingLeaves(page,size);
        return ResponseEntity.ok(pending);
    }

    @DeleteMapping("/delete/employee/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long employeeId){
        managerService.deleteEmployee(employeeId);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}
