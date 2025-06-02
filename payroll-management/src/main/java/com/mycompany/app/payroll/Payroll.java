package com.mycompany.app.payroll;

import com.mycompany.app.employee.Employee;
import com.mycompany.app.employee.FullTimeEmployee;

import java.util.Collection;

public class Payroll {

    public <C extends Collection<Employee>> void printPayroll(C c) {
        for (Employee e : c)
        {
            e.showEmployeeDetails();
            System.out.println("Total Salary:"+taxed(e.getDesignation(),/*e.salaryCalculation()*/1));
            System.out.println();
        }
    }

    public <C extends Collection<Employee>> void printAnnualBonus(C c) {
        for(Employee e : c)
        {
            e.showEmployeeDetails();
            System.out.println("Annual Bonus:" + bonus(e));
            System.out.println();
        }
    }

    private double taxed(String des, double d) {
        double taxPercent = switch (des){
            case "PartTimeEmployee" -> 0.05;
            case "FullTimeEmployee" -> 0.1;
            default -> 0.07;
        };
        return d - (d*taxPercent);
    }

    private String bonus(Employee e) {
        System.out.println(e.getDesignation());
        return switch (e.getDesignation()) {
            case "FullTimeEmployee" -> fullTimeBonus(e);
            default -> "No Bonus to part time";
        };
    }

    private String fullTimeBonus(Employee e) {
        FullTimeEmployee f = (FullTimeEmployee) e;
        return ""+(f.getBonus()*12);
    }
}
