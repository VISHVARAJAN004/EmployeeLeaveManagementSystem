package com.example.EmployeeLeaveManagement.enums;

/**
 * Enum representing the status of an employee in the HRMS system.
 * <p>Used to indicate whether an employee is currently active or inactive.
 * Can be used for validation, access control, and leave eligibility checks.</p>
 */
public enum EmployeeStatus {

    /** Employee is currently active and can perform operations such as applying for leave */
    ACTIVE,

    /** Employee is inactive and cannot perform operations */
    INACTIVE
}
