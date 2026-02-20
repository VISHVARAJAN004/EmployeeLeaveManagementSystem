package com.example.EmployeeLeaveManagement.service.test;

import com.example.EmployeeLeaveManagement.dto.EmployeeDTO;
import com.example.EmployeeLeaveManagement.entity.Employee;
import com.example.EmployeeLeaveManagement.entity.LeaveType;
import com.example.EmployeeLeaveManagement.enums.EmployeeStatus;
import com.example.EmployeeLeaveManagement.enums.Role;
import com.example.EmployeeLeaveManagement.exception.CustomException;
import com.example.EmployeeLeaveManagement.mapper.EmployeeMapper;
import com.example.EmployeeLeaveManagement.repository.EmployeeRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveBalanceRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveTypeRepository;
import com.example.EmployeeLeaveManagement.service.serviceImpl.EmployeeServiceImpl;
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
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EmployeeServiceImpl}.
 * <p>
 * Uses Mockito to mock dependencies and verify interactions.
 * Tests cover:
 * <ul>
 *     <li>Successful employee creation</li>
 *     <li>Duplicate email scenario</li>
 *     <li>Fetching all employees with pagination</li>
 * </ul>
 * </p>
 */
public class EmployeeServiceImplTest {

    @InjectMocks
    EmployeeServiceImpl employeeService;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    LeaveTypeRepository leaveTypeRepository;

    @Mock
    EmployeeMapper employeeMapper;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests successful creation of an employee.
     * Verifies that the employee is saved and leave balances are initialized.
     */
    @Test
    void testCreateEmployeeSuccess(){
        EmployeeDTO dto=new EmployeeDTO();
        dto.setName("Vishva");
        dto.setEmail("vishva@gmail.com");
        dto.setDateOfBirth(LocalDate.of(2004,06,01));
        when(employeeRepository.existsByEmailIgnoreCase(dto.getEmail())).thenReturn(false);

        Employee savedEmployee=new Employee();
        savedEmployee.setId(1L);
        savedEmployee.setName(dto.getName());
        savedEmployee.setEmail(dto.getEmail());
        savedEmployee.setDateOfBirth(dto.getDateOfBirth());
        savedEmployee.setRole(Role.Employee);
        savedEmployee.setStatus(EmployeeStatus.ACTIVE);

       when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
       when(leaveTypeRepository.findByName(anyString())).thenReturn(Optional.of(new LeaveType()));
       when(employeeMapper.toDto(any(Employee.class))).thenReturn(new com.example.EmployeeLeaveManagement.dto.EmployeeResponseDTO());

       var response=employeeService.createEmployee(dto);
       assertNotNull(response);
       verify(employeeRepository,times(1)).save(any(Employee.class));
       verify(leaveBalanceRepository,times(3)).save(any());
    }

    /**
     * Tests that creating an employee with a duplicate email
     * throws a {@link CustomException}.
     */
    @Test
    void testCreateEmployeeDuplicateEmail(){
        EmployeeDTO dto=new EmployeeDTO();
        dto.setEmail("vishva@gmail.com");

        when(employeeRepository.existsByEmailIgnoreCase(dto.getEmail())).thenReturn(true);

        assertThrows(CustomException.class,()->employeeService.createEmployee(dto));
    }

    /**
     * Tests fetching all employees with pagination.
     * Verifies that the correct number of elements are returned.
     */
    @Test
    void testGetAllEmployees(){
        Employee emp=new Employee();
        emp.setId(1L);
        emp.setName("Vishva");
        Page<Employee> page=new PageImpl<>(List.of(emp));
        when(employeeRepository.findAll(PageRequest.of(0,10))).thenReturn(page);
        when(employeeMapper.toDto(emp)).thenReturn(new com.example.EmployeeLeaveManagement.dto.EmployeeResponseDTO());
        Page<com.example.EmployeeLeaveManagement.dto.EmployeeResponseDTO> result =employeeService.getAllEmployees(0,10);
        assertEquals(1,result.getTotalElements());
    }
}
