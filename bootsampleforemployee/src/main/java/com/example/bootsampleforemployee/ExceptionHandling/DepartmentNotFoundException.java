package com.example.bootsampleforemployee.ExceptionHandling;

public class DepartmentNotFoundException extends RuntimeException{

    public DepartmentNotFoundException() {
        super("Department does not exist");
    }
}
