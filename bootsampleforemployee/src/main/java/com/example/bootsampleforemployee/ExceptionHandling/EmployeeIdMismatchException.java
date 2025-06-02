package com.example.bootsampleforemployee.ExceptionHandling;

public class EmployeeIdMismatchException extends RuntimeException {
    public EmployeeIdMismatchException() {
        super("Employee Id does not match");
    }
}
