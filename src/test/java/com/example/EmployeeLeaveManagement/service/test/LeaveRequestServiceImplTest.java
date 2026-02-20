package com.example.EmployeeLeaveManagement.service.test;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import com.example.EmployeeLeaveManagement.entity.Employee;
import com.example.EmployeeLeaveManagement.entity.LeaveRequest;
import com.example.EmployeeLeaveManagement.entity.LeaveType;
import com.example.EmployeeLeaveManagement.enums.EmployeeStatus;
import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import com.example.EmployeeLeaveManagement.exception.CustomException;
import com.example.EmployeeLeaveManagement.mapper.LeaveRequestMapper;
import com.example.EmployeeLeaveManagement.repository.EmployeeRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveBalanceRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveRequestRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveTypeRepository;
import com.example.EmployeeLeaveManagement.service.serviceImpl.LeaveRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link LeaveRequestServiceImpl}.
 * <p>
 * Uses Mockito to mock dependencies and verify service logic.
 * Tests cover:
 * <ul>
 *     <li>Applying normal leave successfully</li>
 *     <li>Handling invalid leave dates (past date, start after end)</li>
 *     <li>Applying birthday leave and checking constraints</li>
 *     <li>Leave overlap detection</li>
 *     <li>Fetching pending leaves and leave history</li>
 * </ul>
 * </p>
 */
public class LeaveRequestServiceImplTest{

    @InjectMocks
    LeaveRequestServiceImpl leaveService;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    LeaveRequestRepository leaveRequestRepository;

    @Mock
    LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    LeaveTypeRepository leaveTypeRepository;

    @Mock
    LeaveRequestMapper leaveRequestMapper;

    Employee employee;
    LeaveType casualLeave;
    LeaveType birthdayLeave;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        employee=new Employee();
        employee.setId(1L);
        employee.setName("Vishva");
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDateOfBirth(LocalDate.of(2004,06,01));

        casualLeave =new LeaveType();
        casualLeave.setId(1L);
        casualLeave.setName("Casual");

