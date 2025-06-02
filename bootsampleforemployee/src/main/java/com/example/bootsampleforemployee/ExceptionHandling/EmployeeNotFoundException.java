package com.example.bootsampleforemployee.ExceptionHandling;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException() {
        super("Employee Not Found");
    }
}
