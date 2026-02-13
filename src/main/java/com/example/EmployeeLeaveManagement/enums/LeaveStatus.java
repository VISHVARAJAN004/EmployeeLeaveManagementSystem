package com.example.EmployeeLeaveManagement.enums;

/**
 * Enum representing the status of a leave request in the HRMS system.
 * <p>Used to track the progress of leave requests submitted by employees.</p>
 */
public enum LeaveStatus {

    /** Leave request has been submitted but not yet processed by a manager */
    Pending,

    /** Leave request has been approved by the manager */
    Approved,

    /** Leave request has been rejected by the manager */
    Rejected
}
