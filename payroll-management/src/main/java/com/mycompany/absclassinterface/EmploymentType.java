package com.mycompany.absclassinterface;

public abstract class EmploymentType {

    private String name;
    private double pay;

    public EmploymentType(String name, double pay) {
        setName(name);
        setPay(pay);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name!=null)
            this.name = name;
        else
            throw new NullPointerException("Name cannot be null");
    }

    public double getPay() {
        return pay;
    }

    public void setPay(double pay) {
        try {
            if(pay<0)
                throw new AssertionError("Salary Cannot be negative");
            else
                this.pay = pay;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void showDetails() {
        System.out.println(getName());
        System.out.println(getPay());
    }
    abstract public int calculateWorkHours();
}
