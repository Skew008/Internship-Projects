package com.mycompany.app.comparators;

import com.mycompany.app.employee.Employee;

import java.util.Comparator;

public class SalaryComparator implements Comparator<Employee> {
    @Override
    public int compare(Employee o1, Employee o2) {
//        if(o1.salaryCalculation()>o2.salaryCalculation())
//            return 1;
//        return -1;
        return 1;
    }
}
