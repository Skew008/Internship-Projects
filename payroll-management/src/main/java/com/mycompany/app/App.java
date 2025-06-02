package com.mycompany.app;


import com.mycompany.app.employee.*;
import com.mycompany.app.interface_lambdaexp.Emp;
import com.mycompany.app.payroll.*;


import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
//        PartTimeEmployee p = new PartTimeEmployee(1, "J1", "CSE", 1000.0, 15);
//        FullTimeEmployee f = new FullTimeEmployee(2, "J2", "IT", 20000.0, 10000.0);
//
//        p.setHoursWorked(5);
//        f.setAnnualBonus(15000.0);
//        p.showEmployeeDetails();
//        System.out.println();
//        f.showEmployeeDetails();

//        Employee[] e = new Employee[5];
//        e[0] = new PartTimeEmployee(1, "A", "CSE", 500, 8);
//        e[1] = new PartTimeEmployee(2, "B", "CSE", 700, 5);
//        e[2] = new FullTimeEmployee(3, "C", "IT", 30000, 15000);
//        e[3] = new FullTimeEmployee(4, "D", "IT", 50000, 25000);
//        e[4] = new FullTimeEmployee(5, "E", "IT", 35000, 20000);
//
//        for(Employee ep: e)
//        {
//            ep.salaryCalculation();
//        }

//        FullTimeEmployee ft = new FullTimeEmployee(1, "Sam","CSE", 20000, 2000);
//        PartTimeEmployee pt = new PartTimeEmployee(2, "William", "IT", 400, 5);
//
//        ft.getContract();
//        System.out.println();
//        pt.getContract();

//        FullTimeEmployee ft = new FullTimeEmployee(1, "Sam","CSE", 20000, 2000);
//        PartTimeEmployee pt = new PartTimeEmployee(2, "William", "IT", 400, 5);
//
//        System.out.println("For a full-time employee:");
//        ft.displaySalaryDetails();
//        System.out.println();
//        System.out.println("For a part-time employee:");
//        pt.displaySalaryDetails();

//        EmployeeList empList = new EmployeeList();
//        empList.addEmployee(new PartTimeEmployee(2, "William", "IT", 400, 5));
//        empList.addEmployee(new FullTimeEmployee(1, "Sam","CSE", 20000, 2000));
//        for(Employee e:empList.getEmployeeList())
//            e.showEmployeeDetails();
//        System.out.println();
//        empList.removeEmployee(2);
//        for(Employee e:empList.getEmployeeList())
//            e.showEmployeeDetails();
//
//        EmployeeMap employeeMap = new EmployeeMap();
//        employeeMap.addEmployee(new PartTimeEmployee(2, "William", "IT", 400, 5));
//        employeeMap.addEmployee(new FullTimeEmployee(1, "Sam","CSE", 20000, 2000) );
//        employeeMap.removeEmployee(2);
//        employeeMap.getEmployee(1).showEmployeeDetails();

//        BenefitMapping benefitMapping = new BenefitMapping(List.of("FullTimeEmployee", "PartTimeEmployee"));
//        benefitMapping.setBenefits("FullTimeEmployee","Annual Bonus");
//        benefitMapping.setBenefits("PartTimeEmployee", "Hourly Pay");
//        System.out.println(benefitMapping.getBenefitsList());
//        System.out.println();
//        benefitMapping.getBenefitCategorically("PartTimeEmployee");
//        System.out.println();
//
//        EmployeeSet s = new EmployeeSet();
//        s.addEmployee(new PartTimeEmployee(2, "William", "IT", 400, 5));
//        s.addEmployee(new FullTimeEmployee(1, "Sam","CSE", 20000, 2000));
//        s.getEmployee(1);
//
//        PayRollQueue q = new PayRollQueue();
//        q.addEmployee(s.getEmployee(1));
//        try {
//            q.removeEmployee();
//            q.removeEmployee();
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }

//        List<Employee> l = new ArrayList<>();
//        l.add(new PartTimeEmployee(2, "William", "IT", 1000, 10));
//        l.add(new FullTimeEmployee(1, "Sam","CSE", 20000, 2000));
//
//        l.sort(new DepartmentComparator());
//        l.forEach(Employee::showEmployeeDetails);
//        System.out.println();
//
//        l.sort(new SalaryComparator());
//        l.forEach(Employee::showEmployeeDetails);
//        System.out.println();
//
//        Objects.requireNonNull(SearchEmployee.getByDepartment(l, "CSE")).forEach(Employee::showEmployeeDetails);
//        System.out.println();
//        Objects.requireNonNull(SearchEmployee.highestPaid(l)).showEmployeeDetails();


