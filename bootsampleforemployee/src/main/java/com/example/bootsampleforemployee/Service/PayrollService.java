package com.example.bootsampleforemployee.Service;

import com.example.bootsampleforemployee.ExceptionHandling.DepartmentNotFoundException;
import com.example.bootsampleforemployee.Repo.Employee;
import com.example.bootsampleforemployee.Repo.EmployeeRepo;
import com.example.bootsampleforemployee.Repo.Payroll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayrollService {

    @Autowired
    EmployeeRepo employeeRepo;

    private final Logger logger = LoggerFactory.getLogger("PayrollService");

    public List<Payroll> calculatePayroll() {
        logger.info("Processing payroll...");
        List<Payroll> payrolls = employeeRepo.findAll()
                .stream()
                .map(Payroll::calculatePayroll)
                .collect(Collectors.toList());
        logger.info("Payroll processing done");
        return payrolls;
    }

    public String calculateAvgByDept(String department) {
        logger.info("Processing salary by department {}", department);
        double avgSalary =
                employeeRepo.findAverageSalaryByDepartment(department)
                        .orElseThrow(DepartmentNotFoundException::new);
        logger.info("Processed average salary of {} = {}", department, avgSalary);
        return department+" : "+avgSalary;
    }

    public String calculateAverageSalaryByDepartment(String department) {
        logger.info("Processing salary by department {}", department);
        double avgSalaryByDepartment =
                employeeRepo.findAll()
                        .stream()
                        .filter(employee -> employee.getDepartment().equals(department))
                        .mapToDouble(Employee::getSalary)
                        .average()
                        .orElseThrow(DepartmentNotFoundException::new);
        logger.info("Processed average salary of {} = {}", department, avgSalaryByDepartment);
        return department+" : "+avgSalaryByDepartment;
    }

    public List<Employee> topNHighestPaidEmployees(int n) {
        logger.info("Checking validation for input n: {}", n);
        if(n<0)
            throw new IllegalArgumentException("Limit cannot be negative");
        logger.info("Searching for {} highest paying employees", n);
        List<Employee> employees = employeeRepo.topNHighestPaidEmployees(n);
        logger.info("Employees with highest payed salaries {}", employees.toString());
        return employees;
    }

    public List<Employee> getTopNHighestPaidEmployees(int n) {
        logger.info("Checking validation for input n: {}", n);
        if(n<0)
            throw new IllegalArgumentException("Limit cannot be negative");
        logger.info("Searching for {} highest paying employees", n);
        List<Employee> employees = employeeRepo.findAll()
                .stream()
                .sorted((e1, e2) -> (int) (e2.getSalary() - e1.getSalary()))
                .limit(n)
                .collect(Collectors.toList());
        logger.info("Employees with highest payed salaries {}", employees.toString());
        return employees;
    }

    public String calculatePayrollByJobTitle(String jobTitle) {
        logger.info("Checking input...");
        if(jobTitle==null || jobTitle.isEmpty())
            throw new NullPointerException("Job Title cannot be empty");
        logger.info("Searching for title: {}", jobTitle);
        if(employeeRepo.findDesignations().stream().noneMatch(s -> s.equals(jobTitle)))
            throw new IllegalArgumentException("Job Title does not exist");

        logger.info("Processing payroll...");
        double sum = employeeRepo.findAll()
                .stream()
                .filter(employee -> employee.getDesignation().equals(jobTitle))
                .mapToDouble((employee)-> employee.getSalary()*12+Payroll.bonus(employee.getDesignation()))
                .sum();
        logger.info("Payroll processed: {}", sum);
        return jobTitle+" : "+sum;
    }
}
