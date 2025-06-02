package com.mycompany.absclassinterface;

public class Contractor extends EmploymentType implements Payable {

    private String in;
    private String out;
    public Contractor(String name, double pay, String in, String out) {
        super(name, pay);
        setIn(in);
        setOut(out);
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        if(in==null)
            throw new AssertionError("In time cannot be null");
        else
            this.in = in;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        if(out==null)
            throw new AssertionError("Out time cannot be null");
        else
            this.out = out;
    }

    @Override
    public int calculateWorkHours() {
        int in = time(getIn());
        int out = time(getOut());
        return (int) Math.ceil((out-in)/60.0);
    }

    @Override
    public double calculatePay() {
        return calculateWorkHours()*getPay();
    }

    private int time(String s) {
        String[] st = s.split(":");
        return Integer.parseInt(st[0])*60+Integer.parseInt(st[1]);
    }
}
