package com.mycompany.app.employee;

public class EmployeeFactory {

    public static Employee.EmployeeBuilder getEmployee(String employeeType) {
        return switch (employeeType) {
            case "FullTimeEmployee" -> new FullTimeEmployee.FullTimeEmployeeBuilder();
            case "PartTimeEmployee" -> new PartTimeEmployee.PartTimeEmployeeBuilder();
            case "ContractorEmployee" -> new ContractorEmployee.ContractorEmployeeBuilder();
            default -> throw new AssertionError("Wrong employee type given");
        };
    }
}
