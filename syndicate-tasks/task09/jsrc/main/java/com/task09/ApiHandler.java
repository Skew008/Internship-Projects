package com.task09;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import org.example.WeatherClient;


import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "api_handler",
	roleName = "api_handler-role",
	isPublishVersion = true,
	layers = {"weather_sdk"},
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaLayer(
		layerName = "weather_sdk",
		libraries = {"libs/WeatherClientAPI-1.0-SNAPSHOT.jar"}
)
@LambdaUrlConfig
public class ApiHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {

		if(getMethod(event).equals("GET") && getPath(event).equals("/weather"))
		{
			return APIGatewayV2HTTPResponse.builder()
					.withStatusCode(200)
					.withHeaders(Map.of("Content-Type", "application/json"))
					.withBody(gson.toJson(WeatherClient.apiCall()))
					.build();
		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("statusCode", 400);
		resultMap.put("message", "Bad request syntax or unsupported method. Request path: "+getPath(event)+". HTTP method: "+getMethod(event));
		return APIGatewayV2HTTPResponse.builder()
				.withStatusCode(400)
				.withHeaders(Map.of("Content-Type", "application/json"))
				.withBody(gson.toJson(resultMap))
				.build();
	}

	private String getMethod(APIGatewayV2HTTPEvent requestEvent) {
		return requestEvent.getRequestContext().getHttp().getMethod();
	}

	private String getPath(APIGatewayV2HTTPEvent requestEvent) {
		return requestEvent.getRequestContext().getHttp().getPath();
	}
}
