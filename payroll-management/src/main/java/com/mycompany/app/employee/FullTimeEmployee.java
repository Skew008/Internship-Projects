package com.mycompany.app.employee;


public class FullTimeEmployee extends Employee {
    private double bonus;
    private final FullTimeEmployeeContract contract;

    public FullTimeEmployee(int empID, String name, String department, double baseSalary, double bonus) {
        super(empID, name, department, baseSalary);
        setBonus(bonus);
        designation = "FullTimeEmployee";
        this.contract = new FullTimeEmployeeContract(super.getDepartment());
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        try {
            if(bonus<0)
                throw new AssertionError("Bonus cannot be negative");
            else
                this.bonus = bonus;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void getContract() {
        contract.viewContract();
    }

    @Override
    public void showEmployeeDetails() {
        super.showEmployeeDetails();
        System.out.println("Bonus: "+bonus);
//        System.out.println("Total Salary: "+salaryCalculation());
    }

//    @Override
//    public double salaryCalculation() {
//        return (getBaseSalary()+bonus);
//    }
//
//    @Override
//    public void displaySalaryDetails() {
//        super.displaySalaryDetails();
//        System.out.println("Annual Bonus: "+bonus);
//        System.out.println("Total Salary: "+salaryCalculation());
//    }

    public static class FullTimeEmployeeBuilder extends EmployeeBuilder{

        protected double bonus;

        @Override
        public FullTimeEmployeeBuilder setEmpID(int empID) {
            this.empID = empID;
            return this;
        }

        @Override
        public FullTimeEmployeeBuilder setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public FullTimeEmployeeBuilder setDepartment(String department) {
            this.department = department;
            return this;
        }

        @Override
        public FullTimeEmployeeBuilder setBaseSalary(double baseSalary) {
            this.baseSalary = baseSalary;
            return this;
        }

        public FullTimeEmployeeBuilder setBonus(double bonus) {
            this.bonus = bonus;
            return this;
        }

        @Override
        public FullTimeEmployee createEmployee() {
            return new FullTimeEmployee(empID, name, department, baseSalary, bonus);
        }
    }
}
