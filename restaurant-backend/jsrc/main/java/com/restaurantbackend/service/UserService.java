package com.restaurantbackend.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.restaurantbackend.dto.SignUp;

public class UserService {

    private final AmazonDynamoDB dynamoDB;
    private final String usersTable;
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z][a-zA-Z]*$");

    public UserService(AmazonDynamoDB dynamoDB) {
        if(dynamoDB==null){
            throw new IllegalArgumentException("dynamoDB is null");
        }
        this.dynamoDB = dynamoDB;
        this.usersTable = System.getenv("USER_TABLE");
    }

    public void createUser(SignUp userDetails, String role)
    {
        if(userDetails==null){
            throw new IllegalArgumentException("userDetails are not present here");
        }
        if(role==null || role.isEmpty()){
            throw new IllegalArgumentException("role is not there");
        }
        // Item creation
        Map<String,AttributeValue> userItem=new HashMap<>();
        userItem.put("email", new AttributeValue().withS(userDetails.email()));
        userItem.put("firstName",new AttributeValue().withS(userDetails.firstName()));
        userItem.put("lastName", new AttributeValue().withS(userDetails.lastName()));
        userItem.put("imageUrl", new AttributeValue().withS("https://team8-user-profile-images.s3.ap-northeast-1.amazonaws.com/default_profile_image.png"));
        userItem.put("role", new AttributeValue().withS(role));

        // Put Request
        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(usersTable)
                .withItem(userItem);

        // Saving data in data
        dynamoDB.putItem(putItemRequest);


    }

    public boolean checkUserExists(String email) {
        return getUserByEmail(email) != null;
    }

    public void validateUserDetails(SignUp signUp) {
        if(signUp==null){
            throw new IllegalArgumentException("signup is not there");
        }
        if (!isValidName(signUp.firstName())) {
            throw new IllegalArgumentException("First name must be up to 50 characters. Only Latin letters, hyphens, and apostrophes are allowed");
        }
        if (!isValidName(signUp.lastName())) {
            throw new IllegalArgumentException("Last name must be up to 50 characters. Only Latin letters, hyphens, and apostrophes are allowed");
        }
        if (!isValidEmail(signUp.email())) {
            throw new IllegalArgumentException("Invalid email address. Please ensure it follows the format: username@domain.com");
        }
    }

    private boolean isValidName(String name) {
        String nameRegex = "^[A-Za-z ]{1,50}$";
        return name != null && Pattern.matches(nameRegex, name);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9](?:[a-zA-Z0-9_.]*[a-zA-Z0-9_])?@[a-zA-Z0-9]+\\.[cC][oO][mM]$";
        return email != null && Pattern.matches(emailRegex, email);
    }


    public Map<String, AttributeValue> getUserByEmail (String email) {
        if(email==null || email.isEmpty()) {
            throw new IllegalArgumentException("email is not present");
        }
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", new AttributeValue().withS(email));

        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName(usersTable)
                .withKey(key);

        GetItemResult result = dynamoDB.getItem(getItemRequest);
        return result.getItem();
    }

    public void updateUserInfo(String email, String firstName, String lastName, String imageUrl) {
        if (!isValidName(firstName)) {
            throw new IllegalArgumentException("Invalid first name format.");
        }
        if (!isValidName(lastName)) {
            throw new IllegalArgumentException("Invalid last name format.");
        }

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("email", new AttributeValue().withS(email));

        Map<String, AttributeValueUpdate> updates = new HashMap<>();
        updates.put("firstName", new AttributeValueUpdate().withValue(new AttributeValue().withS(firstName)).withAction("PUT"));
        updates.put("lastName", new AttributeValueUpdate().withValue(new AttributeValue().withS(lastName)).withAction("PUT"));
        updates.put("imageUrl", new AttributeValueUpdate().withValue(new AttributeValue().withS(imageUrl)).withAction("PUT"));

        UpdateItemRequest updateRequest = new UpdateItemRequest()
                .withTableName(usersTable)
                .withKey(key)
                .withAttributeUpdates(updates);

        // Perform the update in DynamoDB
        dynamoDB.updateItem(updateRequest);
    }
    private boolean isValidFirstName(String firstName) {
        return firstName != null && NAME_PATTERN.matcher(firstName).matches();
    }

    private boolean isValidLastName(String lastName) {
        return lastName != null && NAME_PATTERN.matcher(lastName).matches();
    }

}
