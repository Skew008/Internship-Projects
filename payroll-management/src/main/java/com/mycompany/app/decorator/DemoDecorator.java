package com.mycompany.app.decorator;

public class DemoDecorator {

    public static void main(String[] args) {
        Employee e = new ConcreteEmployee(1, "Sam", 20000.0);

        e.displayDetails();
        System.out.println();

        BonusAddedEmployee b = new BonusAddedEmployee(e, 10000.0);
        b.displayDetails();
        System.out.println("Total salary: "+b.salaryCalculation());
    }
}
