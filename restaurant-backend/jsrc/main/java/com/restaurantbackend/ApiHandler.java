package com.restaurantbackend;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.environment.ValueTransformer;

import java.util.HashMap;
import java.util.Map;

@DependsOn(name = "${user_pool}", resourceType = ResourceType.COGNITO_USER_POOL)
@LambdaHandler(
    lambdaName = "api-handler",
	roleName = "api-handler-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED,
	runtime = DeploymentRuntime.JAVA17
)
@EnvironmentVariables( value = {
		@EnvironmentVariable(key = "REGION", value = "${region}"),
		@EnvironmentVariable(key = "COGNITO_ID", value = "${user_pool}", valueTransformer = ValueTransformer.USER_POOL_NAME_TO_USER_POOL_ID),
		@EnvironmentVariable(key = "CLIENT_ID", value = "${user_pool}", valueTransformer = ValueTransformer.USER_POOL_NAME_TO_CLIENT_ID),
		@EnvironmentVariable(key = "USER_TABLE", value = "${user_table}"),
		@EnvironmentVariable(key = "DISHES_TABLE", value = "${dishes_table}"),
		@EnvironmentVariable(key = "LOCATIONS_TABLE", value = "${locations_table}"),
		@EnvironmentVariable(key = "TABLES_TABLE", value = "${tables_table}"),
		@EnvironmentVariable(key = "RESERVATIONS_TABLE", value = "${reservations_table}"),
		@EnvironmentVariable(key = "WAITERS_TABLE", value = "${waiters_table}"),
		@EnvironmentVariable(key = "SALES_TABLE", value = "${sales_table}"),
		@EnvironmentVariable(key = "SPECIALITY_DISHES_TABLE", value = "${speciality_dishes_table}"),
		@EnvironmentVariable(key = "FEEDBACKS_TABLE", value = "${feedbacks_table}"),
		@EnvironmentVariable(key = "ORDERS_TABLE", value = "${orders_table}"),
		@EnvironmentVariable(key = "SQS_QUEUE", value = "${sqs}"),
		@EnvironmentVariable(key = "ACCOUNT_ID", value = "${account_id}"),
		@EnvironmentVariable(key="USER_PROFILE_IMAGE_BUCKET", value="${user_profile_image_bucket}"),
		@EnvironmentVariable(key="REPORT_TABLE" , value = "${reports_table}"),
		@EnvironmentVariable(key="S3_BUCKET" , value = "${reports_bucket}"),
		@EnvironmentVariable(key="SES_SENDER_EMAIL" , value = "${ses_sender_email}"),
		@EnvironmentVariable(key="WAITER_RECEIVER_EMAIL" , value = "${ses_receiver_email}")
})

public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final AppConfig appConfig = DaggerAppConfig.create();
	private final RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> routeHandler = appConfig.getRouteHandler();
	private final Map<String, String> corsHeaders = appConfig.getCORS();

	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		return routeHandler.handleRequest(request, context)
				.withHeaders(corsHeaders);
	}
}
