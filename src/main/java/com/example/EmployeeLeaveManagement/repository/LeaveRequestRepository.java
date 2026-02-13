package com.example.EmployeeLeaveManagement.repository;

import com.example.EmployeeLeaveManagement.entity.LeaveRequest;
import com.example.EmployeeLeaveManagement.enums.LeaveStatus;
import com.example.EmployeeLeaveManagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing {@link LeaveRequest} entities.
 * <p>Provides CRUD operations for leave requests and custom query methods
 * to retrieve requests by employee and/or status.</p>
 * <p>By extending {@link JpaRepository}, this repository inherits methods such as:
 * <ul>
 *     <li>save()</li>
 *     <li>findById()</li>
 *     <li>findAll()</li>
 *     <li>deleteById()</li>
 * </ul>
 * Custom query methods are defined below.</p>
 */
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,Long> {

    /**
     * Retrieves all leave requests submitted by a specific employee.
     * @param employee the employee whose leave requests are to be fetched
     * @return list of leave requests submitted by the given employee
     */
    List<LeaveRequest> findByEmployee(Employee employee);

    /**
     * Retrieves all leave requests with a specific status.
     * @param status the leave status to filter by (e.g., Pending, Approved, Rejected)
     * @return list of leave requests matching the given status
     */
    List<LeaveRequest> findByStatus(LeaveStatus status);

    /**
     * Retrieves all leave requests for a specific employee with a given status.
     * @param employee the employee whose leave requests are to be fetched
     * @param status the leave status to filter by
     * @return list of leave requests matching the employee and status
     */

    List<LeaveRequest> findByEmployeeAndStatus(Employee employee,LeaveStatus status);
}
