package com.mycompany.app.observer;

import com.mycompany.app.employee.Employee;

public abstract class SalaryObserver {

    Employee e;

    public abstract void update();
}
