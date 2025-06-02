package com.mycompany.absclassinterface;

public class PermanentEmployee extends EmploymentType implements Payable {

    public PermanentEmployee(String name, double pay) {
        super(name, pay);
    }

    @Override
    public int calculateWorkHours() {
        return 8;
    }

    @Override
    public double calculatePay() {
        return getPay();
    }

}
