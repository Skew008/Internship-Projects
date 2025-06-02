package com.restaurantbackend.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.Map;
import java.util.regex.Pattern;

public class RouteHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Map<String, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> handlerMap;
    private final RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> notFound;

    public RouteHandler(
            Map<String, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> handlerMap,
            RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> notFound) {
        this.handlerMap = handlerMap;
        this.notFound = notFound;
    }

//    @Override
//    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
//        String routeKey = requestEvent.getHttpMethod()+":"+requestEvent.getPath();
//
//        return handlerMap.getOrDefault(routeKey, notFound).handleRequest(requestEvent, context);
//    }


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        String method = requestEvent.getHttpMethod();
        String path = requestEvent.getPath();

        // Iterate over all route keys in the handlerMap to find a match.
        for (Map.Entry<String, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> entry : handlerMap.entrySet()) {
            String routeKey = entry.getKey(); // e.g., "GET:/locations/{id}"
            RequestHandler handler = entry.getValue();

            // Split the routeKey into method and route path
            String[] parts = routeKey.split(":", 2);
            if (parts.length != 2) continue; // Skip invalid routeKeys
            String keyMethod = parts[0];
            String keyPath = parts[1];

            // Match the HTTP method, and ensure the keyPath matches dynamically
            if (method.equalsIgnoreCase(keyMethod) && isPathMatch(keyPath, path)) {
                // Set extracted path parameters
                Map<String, String> pathParameters = extractPathParameters(keyPath, path);
                requestEvent.setPathParameters(pathParameters);

                // Delegate to the correct handler
                return (APIGatewayProxyResponseEvent) handler.handleRequest(requestEvent, context);
            }
        }
        return notFound.handleRequest(requestEvent , context);
    }


    private boolean isPathMatch(String keyPath, String requestPath) {
        // Replace dynamic segments (e.g., {id}) with regex for matching
        String regex = keyPath.replaceAll("\\{[^/]+}", "[^/]+");
        regex = "^" + regex + "$"; // Ensure full path match
        return Pattern.matches(regex, requestPath);
    }

    private Map<String, String> extractPathParameters(String keyPath, String requestPath) {
        Map<String, String> pathParameters = new java.util.HashMap<>();

        // Split the key path and request path into parts
        String[] keyParts = keyPath.split("/");
        String[] requestParts = requestPath.split("/");

        for (int i = 0; i < keyParts.length; i++) {
            if (keyParts[i].startsWith("{") && keyParts[i].endsWith("}")) {
                // Extract the parameter name (e.g., {id} -> id)
                String paramName = keyParts[i].substring(1, keyParts[i].length() - 1);
                pathParameters.put(paramName, requestParts[i]);
            }
        }

        return pathParameters;
    }
}


//
//context.getLogger().log(routeKey);
//Map<String , String> map = requestEvent.getPathParameters();