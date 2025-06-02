package com.example.bootsampleforemployee.Controller;

import com.example.bootsampleforemployee.Repo.DTO.DTOMapper;
import com.example.bootsampleforemployee.Repo.Employee;
import com.example.bootsampleforemployee.Repo.DTO.EmployeeDTO;
import com.example.bootsampleforemployee.Repo.Payroll;
import com.example.bootsampleforemployee.Service.EmployeeService;
import com.example.bootsampleforemployee.Service.PayrollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("api/employees")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    PayrollService payrollService;

    private final Logger logger = LoggerFactory.getLogger("EmployeeController");

    @GetMapping
    public List<EmployeeDTO> getEmployees() {
        logger.info("Get request for fetching all the employees");
        return employeeService.getEmployees()
                .stream()
                .map(DTOMapper::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/top-salaries/{n}")
    public List<EmployeeDTO> getTopPaidEmployees(@PathVariable int n) {
        logger.info("Get request for employees with top {} salaries", n);
        return payrollService.getTopNHighestPaidEmployees(n)
                .stream()
                .map(DTOMapper::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/grouped-by-department")
    public Map<String , List<EmployeeDTO>> getEmployeesGroupedByDepartment() {
        logger.info("Get request for employees grouped by department");
        return employeeService.getEmployeesGroupedByDepartment()
        .values()
        .stream()
        .flatMap(Collection::stream)
        .map(DTOMapper::toEmployeeDTO)
        .collect(Collectors.groupingBy(EmployeeDTO::getDepartment));
    }

    @GetMapping("/payroll/job-title/{jobTitle}")
    public String calculatePayrollByJobTitle(@PathVariable String jobTitle) {
        logger.info("Get request for payroll calculation according to job title: {}", jobTitle);
        return payrollService.calculatePayrollByJobTitle(jobTitle.toUpperCase());
    }

    @GetMapping("/hired-in-last/{months}")
    public List<EmployeeDTO> findEmployeesHiredInLastNMonths(@PathVariable int months) {
        logger.info("Get request for employees that were hired in last {} months", months);
        return employeeService.findEmployeesHiredInLastNMonths(months)
                .stream()
                .map(DTOMapper::toEmployeeDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EmployeeDTO getEmployee(@PathVariable int id) {
        logger.info("Get request for employee with id: {}",id);
        return DTOMapper.toEmployeeDTO(employeeService.getEmployee(id));
    }

    @GetMapping("/payroll")
    public List<Payroll> calculatePayroll() {
        logger.info("Get request for calculating the payroll of all the employees");
        return payrollService.calculatePayroll();
    }

    @GetMapping("/department/{departmentName}/average-salary")
    public String avgSalaryByDepartment(@PathVariable String departmentName) {
        logger.info("Get request for calculating average salary of {} department", departmentName);
        return payrollService.calculateAverageSalaryByDepartment(departmentName.toUpperCase());
    }

    @PostMapping(consumes = "application/json")
    public EmployeeDTO addEmployee(@RequestBody Employee e) {
        logger.info("Post request for adding an employee to the database");
        return DTOMapper.toEmployeeDTO(employeeService.addEmployee(e));
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable int id) {
        logger.info("Delete request for employee id: {}", id);
        employeeService.deleteEmployee(id);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public EmployeeDTO updateEmployee(@PathVariable int id, @RequestBody Employee e) {
        logger.info("Put request for employee id: {}", id);
        return DTOMapper.toEmployeeDTO(employeeService.updateEmployee(id, e));
    }
}