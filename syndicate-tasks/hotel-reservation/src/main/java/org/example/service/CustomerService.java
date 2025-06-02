package org.example.service;

import org.example.model.Customer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomerService {

    private static CustomerService customerService;
    private final Map<String, Customer> customerMap;

    private CustomerService() {
        this.customerMap = new HashMap<>();
    }

    public Map<String, Customer> getCustomerMap() {
        return customerMap;
    }

    public static CustomerService getCustomerService() {
        if(customerService==null)
            customerService = new CustomerService();
        return customerService;
    }

    public void addCustomer(String email, String firstName, String lastName) {
        customerMap.put(email, new Customer(firstName, lastName, email));
    }

    public Customer getCustomer(String email) {
        return customerMap.getOrDefault(email, null);
    }


    public Collection<Customer> getAllCustomers() {
        return customerMap.values();
    }
}
