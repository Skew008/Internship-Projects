package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@LambdaHandler(
		lambdaName = "uuid_generator",
		roleName = "uuid_generator-role",
		isPublishVersion = true,
		aliasName = "${lambdas_alias_name}",
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariables(
		value = {
				@EnvironmentVariable(key = "target_bucket", value = "${target_bucket}")
		}
)
@RuleEventSource(
		targetRule = "uuid_trigger"
)
public class UuidGenerator implements RequestHandler<ScheduledEvent, Void> {

	private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
	private final ObjectMapper objectMapper = new ObjectMapper();

	public Void handleRequest(ScheduledEvent request, Context context) {

		String fileName = Instant.now().toString();

		List<String> ids = Stream
				.generate(() -> UUID.randomUUID().toString())
				.limit(10)
				.collect(Collectors.toList());
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("ids", ids);
		String jsonString = null;
		try {
			jsonString = objectMapper.writeValueAsString(jsonMap);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

		s3Client.putObject(
				System.getenv("target_bucket"),
				fileName,
				jsonString);

		return null;
	}
}
