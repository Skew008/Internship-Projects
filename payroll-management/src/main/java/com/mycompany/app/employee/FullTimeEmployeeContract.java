package com.mycompany.app.employee;

public class FullTimeEmployeeContract extends EmployeeContract{

    public FullTimeEmployeeContract(String department) {
        super();
        super.project = assignProject(department);
    }

    @Override
    protected void defineWorkHours() {
        System.out.println("Working Hours: 9:00 AM - 6:00 PM");
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
