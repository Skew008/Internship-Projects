package com.example.bootsampleforemployee;

import com.example.bootsampleforemployee.ExceptionHandling.EmployeeIdMismatchException;
import com.example.bootsampleforemployee.ExceptionHandling.EmployeeNotFoundException;
import com.example.bootsampleforemployee.Repo.Employee;
import com.example.bootsampleforemployee.Repo.EmployeeRepo;
import com.example.bootsampleforemployee.Service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEmployees() {
        List<Employee> mockEmployees = Arrays.asList(
                new Employee(1, "John", "IT", "Developer", 60000, Date.valueOf(LocalDate.now())),
                new Employee(2, "Jane", "HR", "Manager", 80000, Date.valueOf(LocalDate.now()))
        );
        when(employeeRepo.findAll()).thenReturn(mockEmployees);

        List<Employee> employees = employeeService.getEmployees();

        assertEquals(2, employees.size());
        verify(employeeRepo, times(1)).findAll();
    }

    @Test
    void testGetEmployee() {
        Employee mockEmployee = new Employee(1, "John", "IT", "Developer", 60000, Date.valueOf(LocalDate.now()));
        when(employeeRepo.findById(1)).thenReturn(Optional.of(mockEmployee));

        Employee employee = employeeService.getEmployee(1);

        assertNotNull(employee);
        assertEquals("John", employee.getName());
        assertEquals("Developer", employee.getDesignation());
        verify(employeeRepo, times(1)).findById(1);
    }

    @Test
    void testGetEmployeeNotFound() {
        when(employeeRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployee(1));
        verify(employeeRepo, times(1)).findById(1);
    }

    @Test
    void testAddEmployee() {
        Employee mockEmployee = new Employee(1, "John", "IT", "Developer", 60000, Date.valueOf(LocalDate.now()));
        when(employeeRepo.save(mockEmployee)).thenReturn(mockEmployee);

        Employee employee = employeeService.addEmployee(mockEmployee);

        assertNotNull(employee);
        assertEquals(1, employee.getId());
        assertEquals(60000, employee.getSalary());
        verify(employeeRepo, times(1)).save(mockEmployee);
    }

    @Test
    void testDeleteEmployee() {
        Employee mockEmployee = new Employee(1, "John", "IT", "Developer", 60000, Date.valueOf(LocalDate.now()));
        when(employeeRepo.findById(1)).thenReturn(Optional.of(mockEmployee));

        employeeService.deleteEmployee(1);

        verify(employeeRepo, times(1)).findById(1);
        verify(employeeRepo, times(1)).deleteById(1);
    }

    @Test
    void testDeleteEmployeeNotFound() {
        when(employeeRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(1));
        verify(employeeRepo, times(1)).findById(1);
    }

    @Test
    void testUpdateEmployee() {
        Employee mockEmployee = new Employee(1, "John", "IT", "Developer", 60000, Date.valueOf(LocalDate.now()));
        when(employeeRepo.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(employeeRepo.save(mockEmployee)).thenReturn(mockEmployee);

        Employee updatedEmployee = employeeService.updateEmployee(1, mockEmployee);

        assertNotNull(updatedEmployee);
        assertEquals(1, updatedEmployee.getId());
        assertEquals("Developer", updatedEmployee.getDesignation());
        verify(employeeRepo, times(1)).findById(1);
        verify(employeeRepo, times(1)).save(mockEmployee);
    }

    @Test
    void testUpdateEmployeeIdMismatch() {
        Employee mockEmployee = new Employee(2, "John", "IT", "Developer", 60000, Date.valueOf(LocalDate.now()));

        assertThrows(EmployeeIdMismatchException.class, () -> employeeService.updateEmployee(1, mockEmployee));
        verify(employeeRepo, never()).findById(anyInt());
    }

    @Test
    void testGetEmployeesGroupedByDepartment() {
        Employee emp1 = new Employee(1, "John", "IT", "Developer", 60000, Date.valueOf(LocalDate.now()));
        Employee emp2 = new Employee(2, "Jane", "HR", "Manager", 80000, Date.valueOf(LocalDate.now()));
        when(employeeRepo.findAll()).thenReturn(Arrays.asList(emp1, emp2));

        Map<String, List<Employee>> groupedEmployees = employeeService.getEmployeesGroupedByDepartment();

        assertEquals(2, groupedEmployees.size());
        assertTrue(groupedEmployees.containsKey("IT"));
        assertTrue(groupedEmployees.containsKey("HR"));
        verify(employeeRepo, times(1)).findAll();
    }

    @Test
    void testFindEmployeesHiredInLastNMonths() {
        Employee emp1 = new Employee(1, "John", "IT", "Developer", 60000, Date.valueOf(LocalDate.now().minusMonths(1)));
        Employee emp2 = new Employee(2, "Jane", "HR", "Manager", 80000, Date.valueOf(LocalDate.now().minusMonths(3)));
        when(employeeRepo.findAll()).thenReturn(Arrays.asList(emp1, emp2));

        List<Employee> employees = employeeService.findEmployeesHiredInLastNMonths(2);

        assertEquals(1, employees.size());
        assertEquals("John", employees.get(0).getName());
        assertEquals("Developer", employees.get(0).getDesignation());
        verify(employeeRepo, times(1)).findAll();
    }

    @Test
    void testGetEmployeeThrowsEmployeeNotFoundException() {
        when(employeeRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployee(1));
        verify(employeeRepo, times(1)).findById(1);
    }

    @Test
    void testDeleteEmployeeThrowsEmployeeNotFoundException() {
        when(employeeRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(1));
        verify(employeeRepo, times(1)).findById(1);
    }

    @Test
    void testUpdateEmployeeThrowsEmployeeIdMismatchException() {
        Employee mockEmployee = new Employee(2, "John", "IT", "Developer", 60000, Date.valueOf(LocalDate.now()));

        assertThrows(EmployeeIdMismatchException.class, () -> employeeService.updateEmployee(1, mockEmployee));
        verify(employeeRepo, never()).findById(anyInt());
    }

    @Test
    void testFindEmployeesHiredInLastNMonthsThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> employeeService.findEmployeesHiredInLastNMonths(-1));
        assertThrows(IllegalArgumentException.class, () -> employeeService.findEmployeesHiredInLastNMonths(13));
    }

    @Test
    void testFilterByDepartmentReturnsOnlyMatchingEmployees() {
        Employee emp1 = new Employee(1, "John", "IT", "Developer", 60000, Date.valueOf(LocalDate.now()));
        Employee emp2 = new Employee(2, "Jane", "HR", "Manager", 80000, Date.valueOf(LocalDate.now()));
        when(employeeRepo.findAll()).thenReturn(Arrays.asList(emp1, emp2));

        List<Employee> itEmployees = employeeService.getEmployees()
                .stream()
                .filter(emp -> "IT".equals(emp.getDepartment()))
                .toList();

        assertEquals(1, itEmployees.size(), "List size should match the expected number of employees in IT department");
        assertEquals("John", itEmployees.get(0).getName(), "Employee name should match the expected value");
        assertEquals("Developer", itEmployees.get(0).getDesignation(), "Employee designation should match the expected value");
        verify(employeeRepo, times(1)).findAll();
    }

    @Test
    void testFilterByNonExistentDepartmentReturnsEmptyList() {
        Employee emp1 = new Employee(1, "John", "IT", "Developer", 60000, Date.valueOf(LocalDate.now()));
        Employee emp2 = new Employee(2, "Jane", "HR", "Manager", 80000, Date.valueOf(LocalDate.now()));
        when(employeeRepo.findAll()).thenReturn(Arrays.asList(emp1, emp2));

        List<Employee> financeEmployees = employeeService.getEmployees()
                .stream()
                .filter(emp -> "Finance".equals(emp.getDepartment()))
                .toList();

        assertTrue(financeEmployees.isEmpty(), "List should be empty for a non-existent department");
        verify(employeeRepo, times(1)).findAll();
    }

    @Test
    void testFilterByDepartmentWithEmptyEmployeeList() {
        when(employeeRepo.findAll()).thenReturn(List.of());

        List<Employee> employees = employeeService.getEmployees()
                .stream()
                .filter(emp -> "IT".equals(emp.getDepartment()))
                .toList();

        assertTrue(employees.isEmpty(), "List should be empty when no employees exist");
        verify(employeeRepo, times(1)).findAll();
    }
}