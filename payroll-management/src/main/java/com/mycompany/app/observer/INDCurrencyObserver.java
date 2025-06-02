package com.mycompany.app.observer;

import com.mycompany.app.employee.Employee;

import java.util.Objects;

public class INDCurrencyObserver extends SalaryObserver{

    public INDCurrencyObserver(Employee e) {
        Objects.requireNonNull(e);
        this.e = e;
        this.e.attachObserver(this);
    }

    @Override
    public void update() {
        System.out.println("IND Currency: ₹"+e.getBaseSalary()+" INR");
    }
}
