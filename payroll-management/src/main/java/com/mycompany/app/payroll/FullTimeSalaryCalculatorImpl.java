package com.mycompany.app.payroll;

import com.mycompany.app.employee.FullTimeEmployee;

import java.util.Objects;

public class FullTimeSalaryCalculatorImpl implements FullTimeSalaryCalculator{
    @Override
    public double salaryCalculation(FullTimeEmployee e) {
        Objects.requireNonNull(e);
        return e.getBaseSalary()+e.getBonus();
    }
}
