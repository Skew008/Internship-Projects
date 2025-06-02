package org.example;

import org.example.api.HotelResource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class MainMenu {

    private final HotelResource hotelResource = HotelResource.getHotelResource();
    private final AdminMenu adminMenu = new AdminMenu();
    private final Scanner sc = new Scanner(System.in);

    public void findAndReserveARoom() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        System.out.print("Check-In Date: ");
        Date checkInDate = simpleDateFormat.parse(sc.next());
        System.out.print("Check-Out Date: ");
        Date checkOutDate = simpleDateFormat.parse(sc.next());

        System.out.println("\nList of Available rooms:");
        hotelResource.findARoom(checkInDate, checkOutDate).forEach(System.out::println);


        System.out.print("Customer email: ");
        String email = sc.next();
        System.out.print("Room number: ");
        String room = sc.next();

        System.out.println("Reservation: \n" + hotelResource.bookARoom(email, room, checkInDate, checkOutDate));
    }

    public void seeMyReservations() {
        System.out.print("Customer email: ");
        String email = sc.next();
        hotelResource.getCustomersReservation(email).forEach(System.out::println);
    }

    public void createAnAccount() {
        System.out.print("Customer email: ");
        String email = sc.next();
        System.out.print("First name: ");
        String firstName = sc.next();
        System.out.print("Last name: ");
        String lastName = sc.next();
        hotelResource.createACustomer(email, firstName, lastName);
    }

    public void toAdminMenu() {
        adminMenu.adminMenu();
    }

    public final void mainMenu() {

        while (true) {
            System.out.println("Main Menu:");
            System.out.println("1. Find and reserve a room");
            System.out.println("2. See my reservations");
            System.out.println("3. Create an account");
            System.out.println("4. Admin menu");
            System.out.println("5. Exit");
            System.out.println("Enter choice: ");

            int choice = sc.nextInt();

            try {
                switch (choice) {
                    case 1 -> findAndReserveARoom();
                    case 2 -> seeMyReservations();
                    case 3 -> createAnAccount();
                    case 4 -> toAdminMenu();
                    default -> {
                        sc.close();
                        System.out.println();
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
            System.out.println();
        }
    }
}
