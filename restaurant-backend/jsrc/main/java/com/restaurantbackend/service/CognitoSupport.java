package com.restaurantbackend.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.restaurantbackend.dto.SignUp;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class CognitoSupport {

    private final String userPoolId = System.getenv("COGNITO_ID");
    private final String clientId = System.getenv("CLIENT_ID");
    private final AWSCognitoIdentityProvider cognitoClient;

    public CognitoSupport(AWSCognitoIdentityProvider cognitoClient) {
        if(cognitoClient==null){
            throw new IllegalArgumentException("cognito client is not present");
        }
        this.cognitoClient = cognitoClient;
    }

    public AdminInitiateAuthResult cognitoSignIn(String email, String password, Context context) {
        if(email==null || email.isEmpty()){
            throw new IllegalArgumentException("email is not present");
        }
        if(password==null || password.isEmpty()){
            throw new IllegalArgumentException("password is not present");
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        Map<String, String> authParams = Map.of(
                "USERNAME", email,
                "PASSWORD", password
        );
        try {
            return cognitoClient.adminInitiateAuth(new AdminInitiateAuthRequest()
                    .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .withAuthParameters(authParams)
                    .withUserPoolId(userPoolId)
                    .withClientId(clientId)
            );
        } catch (UserNotFoundException e) {
            throw new IllegalArgumentException("User not found. Please check your email.");
        } catch (NotAuthorizedException e) {
            throw new IllegalArgumentException("Incorrect email or password.");
        } catch (InvalidParameterException e) {
            throw new IllegalArgumentException("Invalid login request.");
        } catch (AWSCognitoIdentityProviderException e) {
            context.getLogger().log("Cognito error: " + e.getErrorMessage());
            throw new RuntimeException("Authentication failed: " + e.getErrorMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    public AdminCreateUserResult cognitoSignUp(SignUp signUp, Context context) throws Exception{
        if(signUp==null){
            throw new IllegalArgumentException("signup request body is not given.");
        }
        if(context==null){
            throw new IllegalArgumentException("context is null");
        }

        try {
            return cognitoClient.adminCreateUser(new AdminCreateUserRequest()
                    .withUserPoolId(userPoolId)
                    .withUsername(signUp.email())
                    .withTemporaryPassword(signUp.password())
                    .withUserAttributes(
                            new AttributeType()
                                    .withName("given_name")
                                    .withValue(signUp.firstName()),
                            new AttributeType()
                                    .withName("family_name")
                                    .withValue(signUp.lastName()),
                            new AttributeType()
                                    .withName("email")
                                    .withValue(signUp.email()),
                            new AttributeType()
                                    .withName("email_verified")
                                    .withValue("true"))
                    .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                    .withMessageAction("SUPPRESS")
                    .withForceAliasCreation(Boolean.FALSE)
            );
        }
        catch (UsernameExistsException e) {
            throw new IllegalArgumentException("Provided email is already registered");
        }
        catch (InvalidPasswordException e) {
            throw new IllegalArgumentException("Password is invalid");
        }
        catch (InvalidParameterException e) {
            throw new IllegalArgumentException("Invalid parameters are provided");
        }
        catch (AWSCognitoIdentityProviderException e) {
            context.getLogger().log("Cognito error: " + e.getErrorMessage());
            throw new RuntimeException("Signup failed: " + e.getErrorMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    public AdminRespondToAuthChallengeResult confirmSignUp(SignUp signUp, Context context) {
        if(signUp==null){
            throw new IllegalArgumentException("sign up is not present");
        }
        AdminInitiateAuthResult adminInitiateAuthResponse = cognitoSignIn(signUp.email(), signUp.password(), context);
        if(adminInitiateAuthResponse==null){
            throw new IllegalArgumentException("Admin Initiate Auth Response not found");
        }
        if (!ChallengeNameType.NEW_PASSWORD_REQUIRED.name().equals(adminInitiateAuthResponse.getChallengeName())) {
            throw new RuntimeException("unexpected challenge: " + adminInitiateAuthResponse.getChallengeName());
        }

        Map<String, String> challengeResponses = Map.of(
                "USERNAME", signUp.email(),
                "PASSWORD", signUp.password(),
                "NEW_PASSWORD", signUp.password()
        );

        return cognitoClient.adminRespondToAuthChallenge(new AdminRespondToAuthChallengeRequest()
                .withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                .withChallengeResponses(challengeResponses)
                .withUserPoolId(userPoolId)
                .withClientId(clientId)
                .withSession(adminInitiateAuthResponse.getSession()));
    }

    public String getEmailByToken(APIGatewayProxyRequestEvent event, Context context) {
        context.getLogger().log(event.getRequestContext().getAuthorizer().toString());
        Map<String, Object> m =
                (Map<String, Object>) event.getRequestContext()
                .getAuthorizer()
                .get("claims");

        return m.get("email").toString();
    }

    public void changePassword(String accessToken, String oldPassword, String newPassword) {

        try {
            cognitoClient.changePassword(
                    new ChangePasswordRequest()
                            .withPreviousPassword(oldPassword)
                            .withProposedPassword(newPassword)
                            .withAccessToken(accessToken)
            );
        }
        catch (NotAuthorizedException e) {
            throw new IllegalArgumentException("Password is invalid");
        }
        catch (InvalidPasswordException e) {
            throw new IllegalArgumentException("New password does not match password policy");
        }
        catch (LimitExceededException e) {
            throw new IllegalArgumentException("Limit exceeded, Please try after sometime");
        }
        catch (Exception e) {
            throw new RuntimeException("Error updating password");
        }
    }
}
