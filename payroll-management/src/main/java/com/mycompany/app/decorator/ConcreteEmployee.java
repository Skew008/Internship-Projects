package com.mycompany.app.decorator;

import java.util.Objects;

public class ConcreteEmployee implements Employee{

    private int id;
    private String name;
    private double salary;

    public ConcreteEmployee(int id, String name, double salary) {
        setId(id);
        setName(name);
        setSalary(salary);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if(id < 0)
            throw new IllegalArgumentException("Id cannot be negative");
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if(salary < 0)
            throw new IllegalArgumentException("Salary cannot be negative");
        this.salary = salary;
    }

    @Override
    public double salaryCalculation() {
        return salary*12;
    }

    @Override
    public void displayDetails() {
        System.out.println("Id: "+getId());
        System.out.println("Name: "+getName());
        System.out.println("Salary: "+getSalary());
    }
}
