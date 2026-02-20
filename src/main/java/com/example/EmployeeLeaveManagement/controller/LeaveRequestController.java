package com.example.EmployeeLeaveManagement.controller;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import com.example.EmployeeLeaveManagement.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Employee Leave Requests.
 * <p>
 * Provides endpoints to apply for leave, fetch pending leave requests,
 * and view leave history for a specific employee.
 * </p>
 */
@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    /**
     * Apply for a new leave.
     *
     * @param dto the leave request data transfer object
     * @return the created leave request with HTTP status 201
     */
    @PostMapping("/apply")
    public ResponseEntity<LeaveRequestDTO> applyLeave(@Valid @RequestBody LeaveRequestDTO dto){
        LeaveRequestDTO response=leaveRequestService.applyLeave(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve all pending leave requests with pagination.
     *
     * @param page the page number (default 0)
     * @param size the page size (default 10)
     * @return paginated list of pending leave requests
     */
    @GetMapping("/pending")
    public Page<LeaveRequestDTO> getPendingLeaves(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size
    ){
        return leaveRequestService.getPendingLeaves(page,size);
    }

    /**
     * Retrieve leave history for a specific employee with pagination.
     *
     * @param employeeId the employee ID
     * @param page the page number (default 0)
     * @param size the page size (default 10)
     * @return paginated list of leave requests for the employee
     */
    @GetMapping("/history/{employeeId}")
    public Page<LeaveRequestDTO> getLeaveHistory(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size
    ){
        return leaveRequestService.getLeaveHistory(employeeId,page,size);
    }
}












