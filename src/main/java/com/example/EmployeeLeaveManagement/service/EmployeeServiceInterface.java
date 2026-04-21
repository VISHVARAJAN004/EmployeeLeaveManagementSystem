package com.example.EmployeeLeaveManagement.service;

import com.example.EmployeeLeaveManagement.dto.EmployeeDTO;
import com.example.EmployeeLeaveManagement.dto.EmployeeResponseDTO;
import org.springframework.data.domain.Page;

/**
 * Service interface for employee management operations.
 * <p>
 * Provides methods to create employees, fetch employee details,
 * and retrieve a paginated list of all employees.
 * </p>
 */
public interface EmployeeServiceInterface {

    /**
     * Creates a new employee and initializes their leave balances.
     *
     * @param dto Data Transfer Object containing employee details
     * @return EmployeeResponseDTO with created employee details
     */
    EmployeeResponseDTO createEmployee(EmployeeDTO dto);

    /**
     * Retrieves details of an employee by their ID.
     *
     * @param id ID of the employee
     * @return EmployeeResponseDTO containing employee information
     */
    EmployeeResponseDTO getEmployeeById(Long id);

    /**
     * Retrieves a paginated list of all employees.
     *
     * @param page Page number (0-based)
     * @param size Number of records per page
     * @return Page of EmployeeResponseDTO representing employees
     */
    Page<EmployeeResponseDTO> getAllEmployees(int page,int size);

    EmployeeResponseDTO updateEmployee(Long id,EmployeeDTO dto);


}

