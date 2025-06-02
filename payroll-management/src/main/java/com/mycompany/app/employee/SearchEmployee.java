package com.mycompany.app.employee;

import com.mycompany.app.comparators.SalaryComparator;

import java.util.List;

public class SearchEmployee {

    public static List<Employee> getByDepartment(List<Employee> employees, String department) {
        List<Employee> e = employees.stream().filter(employee -> employee.getDepartment().equals(department)).toList();
        if(e.isEmpty())
        {
            System.out.println("No Employees");
            return null;
        }
        return e;
    }

    public static List<Employee> getByName(List<Employee> employees, String name) {
        List<Employee> e = employees.stream().filter(employee -> employee.getDepartment().equals(name)).toList();
        if(e.isEmpty())
        {
            System.out.println("No Employees");
            return null;
        }
        return e;    }

    public static Employee highestPaid(List<Employee> employees) {
        if(employees.isEmpty())
        {
            System.out.println("No Employees");
            return null;
        }
        return employees.stream().max(new SalaryComparator()).stream().toList().get(0);
    }

    public static Employee leastPaid(List<Employee> employees) {
        if(employees.isEmpty())
        {
            System.out.println("No Employees");
            return null;
        }
        return employees.stream().min(new SalaryComparator()).stream().toList().get(0);
    }

}
