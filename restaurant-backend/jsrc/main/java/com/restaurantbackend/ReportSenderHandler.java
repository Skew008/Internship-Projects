package com.restaurantbackend;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.restaurantbackend.service.ReportService;
import com.restaurantbackend.service.UserService;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.events.EventBridgeRuleSource;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.resources.DependsOn;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.ResourceType;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.environment.ValueTransformer;
import org.joda.time.DateTime;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "ReportSenderHandler",
	roleName = "ReportSenderHandler-role",
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
		@EnvironmentVariable(key = "SQS_QUEUE", value = "${sqs}"),
		@EnvironmentVariable(key = "ACCOUNT_ID", value = "${account_id}"),
		@EnvironmentVariable(key="REPORT_TABLE" , value = "${reports_table}"),
		@EnvironmentVariable(key="ORDERS_TABLE" , value = "${orders_table}"),
		@EnvironmentVariable(key="S3_BUCKET" , value = "${reports_bucket}"),
		@EnvironmentVariable(key="SES_SENDER_EMAIL" , value = "${ses_sender_email}"),
		@EnvironmentVariable(key="SES_RECEIVER_EMAIL" , value = "${ses_receiver_email}"),
})
@RuleEventSource(
		targetRule = "reports-trigger"
)
public class ReportSenderHandler implements RequestHandler<ScheduledEvent, Void> {

	private final ReportService reportService = new ReportService(AmazonDynamoDBClientBuilder.defaultClient(), AmazonS3ClientBuilder.defaultClient(), new UserService(AmazonDynamoDBClientBuilder.defaultClient()));

    public Void handleRequest(ScheduledEvent request, Context context) {
		try{
			reportService.generateAndSendReports();
		} catch (Exception e) {
			context.getLogger().log(e.getMessage());
			context.getLogger().log(Arrays.toString(e.getStackTrace()));
		}
		return null;
	}
}


