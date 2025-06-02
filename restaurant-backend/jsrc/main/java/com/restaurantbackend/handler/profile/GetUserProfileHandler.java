package com.restaurantbackend.handler.profile;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.CognitoSupport;
import com.restaurantbackend.service.S3Service;
import com.restaurantbackend.service.UserService;
import com.restaurantbackend.service.WaiterService;
import org.json.JSONObject;

import java.util.Map;

public class GetUserProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final UserService userService;
    private final CognitoSupport cognitoSupport;
    private final WaiterService waiterService;
    private final S3Service s3Service;

    public GetUserProfileHandler(UserService userService, CognitoSupport cognitoSupport, WaiterService waiterService,S3Service s3Service) {
        this.userService = userService;
        this.cognitoSupport = cognitoSupport;
        this.waiterService = waiterService;
        this.s3Service= s3Service;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        try {
            String email = cognitoSupport.getEmailByToken(requestEvent, context);

            Map<String, AttributeValue> userByEmail = userService.getUserByEmail(email);
            Map<String, AttributeValue> person = waiterService.getWaiterById(email);
            // Get image URL from DynamoDB
            String imageUrl = userByEmail.get("imageUrl").getS();
            context.getLogger().log(imageUrl);
            // Convert to Base64 using S3Service
            String imageBase64 = s3Service.getBase64ImageFromS3(imageUrl);
            context.getLogger().log(imageBase64);
            JSONObject jsonObject = new JSONObject()
                    .put("firstName", userByEmail.get("firstName").getS())
                    .put("lastName", userByEmail.get("lastName").getS())
                    .put("email", userByEmail.get("email").getS())
                    .put("role", userByEmail.get("role").getS())
                    .put("imageBase64", imageBase64);  // Embed base64 string instead of URL

            if (person != null) {
                jsonObject.put("locationId", person.get("locationId").getS());
            }

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(jsonObject.toString());

        } catch (Exception e) {
            context.getLogger().log("Error in GetUserProfileHandler: " + e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("message", e.getMessage()).toString());
        }
    }
}
