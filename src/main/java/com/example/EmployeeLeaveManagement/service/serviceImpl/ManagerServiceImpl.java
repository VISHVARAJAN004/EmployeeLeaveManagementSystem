package com.example.EmployeeLeaveManagement.service.serviceImpl;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import com.example.EmployeeLeaveManagement.entity.Employee;
import com.example.EmployeeLeaveManagement.entity.LeaveBalance;
import com.example.EmployeeLeaveManagement.entity.LeaveRequest;
import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import com.example.EmployeeLeaveManagement.exception.CustomException;
import com.example.EmployeeLeaveManagement.mapper.LeaveRequestMapper;
import com.example.EmployeeLeaveManagement.repository.EmployeeRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveBalanceRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveRequestRepository;
import com.example.EmployeeLeaveManagement.service.ManagerServiceInterface;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service implementation for manager operations on leave requests.
 * <p>
 * Provides methods to approve, reject, and view pending leave requests.
 * Ensures leave balance is sufficient before approving a leave.
 * </p>
 */
@Service
@Slf4j
public class ManagerServiceImpl implements ManagerServiceInterface {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final EmployeeRepository employeeRepository;

    /**
     * Constructor for ManagerServiceImpl.
     */
    public ManagerServiceImpl(LeaveRequestRepository leaveRequestRepository, LeaveBalanceRepository leaveBalanceRepository, LeaveRequestMapper leaveRequestMapper,EmployeeRepository employeeRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveRequestMapper = leaveRequestMapper;
        this.employeeRepository=employeeRepository;
    }

    /**
     * Approves a pending leave request if leave balance is sufficient.
     *
     * @param leaveRequestId ID of the leave request
     * @return LeaveRequestDTO after approval
     * @throws CustomException if leave request is invalid or balance is insufficient
     */
    @Transactional
    public LeaveRequestDTO approveLeave(Long leaveRequestId) {
        log.debug("Approving leave request with ID: {}", leaveRequestId);
        LeaveRequest lr = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> {
                    log.error("Leave request not found with ID: {}", leaveRequestId);
                    return new CustomException("Leave request not found");
                });

        if (lr.getStatus() != LeaveStatus.Pending) {
            log.error("Leave request ID {} already processed", leaveRequestId);
            throw new CustomException("Leave already processed");
        }
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeAndLeaveType(lr.getEmployee(), lr.getLeaveType())
                .orElseThrow(() -> {
                    log.error("Leave balance not found for leave request ID: {}", leaveRequestId);
                    return new CustomException("Leave balance not found");
                });

        if (balance.getRemainingDays() < lr.getTotalDays()) {
            lr.setStatus(LeaveStatus.Rejected);
            leaveRequestRepository.save(lr);
            log.error("Insufficient leave balance for employee ID {}. Leave rejected.", lr.getEmployee().getId());
            throw new CustomException("Insufficient leave balance.Leave rejected");
        }
        balance.setRemainingDays(balance.getRemainingDays() - lr.getTotalDays());
        leaveBalanceRepository.save(balance);
        log.debug("Updated leave balance for employee ID {}. Remaining days: {}", lr.getEmployee().getId(), balance.getRemainingDays());

        lr.setStatus(LeaveStatus.Approved);
        LeaveRequest updated = leaveRequestRepository.save(lr);
        log.info("Leave request ID {} approved successfully", leaveRequestId);

        return leaveRequestMapper.toDto(updated);
    }

    /**
     * Rejects a pending leave request.
     *
     * @param leaveRequestId ID of the leave request
     * @return LeaveRequestDTO after rejection
     * @throws CustomException if leave request is invalid or already processed
     */
    @Transactional
    public LeaveRequestDTO rejectLeave(Long leaveRequestId) {
        log.debug("Rejected leave request with ID: {}",leaveRequestId);
        LeaveRequest lr = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(()->{
                    log.error("Leave request not found with ID: {}",leaveRequestId);
                    return new CustomException("Leave request not found");
                });
        if (lr.getStatus() != LeaveStatus.Pending) {
            log.error("Leave request ID {} already processed",leaveRequestId);
            throw new CustomException("Leave already processed");
        }

        lr.setStatus(LeaveStatus.Rejected);
        LeaveRequest updated = leaveRequestRepository.save(lr);
        log.info("Leave request ID {} rejected successfully", leaveRequestId);

        return leaveRequestMapper.toDto(updated);
    }

    /**
     * Retrieves all pending leave requests with pagination.
     *
     * @param page Page number
     * @param size Page size
     * @return List of LeaveRequestDTO representing pending leaves
     */
    public List<LeaveRequestDTO> getPendingLeaves(int page, int size) {
        log.debug("Fetching pending leaves - Page: {}, Size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return leaveRequestRepository.findByStatus(LeaveStatus.Pending, pageable)
                .map(leaveRequestMapper::toDto)
                .getContent();
    }

    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomException("Employee not found with id " + employeeId));

        leaveRequestRepository.deleteByEmployee(emp);
        leaveBalanceRepository.deleteByEmployee(emp);

        employeeRepository.delete(emp);
        log.info("Employee with id {} deleted successfully", employeeId);
    }

}