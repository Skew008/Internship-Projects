package com.mycompany.app.payroll;

import com.mycompany.app.employee.Employee;
import com.mycompany.app.employee.FullTimeEmployee;

import java.util.Objects;

public class FullTimeSalaryCalculation implements SalaryCalculation{

    @Override
    public double salaryCalculation(Employee e) {
        Objects.requireNonNull(e);
        FullTimeEmployee f = (FullTimeEmployee) e;
        return f.getBaseSalary()+f.getBonus();
    }

    @Override
    public void displaySalaryDetails(Employee e) {
        Objects.requireNonNull(e);
        System.out.println("Full Time Employee:");
        System.out.println("Base Salary: "+e.getBaseSalary());
        System.out.println("Annual Bonus: "+((FullTimeEmployee)e).getBonus());
        System.out.println("Total Salary: "+salaryCalculation(e));
    }
}
