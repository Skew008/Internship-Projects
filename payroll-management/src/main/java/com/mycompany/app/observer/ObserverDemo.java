package com.mycompany.app.observer;

import com.mycompany.app.employee.Employee;
import com.mycompany.app.employee.FullTimeEmployee;

public class ObserverDemo {

    public static void main(String[] args) {
        Employee e = new FullTimeEmployee(1, "Sam", "CSE", 4090, 200);

        e.showEmployeeDetails();
        System.out.println();

        new USCurrencyObserver(e);
        new INDCurrencyObserver(e);

        e.setBaseSalary(30000.0);
        System.out.println();
        e.showEmployeeDetails();
    }
}
