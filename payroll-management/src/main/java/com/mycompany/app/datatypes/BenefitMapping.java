package com.mycompany.app.datatypes;

import java.util.*;

public class BenefitMapping {

    private final HashMap<String, Set<String>> m;

    public BenefitMapping(List<String> categories) {
        this.m = new HashMap<>();
        for(String categogry:categories)
            m.put(categogry, new HashSet<>());
    }

    public HashMap<String, Set<String>> getBenefitsList() {
        return m;
    }

    public void setBenefits(String  employeeCategory, String benefit) {
        if(!m.containsKey(employeeCategory))
            throw new NoSuchElementException("Category not found, add if required");
        m.get(employeeCategory).add(benefit);
    }

    public void removeBenefit(String employeeCategory, String benefit) {
        if(!m.containsKey(employeeCategory))
            throw new NoSuchElementException("Category does not exist");
        if(!m.get(employeeCategory).contains(benefit))
            throw new NoSuchElementException("No such benefit in this category");
        m.get(employeeCategory).remove(benefit);
    }


    public List<String> getBenefitCategorically(String employeeCategory) {
        if(!m.containsKey(employeeCategory))
            throw new NoSuchElementException("Category does not exist");
        return new ArrayList<>(m.get(employeeCategory));
    }

    public void removeBenefitCategory(String employeeCategory) {
        if(!m.containsKey(employeeCategory))
            throw new NoSuchElementException("Category does not exist");
        m.remove(employeeCategory);
    }

}
