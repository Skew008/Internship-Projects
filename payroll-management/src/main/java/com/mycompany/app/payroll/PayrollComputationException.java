package com.mycompany.app.payroll;

public class PayrollComputationException extends Exception{
    public PayrollComputationException(String message, ArithmeticException cause) {
        super(message);
        cause.printStackTrace();
    }
}