//        Payroll payroll = new Payroll();
//        payroll.printPayroll(List.of(p,f));
//        payroll.printPayroll(Set.of(p,f));
//        EmployeeSet employeeSet1 = new EmployeeSet();
//        employeeSet1.addEmployee(f);
//        payroll.printPayroll(employeeSet1.getEmployeeSet());
//
//        payroll.printAnnualBonus(List.of(p,f));
//        payroll.printAnnualBonus(Set.of(p,f));
//        EmployeeSet employeeSet2 = new EmployeeSet();
//        employeeSet2.addEmployee(p);
//        payroll.printAnnualBonus(employeeSet2.getEmployeeSet());
//        PartTimeEmployee e1 = new PartTimeEmployee(1, "J1", "CSE", 1000.0, 15);
//        try {
//            e1.divByZero();
//        } catch (PayrollComputationException e) {
//            System.out.println(e);
//        }

//        Emp e = new Emp(1, "Sam", "CSE", "Sample",20000.0);
//        RegularSalaryCalculator r = new RegularSalaryCalculator();
//        System.out.println(r.calculate(e.getBaseSalary()));
//
//        BonusCalculator b = (baseSalary) -> 0.1*baseSalary;
//        System.out.println(b.calculateBonus(e.getBaseSalary()));

//        SalaryCalculator s = (baseSalary) -> baseSalary*12;
//        BonusCalculator b = (baseSalary) -> 0.1*baseSalary;
//        System.out.println(totalCompensation(s,b,e.getBaseSalary()));

//        List<Double> salaries = List.of(10000.0, 20000.0, 25000.0, 15000.0);
//        SalaryFilter<Double> sf = new SalaryFilter<>((salary)->salary>15000.0);
//        sf.filterSalary(salaries).forEach(System.out::println);

//        VariableSalaryCalculator v = (baseSalary, percentage) -> baseSalary-(percentage*baseSalary);
//        System.out.println(variableSalary(v, e, 0.1));
//        System.out.println(variableSalary(v, e, 0.15));

//        SalaryCalculator s = (baseSalary) -> baseSalary*12;
//        System.out.println(s.calculate(e.getBaseSalary()));

//        MultiParameterBonusCalculator m = (baseSalary, rating) -> {
//            if(baseSalary < 0)
//                throw new IllegalArgumentException("Salary cannot be negative");
//            if(rating < 0)
//                throw new IllegalArgumentException("Rating cannot be negative");
//            return baseSalary * ((double) rating / 10) * 12;
//        };
//        System.out.println(m.calculate(e.getBaseSalary(), 7));

//        SalaryList salaries = new SalaryList();
//        salaries.add(12000.0);
//        salaries.add(24000.0);
//        salaries.add(10000.0);
//        salaries.add(30000.0);
//        salaries.add(25000.0);
//        salaries.sort((s1,s2) -> s1>s2 ? 1 : s2>s1 ? -1 : 0);
//        salaries.getSalaries().forEach(System.out::println);

//        salaries.filter((salary)-> salary>20000.0).forEach(System.out::println);

//        salaries.map((salary) -> salary*12).forEach(System.out::println);

//        List<Emp> employees = new ArrayList<>();
//        employees.add(new Emp(1, "Sam", "JAVA","SDE1", 2000));
//        employees.add(new Emp(2, "Ron", ".NET", "SDE2", 3000));
//        employees.add(new Emp(3, "Samuel", "JAVA", "Manager",5000));
//        employees.add(new Emp(4, "Saif", ".NET", "Manager", 6000));
//        employees.add(new Emp(5, "Daisy","Python", "Manager", 4000));
//        employees.add(new Emp(6, "Rony", "JAVA", "SDE1",2000));
//        employees.add(new Emp(7, "Rose", ".NET", "SDE2", 3000));
//        employees.add(new Emp(8, "Rosy","Python", "Manager", 4000));

        //        employees.stream()
//                .filter(emp -> emp.getRole().equals("Manager"))
//                .filter(emp -> emp.getBaseSalary()>=5000)
//                .forEach(emp -> System.out.println(emp.getEmpID()+" "+emp.getRole()+" "+emp.getBaseSalary()));

//        List<String> names = employees.stream()
//                .map(Emp::getName)
//                .toList();
//        names.forEach(System.out::println);

//        double allSalaries = employees.stream()
//                .map(Emp::getBaseSalary)
//                .reduce(0.0, ((c,e) -> c+e));
//        System.out.println(allSalaries);

//        Map<String, List<Emp>> groupedInDepartments = employees==null || employees.isEmpty() ? new HashMap<>() :
//                employees.stream()
//                .collect(Collectors.groupingBy(Emp::getDept));
//        groupedInDepartments.entrySet().forEach(System.out::println);

//        List<Double> salaries = employees==null || employees.isEmpty() ? new ArrayList<>() :
//                employees.stream()
//                .map(Emp::getBaseSalary)
//                .sorted((s1,s2) -> s2.compareTo(s1))
//                .toList();
//        salaries.forEach(System.out::println);

//        Emp emp1 = employees.stream()
//                .filter(emp -> emp.getBaseSalary() >= 5000)
//                .findFirst()
//                .orElseThrow();
//        System.out.println(emp1.getName()+" "+emp1.getBaseSalary());

