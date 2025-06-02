package com.example.bootsampleforemployee.Repo;


import jakarta.persistence.*;

import java.sql.Date;


@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String department;
    @Column(nullable = false)
    private String designation;
    private double salary;
    @Column(nullable = false)
    private Date dateOfJoining;

    public Employee() {
    }

    public Employee(int id, String name, String department, String designation, double salary, Date dateOfJoining) {
        setId(id);
        setName(name);
        setDepartment(department);
        setDesignation(designation);
        setSalary(salary);
        setDateOfJoining(dateOfJoining);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if(id<0)
            throw new IllegalArgumentException("ID cannot be negative");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name==null || name.isEmpty())
            throw new NullPointerException("Name cannot be empty");
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if(department==null || department.isEmpty())
            throw new NullPointerException("Department cannot be null");
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        if(designation==null || designation.isEmpty())
            throw new NullPointerException("Designation cannot be null");
        this.designation = designation;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if(salary<0)
            throw new IllegalArgumentException("Salary cannot be negative");
        this.salary = salary;
    }

    public Date getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(Date dateOfJoining) {
        if(dateOfJoining==null)
            throw new NullPointerException("Date cannot be empty");
        this.dateOfJoining = dateOfJoining;
    }
}
