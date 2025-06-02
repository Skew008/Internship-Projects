package com.mycompany.app.interface_lambdaexp;

public sealed class SealedEmployee permits FullTimeEmployee, ContractorEmployee {

    public void show() {
        System.out.println("In sealed class");
    }
}
