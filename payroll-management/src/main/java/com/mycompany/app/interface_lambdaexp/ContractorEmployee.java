package com.mycompany.app.interface_lambdaexp;

public final class ContractorEmployee extends SealedEmployee{
    @Override
    public void show() {
        super.show();
        System.out.println("In ContractorEmployee which is final");
    }
}
