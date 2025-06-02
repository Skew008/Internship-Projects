package com.task11;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;
import com.task11.dto.RouteKey;
import com.task11.handler.*;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.endpoints.internal.Value;

import java.util.Map;

import static com.syndicate.deployment.model.environment.ValueTransformer.USER_POOL_NAME_TO_CLIENT_ID;
import static com.syndicate.deployment.model.environment.ValueTransformer.USER_POOL_NAME_TO_USER_POOL_ID;

@DependsOn(resourceType = ResourceType.COGNITO_USER_POOL, name = "${booking_userpool}")
@LambdaHandler(
    lambdaName = "api_handler",
	roleName = "api_handler-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariables(value = {
		@EnvironmentVariable(key = "REGION", value = "${region}"),
		@EnvironmentVariable(key = "COGNITO_ID", value = "${booking_userpool}", valueTransformer = USER_POOL_NAME_TO_USER_POOL_ID),
		@EnvironmentVariable(key = "CLIENT_ID", value = "${booking_userpool}", valueTransformer = USER_POOL_NAME_TO_CLIENT_ID),
		@EnvironmentVariable(key = "tables_table", value = "${tables_table}"),
		@EnvironmentVariable(key = "reservations_table", value = "${reservations_table}")
})
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final CognitoIdentityProviderClient cognitoClient;
	private final Map<RouteKey, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> handlersByRouteKey;
	private final Map<String, String> headersForCORS;

	public ApiHandler() {
		this.cognitoClient = initCognitoClient();
		this.handlersByRouteKey = initHandlers();
		this.headersForCORS = initHeadersForCORS();
	}

	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
            return getHandler(request)
                    .handleRequest(request, context)
                    .withHeaders(headersForCORS);

    }

	private RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getHandler(APIGatewayProxyRequestEvent requestEvent) {

		RouteKey match = getRouteKey(requestEvent);
		if(handlersByRouteKey.containsKey(match))
			return handlersByRouteKey.get(match);

		for(RouteKey routeKey: handlersByRouteKey.keySet())
		{
			if(isDynamicMatch(routeKey.getPath(), match.getPath()))
				return handlersByRouteKey.get(routeKey);
		}
		return new NotFoundHandler(cognitoClient);
	}

	private boolean isDynamicMatch(String routePattern, String requestPath) {
		String regexPattern = routePattern.replaceAll("\\{[^/]+\\}", "[^/]+"); // Convert {param} to regex
		return requestPath.matches(regexPattern);
	}

	private RouteKey getRouteKey(APIGatewayProxyRequestEvent requestEvent) {
		return new RouteKey(requestEvent.getHttpMethod(), requestEvent.getPath());
	}

	private Map<RouteKey, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> initHandlers() {
		return Map.of(
				new RouteKey("POST", "/signup"), new PostSignUpHandler(cognitoClient),
				new RouteKey("POST", "/signin"), new PostSignInHandler(cognitoClient),
				new RouteKey("GET", "/tables"), new GetTablesHandler(cognitoClient),
				new RouteKey("POST", "/tables"), new PostTablesHandler(cognitoClient),
				new RouteKey("GET", "/tables/{tableId}"), new GetTableByIdHandler(cognitoClient),
				new RouteKey("POST", "/reservations"), new PostReservationsHandler(cognitoClient),
				new RouteKey("GET", "/reservations"), new GetReservationsHandler(cognitoClient)
		);
	}

	private Map<String, String> initHeadersForCORS() {
		return Map.of(
				"Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
				"Access-Control-Allow-Origin", "*",
				"Access-Control-Allow-Methods", "*",
				"Accept-Version", "*"
		);
	}

	private CognitoIdentityProviderClient initCognitoClient() {
		return CognitoIdentityProviderClient.builder()
				.region(Region.of(System.getenv("REGION")))
				.credentialsProvider(DefaultCredentialsProvider.create())
				.build();
	}
}
