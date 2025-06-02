package com.mycompany.app.decorator;

public abstract class EmployeeDecorator implements Employee {

    Employee e;

    public EmployeeDecorator(Employee e) {
        this.e = e;
    }

    @Override
    public double salaryCalculation() {
        return e.salaryCalculation();
    }
}
