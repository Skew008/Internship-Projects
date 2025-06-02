package com.mycompany.app.interface_lambdaexp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class SalaryList {

    private final List<Double> salaries;

    public SalaryList() {
        this.salaries = new ArrayList<>();
    }

    public List<Double> getSalaries() {
        return salaries;
    }

    public void add(double s) {
        if(s < 0)
            throw new IllegalArgumentException("Salary cannot be negative");
        salaries.add(s);
    }

    public void sort(Comparator<Double> c) {
        if(c==null)
            throw new NullPointerException("Comparator cannot be null");
        Collections.sort(salaries, c);
    }

    public List<Double> filter(Predicate<Double> p) {
        if(p==null)
            throw new NullPointerException("Filtering Condition cannot be null");
        if(salaries.isEmpty())
            return salaries;
        List<Double> filtered = new ArrayList<>();
        for(double salary:salaries)
        {
            if(p.test(salary))
                filtered.add(salary);
        }
        return filtered;
    }

    public List<Double> map(Function<Double, Double> f) {
        if(f==null)
            throw new NullPointerException("Mapping function cannot be negative");
        if(salaries==null)
            throw new NullPointerException("No salaries to process");
        if(salaries.isEmpty())
            return salaries;
        List<Double> transformedSalaries = new ArrayList<>();
        for(double salary:salaries)
        {
            if(salary < 0)
                throw new IllegalArgumentException("Salary cannot be negative");
            transformedSalaries.add(f.apply(salary));
        }
        return transformedSalaries;
    }
}
