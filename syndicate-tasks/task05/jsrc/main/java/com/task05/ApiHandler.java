package com.task05;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.model.RetentionSetting;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.util.UUID;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@LambdaHandler(
    lambdaName = "api_handler",
	roleName = "api_handler-role",
	isPublishVersion = true,
	aliasName = "learn",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariables(value = {
		@EnvironmentVariable(key = "target_table", value = "${target_table}")
})
public class ApiHandler implements RequestHandler<Request, Map<String, Object>> {

	private static final String TABLE_NAME = System.getenv("target_table");
	private static final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
	private static final DynamoDB dynamoDB = new DynamoDB(client);
	@Override
	public Map<String, Object> handleRequest(Request request, Context context) {

		ObjectMapper objectMapper = new ObjectMapper();

		Event event = new Event(
				request.getPrincipalId(),
				Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT),
				request.getContent());

		Table table = dynamoDB.getTable(TABLE_NAME);
		Item item = new Item()
				.withPrimaryKey("id",event.getId())
				.withNumber("principalId",event.getPrincipalId())
				.withString("createdAt",event.getCreatedAt())
				.withMap("body", event.getBody());
		table.putItem(item);

		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("statusCode", 201);
		resultMap.put("event", event);
		return resultMap;
	}
}

class Request {
	int principalId;
	Map<String, String> content;

	public Request() {}
	public Request(int principalId, Map<String, String> content) {
		this.principalId = principalId;
		this.content = content;
	}

	public int getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(int principalId) {
		this.principalId = principalId;
	}

	public Map<String, String> getContent() {
		return content;
	}

	public void setContent(Map<String, String> content) {
		this.content = content;
	}
}

class Event {
	private String id;             // UUID v4 as String
	private int principalId;       // Integer
	private String createdAt;     // ISO 8601 Date-Time
	private Map<String, String> body;  // Generic content as JSON Object



	public Event(int principalId, String createdAt, Map<String, String> body) {
		this.id = UUID.randomUUID().toString();
		this.principalId = principalId;
		this.createdAt = createdAt;
		this.body = body;
	}

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public int getPrincipalId() { return principalId; }
	public void setPrincipalId(int principalId) { this.principalId = principalId; }

	public String getCreatedAt() { return createdAt; }
	public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

	public Map<String, String> getBody() { return body; }
	public void setBody(Map<String, String> body) { this.body = body; }

	@Override
	public String toString() {
		return "{" +
				"   id='" + id + '\'' +
				"\n,  principalId=" + principalId +
				"\n,  createdAt=" + createdAt +
				"\n,  body=" + body +
				"\n}";
	}
}