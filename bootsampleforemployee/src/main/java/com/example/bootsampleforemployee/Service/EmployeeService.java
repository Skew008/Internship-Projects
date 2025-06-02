package com.example.bootsampleforemployee.Service;

import com.example.bootsampleforemployee.ExceptionHandling.EmployeeIdMismatchException;
import com.example.bootsampleforemployee.ExceptionHandling.EmployeeNotFoundException;
import com.example.bootsampleforemployee.Repo.Employee;
import com.example.bootsampleforemployee.Repo.EmployeeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepo employeeRepo;

    public List<Employee> getEmployees()
    {
        logger.info("Fetching employees...");
        List<Employee> employees = employeeRepo.findAll();
        logger.info("Sending employees...");
        return employees;
    }

    private final Logger logger = LoggerFactory.getLogger("EmployeeService");

    public Employee getEmployee(int id) {
        logger.info("Searching employee with id: {}", id);
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(EmployeeNotFoundException::new);
        logger.info("Employee fetched");
        return employee;
    }

    public Employee addEmployee(Employee e) {
        logger.info("Creating employee...");
        Objects.requireNonNull(e);
        Employee employee = employeeRepo.save(e);
        logger.info("Employee created, id: {}", employee.getId());
        return employee;
    }

    public void deleteEmployee(int id) {
        logger.info("Searching employee with id: {}", id);
        employeeRepo.findById(id)
                .orElseThrow(EmployeeNotFoundException::new);
        employeeRepo.deleteById(id);
        logger.info("Employee with id: {} deleted", id);
    }

    public Employee updateEmployee(int id, Employee e) {
        logger.info("Checking id");
        if(id!=e.getId())
            throw new EmployeeIdMismatchException();
        logger.info("Searching employee with id: {}", id);
        employeeRepo.findById(id)
                .orElseThrow(EmployeeNotFoundException::new);

        Employee employee = employeeRepo.save(e);
        logger.info("Employee with id: {} updated", id);
        return employee;
    }

    public Map<String, List<Employee>> getEmployeesGroupedByDepartment() {
        logger.info("Searching employees...");
        Map<String, List<Employee>> employees = employeeRepo.findAll()
                .stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));
        logger.info("Mapped employee according to departments");
        return employees;
    }


    public List<Employee> findEmployeesHiredInLastNMonths(int months) {
        logger.info("Checking validation for input, months: {}", months);
        if(months < 0 || months>LocalDate.now().getMonthValue())
            throw new IllegalArgumentException("Months cannot be negative or greater than current time");

        LocalDate nMonthsBack = LocalDate.now().minusMonths(months);

        logger.info("Searching for employees");
        List<Employee> employees = employeeRepo.findAll()
                .stream()
                .filter(employee -> employee.getDateOfJoining().toLocalDate().isAfter(nMonthsBack))
                .collect(Collectors.toList());
        logger.info("Employees for last {} months", months);
        return employees;
    }
}
