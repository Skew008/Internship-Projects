package com.restaurantbackend.dto;

import org.json.JSONObject;

public record SignUp (String firstName, String lastName, String email, String password) {
    public static SignUp fromJson (String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        String firstName = json.optString("firstName", null);
        String lastName = json.optString("lastName", null);
        String email = json.optString("email", null).toLowerCase();
        String password = json.optString("password", null);
        checkPassword(password);

        return new SignUp(firstName, lastName, email, password);

    }

    private static void checkPassword(String password) {
        String uppercasePattern = ".*[A-Z].*";
        String lowercasePattern = ".*[a-z].*";
        String digitPattern = ".*\\d.*";
        String specialCharPattern = ".*[!@#$%^&*()-+=<>?/{}~|].*";

        if (password == null || password.length() < 8 || password.length() > 16) {
            throw new IllegalArgumentException("Password must be 8-16 characters long.");
        }
        if (!password.matches(uppercasePattern)) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter.");
        }
        if (!password.matches(lowercasePattern)) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter.");
        }
        if (!password.matches(digitPattern)) {
            throw new IllegalArgumentException("Password must contain at least one numeric character.");
        }
        if (!password.matches(specialCharPattern)) {
            throw new IllegalArgumentException("Password must contain at least one special character.");
        }
    }
}
