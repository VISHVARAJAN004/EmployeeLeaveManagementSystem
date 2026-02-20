package com.example.EmployeeLeaveManagement.repository;

import com.example.EmployeeLeaveManagement.entity.LeaveBalance;
import com.example.EmployeeLeaveManagement.entity.LeaveType;
import com.example.EmployeeLeaveManagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for {@link LeaveBalance} entity.
 * <p>
 * Extends JpaRepository to provide CRUD operations and custom queries
 * for leave balances of employees.
 * </p>
 *
 * <p>Custom query methods:</p>
 * <ul>
 *     <li>{@link #findByEmployeeAndLeaveType(Employee, LeaveType)} – retrieves
 *     a leave balance record by employee and leave type.</li>
 *     <li>{@link #findByEmployeeAndLeaveType_Name(Employee, String)} – retrieves
 *     a leave balance record by employee and leave type name.</li>
 * </ul>
 */
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance,Long> {

    Optional<LeaveBalance> findByEmployeeAndLeaveType(Employee employee, LeaveType leaveType);

    Optional<LeaveBalance> findByEmployeeAndLeaveType_Name(Employee employee,String name);
}
