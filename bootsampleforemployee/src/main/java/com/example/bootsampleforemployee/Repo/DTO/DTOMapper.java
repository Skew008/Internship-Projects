package com.example.bootsampleforemployee.Repo.DTO;

import com.example.bootsampleforemployee.Repo.Employee;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {

    public static EmployeeDTO toEmployeeDTO(Employee e) {
        return new EmployeeDTO(e.getId(), e.getName(), e.getDepartment(), e.getDesignation());
    }
}
