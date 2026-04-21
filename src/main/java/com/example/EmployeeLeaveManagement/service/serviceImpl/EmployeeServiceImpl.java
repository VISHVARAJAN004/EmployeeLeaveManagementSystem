package com.example.EmployeeLeaveManagement.service.serviceImpl;

import com.example.EmployeeLeaveManagement.dto.EmployeeDTO;
import com.example.EmployeeLeaveManagement.dto.EmployeeResponseDTO;
import com.example.EmployeeLeaveManagement.entity.Employee;
import com.example.EmployeeLeaveManagement.entity.LeaveBalance;
import com.example.EmployeeLeaveManagement.entity.LeaveType;
import com.example.EmployeeLeaveManagement.enums.EmployeeStatus;
import com.example.EmployeeLeaveManagement.enums.Role;
import com.example.EmployeeLeaveManagement.exception.CustomException;
import com.example.EmployeeLeaveManagement.mapper.EmployeeMapper;
import com.example.EmployeeLeaveManagement.repository.EmployeeRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveBalanceRepository;
import com.example.EmployeeLeaveManagement.repository.LeaveTypeRepository;
import com.example.EmployeeLeaveManagement.service.EmployeeServiceInterface;
import com.example.EmployeeLeaveManagement.util.LeaveConstants;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing employees.
 * <p>
 * Provides methods to create employees, fetch employee details,
 * fetch paginated employee lists, and initialize leave balances.
 * Handles business logic like email uniqueness and leave balance setup.
 * </p>
 */
@Service
@Slf4j
@Transactional
public class EmployeeServiceImpl implements EmployeeServiceInterface {
    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeMapper employeeMapper;

    /**
     * Constructor for EmployeeServiceImpl.
     */
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               LeaveBalanceRepository leaveBalanceRepository,
                               LeaveTypeRepository leaveTypeRepository,
                               EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.employeeMapper=employeeMapper;
    }

    /**
     * Creates a new employee and initializes their leave balances.
     *
     * @param dto Employee data transfer object with details.
     * @return EmployeeResponseDTO with employee details and leave balances.
     * @throws CustomException if employee email already exists.
     */
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeDTO dto){
        log.debug("Creating employee with email {}",dto.getEmail());
        if(employeeRepository.existsByEmailIgnoreCase(dto.getEmail())){
            throw new CustomException("Employee with email "+dto.getEmail()+" already exists");
        }
        Employee emp =new Employee();
        emp.setName(dto.getName());
        emp.setEmail(dto.getEmail());
        emp.setDateOfBirth(dto.getDateOfBirth());
        emp.setRole(Role.Employee);
        emp.setStatus(EmployeeStatus.ACTIVE);
        employeeRepository.save(emp);

        createLeaveBalance(emp,"Casual",LeaveConstants.CASUAL_LEAVE);
        createLeaveBalance(emp,"Sick",LeaveConstants.SICK_LEAVE);
        createLeaveBalance(emp,"Birthday",LeaveConstants.BIRTHDAY_LEAVE);

        log.info("Created employee {} ",emp.getName());
        return mapToResponse(emp);
    }

    /**
     * Fetches employee details by ID.
     *
     * @param id Employee ID.
     * @return EmployeeResponseDTO with employee details.
     * @throws CustomException if employee not found.
     */
    public EmployeeResponseDTO getEmployeeById(Long id){
        log.debug("Fetching employee with ID:{}",id);
        Employee emp=employeeRepository.findById(id)
                .orElseThrow(()->{
                    log.error("Employee not found with ID {}",id);
                    return new CustomException("Employee not found with id " +id);
                });
        return employeeMapper.toDto(emp);
    }

    /**
     * Retrieves all employees in a paginated format.
     *
     * @param page Page number.
     * @param size Number of records per page.
     * @return Paginated EmployeeResponseDTO list.
     */
    public Page<EmployeeResponseDTO> getAllEmployees(int page,int size){
        log.debug("Fetching employees - Page: {},Size {}",page,size);
        Pageable pageable=PageRequest.of(page,size);
        return employeeRepository.findAll(pageable)
                .map(employeeMapper::toDto);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeDTO dto){
        log.debug("Updating employee with id {}",id);

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new CustomException("Employee not found with id " + id));

        if(!emp.getEmail().equalsIgnoreCase(dto.getEmail()) &&
                employeeRepository.existsByEmailIgnoreCase(dto.getEmail())){
            throw new CustomException("Employee with email "+dto.getEmail()+" already exists");
        }

        emp.setName(dto.getName());
        emp.setEmail(dto.getEmail());
        emp.setDateOfBirth(dto.getDateOfBirth());

        employeeRepository.save(emp);

        log.info("Employee {} updated successfully",emp.getName());

        return mapToResponse(emp);
    }

    /**
     * Initializes leave balance for a given employee and leave type.
     */
    private void createLeaveBalance(Employee emp,String leaveName,int days){
        LeaveType leaveType=leaveTypeRepository.findByName(leaveName)
                        .orElseGet(()-> {
                            LeaveType newType = new LeaveType();
                            newType.setName(leaveName);
                            newType.setDefaultDays(days);
                            log.info("Created leave type {} with {} days ",leaveName,days);
                            return leaveTypeRepository.save(newType);
                        });
        LeaveBalance balance=new LeaveBalance();
        balance.setEmployee(emp);
        balance.setLeaveType(leaveType);
        balance.setRemainingDays(days);
        balance.setLeaveTypeName(leaveType.getName());
        leaveBalanceRepository.save(balance);
        log.debug("Leave balance for {} set days",leaveName,days);
    }

    /**
     * Maps an Employee entity to EmployeeResponseDTO including leave balances.
     */
    private EmployeeResponseDTO mapToResponse(Employee emp){

        EmployeeResponseDTO dto=employeeMapper.toDto(emp);
        dto.setCasualLeave(getLeaveDays(emp,"Casual"));
        dto.setSickLeave(getLeaveDays(emp,"Sick"));
        dto.setBirthdayLeave(getLeaveDays(emp,"Birthday"));
        return dto;
    }

    /**
     * Gets remaining leave days for an employee and leave type.
     */
    private int getLeaveDays(Employee emp,String leaveName){
        return leaveBalanceRepository
                .findByEmployeeAndLeaveType_Name(emp,leaveName)
                .map(LeaveBalance::getRemainingDays)
                .orElse(0);
    }
}
