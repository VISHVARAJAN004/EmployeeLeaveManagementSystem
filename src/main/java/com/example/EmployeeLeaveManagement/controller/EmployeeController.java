package com.example.EmployeeLeaveManagement.controller;

import com.example.EmployeeLeaveManagement.dto.EmployeeDTO;
import com.example.EmployeeLeaveManagement.dto.EmployeeResponseDTO;
import com.example.EmployeeLeaveManagement.entity.LeaveBalance;
import com.example.EmployeeLeaveManagement.enums.EmployeeStatus;
import com.example.EmployeeLeaveManagement.enums.LeaveTypeEnum;
import com.example.EmployeeLeaveManagement.entity.Employee;
import com.example.EmployeeLeaveManagement.enums.Role;
import com.example.EmployeeLeaveManagement.repository.EmployeeRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveBalanceRepository;
import com.example.EmployeeLeaveManagement.util.LeaveConstants;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller to manage employees.
 * Provides APIs to create, view single and list all employees.
 * <p>Initial leave balances are also created when a new employee is added.</p>
 */
@RestController
@RequestMapping("/api/employees")
@Slf4j
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public EmployeeController(EmployeeRepository employeeRepository, LeaveBalanceRepository leaveBalanceRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
    }
    /**
     * Create a new employee and initialize leave balances.
     * @param dto EmployeeDTO containing name, email, and date of birth
     * @return EmployeeResponseDTO with created employee details and leave balances
     */
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeDTO dto){
        Employee emp=new Employee();
        emp.setName(dto.getName());
        emp.setEmail(dto.getEmail());
        emp.setDateOfBirth(dto.getDateOfBirth());
        emp.setRole(Role.Employee);
        emp.setStatus(EmployeeStatus.ACTIVE);
        employeeRepository.save(emp);
        createLeaveBalance(emp, LeaveTypeEnum.Casual, LeaveConstants.CASUAL_LEAVE);
        createLeaveBalance(emp, LeaveTypeEnum.Sick,LeaveConstants.SICK_LEAVE);
        createLeaveBalance(emp, LeaveTypeEnum.Birthday,LeaveConstants.BIRTHDAY_LEAVE);
        log.info("Created employee {} with initial leave balance", emp.getName());
        EmployeeResponseDTO response=mapToResponse(emp);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    /**
     * Get a specific employee by ID.
     * @param id Employee ID
     * @return EmployeeResponseDTO for the requested employee
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployee(@PathVariable Long id){
        Employee emp=employeeRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Employee not found"));
        return ResponseEntity.ok(mapToResponse(emp));
    }
    /**
     * Get all employees.
     * @return List of EmployeeResponseDTO for all employees
     */
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees(){
        List<EmployeeResponseDTO> responses=new ArrayList<>();
        List<Employee> employees=employeeRepository.findAll();
        for(Employee emp:employees){
            responses.add(mapToResponse(emp));
        }
        return ResponseEntity.ok(responses);
    }

    /**
     * Helper method to create a leave balance for a specific employee and leave type.
     * @param emp Employee for whom the leave balance is being created
     * @param type Type of leave (Casual, Sick, Birthday)
     * @param days Number of leave days allocated
     */
    private void createLeaveBalance(Employee emp,LeaveTypeEnum type,int days){
        LeaveBalance balance=new LeaveBalance();
        balance.setEmployee(emp);
        balance.setLeaveType(type);
        balance.setRemainingDays(days);
        leaveBalanceRepository.save(balance);
    }

    /**
     * Helper method to map an Employee entity to EmployeeResponseDTO.
     * <p>This method calculates the remaining leave days for each leave type
     * and populates the response DTO.</p>
     * @param emp Employee entity to map
     * @return EmployeeResponseDTO containing employee details and leave balances
     */
    private EmployeeResponseDTO mapToResponse(Employee emp){
        int casual=getLeaveDays(emp,LeaveTypeEnum.Casual);
        int sick=getLeaveDays(emp,LeaveTypeEnum.Sick);
        int birthday =getLeaveDays(emp,LeaveTypeEnum.Birthday);
        return new EmployeeResponseDTO(emp,casual,sick,birthday);
    }

    /**
     * Helper method to get remaining leave days for a specific employee and leave type.
     * @param emp Employee entity
     * @param type LeaveTypeEnum representing type of leave
     * @return Number of remaining leave days, or 0 if no leave balance found
     */
    private int getLeaveDays(Employee emp,LeaveTypeEnum type){
        LeaveBalance balance=leaveBalanceRepository.findByEmployeeAndLeaveType(emp,type).orElse(null);
        return balance != null?balance.getRemainingDays():0;
    }
}
