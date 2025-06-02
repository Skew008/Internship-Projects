package com.task12.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task12.dto.SignUp;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

public class PostSignUpHandler extends CognitoSupport implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public PostSignUpHandler(CognitoIdentityProviderClient cognitoClient) {
        super(cognitoClient);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        try{
            SignUp signUp = SignUp.fromJson(apiGatewayProxyRequestEvent.getBody());

            cognitoSignUp(signUp, context);

            confirmSignUp(signUp);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200);
        }
        catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400);
        }
    }
}
