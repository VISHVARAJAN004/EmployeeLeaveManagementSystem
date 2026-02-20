package com.example.EmployeeLeaveManagement.repository;

import com.example.EmployeeLeaveManagement.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for managing LeaveType entities.
 * Provides method to find leave type by its name.
 */
public interface LeaveTypeRepository extends JpaRepository<LeaveType,Long> {

    Optional<LeaveType> findByName(String name);

}
