package com.mycompany.app.payroll;

import com.mycompany.app.employee.ContractorEmployee;

import java.util.Objects;

public class ContractorSalaryCalculatorImpl implements ContractorSalaryCalculator{
    @Override
    public double salaryCalculation(ContractorEmployee e) {
        Objects.requireNonNull(e);
        return e.getHoursWorked()*e.getBaseSalary();
    }
}
