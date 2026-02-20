package com.example.EmployeeLeaveManagement.mapper;

import com.example.EmployeeLeaveManagement.dto.LeaveRequestDTO;
import com.example.EmployeeLeaveManagement.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting {@link LeaveRequest} entities to {@link LeaveRequestDTO} objects.
 * <p>
 * Uses MapStruct to automatically map entity fields to DTO fields, including nested properties:
 * <ul>
 *     <li>leaveId ← id</li>
 *     <li>employeeId ← employee.id</li>
 *     <li>leaveTypeName ← leaveType.name</li>
 *     <li>numberOfDays ← totalDays</li>
 * </ul>
 * </p>
 *
 * <p>Configured with Spring's component model for dependency injection.</p>
 */
@Mapper(componentModel = "spring")
public interface LeaveRequestMapper{

    /**
     * Converts a LeaveRequest entity to a LeaveRequestDTO.
     *
     * @param leaveRequest the LeaveRequest entity
     * @return a LeaveRequestDTO with mapped fields
     */
    @Mapping(target="leaveId",source="id")
    @Mapping(target="employeeId",source="employee.id")
    @Mapping(target="leaveTypeName",source="leaveType.name")
    @Mapping(target="numberOfDays",source="totalDays")
    LeaveRequestDTO toDto(LeaveRequest leaveRequest);
}
