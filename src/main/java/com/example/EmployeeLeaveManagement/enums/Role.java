package com.example.EmployeeLeaveManagement.enums;

/**
 * Enum representing the roles of users in the HRMS system.
 * <p>Used to control access and permissions for different operations
 * such as applying for leave or approving leave requests.</p>
 */
public enum Role {

    /** Regular employee with access to apply for leave and view their own records */
    Employee,

    /** Manager with access to approve or reject leave requests and view pending leaves */
    Manager
}
