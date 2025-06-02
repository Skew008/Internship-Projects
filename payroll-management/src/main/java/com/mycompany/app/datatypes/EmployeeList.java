package com.mycompany.app.datatypes;

import com.mycompany.app.employee.Employee;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EmployeeList {

    private final List<Employee> l;

    public EmployeeList() {
        this.l = new ArrayList<>();
    }

    public void addEmployee(Employee e) {
        l.add(e);
    }

    public void removeEmployee(int empID) {
        Iterator<Employee> i = l.iterator();
        int idx = 0;
        while(i.hasNext())
        {
            if(i.next().getEmpID()==empID)
                break;
            idx++;
        }
        if(idx==l.size())
            throw new IndexOutOfBoundsException("Employee not found");
        l.remove(idx);
    }

    public List<Employee> getEmployeeList() {
        return l;
    }

    public Employee getEmployee(int empID) {
        for(Employee e:l)
        {
            if(empID==e.getEmpID())
                return e;
        }
        System.out.println("No employee");
        return null;
    }

    public void setEmployee(Employee emp) {
        for(Employee e:l)
        {
            if(emp.getEmpID()==e.getEmpID()) {
                e = emp;
                break;
            }
        }
    }
}
