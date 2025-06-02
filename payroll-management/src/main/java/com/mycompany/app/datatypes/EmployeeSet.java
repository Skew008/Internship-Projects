package com.mycompany.app.datatypes;

import com.mycompany.app.employee.Employee;

import java.util.HashSet;
import java.util.NoSuchElementException;

public class EmployeeSet {
    private final HashSet<Employee> s;

    public EmployeeSet() {
        this.s = new HashSet<Employee>();
    }

    public HashSet<Employee> getEmployeeSet() {
        return s;
    }

    public void addEmployee(Employee e) {
        s.add(e);
    }

    public void updateEmployee(int empId, Employee e) {
        if(isPresent(empId))
        {
            removeEmployee(empId);
            addEmployee(e);
        }
        else
            throw new NoSuchElementException("Employee not preset, could not be updated");
    }

    public void removeEmployee(int empId) {
        if(isPresent(empId))
            s.remove(getEmployee(empId));
        else
            throw new NoSuchElementException("Employee not present, could not be deleted");
    }

    public Employee getEmployee(int empId) {
        if(isPresent(empId))
            return s.stream().filter(employee -> employee.getEmpID()==empId).toList().get(0);
        else
            throw new NoSuchElementException("Employee not present");
    }

    private boolean isPresent(int empId) {
        return (s.stream().anyMatch(employee -> employee.getEmpID()==empId));
    }

}
