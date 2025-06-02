package com.restaurantbackend.handler.auth;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.SignUp;
import com.restaurantbackend.service.CognitoSupport;
import com.restaurantbackend.service.UserService;
import com.restaurantbackend.service.WaiterService;
import org.json.JSONObject;

import java.util.List;

public class PostSignUpHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoSupport cognitoSupport;
    private final UserService userService;
    private final WaiterService waiterService;

    public PostSignUpHandler(CognitoSupport cognitoSupport, UserService userService, WaiterService waiterService) {
        this.cognitoSupport = cognitoSupport;
        this.userService = userService;
        this.waiterService = waiterService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        try{

            SignUp signUp = SignUp.fromJson(requestEvent.getBody());

            userService.validateUserDetails(signUp);

            if(userService.checkUserExists(signUp.email())) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(409)
                        .withBody(new JSONObject().put("message", "A user with this email address already exists").toString());
            }

            cognitoSupport.cognitoSignUp(signUp, context);

            cognitoSupport.confirmSignUp(signUp, context);

            List<String> waiters = waiterService.getWaiters();


            if(waiters.stream().anyMatch(email -> email.equals(signUp.email()))) {
                userService.createUser(signUp, "Waiter");
            }
            else {
                userService.createUser(signUp, "Customer");
            }

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(201)
                    .withBody(new JSONObject().put("message", "User registered successfully").toString());
        }
        catch (Exception e){
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("message", e.getMessage()).toString());
        }
    }
}
