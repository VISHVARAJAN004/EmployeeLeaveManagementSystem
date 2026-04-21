package com.example.EmployeeLeaveManagement.controller;

import com.example.EmployeeLeaveManagement.dto.EmployeeDTO;
import com.example.EmployeeLeaveManagement.dto.EmployeeResponseDTO;
import com.example.EmployeeLeaveManagement.service.EmployeeServiceInterface;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Employee operations.
 * <p>
 * Provides endpoints to create employees, retrieve all employees (with pagination),
 * and fetch a single employee by ID.
 * </p>
 */
@RestController
@RequestMapping("/api/employees")
@Slf4j
public class EmployeeController {

    private final EmployeeServiceInterface employeeService;

    public EmployeeController(EmployeeServiceInterface employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Creates a new employee.
     *
     * @param dto the employee data transfer object
     * @return the created employee response with HTTP status 201
     */
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeDTO dto){
        return new ResponseEntity<>(employeeService.createEmployee(dto),HttpStatus.CREATED);
    }

    /**
     * Retrieves all employees with pagination.
     *
     * @param page the page number (default 0)
     * @param size the page size (default 10)
     * @return a paginated list of employee responses
     */
    @GetMapping
    public ResponseEntity<Page<EmployeeResponseDTO>> getAllEmployees(
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "10")int size
    ){
        Page<EmployeeResponseDTO> employees =employeeService.getAllEmployees(page,size);
        return ResponseEntity.ok(employees);
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param id the employee ID
     * @return the employee response
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id){
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable Long id,@Valid @RequestBody EmployeeDTO dto){
        EmployeeResponseDTO updated=employeeService.updateEmployee(id,dto);
        return ResponseEntity.ok(updated);
    }

}
