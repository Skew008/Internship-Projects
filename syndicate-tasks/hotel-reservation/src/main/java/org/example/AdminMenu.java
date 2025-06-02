package org.example;

import org.example.api.AdminResource;
import org.example.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final AdminResource adminResource = AdminResource.getAdminResource();
    private final Scanner sc = new Scanner(System.in);

    public void seeAllCustomers() {
        for (Customer customer : adminResource.getAllCustomers()) {
            System.out.println(customer.toString());
        }
    }

    public void seeAllRooms() {
        for (IRoom room : adminResource.getAllRooms()) {
            System.out.println(room.toString());
        }
    }

    public void seeAllReservations() {
        adminResource.displayAllReservations();
    }

    public void addARoom() {
        List<IRoom> rooms = new ArrayList<>();
        while (true) {
            System.out.println("1. Create a room");
            System.out.println("2. Add rooms");
            int choice = sc.nextInt();

            if (choice == 1) {
                System.out.print("Room number: ");
                String roomNumber = sc.next();
                System.out.print("Room type: ");
                String roomType = sc.next();
                System.out.println("Free room?(true/false): ");
                boolean isFree = sc.nextBoolean();
                if(isFree)
                    rooms.add(new FreeRoom(roomNumber, RoomType.valueOf(roomType)));
                else {
                    System.out.print("Room Price: ");
                    double price = sc.nextDouble();
                    rooms.add(new Room(roomNumber,price,RoomType.valueOf(roomType)));
                }
            }
            else break;
            System.out.println();
        }
        adminResource.addRoom(rooms);
    }

    public final void adminMenu() {

        while (true) {
            System.out.println("Admin Menu:");
            System.out.println("1. See all customers");
            System.out.println("2. See all rooms");
            System.out.println("3. See all reservations");
            System.out.println("4. Add a room");
            System.out.println("5. Back to main menu");
            System.out.println("Enter choice: ");
            int choice = sc.nextInt();

            try {
                switch (choice) {
                    case 1 -> seeAllCustomers();
                    case 2 -> seeAllRooms();
                    case 3 -> seeAllReservations();
                    case 4 -> addARoom();
                    default -> {
//                        sc.close();
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
