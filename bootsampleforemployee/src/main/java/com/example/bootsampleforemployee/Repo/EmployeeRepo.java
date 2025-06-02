package com.example.bootsampleforemployee.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Integer> {

    @Query(nativeQuery = true,
            value = "select avg(salary) from employee group by department having department= :department")
    Optional<Double> findAverageSalaryByDepartment(@Param("department") String department);

    @Query(nativeQuery = true,
            value = "select * from employee order by salary desc limit :n")
    List<Employee> topNHighestPaidEmployees(@Param("n") int n);

    @Query(nativeQuery = true,
            value = "select distinct(designation) from employee")
    List<String> findDesignations();
}
