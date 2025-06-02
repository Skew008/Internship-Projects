package com.restaurantbackend.handler;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.handler.auth.PostSignInHandler;
import com.restaurantbackend.handler.auth.PostSignUpHandler;
import com.restaurantbackend.handler.booking.PutReservationHandler;
import com.restaurantbackend.handler.cart.GetCartOfParticularReservationId;
import com.restaurantbackend.handler.cart.PutCartHandler;
import com.restaurantbackend.handler.feedback.GetFeedbackByReservationIdHandler;
import com.restaurantbackend.handler.feedback.GetWaiterDetailsByReservationId;
import com.restaurantbackend.handler.profile.PasswordChangeHandler;
import com.restaurantbackend.handler.reports.GetReportsHandler;
import com.restaurantbackend.handler.reservation.GetReservationOfWaiterHandler;
import com.restaurantbackend.handler.booking.GetTablesHandler;
import com.restaurantbackend.handler.booking.PostReservationByWaiterHandler;
import com.restaurantbackend.handler.booking.PostReservationsHandler;
import com.restaurantbackend.handler.cart.GetCartHandler;
import com.restaurantbackend.handler.dish.GetAllDishesHandler;
import com.restaurantbackend.handler.dish.GetDishByIdHandler;
import com.restaurantbackend.handler.dish.GetPopularDishesHandler;
import com.restaurantbackend.handler.feedback.PostFeedbackHandler;
import com.restaurantbackend.handler.location.GetAllLocationsHandler;
import com.restaurantbackend.handler.location.GetFeedbacksByLocationIdHandler;
import com.restaurantbackend.handler.location.GetListLocationsHandler;
import com.restaurantbackend.handler.location.GetPopularDishesByLocationIdHandler;
import com.restaurantbackend.handler.profile.GetUserProfileHandler;
import com.restaurantbackend.handler.profile.PutUserProfileHandler;
import com.restaurantbackend.handler.reservation.*;
import com.restaurantbackend.service.*;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

@Module
public class HandlerModule {

    @Singleton
    @Provides
    @Named("general")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideRouteHandler(
            Map<String, RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>> handlerMap,
            @Named("not_found")RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> notFound
    ) {
        return new RouteHandler(handlerMap, notFound);
    }

