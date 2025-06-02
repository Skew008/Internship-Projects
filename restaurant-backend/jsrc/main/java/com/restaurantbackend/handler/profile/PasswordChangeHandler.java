package com.restaurantbackend.handler.profile;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.CognitoSupport;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class PasswordChangeHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoSupport cognitoSupport;

    public PasswordChangeHandler(CognitoSupport cognitoSupport) {
        this.cognitoSupport = cognitoSupport;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        try {
            JSONObject password = new JSONObject(requestEvent.getBody());

            cognitoSupport.changePassword(password.getString("passwordChangeToken"), password.getString("oldPassword"), password.getString("newPassword"));

            AuthenticationResultType authenticationResult = cognitoSupport.cognitoSignIn(cognitoSupport.getEmailByToken(requestEvent,context), password.getString("newPassword"), context)
                    .getAuthenticationResult();

            String accessToken = authenticationResult.getIdToken();
            String passwordChangeToken = authenticationResult.getAccessToken();

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject()
                            .put("message","Password has been successfully updated")
                            .put("accessToken", accessToken)
                            .put("passwordChangeToken", passwordChangeToken).toString());
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            context.getLogger().log(Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(401)
                    .withBody(new JSONObject().put("message", e.getMessage()).toString());
        }
    }
}
