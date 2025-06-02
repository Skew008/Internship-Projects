package com.task06;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
		lambdaName = "audit_producer",
		roleName = "audit_producer-role",
		isPublishVersion = true,
		aliasName = "${lambdas_alias_name}",
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@DynamoDbTriggerEventSource(
		targetTable = "Configuration",
		batchSize = 10
)
@EnvironmentVariables(
		value = {
				@EnvironmentVariable(key = "target_table", value = "${target_table}")
		}
)
public class AuditProducer implements RequestHandler<DynamodbEvent, Map<String, Object>> {

	private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
	private final String table = System.getenv("target_table");

	public Map<String, Object> handleRequest(DynamodbEvent request, Context context) {

		context.getLogger().log("Content: "+request.getRecords().toString());
		for(DynamodbEvent.DynamodbStreamRecord streamRecord: request.getRecords())
		{
			Map<String, AttributeValue> newItem = convertDynamoDBMap(streamRecord.getDynamodb().getNewImage());
			Map<String, AttributeValue> auditEntry = new HashMap<>();
			auditEntry.put("id", new AttributeValue().withS(UUID.randomUUID().toString()));
			auditEntry.put("itemKey", new AttributeValue().withS(newItem.get("key").getS()));
			auditEntry.put("modificationTime", new AttributeValue().withS(Instant.now().toString()));
			if("INSERT".equals(streamRecord.getEventName()))
			{
				Map<String, AttributeValue> newValue = new HashMap<>();
				newValue.put("key", new AttributeValue().withS(newItem.get("key").getS()));
				newValue.put("value", new AttributeValue().withN(newItem.get("value").getN()));
				auditEntry.put("newValue", new AttributeValue().withM(newValue));
			}
			else if("MODIFY".equals(streamRecord.getEventName()))
			{
				Map<String, AttributeValue> oldItem = convertDynamoDBMap(streamRecord.getDynamodb().getOldImage());
				auditEntry.put("updatedAttribute", new AttributeValue().withS("value"));
				auditEntry.put("newValue", new AttributeValue().withN(newItem.get("value").getN()));
				auditEntry.put("oldValue", new AttributeValue().withN(oldItem.get("value").getN()));
			}

			PutItemRequest putItemRequest = new PutItemRequest()
					.withTableName(table)
					.withItem(auditEntry);
			client.putItem(putItemRequest);
		}


		System.out.println("Hello from lambda");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("statusCode", 200);
		resultMap.put("body", "Hello from Lambda");
		return resultMap;
	}

	private Map<String, AttributeValue> convertDynamoDBMap(Map<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue> dynamoDBMap) {
		Map<String, AttributeValue> resultMap = new HashMap<>();
		for (Map.Entry<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue> entry : dynamoDBMap.entrySet()) {
			com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue oldValue = entry.getValue();

			AttributeValue newValue = new AttributeValue();

			if (oldValue.getS() != null) {
				newValue.withS(oldValue.getS());
			} else if (oldValue.getN() != null) {
				newValue.withN(oldValue.getN());
			} else if (oldValue.getBOOL() != null) {
				newValue.withBOOL(oldValue.getBOOL());
			} else if (oldValue.getL() != null) {
				newValue.withL(oldValue.getL().stream()
						.map(v -> new AttributeValue().withS(v.getS()))
						.toList());
			} else if (oldValue.getM() != null) {
				newValue.withM(convertDynamoDBMap(oldValue.getM()));
			}

			resultMap.put(entry.getKey(), newValue);
		}
		return resultMap;
	}

}