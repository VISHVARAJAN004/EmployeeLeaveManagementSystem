package com.example.EmployeeLeaveManagement.service.serviceImpl;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import com.example.EmployeeLeaveManagement.entity.LeaveBalance;
import com.example.EmployeeLeaveManagement.entity.LeaveRequest;
import com.example.EmployeeLeaveManagement.enums.EmployeeStatus;
import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import com.example.EmployeeLeaveManagement.entity.Employee;
import com.example.EmployeeLeaveManagement.exception.CustomException;
import com.example.EmployeeLeaveManagement.repository.EmployeeRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveBalanceRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveRequestRepository;
import com.example.EmployeeLeaveManagement.service.LeaveRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link LeaveRequestService} that manages leave applications,
 * approvals, rejections, and retrieval of leave history.
 * <p>Handles business rules such as:</p>
 * <ul>
 *     <li>Preventing inactive employees from applying for leave</li>
 *     <li>Validating leave dates and overlap with approved leaves</li>
 *     <li>Restricting birthday leave to one day within 7 days before/after birthday</li>
 *     <li>Checking sufficient leave balance before approving leaves</li>
 * </ul>
 */
@Service
@Slf4j
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository, EmployeeRepository employeeRepository, LeaveBalanceRepository leaveBalanceRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    /**
     * Applies for a leave on behalf of an employee.
     * @param dto {@link LeaveRequestDTO} containing leave type, start/end dates, employee ID, and note
     * @return {@link LeaveRequestDTO} representing the created leave request with status set to Pending
     * @throws CustomException if employee is inactive, leave overlaps, or violates business rules
     */
    @Override
    public LeaveRequestDTO applyLeave(LeaveRequestDTO dto){
        log.info("Applying leave for employee id {}",dto.getEmployeeId());
        Employee emp=employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(()-> {
                    log.error("Employee not found with id {}", dto.getEmployeeId());
                    return new CustomException("Employee not found");
                });
        if(emp.getStatus() != EmployeeStatus.ACTIVE){
            throw new CustomException("Inactive employee cannot apply leave");
        }
        if(dto.getStartDate().isBefore(LocalDate.now())){
            throw new CustomException("Cannot apply leave for past dates");
        }
        if(dto.getStartDate().isAfter(dto.getEndDate())){
            throw new CustomException("Start date cannot be after end date");
        }

        long daysBetween=ChronoUnit.DAYS.between(dto.getStartDate(),dto.getEndDate())+1;
        if(dto.getLeaveType().name().equalsIgnoreCase("Birthday")){
            LocalDate birthdayThisYear=emp.getDateOfBirth().withYear(LocalDate.now().getYear());
            LocalDate allowedStart=birthdayThisYear.minusDays(7);
            LocalDate allowedEnd=birthdayThisYear.plusDays(7);
            if(dto.getStartDate().isBefore(allowedStart) ||
            dto.getStartDate().isAfter(allowedEnd)){
                throw new CustomException("Birthday leave must be applied within 7 days before or after birthday");
            }
            if(daysBetween>1){
                throw new CustomException("Birthday leave can only be for 1 day");
            }
        }

        List<LeaveRequest> approvedLeaves=leaveRequestRepository.findByEmployeeAndStatus(emp,LeaveStatus.Approved);
        for(LeaveRequest approved :approvedLeaves){
            boolean isOverlapping=!(dto.getEndDate().isBefore(approved.getStartDate()) ||
                    dto.getStartDate().isAfter(approved.getEndDate()));

            if(isOverlapping){
                throw new CustomException("Leave dates overlap with already approved leave.");
            }
            List<LeaveRequest> birthdayLeaves=leaveRequestRepository.findByEmployee(emp);
            boolean alreadyTaken =birthdayLeaves.stream()
                    .anyMatch(lr->
                            lr.getLeaveType().name().equalsIgnoreCase("Birthday")
                    &&lr.getStartDate().getYear()==LocalDate.now().getYear()
                    && lr.getStatus()==LeaveStatus.Approved);
            if(alreadyTaken){
                throw new CustomException("Birthday leave already taken this year");
            }
        }
        LeaveRequest lr =new LeaveRequest();
        lr.setEmployee(emp);
        lr.setLeaveType(dto.getLeaveType());
        lr.setStartDate(dto.getStartDate());
        lr.setEndDate(dto.getEndDate());
        lr.setTotalDays((int)daysBetween);
        lr.setLeaveNote(dto.getLeaveNote());
        lr.setStatus(LeaveStatus.Pending);
        LeaveRequest saved=leaveRequestRepository.save(lr);
        log.info("Leave applied successfully with id {}",saved.getId());
        return mapToDTO(saved);
    }

    /**
     * Approves a leave request by manager.
     * @param leaveRequestId ID of the leave request to approve
     * @return {@link LeaveRequestDTO} representing the approved leave
     * @throws CustomException if leave request not found, already processed, or insufficient balance
     */
    @Override
    public LeaveRequestDTO approveLeaveByManager(Long leaveRequestId) {
        log.info("Manager approved leave id {}",leaveRequestId);
        LeaveRequest lr=leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(()->new CustomException("Leave request not found"));
        if(lr.getStatus() !=LeaveStatus.Pending){
            throw new CustomException("Leave request already processed");
        }
        LeaveBalance balance=leaveBalanceRepository.findByEmployeeAndLeaveType(lr.getEmployee(),lr.getLeaveType())
                .orElseThrow(()->new CustomException("Leave balance not found"));
        if(balance.getRemainingDays()<lr.getTotalDays()){
            lr.setStatus(LeaveStatus.Rejected);
            leaveRequestRepository.save(lr);
            throw new CustomException("Insufficient leave balance. Leave rejected");
        }
        balance.setRemainingDays(balance.getRemainingDays()- lr.getTotalDays());
        leaveBalanceRepository.save(balance);
        lr.setStatus(LeaveStatus.Approved);
        LeaveRequest updated =leaveRequestRepository.save(lr);
        log.info("Leave id {} approved successfully",leaveRequestId);
        return mapToDTO(updated);
    }

    /**
     * Rejects a leave request by manager.
     * @param leaveRequestId ID of the leave request to reject
     * @return {@link LeaveRequestDTO} representing the rejected leave
     * @throws CustomException if leave request not found or already processed
     */
    @Override
    public LeaveRequestDTO rejectLeaveByManager(Long leaveRequestId) {
        LeaveRequest lr=leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(()->new CustomException("Leave request not found"));
        if(lr.getStatus()!= LeaveStatus.Pending){
            throw new CustomException("Leave request already processed");
        }
        lr.setStatus(LeaveStatus.Rejected);
        LeaveRequest updated=leaveRequestRepository.save(lr);
        return mapToDTO(updated);
    }

    /**
     * Retrieves all pending leave requests.
     * @return list of {@link LeaveRequestDTO} representing pending leaves
     */
    @Override
    public List<LeaveRequestDTO> getPendingLeaves() {
        return leaveRequestRepository.findByStatus(LeaveStatus.Pending)
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves leave history for a specific employee.
     * @param employeeId ID of the employee
     * @return list of {@link LeaveRequestDTO} representing all leaves of the employee
     * @throws CustomException if employee not found
     */
    @Override
    public List<LeaveRequestDTO> getLeaveHistory(Long employeeId){
        Employee emp=employeeRepository.findById(employeeId)
                .orElseThrow(()->new CustomException("Employee not found"));
        return leaveRequestRepository.findByEmployee(emp)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Maps {@link LeaveRequest} entity to {@link LeaveRequestDTO}.
     * @param leaveRequest the leave request entity
     * @return corresponding {@link LeaveRequestDTO}
     */
    private LeaveRequestDTO mapToDTO(LeaveRequest leaveRequest){
        LeaveRequestDTO dto=new LeaveRequestDTO();
        dto.setLeaveId(leaveRequest.getId());
        dto.setEmployeeId(leaveRequest.getEmployee().getId());
        dto.setLeaveType(leaveRequest.getLeaveType());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setNumberOfDays(leaveRequest.getTotalDays());
        dto.setLeaveNote(leaveRequest.getLeaveNote());
        dto.setStatus(leaveRequest.getStatus());
        return dto;
    }
}