        birthdayLeave=new LeaveType();
        birthdayLeave.setId(2L);
        birthdayLeave.setName("Birthday");

    }
    @Test
    void testApplyNormalLeaveSuccess(){
        LeaveRequestDTO dto=new LeaveRequestDTO();
        dto.setEmployeeId(employee.getId());
        dto.setLeaveTypeName("Casual");
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(3));

        LeaveRequest savedEntity=new LeaveRequest();
        savedEntity.setLeaveType(casualLeave);

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findByName("Casual")).thenReturn(Optional.of(casualLeave));
        when(leaveRequestRepository.findByEmployee(employee)).thenReturn(List.of());
        when(leaveRequestRepository.save(any())).thenReturn(savedEntity);
        when(leaveRequestMapper.toDto(savedEntity)).thenReturn(dto);

        LeaveRequestDTO result =leaveService.applyLeave(dto);

        assertNotNull(result);
        assertEquals("Casual",result.getLeaveTypeName());
    }
    @Test
    void testApplyLeavePasteDate(){
        LeaveRequestDTO dto=new LeaveRequestDTO();
        dto.setEmployeeId(employee.getId());
        dto.setLeaveTypeName("Casual");
        dto.setStartDate(LocalDate.now().minusDays(1));
        dto.setEndDate(LocalDate.now());

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        CustomException ex=assertThrows(CustomException.class,()->leaveService.applyLeave(dto));
        assertEquals("Cannot apply leave for past dates",ex.getMessage());
    }
    @Test
    void testApplyLeaveStartAfterEnd(){
        LeaveRequestDTO dto=new LeaveRequestDTO();
        dto.setEmployeeId(employee.getId());
        dto.setLeaveTypeName("Casual");
        dto.setStartDate(LocalDate.now().plusDays(5));
        dto.setEndDate(LocalDate.now().plusDays(3));

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        CustomException ex=assertThrows(CustomException.class,()->leaveService.applyLeave(dto));
        assertEquals("Start date cannot be after end date",ex.getMessage());
    }

    @Test
    void testApplyBirthdayLeaveSuccess(){

        int currentYear=LocalDate.now().getYear();
        LocalDate birthdayThisYear=LocalDate.of(currentYear,6,1);

        LeaveRequestDTO dto=new LeaveRequestDTO();
        dto.setEmployeeId(employee.getId());
        dto.setLeaveTypeName("Birthday");
        dto.setStartDate(birthdayThisYear);
        dto.setEndDate(birthdayThisYear);
        dto.setLeaveNote("Birthday leave");

        LeaveRequest savedEntity =new LeaveRequest();
        savedEntity.setLeaveType(birthdayLeave);

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findByName("Birthday")).thenReturn(Optional.of(birthdayLeave));
        when(leaveRequestRepository.findByEmployee(employee)).thenReturn(List.of());
        when(leaveRequestRepository.save(any())).thenReturn(savedEntity);
        when(leaveRequestMapper.toDto(any())).thenReturn(dto);

        LeaveRequestDTO result =leaveService.applyLeave(dto);

        assertNotNull(result);
        assertEquals("Birthday",result.getLeaveTypeName());
    }

    @Test
    void testApplyBirthdayLeaveAlreadyTaken(){

        int currentYear=LocalDate.now().getYear();
        LocalDate birthdayThisYear=LocalDate.of(currentYear,6,1);

        LeaveRequest existing =new LeaveRequest();
        existing.setLeaveType(birthdayLeave);
        existing.setStartDate(birthdayThisYear);
        existing.setStatus(LeaveStatus.Approved);

        LeaveRequestDTO dto=new LeaveRequestDTO();
        dto.setEmployeeId(employee.getId());
        dto.setLeaveTypeName("Birthday");
        dto.setStartDate(birthdayThisYear);
        dto.setEndDate(birthdayThisYear);

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findByName("Birthday")).thenReturn(Optional.of(birthdayLeave));
        when(leaveRequestRepository.findByEmployee(employee)).thenReturn(List.of(existing));

        CustomException ex=assertThrows(CustomException.class,()->leaveService.applyLeave(dto));
        assertEquals("Birthday leave already taken this year",ex.getMessage());
    }

    @Test
    void testApplyLeaveOverlap(){
        LeaveRequest existing =new LeaveRequest();
        existing.setLeaveType(casualLeave);
        existing.setStartDate(LocalDate.now().plusDays(2));
        existing.setEndDate(LocalDate.now().plusDays(4));
        existing.setStatus(LeaveStatus.Approved);

        LeaveRequestDTO dto=new LeaveRequestDTO();
        dto.setEmployeeId(employee.getId());
        dto.setLeaveTypeName("Casual");
        dto.setStartDate(LocalDate.now().plusDays(3));
        dto.setEndDate(LocalDate.now().plusDays(3));

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findByName("Casual")).thenReturn(Optional.of(casualLeave));
        when(leaveRequestRepository.findByEmployee(employee)).thenReturn(List.of(existing));
    }

    @Test
    void testGetPendingLeave(){
        LeaveRequest leave=new LeaveRequest();
        leave.setId(1L);
        leave.setStatus(LeaveStatus.Pending);

        when(leaveRequestRepository.findByStatus(eq(LeaveStatus.Pending),any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(leave)));
        when(leaveRequestMapper.toDto(leave)).thenReturn(new LeaveRequestDTO());
        Page<LeaveRequestDTO> result =leaveService.getPendingLeaves(0,10);
        assertEquals(1,result.getTotalElements());
    }

    @Test
    void testGetLeaveHistory(){
        LeaveRequest leave=new LeaveRequest();
        leave.setId(1L);
        leave.setEmployee(employee);

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.findByEmployee(employee,PageRequest.of(0,10)))
                .thenReturn(new PageImpl<>(List.of(leave)));
        when(leaveRequestMapper.toDto(leave)).thenReturn(new LeaveRequestDTO());

        Page<LeaveRequestDTO> result =leaveService.getLeaveHistory(employee.getId(),0,10);
        assertEquals(1,result.getTotalElements());
    }
}
