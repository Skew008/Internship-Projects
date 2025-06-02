package com.example.bootsampleforemployee;

import com.example.bootsampleforemployee.Controller.EmployeeController;
import com.example.bootsampleforemployee.Repo.DTO.EmployeeDTO;
import com.example.bootsampleforemployee.Repo.Employee;
import com.example.bootsampleforemployee.Repo.Payroll;
import com.example.bootsampleforemployee.Service.EmployeeService;
import com.example.bootsampleforemployee.Service.PayrollService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private PayrollService payrollService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetEmployees() throws Exception {
        List<EmployeeDTO> employees = Arrays.asList(
                new EmployeeDTO(1, "John Doe", "Engineering", "Developer"),
                new EmployeeDTO(2, "Jane Smith", "HR", "Manager")
        );

        Mockito.when(employeeService.getEmployees()).thenReturn(Arrays.asList(
                new Employee(1, "John Doe", "Engineering", "Developer", 50000, Date.valueOf("2022-01-01")),
                new Employee(2, "Jane Smith", "HR", "Manager", 70000, Date.valueOf("2021-05-20"))
        ));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].name").value("John Doe"))
                .andExpect(jsonPath("$.[0].department").value("Engineering"))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$.[1].designation").value("Manager"));
    }

    @Test
    void testAddEmployee() throws Exception {
        Employee newEmployee = new Employee(0, "Jane Doe", "Marketing", "Analyst", 60000, Date.valueOf("2023-01-01"));
        EmployeeDTO expectedEmployeeDTO = new EmployeeDTO(1, "Jane Doe", "Marketing", "Analyst");

        Mockito.when(employeeService.addEmployee(any(Employee.class)))
                .thenReturn(new Employee(1, "Jane Doe", "Marketing", "Analyst", 60000, Date.valueOf("2023-01-01")));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedEmployeeDTO.getId()))
                .andExpect(jsonPath("$.name").value(expectedEmployeeDTO.getName()))
                .andExpect(jsonPath("$.department").value(expectedEmployeeDTO.getDepartment()))
                .andExpect(jsonPath("$.designation").value(expectedEmployeeDTO.getDesignation()));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        Mockito.doNothing().when(employeeService).deleteEmployee(anyInt());

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetEmployeeById() throws Exception {
        Employee employee = new Employee(1, "John Doe", "Engineering", "Developer", 50000, Date.valueOf("2020-01-01"));
        EmployeeDTO employeeDTO = new EmployeeDTO(1, "John Doe", "Engineering", "Developer");

        Mockito.when(employeeService.getEmployee(1)).thenReturn(employee);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(employeeDTO.getId()))
                .andExpect(jsonPath("$.name").value(employeeDTO.getName()))
                .andExpect(jsonPath("$.department").value(employeeDTO.getDepartment()))
                .andExpect(jsonPath("$.designation").value(employeeDTO.getDesignation()));
    }

    @Test
    void testGetEmployeesGroupedByDepartment() throws Exception {
        List<Employee> engineeringEmployees = Arrays.asList(
                new Employee(1, "John Doe", "Engineering", "Developer", 50000, Date.valueOf("2021-01-01"))
        );

        Mockito.when(employeeService.getEmployeesGroupedByDepartment())
                .thenReturn(Collections.singletonMap("Engineering", engineeringEmployees));

        mockMvc.perform(get("/api/employees/grouped-by-department"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Engineering[0].name").value("John Doe"))
                .andExpect(jsonPath("$.Engineering[0].department").value("Engineering"));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        Employee updatedEmployee = new Employee(1, "John Doe", "Engineering", "Team Lead", 80000, Date.valueOf("2020-01-01"));
        EmployeeDTO expectedEmployeeDTO = new EmployeeDTO(1, "John Doe", "Engineering", "Team Lead");

        Mockito.when(employeeService.updateEmployee(anyInt(), any(Employee.class))).thenReturn(updatedEmployee);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEmployee)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedEmployeeDTO.getId()))
                .andExpect(jsonPath("$.name").value(expectedEmployeeDTO.getName()))
                .andExpect(jsonPath("$.department").value(expectedEmployeeDTO.getDepartment()))
                .andExpect(jsonPath("$.designation").value(expectedEmployeeDTO.getDesignation()));
    }


    @Test
    void testCalculatePayrollByJobTitle() throws Exception {
        String responseText = "Total payroll for Developers: $300,000";
        Mockito.when(payrollService.calculatePayrollByJobTitle(anyString())).thenReturn(responseText);

        mockMvc.perform(get("/api/employees/payroll/job-title/Developer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(responseText));
    }
}