package com.example.EmployeeLeaveManagement.repository;

import com.example.EmployeeLeaveManagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Employee} entities.
 * <p>Provides CRUD operations and JPA query methods for employees.</p>
 * <p>By extending {@link JpaRepository}, this repository inherits methods such as:
 * <ul>
 *     <li>save()</li>
 *     <li>findById()</li>
 *     <li>findAll()</li>
 *     <li>deleteById()</li>
 * </ul>
 * Custom queries can also be defined here if needed.</p>
 */
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
}
