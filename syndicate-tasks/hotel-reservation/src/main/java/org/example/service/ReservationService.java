package org.example.service;

import org.example.model.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReservationService {

    private static ReservationService reservationService;
    private final CustomerService customerService;
    private  final Map<String, IRoom> roomMap;
    private final Map<String, IRoom> availableRooms;
    private final Map<String, List<Reservation>> reservationMap;

    private ReservationService(){
        customerService = CustomerService.getCustomerService();
        reservationMap = new HashMap<>();
        roomMap = new HashMap<>();
        availableRooms = new HashMap<>();
    }

    public static ReservationService getReservationService() {
        if(reservationService==null)
            reservationService = new ReservationService();
        return reservationService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public Map<String, IRoom> getRoomMap() {
        return roomMap;
    }

    public Map<String, List<Reservation>> getReservationMap() {
        return reservationMap;
    }

    public void addRoom(IRoom room) {
        if(room instanceof FreeRoom)
            roomMap.put(room.getRoomNumber(), new FreeRoom(room.getRoomNumber(), room.getRoomType()));
        else
            roomMap.put(room.getRoomNumber(), new Room(room.getRoomNumber(), room.getRoomPrice(), room.getRoomType()));

        availableRooms.put(room.getRoomNumber(), getARoom(room.getRoomNumber()));
    }

    public IRoom getARoom(String roomId) {
        return roomMap.getOrDefault(roomId, null);
    }

    public Reservation reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutdate) {
        if(customerService.getCustomer(customer.getEmail())==null || getARoom(room.getRoomNumber())==null)
            throw new IllegalArgumentException();

        availableRooms.remove(room.getRoomNumber());

        Reservation reservation = new Reservation(customer, room, checkInDate, checkOutdate);

        if(!reservationMap.containsKey(customer.getEmail()))
            reservationMap.put(customer.getEmail(), new ArrayList<>());
        reservationMap.get(customer.getEmail()).add(reservation);

        return reservation;
    }

    public Collection<Reservation> getCustomersReservation(Customer customer) {
        return reservationMap.get(customer.getEmail());
    }

    public Collection<IRoom> findARoom(Date checkIndate, Date checkOutDate) {
         return Stream.concat(reservationMap.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(reservation -> checkIndate.after(reservation.getCheckOutDate()) || checkOutDate.before(reservation.getCheckInDate()))
                .map(Reservation::getRoom)
                 , availableRooms.values().stream())
                 .collect(Collectors.toCollection(ArrayList::new));

    }

        public void printAllReservations() {
        reservationMap.values()
                .stream()
                .flatMap(Collection::stream)
                .forEach((reservation) -> System.out.println(reservation.toString()));
    }
}
