package com.mycompany.app.employee;

import com.mycompany.app.observer.SalaryObserver;
import com.mycompany.app.payroll.PayrollAdjustment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Employee implements Comparable<Employee>{
    private int empID;
    private String name;
    private String department;
    private double baseSalary;
    protected String designation;
    protected List<SalaryObserver> observers;


    public Employee(){}

    public Employee(int empID, String name, String department, double baseSalary) {
        setEmpID(empID);
        setName(name);
        setDepartment(department);
        setBaseSalary(baseSalary);
        observers = new ArrayList<>();
    }


    @Override
    public int compareTo(Employee o) {
        return this.empID-o.getEmpID();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return empID == employee.empID && Double.compare(baseSalary, employee.baseSalary) == 0 && Objects.equals(name, employee.name) && Objects.equals(department, employee.department);
    }

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        if(empID < 0)
            throw new IllegalArgumentException("Employee ID cannot be negative");
        this.empID = empID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name!=null)
        {
            if(name.isEmpty())
                throw new IllegalArgumentException("Name cannot be empty");
            else
                this.name = name;
        }
        else
            throw new NullPointerException("Name cannot be null");
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if(department!=null)
        {
            if(department.isEmpty())
                throw new IllegalArgumentException("Department cannot be empty");
            else
                this.department = department;
        }
        else
            throw new NullPointerException("Department cannot be null");
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        try {
            if (baseSalary < 0)
                throw new AssertionError("Salary Cannot be negative");
            else{
                this.baseSalary = baseSalary;
                notifyObserver();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String getDesignation() {
        return designation;
    }

    public void showEmployeeDetails() {
        System.out.println("EmployeeID: "+empID);
        System.out.println("Name:"+name);
        System.out.println("Department: "+department);
        System.out.println("Designation: "+getDesignation());
        System.out.println("Salary: "+baseSalary);
    }

    public void attachObserver(SalaryObserver o) {
        Objects.requireNonNull(o);
        System.out.println("Observer added");
        observers.add(o);
    }

    public void notifyObserver() {
        if(observers==null)
            return;
        System.out.println("Salary Updated");
        for(SalaryObserver o:observers)
            o.update();
    }

//    public void displaySalaryDetails() {
//        System.out.println("Base Salary: "+baseSalary);
//    }
//
//    @PayrollAdjustment
//    abstract public double salaryCalculation();

    public static abstract class EmployeeBuilder {
        protected int empID;
        protected String name;
        protected String department;
        protected double baseSalary;

        public abstract EmployeeBuilder setEmpID(int empID);

        public abstract EmployeeBuilder setName(String name);

        public abstract EmployeeBuilder setDepartment(String department);

        public abstract EmployeeBuilder setBaseSalary(double baseSalary);

        public abstract Employee createEmployee();
    }
}
