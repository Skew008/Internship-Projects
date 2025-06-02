package com.mycompany.app.interface_lambdaexp;

public class Emp {
    private int empID;
    private String name;
    private String dept;
    private String role;
    private double baseSalary;

    public Emp(int empID, String name, String dept, String role, double baseSalary) {
        setEmpID(empID);
        setName(name);
        setDept(dept);
        setRole(role);
        setBaseSalary(baseSalary);
    }

    public int getEmpID() {
        return empID;
    }

    public void setEmpID(int empID) {
        if(empID < 0)
            throw new IllegalArgumentException("Emp id cannot be negative");
        this.empID = empID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name==null)
            throw new NullPointerException("Name cannot be null");
        if(name.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if(role==null)
            throw new NullPointerException("Role cannot be null");
        if(role.isEmpty())
            throw new IllegalArgumentException("Role cannot be empty");
        this.role = role;
    }
    
    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        if(dept==null)
            throw new NullPointerException("Name cannot be null");
        if(dept.isEmpty())
            throw new IllegalArgumentException("Name cannot be empty");
        this.dept = dept;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        if(baseSalary < 0)
            throw new IllegalArgumentException("Base salary cannot be negative");
        this.baseSalary = baseSalary;
    }
}
