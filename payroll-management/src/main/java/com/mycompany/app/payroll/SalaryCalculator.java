package com.mycompany.app.payroll;


import com.mycompany.app.employee.Employee;

import java.util.Objects;

public class SalaryCalculator {

    private SalaryCalculation salaryCalculation;

    public SalaryCalculator() {
        this.salaryCalculation = null;
    }

    public SalaryCalculation getSalaryCalculationState() {
        Objects.requireNonNull(this.salaryCalculation);
        return salaryCalculation;
    }

    public void setSalaryCalculationState(SalaryCalculation salaryCalculation) {
        Objects.requireNonNull(salaryCalculation);
        this.salaryCalculation = salaryCalculation;
    }

    public void salaryCalculation(Employee e) {
        salaryCalculation.displaySalaryDetails(e);
    }

}
