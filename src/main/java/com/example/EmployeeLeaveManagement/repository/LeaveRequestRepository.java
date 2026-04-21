package com.example.EmployeeLeaveManagement.repository;

import com.example.EmployeeLeaveManagement.entity.LeaveRequest;
import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import com.example.EmployeeLeaveManagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Repository for managing LeaveRequest entities.
 * Provides methods to fetch leave requests by status or employee.
 */
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,Long> {

    Page<LeaveRequest> findByStatus(LeaveStatus status,Pageable pageable);

    Page<LeaveRequest> findByEmployee(Employee employee,Pageable pageable);

    List<LeaveRequest> findByEmployee(Employee employee);

    void deleteByEmployee(Employee employee);

}
