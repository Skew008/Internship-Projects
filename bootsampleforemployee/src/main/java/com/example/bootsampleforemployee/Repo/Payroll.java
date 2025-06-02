package com.example.bootsampleforemployee.Repo;

import com.example.bootsampleforemployee.Repo.DTO.DTOMapper;
import com.example.bootsampleforemployee.Repo.DTO.EmployeeDTO;

import java.util.Objects;


public class Payroll {

    private EmployeeDTO employee;
    private double payroll;

    private Payroll(EmployeeDTO e, double p) {
        setEmployee(e);
        setPayroll(p);
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        Objects.requireNonNull(employee);
        this.employee = employee;
    }

    public double getPayroll() {
        return payroll;
    }

    public void setPayroll(double payroll) {
        if(payroll < 0)
            throw new IllegalArgumentException("Payroll cannot be negative");
        this.payroll = payroll;
    }

    public static Payroll calculatePayroll(Employee employee) {
        Objects.requireNonNull(employee);

        return new Payroll(DTOMapper.toEmployeeDTO(employee),
                employee.getSalary()*12+bonus(employee.getDesignation()));
    }

    public static double bonus(String designation) {
        return switch (designation) {
            case "JSE" -> 2000.0;
            case "SE" -> 3000.0;
            case "SSE" -> 4000.0;
            case "LSE" -> 5000.0;
            default -> 1000.0;
        };
    }
}
