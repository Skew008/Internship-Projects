package com.mycompany.app.employee;

public class ContractorEmployee extends Employee{

    private int hoursWorked;

    public ContractorEmployee(int empID, String name, String department, double baseSalary, int hoursWorked) {
        super(empID, name, department, baseSalary);
        setHoursWorked(hoursWorked);
        this.designation = "ContractorEmployee";
    }


    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        if(hoursWorked < 0)
            throw new IllegalArgumentException("Hours cannot be negative");
        this.hoursWorked = hoursWorked;
    }

    @Override
    public void showEmployeeDetails() {
        super.showEmployeeDetails();
        System.out.println("Hours worked: "+hoursWorked);
    }

    public static class ContractorEmployeeBuilder extends EmployeeBuilder{

        protected int hoursWorked;

        @Override
        public ContractorEmployeeBuilder setEmpID(int empID) {
            this.empID = empID;
            return this;
        }

        @Override
        public ContractorEmployeeBuilder setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public ContractorEmployeeBuilder setDepartment(String department) {
            this.department = department;
            return this;
        }

        @Override
        public ContractorEmployeeBuilder setBaseSalary(double baseSalary) {
            this.baseSalary = baseSalary;
            return this;
        }

        public ContractorEmployeeBuilder setHoursWorked(int hoursWorked) {
            this.hoursWorked = hoursWorked;
            return this;
        }

        @Override
        public ContractorEmployee createEmployee() {
            return new ContractorEmployee(empID, name, department, baseSalary, hoursWorked);
        }
    }
}
