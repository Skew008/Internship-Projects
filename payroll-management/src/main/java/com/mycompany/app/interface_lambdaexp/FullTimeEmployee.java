package com.mycompany.app.interface_lambdaexp;

public non-sealed class FullTimeEmployee extends SealedEmployee{
    @Override
    public void show() {
        super.show();
        System.out.println("In FullTimeEmployee which is non-sealed");
    }
}
