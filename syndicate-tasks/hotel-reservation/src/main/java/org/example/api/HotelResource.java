package org.example.api;

import org.example.model.Customer;
import org.example.model.IRoom;
import org.example.model.Reservation;
import org.example.service.CustomerService;
import org.example.service.ReservationService;

import java.util.Collection;
import java.util.Date;

public class HotelResource {

    private static HotelResource hotelResource;
    private final CustomerService customerService;
    private final ReservationService reservationService;

    private HotelResource(){
        this.customerService = CustomerService.getCustomerService();
        this.reservationService = ReservationService.getReservationService();
    }

    public static HotelResource getHotelResource() {
        if (hotelResource==null)
            hotelResource = new HotelResource();
        return hotelResource;
    }

    public Customer getCustomer(String email) {
        return customerService.getCustomer(email);
    }

    public void createACustomer(String email, String firstName, String lastName) {
        customerService.addCustomer(email, firstName, lastName);
    }

    public IRoom getRoom(String roomNumber) {
        return reservationService.getARoom(roomNumber);
    }

    public Reservation bookARoom(String customerEmail, String roomNumber, Date checkInDate, Date checkOutDate) {
        return reservationService.reserveARoom(getCustomer(customerEmail), getRoom(roomNumber), checkInDate, checkOutDate);
    }

    public Collection<Reservation> getCustomersReservation(String customerEmail) {
        return reservationService.getCustomersReservation(getCustomer(customerEmail));
    }

    public Collection<IRoom> findARoom(Date checkIndate, Date checkOutDate) {
        return reservationService.findARoom(checkIndate, checkOutDate);
    }
}