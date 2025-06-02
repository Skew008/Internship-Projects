package com.mycompany.app.employee;

public class PartTimeEmployeeContract extends EmployeeContract{

    private int hours;
    public PartTimeEmployeeContract(String department, int hours) {
        super();
        super.project = assignProject(department);
        this.hours = hours;
    }

    @Override
    protected void defineWorkHours() {
        System.out.println("Any time between 9:00 AM - 6:00 PM for "+hours);
    }

    @Override
    protected String assignProject(String department) {
        return switch (department) {
            case "CSE" -> "Computer Related";
            case "IT" -> "Maintenance Related";
            default -> throw new AssertionError();
        };
    }

    @Override
    public void viewContract() {
        defineWorkHours();
        System.out.println("Project: "+super.project);
    }
}