    @Singleton
    @Provides
    @Named("not_found")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideNotFoundHandler() {
        return new NotFoundHandler();
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("POST:/auth/sign-in")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideSignInHandler(CognitoSupport cognitoSupport, UserService userService) {
        return new PostSignInHandler(cognitoSupport, userService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("POST:/auth/sign-up")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideSignUpHandler(CognitoSupport cognitoSupport, UserService userService, WaiterService waiterService) {
        return new PostSignUpHandler(cognitoSupport, userService, waiterService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/locations")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideGetAllLocationsHandler(LocationService locationService){
        return new GetAllLocationsHandler(locationService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("POST:/bookings/client")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> providePostReservationHandler(ReservationService reservationService, LocationService locationService,CognitoSupport cognitoSupport, WaiterService waiterService, TablesService tablesService){
        return new PostReservationsHandler(reservationService,locationService,cognitoSupport,waiterService, tablesService);
    }


    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/locations/select-options")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideGetSelectOptionsLocationsHandler(LocationService locationService){
        return new GetListLocationsHandler(locationService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/reservations")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideGetReservationsHandler(ReservationService reservationService, CognitoSupport cognitoSupport, LocationService locationService, UserService userService) {
        return new GetReservationsHandler(reservationService, cognitoSupport, locationService, userService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("DELETE:/reservations/{id}")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideDeleteReservationByIdHandler(ReservationService reservationService) {
        return new DeleteReservationByIdHandler(reservationService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/dishes/popular")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> providePopularDishesHandler(DishService dishService) {
        return new GetPopularDishesHandler(dishService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/locations/{id}/speciality-dishes")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideGetPopularDishesByLocationHandler(DishService dishService) {
        return new GetPopularDishesByLocationIdHandler(dishService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/users/profile")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideGetUserProfileHandler(UserService userService, CognitoSupport cognitoSupport, WaiterService waiterService,S3Service s3Service) {
        return new GetUserProfileHandler(userService, cognitoSupport, waiterService,s3Service);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("PUT:/users/profile")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> providePutUserProfileHandler(UserService userService, CognitoSupport cognitoSupport,S3Service s3Service) {
        return new PutUserProfileHandler(userService, cognitoSupport,s3Service);
    }


    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/bookings/tables")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideGetTablesHandler(TablesService tablesService, ReservationService reservationService, LocationService locationService){
        return new GetTablesHandler(tablesService, reservationService, locationService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/locations/{id}/feedbacks")
    public RequestHandler<APIGatewayProxyRequestEvent , APIGatewayProxyResponseEvent> provideFeedbackLocationHandler(FeedbackService feedbackService , ReservationService reservationService, UserService userService, S3Service s3Service)
    {
        return new GetFeedbacksByLocationIdHandler( reservationService , feedbackService, userService, s3Service);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/dishes/{id}")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideGetDishByIdHandler(DishService dishService) {
        return new GetDishByIdHandler(dishService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/dishes")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> provideGetAllDishesHandler(DishService dishService) {
        return new GetAllDishesHandler(dishService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("POST:/feedbacks")
    public RequestHandler<APIGatewayProxyRequestEvent , APIGatewayProxyResponseEvent> providePostFeedbackHandler(FeedbackService feedbackService, WaiterService waiterService, ReservationService reservationService)
    {
        return new PostFeedbackHandler(feedbackService, waiterService, reservationService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("POST:/bookings/waiter")
    public RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> providePostReservationByWaiterHandler(ReservationService reservationService,WaiterService waiterService,CognitoSupport cognitoSupport,LocationService locationService,UserService userService,TablesService tablesService)
    {
        return new PostReservationByWaiterHandler(reservationService,waiterService,cognitoSupport,locationService,userService,tablesService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("PUT:/bookings/client")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> providePutReservationHandler(TablesService tablesService, ReservationService reservationService, LocationService locationService) {
        return new PutReservationHandler(tablesService, reservationService, locationService);
    }


    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/reservations/waiter")
    public RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> providesGetReservationOfWaiterHandler(ReservationService reservationService,WaiterService waiterService,CognitoSupport cognitoSupport,LocationService locationService,UserService userService)
    {
        return new GetReservationOfWaiterHandler(reservationService,waiterService,cognitoSupport,locationService,userService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/reservations/{id}/available-dishes")
    public RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> provideGetAvailableDishesHandler(DishService dishService, ReservationService reservationService, CognitoSupport cognitoSupport, UserService userService)
    {
        return new GetAvailableDishesHandler(dishService, reservationService, cognitoSupport, userService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("POST:/reservations/{id}/order/{dishId}")
    public RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> providePostDishToCartHandler(DishService dishService, ReservationService reservationService, OrderService orderService)
    {
        return new PostDishToCartHandler(reservationService,dishService, orderService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/cart")
    public RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> provideGetCartHandler(DishService dishService, ReservationService reservationService, OrderService orderService, LocationService locationService, CognitoSupport cognitoSupport)
    {
        return new GetCartHandler(cognitoSupport, reservationService, orderService, dishService, locationService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("PUT:/cart")
    public RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> providePutCartHandler(DishService dishService, ReservationService reservationService, OrderService orderService, CognitoSupport cognitoSupport)
    {
        return new PutCartHandler(cognitoSupport, reservationService, orderService, dishService);
    }
    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/waiterDetails")
    public RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> providesGetWaiterDetailByReservationIdHandler(WaiterService waiterService,UserService userService, S3Service s3Service)
    {
        return new GetWaiterDetailsByReservationId(waiterService,userService, s3Service);
    }
    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/cart/{reservationId}")
    public RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> providesGetCartOfParticularReservationId(ReservationService reservationService,OrderService orderService,DishService dishService, LocationService locationService)
    {
        return new GetCartOfParticularReservationId(reservationService,orderService,dishService,locationService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/feedbacks/{id}")
    public RequestHandler<APIGatewayProxyRequestEvent , APIGatewayProxyResponseEvent> provideGetFeedbackByReservationIdHandler(FeedbackService feedbackService)
    {
        return new GetFeedbackByReservationIdHandler(feedbackService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("PUT:/users/profile/password")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> providePasswordChangeHandler(CognitoSupport cognitoSupport) {
        return new PasswordChangeHandler(cognitoSupport);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/reservations/{id}")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> providesGetReservationByIdHandler(ReservationService reservationService, LocationService locationService) {
        return new GetReservationByIdHandler(reservationService,locationService);
    }

    @Singleton
    @Provides
    @IntoMap
    @StringKey("GET:/reports")
    public RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>  provideGetReports(ReportService reportService) {
        return new GetReportsHandler(reportService);
    }

}
