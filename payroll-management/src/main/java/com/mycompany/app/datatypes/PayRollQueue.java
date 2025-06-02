package com.mycompany.app.datatypes;

import com.mycompany.app.employee.Employee;
import com.mycompany.app.payroll.PayrollAdjustment;

import java.util.LinkedList;
import java.util.Queue;

public class PayRollQueue {

    private final Queue<Employee> queue;

    public PayRollQueue() {
        this.queue = new LinkedList<>();
    }

    public void addEmployee(Employee e) {
        queue.add(e);
    }

    public void removeEmployee() throws NoSuchMethodException {
        if(queue.isEmpty())
        {
            System.out.println("NO employee in queue currently");
            return;
        }
        Employee e = queue.remove();
        PayrollAdjustment payrollAdjustment = Employee.class.getMethod("salaryCalculation",null).getAnnotation(PayrollAdjustment.class);
//        double salary = e.salaryCalculation();
//        double payroll = salary - (salary*payrollAdjustment.taxDeduction());
        System.out.println("Processing payroll...\n"+-1);
    }
}
