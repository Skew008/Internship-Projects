package com.mycompany.app.payroll;

import com.mycompany.app.employee.Employee;
import com.mycompany.app.employee.PartTimeEmployee;

import java.util.Objects;

public class PartTimeSalaryCalculation implements SalaryCalculation{

    @Override
    public double salaryCalculation(Employee e) {
        Objects.requireNonNull(e);
        PartTimeEmployee p = (PartTimeEmployee) e;
        return p.getBaseSalary()*p.getHoursWorked();
    }

    @Override
    public void displaySalaryDetails(Employee e) {
        System.out.println("Part Time Employee:");
        System.out.println("Base Salary: "+e.getBaseSalary());
        System.out.println("Hours Worked: "+((PartTimeEmployee)e).getHoursWorked());
        System.out.println("Total Salary: "+salaryCalculation(e));
    }
}
