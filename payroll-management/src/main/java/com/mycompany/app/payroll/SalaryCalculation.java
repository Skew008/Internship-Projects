package com.mycompany.app.payroll;

import com.mycompany.app.employee.Employee;
import com.mycompany.app.employee.FullTimeEmployee;


public interface SalaryCalculation {
    double salaryCalculation(Employee e);
    void displaySalaryDetails(Employee e);
}
