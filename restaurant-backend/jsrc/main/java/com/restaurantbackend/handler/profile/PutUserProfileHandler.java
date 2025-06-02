package com.restaurantbackend.handler.profile;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.CognitoSupport;
import com.restaurantbackend.service.S3Service;
import com.restaurantbackend.service.UserService;
import org.json.JSONObject;

import java.util.Arrays;

public class PutUserProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final UserService userService;
    private final CognitoSupport cognitoSupport;
    private final S3Service s3Service;

    public PutUserProfileHandler(UserService userService, CognitoSupport cognitoSupport, S3Service s3Service) {
        this.userService = userService;
        this.cognitoSupport = cognitoSupport;
        this.s3Service= s3Service;
    }
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try{
            String email = cognitoSupport.getEmailByToken(requestEvent,context);
            JSONObject body= new JSONObject(requestEvent.getBody());
            String firstName = body.getString("firstName");
            String lastName = body.getString("lastName");
            String base64encodedImage = body.getString("imageBase64");
            context.getLogger().log(base64encodedImage);
            String imageUrl = s3Service.uploadBase64Image(base64encodedImage,email,context);
            context.getLogger().log(imageUrl);
            userService.updateUserInfo(email, firstName, lastName ,imageUrl);
            JSONObject responseBody = new JSONObject()
                    .put("message","User profile Updated Successfully");

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(responseBody.toString());
        }
        catch(Exception e){
            context.getLogger().log("Error Updating user: "+ e.getMessage());
            context.getLogger().log(Arrays.toString(e.getStackTrace()));
            JSONObject errorBody = new JSONObject()
                    .put("error","Failed to update profile")
                    .put("details", e.getMessage());

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(errorBody.toString());

        }
    }
}
