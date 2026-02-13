package com.example.EmployeeLeaveManagement.controller;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import com.example.EmployeeLeaveManagement.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller to manage leave requests for employees.
 * <p>Provides REST endpoints for employees to:
 * <ul>
 *     <li>Apply for a leave</li>
 *     <li>View pending leaves</li>
 *     <li>View leave history</li>
 * </ul>
 * <p>This controller interacts with {@link LeaveRequestService} for all business logic.</p>
 */
@RestController
@RequestMapping("/api/leaves")
@Data
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * Constructor for LeaveRequestController.
     * @param leaveRequestService Service layer for leave request operations
     */
    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    /**
     * Apply for a new leave.
     * <p>Validates the leave request DTO and forwards it to the service layer.
     * Returns the created leave request with status Pending.</p>
     * @param dto LeaveRequestDTO containing employee ID, leave type, start date, end date, and optional leave note
     * @return ResponseEntity containing LeaveRequestDTO with generated leave ID, number of days, and status,
     * along with HTTP status CREATED (201)
     */
    @PostMapping("/apply")
    public ResponseEntity<LeaveRequestDTO> applyLeave(@Valid @RequestBody LeaveRequestDTO dto){
        LeaveRequestDTO response=leaveRequestService.applyLeave(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve all pending leave requests.
     * <p>This endpoint is typically used by managers to view leaves that require approval.</p>
     * @return ResponseEntity containing a list of LeaveRequestDTO with status Pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequestDTO>> getPendingLeaves(){
        List<LeaveRequestDTO> pending=leaveRequestService.getPendingLeaves();
        return ResponseEntity.ok(pending);
    }

    /**
     * Retrieve the leave history of a specific employee.
     * <p>Returns all leave requests (approved, rejected, and pending) for the given employee ID.</p>
     * @param employeeId ID of the employee whose leave history is being retrieved
     * @return ResponseEntity containing a list of LeaveRequestDTO for the employee
     */
    @GetMapping("/history/{employeeId}")
    public ResponseEntity<List<LeaveRequestDTO>> getLeaveHistory(@PathVariable Long employeeId){
        List<LeaveRequestDTO> history=leaveRequestService.getLeaveHistory(employeeId);
        return ResponseEntity.ok(history);
    }
}












