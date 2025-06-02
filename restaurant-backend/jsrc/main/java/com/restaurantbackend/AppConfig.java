package com.restaurantbackend;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.handler.HandlerModule;
import com.restaurantbackend.service.ServiceModule;
import dagger.Component;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
@Component(modules = {ServiceModule.class, HandlerModule.class})
public interface AppConfig {

    @Named("general")
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getRouteHandler();

    @Named("cors")
    Map<String, String> getCORS();
}
