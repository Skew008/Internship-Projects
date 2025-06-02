package com.mycompany.app.payroll;

import com.mycompany.app.employee.ContractorEmployee;

@FunctionalInterface
public interface ContractorSalaryCalculator {

    double salaryCalculation(ContractorEmployee e);
}
