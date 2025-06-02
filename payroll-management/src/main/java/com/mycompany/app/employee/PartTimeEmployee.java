package com.mycompany.app.employee;

import com.mycompany.app.payroll.PayrollComputationException;

public class PartTimeEmployee extends Employee{
    private int hoursWorked;
    private final PartTimeEmployeeContract contract;

    public PartTimeEmployee(int empID, String name, String department, double baseSalary, int hoursWorked) {
        super(empID, name, department, baseSalary);
        setHoursWorked(hoursWorked);
        designation = "PartTimeEmployee";
        this.contract = new PartTimeEmployeeContract(super.getDepartment(), hoursWorked);
    }


    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = Math.max(hoursWorked, 0);
    }

    public void getContract() {
        contract.viewContract();
    }

    @Override
    public void showEmployeeDetails() {
        super.showEmployeeDetails();
        System.out.println("Hours Worked: "+hoursWorked);
    }

//    @Override
//    public double salaryCalculation() {
//        return hoursWorked*getBaseSalary();
//    }
//
//    @Override
//    public void displaySalaryDetails() {
//        super.displaySalaryDetails();
//        System.out.println("Total Salary: "+salaryCalculation());
//    }

    public void divByZero() throws PayrollComputationException {
        try{
            int overtimePay = this.getHoursWorked()>10 ? (this.getHoursWorked()-10)*((int)this.getBaseSalary()/0):0;
        }
        catch (ArithmeticException ex) {
            throw new PayrollComputationException("Error in computing overtime pay: Division by Zero",ex);
        }
    }

    public static class PartTimeEmployeeBuilder extends EmployeeBuilder{

        protected int hoursWorked;

        @Override
        public PartTimeEmployeeBuilder setEmpID(int empID) {
            this.empID = empID;
            return this;
        }

        @Override
        public PartTimeEmployeeBuilder setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public PartTimeEmployeeBuilder setDepartment(String department) {
            this.department = department;
            return this;
        }

        @Override
        public PartTimeEmployeeBuilder setBaseSalary(double baseSalary) {
            this.baseSalary = baseSalary;
            return this;
        }

        public PartTimeEmployeeBuilder setHoursWorked(int hoursWorked) {
            this.hoursWorked = hoursWorked;
            return this;
        }

        @Override
        public PartTimeEmployee createEmployee() {
            return new PartTimeEmployee(empID, name, department, baseSalary, hoursWorked);
        }
    }
}
