package com.mycompany.app.datatypes;

import com.mycompany.app.employee.Employee;

import java.util.*;

public class EmployeeMap {
    private HashMap<Integer, Employee> m;

    public EmployeeMap() {
        this.m = new HashMap<>();
    }

    public void addEmployee(Employee e) {
        m.put(e.getEmpID(), e);
    }

    public Employee getEmployee(int empId) {
        if(!m.containsKey(empId))
            throw new NoSuchElementException("No Employee found");
        return m.get(empId);
    }

    public void removeEmployee(int empId) {
        if(!m.containsKey(empId))
            throw new NoSuchElementException("No Employee found");
        m.remove(empId);
        System.out.println("Employee Removed");
    }

    public void updateEmployee(Employee emp) {
        if(!m.containsKey(emp.getEmpID()))
            throw new NoSuchElementException("No Employee found");
        m.put(emp.getEmpID(), emp);
    }

    public List<Employee> getEmployeeList() {
        return m.values().stream().toList();
    }
}
