package com.mycompany.app.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmployeeSubject {

    private int id;
    private String name;
    private double salary;
    protected List<SalaryObserver> observers;

    public EmployeeSubject(int id, String name, double salary) {
        setId(id);
        setName(name);
        setSalary(salary);
        observers = new ArrayList<>();
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
        notifyObserver();
    }

    public void show() {
        System.out.println("Id: "+getId());
        System.out.println("Name: "+getName());
        System.out.println("Salary: "+getSalary());
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
}
