package com.mycompany.absclassinterface;

public class App {
    public static void main(String[] args) {
        Contractor c = new Contractor("Jay", 2000, "9:00", "18:00");
        PermanentEmployee p = new PermanentEmployee("Mark", 30000);

        c.showDetails();
        System.out.println();
        p.showDetails();
        System.out.println();
        System.out.println(c.calculateWorkHours());
        System.out.println();
        System.out.println(p.calculateWorkHours());
        System.out.println();
        System.out.println(c.calculatePay());
        System.out.println();
        System.out.println(p.calculatePay());
    }
}