//        long countEmployee = employees.stream()
//                .filter(emp -> emp.getBaseSalary()>=5000.0)
//                .count();
//        System.out.println(countEmployee);

//        List<Double> uniqueSalaries = employees==null || employees.isEmpty() ? new ArrayList<>() :
//                employees.stream().map(Emp::getBaseSalary).distinct().toList();
//        uniqueSalaries.forEach(System.out::println);

//        Set<String> names = employees==null || employees.isEmpty() ? new HashSet<>() :
//                employees.stream().map(Emp::getName).collect(Collectors.toSet());
//        names.forEach(System.out::println);

//        SealedEmployee e1 = new FullTimeEmployee();
//        e1.show();
//        e1 = new ContractorEmployee();
//        e1.show();

//        EmployeeRecord er = new EmployeeRecord("Sam", 2000);
//        System.out.println(er);

//        SalaryCalculator salaryCalculator = new SalaryCalculator();
//        salaryCalculator.displaySalaryDetails(p);
//        System.out.println();
//        salaryCalculator.displaySalaryDetails(f);

//        SalaryCalculator salaryCalculator = new SalaryCalculator();
//        salaryCalculator.setSalaryCalculationState(new FullTimeSalaryCalculation());
//        salaryCalculator.salaryCalculation(f);
//        System.out.println();
//        salaryCalculator.setSalaryCalculationState(new PartTimeSalaryCalculation());
//        salaryCalculator.salaryCalculation(p);

//        FullTimeEmployee f = new FullTimeEmployee(2, "J2", "CSE", 20000.0, 10000.0);
//        ContractorEmployee c = new ContractorEmployee(3, "John", "IT", 30000.0, 25);

//        f.showEmployeeDetails();
//        System.out.println();
//        c.showEmployeeDetails();

//        FullTimeSalaryCalculatorImpl fc = new FullTimeSalaryCalculatorImpl();
//        ContractorSalaryCalculatorImpl cc = new ContractorSalaryCalculatorImpl();
//
//        f.showEmployeeDetails();
//        System.out.println("Total salary:"+fc.salaryCalculation(f));
//        System.out.println();
//        c.showEmployeeDetails();
//        System.out.println("Total salary:"+cc.salaryCalculation(c));

//        Employee e = new FullTimeEmployee.FullTimeEmployeeBuilder()
//                .setName("Sam")
//                .setEmpID(1)
//                .setBaseSalary(30000.0)
//                .setDepartment("CSE")
//                .setBonus(10000.0)
//                .createEmployee();
//
//        e.showEmployeeDetails();
//        System.out.println();
//
//        e = new ContractorEmployee.ContractorEmployeeBuilder()
//                .setName("Ron")
//                .setEmpID(2)
//                .setDepartment("IT")
//                .setBaseSalary(3000)
//                .setHoursWorked(30)
//                .createEmployee();
//
//        e.showEmployeeDetails();
//        System.out.println();
//
//        e = new PartTimeEmployee.PartTimeEmployeeBuilder()
//                .setName("Shaun")
//                .setEmpID(3)
//                .setDepartment("CSE")
//                .setBaseSalary(2000)
//                .setHoursWorked(20)
//                .createEmployee();
//
//        e.showEmployeeDetails();

        FullTimeEmployee.FullTimeEmployeeBuilder fb =
                (FullTimeEmployee.FullTimeEmployeeBuilder) EmployeeFactory
                        .getEmployee("FullTimeEmployee");

        Employee e = fb.setName("Sam")
                .setEmpID(1)
                .setBaseSalary(30000.0)
                .setDepartment("CSE")
                .setBonus(10000.0)
                .createEmployee();

                e.showEmployeeDetails();
    }

//    private static double variableSalary(VariableSalaryCalculator v, Emp e, double deductionPercent) {
//        if(v==null)
//            throw new NullPointerException("Calculator cannot be null");
//        if(e==null)
//            throw new NullPointerException("Employee cannot be null");
//        if(deductionPercent<0)
//            throw new IllegalArgumentException("Deductions cannot be negative");
//        if(deductionPercent>0.3)
//            throw new RuntimeException("Deductions cannot be greater than 30%");
//        return v.calculate(e.getBaseSalary(),deductionPercent);
//    }

//    private static double totalCompensation(SalaryCalculator s, BonusCalculator b, double baseSalary) {
//        if(b==null && s==null)
//            throw new NullPointerException("SalaryCalculator and BonusCalculator object not present");
//        if(s==null)
//            throw new NullPointerException("SalaryCalculator object not present");
//        if(b==null)
//            throw new NullPointerException("BonusCalculator object not present");
//        if(baseSalary < 0)
//            throw new IllegalArgumentException("Salary cannot be negative");
//        return s.calculate(baseSalary)+b.calculateBonus(baseSalary);
//    }
}