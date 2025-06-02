package com.mycompany.app.interface_lambdaexp;

import com.mycompany.app.employee.Employee;

public class RegularSalaryCalculator implements SalaryCalculator {


    @Override
    public double calculate(double baseSalary) {
        if(baseSalary < 0)
            throw new IllegalArgumentException("Base salary cannot be negative");
        return baseSalary*12;
    }
}
