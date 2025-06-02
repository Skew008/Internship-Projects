package com.mycompany.app.employee;

abstract public class EmployeeContract {

    protected String project;

    public EmployeeContract() {}
    abstract protected void defineWorkHours();
    abstract protected String assignProject(String department);
    abstract void viewContract();
}
