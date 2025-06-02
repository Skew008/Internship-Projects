package com.mycompany.app.comparators;

import com.mycompany.app.employee.Employee;

import java.util.Comparator;

public class DepartmentComparator implements Comparator<Employee> {
    @Override
    public int compare(Employee e1, Employee e2) {
        return e1.getDepartment().compareTo(e2.getDepartment());
    }
}