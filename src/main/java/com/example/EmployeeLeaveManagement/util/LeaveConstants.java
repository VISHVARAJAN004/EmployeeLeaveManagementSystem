package com.example.EmployeeLeaveManagement.util;

/**
 * Utility class containing constants for the initial leave balances of employees.
 * <p>This class defines the default number of leave days assigned to a new employee
 * for each leave type: Casual, Sick, and Birthday leaves.</p>
 * <p>These constants are used when creating new employees to initialize their leave balances.</p>
 */
public class LeaveConstants {

    /** Default number of Casual leave days assigned to a new employee. */
    public static final int CASUAL_LEAVE=12;

    /** Default number of Sick leave days assigned to a new employee. */
    public static final int SICK_LEAVE=12;

    /** Default number of Birthday leave days assigned to a new employee. */
    public static final int BIRTHDAY_LEAVE=1;
}
