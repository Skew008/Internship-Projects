package com.restaurantbackend.handler.auth;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.SignIn;
import com.restaurantbackend.service.CognitoSupport;
import com.restaurantbackend.service.UserService;
import org.json.JSONObject;

import java.util.Map;

public class PostSignInHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoSupport cognitoSupport;
    private final UserService userService;

    public PostSignInHandler(CognitoSupport cognitoSupport, UserService userService) {
        this.cognitoSupport = cognitoSupport;
        this.userService = userService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        try{

            SignIn signIn = SignIn.fromJson(requestEvent.getBody());

            AuthenticationResultType authenticationResult = cognitoSupport.cognitoSignIn(signIn.email(), signIn.password(), context)
                    .getAuthenticationResult();

            String accessToken = authenticationResult.getIdToken();
            String passwordChangeToken = authenticationResult.getAccessToken();

            Map<String, AttributeValue> user = userService.getUserByEmail(signIn.email());

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject()
                            .put("accessToken", accessToken)
                            .put("username", user.get("firstName").getS() + " " + user.get("lastName").getS())
                            .put("role", user.get("role").getS())
                            .put("passwordChangeToken", passwordChangeToken).toString());

        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(401)
                    .withBody(new JSONObject().put("message", e.getMessage()).toString());
        }
    }
}
