package com.example.EmployeeLeaveManagement.service.test;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import com.example.EmployeeLeaveManagement.entity.Employee;
import com.example.EmployeeLeaveManagement.entity.LeaveBalance;
import com.example.EmployeeLeaveManagement.entity.LeaveRequest;
import com.example.EmployeeLeaveManagement.entity.LeaveType;
import com.example.EmployeeLeaveManagement.enums.EmployeeStatus;
import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import com.example.EmployeeLeaveManagement.exception.CustomException;
import com.example.EmployeeLeaveManagement.mapper.LeaveRequestMapper;
import com.example.EmployeeLeaveManagement.repository.LeaveBalanceRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveRequestRepository;
import com.example.EmployeeLeaveManagement.service.serviceImpl.ManagerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ManagerServiceImpl}.
 * <p>
 * Uses Mockito to mock dependencies and verify manager leave approval and rejection logic.
 * Tests cover:
 * <ul>
 *     <li>Successful leave approval and balance deduction</li>
 *     <li>Leave approval failures: not found, already processed, insufficient balance</li>
 *     <li>Successful leave rejection</li>
 *     <li>Leave rejection failure: already processed</li>
 * </ul>
 * </p>
 */
public class ManagerServiceImplTest {

    @InjectMocks
    private ManagerServiceImpl managerService;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    private Employee employee;
    private LeaveType leaveType;
    private LeaveRequest leaveRequest;
    private LeaveBalance leaveBalance;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        employee = new Employee();
        employee.setId(1L);
        employee.setStatus(EmployeeStatus.ACTIVE);

        leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Casual");

        leaveRequest = new LeaveRequest();
        leaveRequest.setId(1L);
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(LocalDate.now());
        leaveRequest.setEndDate(LocalDate.now().plusDays(2));
        leaveRequest.setTotalDays((int) ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1);
        leaveRequest.setStatus(LeaveStatus.Pending);

        leaveBalance = new LeaveBalance();
        leaveBalance.setEmployee(employee);
        leaveBalance.setLeaveType(leaveType);
        leaveBalance.setRemainingDays(10);
    }

    @Test
    void testApproveLeaveSuccess() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType))
                .thenReturn(Optional.of(leaveBalance));
        when(leaveRequestRepository.save(any())).thenReturn(leaveRequest);
        when(leaveBalanceRepository.save(any())).thenReturn(leaveBalance);
        when(leaveRequestMapper.toDto(any())).thenReturn(new LeaveRequestDTO());

        LeaveRequestDTO result = managerService.approveLeave(1L);

        assertEquals(LeaveStatus.Approved, leaveRequest.getStatus());

        long expectedDays = ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
        int expectedRemaining = 10 - (int) expectedDays;
        assertEquals(expectedRemaining, leaveBalance.getRemainingDays());

        verify(leaveRequestRepository, times(1)).save(leaveRequest);
        verify(leaveBalanceRepository, times(1)).save(leaveBalance);
        assertNotNull(result);
    }

    @Test
    void testApproveLeaveNotFound() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class, () -> managerService.approveLeave(1L));
        assertEquals("Leave request nor found", ex.getMessage());
    }


    @Test
    void testApproveLeaveAlreadyProcessed() {
        leaveRequest.setStatus(LeaveStatus.Approved);
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        CustomException ex = assertThrows(CustomException.class, () -> managerService.approveLeave(1L));
        assertEquals("Leave already processed", ex.getMessage());
    }


    @Test
    void testApproveLeaveInsufficientBalance() {
        leaveBalance.setRemainingDays(0);
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType))
                .thenReturn(Optional.of(leaveBalance));

        CustomException ex = assertThrows(CustomException.class, () -> managerService.approveLeave(1L));
        assertEquals("Insufficient leave balance.Leave rejected", ex.getMessage());
        assertEquals(LeaveStatus.Rejected, leaveRequest.getStatus());
    }

    @Test
    void testRejectLeaveSuccess() {
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any())).thenReturn(leaveRequest);
        when(leaveRequestMapper.toDto(any())).thenReturn(new LeaveRequestDTO());

        LeaveRequestDTO result = managerService.rejectLeave(1L);

        assertEquals(LeaveStatus.Rejected, leaveRequest.getStatus());
        verify(leaveRequestRepository, times(1)).save(leaveRequest);
        assertNotNull(result);
    }

    @Test
    void testRejectLeaveAlreadyProcessed() {
        leaveRequest.setStatus(LeaveStatus.Approved);
        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));

        CustomException ex = assertThrows(CustomException.class, () -> managerService.rejectLeave(1L));
        assertEquals("Leave already processed", ex.getMessage());
    }
}