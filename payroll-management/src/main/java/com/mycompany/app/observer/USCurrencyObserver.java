package com.mycompany.app.observer;

import com.mycompany.app.employee.Employee;

import java.util.Objects;

public class USCurrencyObserver extends SalaryObserver{

    public USCurrencyObserver(Employee e) {
        Objects.requireNonNull(e);
        this.e = e;
        this.e.attachObserver(this);
    }

    @Override
    public void update() {
        System.out.println("US Currency: $"+(e.getBaseSalary()/50)+" USD");
    }
}
