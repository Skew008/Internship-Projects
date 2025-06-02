package com.mycompany.app.interface_lambdaexp;

public record EmployeeRecord(String name, double salary) {

    public EmployeeRecord(String name, double salary) {
        if(name==null || name.isEmpty())
            throw new NullPointerException("Name cannot be empty");
        if(salary < 0)
            throw new IllegalArgumentException("Salary cannot be negative");
        this.name = name;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return name+" "+salary;
    }
}
