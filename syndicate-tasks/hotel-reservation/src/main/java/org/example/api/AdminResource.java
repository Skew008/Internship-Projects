package org.example.api;

import org.example.model.Customer;
import org.example.model.IRoom;
import org.example.service.CustomerService;
import org.example.service.ReservationService;

import java.util.Collection;
import java.util.List;

public class AdminResource {

    private static AdminResource adminResource;
    private final CustomerService customerService;
    private final ReservationService reservationService;

    private AdminResource() {
        this.customerService = CustomerService.getCustomerService();
        this.reservationService = ReservationService.getReservationService();
    }

    public static AdminResource getAdminResource() {
        if(adminResource==null)
            adminResource = new AdminResource();
        return adminResource;
    }

    public Customer getCustomer(String email) {
        return customerService.getCustomer(email);
    }

    public void addRoom(List<IRoom> rooms) {
        for (IRoom room:rooms)
            reservationService.addRoom(room);
    }

    public Collection<IRoom> getAllRooms() {
        return reservationService.getRoomMap().values();
    }

    public Collection<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    public void displayAllReservations() {
        reservationService.printAllReservations();
    }
}