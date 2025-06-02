package com.mycompany.app.payroll;

import com.mycompany.app.employee.FullTimeEmployee;

@FunctionalInterface
public interface FullTimeSalaryCalculator {

    double salaryCalculation(FullTimeEmployee e);
}
