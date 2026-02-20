package com.example.EmployeeLeaveManagement.service.serviceImpl;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import com.example.EmployeeLeaveManagement.entity.LeaveBalance;
import com.example.EmployeeLeaveManagement.entity.LeaveRequest;
import com.example.EmployeeLeaveManagement.entity.LeaveType;
import com.example.EmployeeLeaveManagement.enums.EmployeeStatus;
import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import com.example.EmployeeLeaveManagement.entity.Employee;
import com.example.EmployeeLeaveManagement.exception.CustomException;
import com.example.EmployeeLeaveManagement.mapper.LeaveRequestMapper;
import com.example.EmployeeLeaveManagement.repository.EmployeeRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveBalanceRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveRequestRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveTypeRepository;
import com.example.EmployeeLeaveManagement.service.LeaveRequestService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service implementation for managing leave requests.
 * <p>
 * Handles applying for leave, approving/rejecting leave by managers,
 * checking leave rules, overlapping leaves, and retrieving leave history.
 * </p>
 */
@Service
@Slf4j
public class LeaveRequestServiceImpl implements LeaveRequestService {
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestMapper leaveRequestMapper;

    /**
     * Constructor for LeaveRequestServiceImpl.
     */
    public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository,
                                   EmployeeRepository employeeRepository,
                                   LeaveBalanceRepository leaveBalanceRepository,
                                   LeaveTypeRepository leaveTypeRepository,
                                   LeaveRequestMapper leaveRequestMapper) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveRequestMapper = leaveRequestMapper;
    }

    /**
     * Applies a leave request for an employee.
     * Validates leave rules, overlapping dates, and birthday leave constraints.
     *
     * @param dto LeaveRequestDTO with leave details
     * @return LeaveRequestDTO after saving
     */
    @Override
    @Transactional
    public LeaveRequestDTO applyLeave(LeaveRequestDTO dto){
        log.debug("Applying leave for employee id {}",dto.getEmployeeId());
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
        LeaveType leaveType=leaveTypeRepository.findByName(dto.getLeaveTypeName())
                .orElseThrow(()->new CustomException("Leave type not found "+dto.getLeaveTypeName()));

        if(leaveType.getName().equalsIgnoreCase("Birthday")){
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

            boolean alreadyTaken=leaveRequestRepository.findByEmployee(emp).stream()
                    .anyMatch(lr->lr.getLeaveType().getName().equalsIgnoreCase("Birthday")
                    && lr.getStartDate().getYear()==LocalDate.now().getYear()
                    && lr.getStatus() == LeaveStatus.Approved);
            if(alreadyTaken){
                throw new CustomException("Birthday leave already taken this year");
            }
        }
        List<LeaveRequest> existingLeaves=leaveRequestRepository.findByEmployee(emp);
        boolean isOverlapping=existingLeaves.stream()
                .filter(lr->lr.getStatus()!=LeaveStatus.Rejected)
                .anyMatch(lr->!(dto.getEndDate().isBefore(lr.getStartDate()) ||
                        dto.getStartDate().isAfter(lr.getEndDate())));

        if(isOverlapping){
            throw new CustomException("Leave dates overlap with existing leave");
        }
            LeaveRequest lr = new LeaveRequest();
            lr.setEmployee(emp);
            lr.setLeaveType(leaveType);
            lr.setLeaveTypeName(leaveType.getName());
            lr.setStartDate(dto.getStartDate());
            lr.setEndDate(dto.getEndDate());
            lr.setTotalDays((int) daysBetween);
            lr.setLeaveNote(dto.getLeaveNote());
            lr.setStatus(LeaveStatus.Pending);
            LeaveRequest saved = leaveRequestRepository.save(lr);
            log.info("Leave applied successfully with id {}", saved.getId());
            return mapToDTO(saved);
    }

    /**
     * Approves a pending leave request by a manager.
     * Updates leave balance accordingly.
     *
     * @param leaveRequestId ID of the leave request
     * @return LeaveRequestDTO after approval
     */
    public LeaveRequestDTO approveLeaveByManager(Long leaveRequestId) {
        log.debug("Manager approved leave id {}",leaveRequestId);
        LeaveRequest lr=leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(()->new CustomException("Leave request not found"));
        if(lr.getStatus() !=LeaveStatus.Pending){
            log.error("Leave request ID {} already processed",leaveRequestId);
            throw new CustomException("Leave request already processed");
        }
        LeaveBalance balance=leaveBalanceRepository.findByEmployeeAndLeaveType(lr.getEmployee(),lr.getLeaveType())
                .orElseThrow(()->{
                    log.error("Leave balance not found for leave request ID: {}",leaveRequestId);
                    return new CustomException("Leave balance not found");
                });
        if(balance.getRemainingDays()<lr.getTotalDays()){
            lr.setStatus(LeaveStatus.Rejected);
            leaveRequestRepository.save(lr);
            log.error("Insufficient leave balance for employee ID {}. Leave rejected.",lr.getEmployee().getId());
            throw new CustomException("Insufficient leave balance. Leave rejected");
        }
        balance.setRemainingDays(balance.getRemainingDays()- lr.getTotalDays());
        leaveBalanceRepository.save(balance);
        log.debug("Updated leave balance for employee ID {}. Remaining days: {}",lr.getEmployee().getId(),balance.getRemainingDays());

        lr.setStatus(LeaveStatus.Approved);
        LeaveRequest updated =leaveRequestRepository.save(lr);
        log.info("Leave request ID {} approved succcessfully",leaveRequestId);
        return leaveRequestMapper.toDto(updated);
    }

    /**
     * Rejects a pending leave request by a manager.
     *
     * @param leaveRequestId ID of the leave request
     * @return LeaveRequestDTO after rejection
     */
    public LeaveRequestDTO rejectLeaveByManager(Long leaveRequestId) {
        log.debug("Rejected leave request with ID: {}",leaveRequestId);
        LeaveRequest lr=leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(()->{
                    log.error("Leave request not found with ID: {}",leaveRequestId);
                    return new CustomException("Leave request already processed");
                });
        if(lr.getStatus()!= LeaveStatus.Pending){
            log.error("Leave request ID {} rejected successfully",leaveRequestId);
            throw new CustomException("Leave request already processed");
        }
        lr.setStatus(LeaveStatus.Rejected);
        LeaveRequest updated = leaveRequestRepository.save(lr);
        log.info("Leave request ID {} rejected successfully",leaveRequestId);
        return leaveRequestMapper.toDto(updated);
    }

    /**
     * Retrieves pending leave requests with pagination.
     *
     * @param page Page number
     * @param size Page size
     * @return Page of LeaveRequestDTO for pending leaves
     */
    @Override
    public Page<LeaveRequestDTO> getPendingLeaves(int page,int size) {
        log.debug("Fetching pending leaves- Page: {},Size: {}",page,size);
        Pageable pageable=PageRequest.of(page,size);

        return leaveRequestRepository.findByStatus(LeaveStatus.Pending,pageable)
                .map(leaveRequestMapper::toDto);
    }

    /**
     * Retrieves leave history of a specific employee with pagination.
     *
     * @param employeeId Employee ID
     * @param page Page number
     * @param size Page size
     * @return Page of LeaveRequestDTO for the employee
     */
    @Override
    public Page<LeaveRequestDTO> getLeaveHistory(Long employeeId,int page,int size){
        Employee emp=employeeRepository.findById(employeeId)
                .orElseThrow(()->new CustomException("Employee not found"));
        Pageable pageable=PageRequest.of(page,size);
        return leaveRequestRepository.findByEmployee(emp,pageable)
                .map(leaveRequestMapper::toDto);
    }

    /**
     * Maps LeaveRequest entity to LeaveRequestDTO.
     */
    private LeaveRequestDTO mapToDTO(LeaveRequest leaveRequest){
        return leaveRequestMapper.toDto(leaveRequest);
    }

}
