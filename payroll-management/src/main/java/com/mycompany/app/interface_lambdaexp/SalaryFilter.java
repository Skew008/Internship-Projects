package com.mycompany.app.interface_lambdaexp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SalaryFilter<T extends Number> {

    private final Predicate<T> predicate;

    public SalaryFilter(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    public List<T> filterSalary(List<T> salaries) {
        List<T> filtered = new ArrayList<>();
        if(salaries==null || salaries.isEmpty())
            return filtered;
        for(T e:salaries)
        {
            if(predicate.test(e))
                filtered.add(e);
        }
        return filtered;
    }
}
