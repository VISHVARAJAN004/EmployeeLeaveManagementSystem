package com.example.EmployeeLeaveManagement.repository;

import com.example.EmployeeLeaveManagement.entity.LeaveBalance;
import com.example.EmployeeLeaveManagement.enums.LeaveTypeEnum;
import com.example.EmployeeLeaveManagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing {@link LeaveBalance} entities.
 * <p>Provides CRUD operations for employee leave balances and custom queries
 * to fetch balances by employee and leave type.</p>
 * <p>By extending {@link JpaRepository}, this repository inherits methods such as:
 * <ul>
 *     <li>save()</li>
 *     <li>findById()</li>
 *     <li>findAll()</li>
 *     <li>deleteById()</li>
 * </ul>
 * </p>
 */
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance,Long> {

    /**
     * Finds the leave balance for a specific employee and leave type.
     * @param employee the employee whose leave balance is to be retrieved
     * @param leaveType the type of leave
     * @return an {@link Optional} containing the leave balance if found, or empty if not found
     */
    Optional<LeaveBalance> findByEmployeeAndLeaveType(Employee employee, LeaveTypeEnum leaveType);
}
