package com.example.EmployeeLeaveManagement.mapper;

import com.example.EmployeeLeaveManagement.dto.EmployeeResponseDTO;
import com.example.EmployeeLeaveManagement.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting {@link Employee} entities to {@link EmployeeResponseDTO} objects.
 * <p>
 * Uses MapStruct for automatic mapping. Certain fields like leave balances (casualLeave, sickLeave,
 * birthdayLeave) are ignored because they are managed separately in the system.
 * </p>
 *
 * <p>Configured with Spring's component model for dependency injection.</p>
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper{

    /**
     * Converts an Employee entity to a response DTO.
     *
     * @param emp the Employee entity
     * @return an EmployeeResponseDTO with mapped fields; leave balances are ignored
     */
    @Mapping(target="casualLeave",ignore=true)
    @Mapping(target="sickLeave",ignore=true)
    @Mapping(target="birthdayLeave",ignore=true)
    @Mapping(target="role",source="role")
    EmployeeResponseDTO toDto(Employee emp);
}
