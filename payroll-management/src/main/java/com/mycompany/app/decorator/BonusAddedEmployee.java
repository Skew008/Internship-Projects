package com.mycompany.app.decorator;

public class BonusAddedEmployee extends EmployeeDecorator{

    private double bonus;
    public BonusAddedEmployee(Employee e, double bonus) {
        super(e);
        setBonus(bonus);
    }

    public void setBonus(double bonus) {
        if(bonus < 0)
            throw new IllegalArgumentException("Bonus cannot be null");
        this.bonus = bonus;
    }

    public double getBonus() {
        return bonus;
    }

    @Override
    public double salaryCalculation() {
        return super.salaryCalculation()+this.bonus;
    }

    @Override
    public void displayDetails() {
        e.displayDetails();
        System.out.println("Bonus: "+getBonus());
    }
}
