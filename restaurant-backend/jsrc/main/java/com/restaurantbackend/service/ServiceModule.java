package com.restaurantbackend.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sqs.*;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;

@Module
public class ServiceModule {

    @Singleton
    @Provides
    public AmazonDynamoDB providesDynamoDB() {
        return AmazonDynamoDBClientBuilder.defaultClient();
    }

    @Singleton
    @Provides
    public AWSCognitoIdentityProvider providesCognitoIdentity() {
        return AWSCognitoIdentityProviderClientBuilder.defaultClient();
    }

    @Singleton
    @Provides
    public AmazonSimpleEmailService providesAmazonSimpleEmailClient(){
        return AmazonSimpleEmailServiceClientBuilder.standard().build();
    }

    @Singleton
    @Provides
    public S3Service providesS3Service(AmazonS3 amazonS3){return new S3Service(amazonS3);}


    @Singleton
    @Provides
    public CognitoSupport providesCognitoSupport(AWSCognitoIdentityProvider awsCognitoIdentityProvider) {
        return new CognitoSupport(awsCognitoIdentityProvider);
    }

    @Singleton
    @Provides
    public DishService provideDishesService(AmazonDynamoDB dynamoDB) {
        return new DishService(dynamoDB);
    }

    @Singleton
    @Provides
    public LocationService provideLocationService(AmazonDynamoDB dynamoDB) {
        return new LocationService(dynamoDB);
    }

    @Singleton
    @Provides
    public ReservationService provideReservationService(AmazonDynamoDB dynamoDB) {
        return new ReservationService(dynamoDB);
    }

    @Singleton
    @Provides
    public TablesService provideTablesService(AmazonDynamoDB dynamoDB) {
        return new TablesService(dynamoDB);
    }

    @Singleton
    @Provides
    public UserService provideUserService(AmazonDynamoDB dynamoDB) {
        return new UserService(dynamoDB);
    }

    @Singleton
    @Provides
    public WaiterService providesWaiterService(AmazonDynamoDB dynamoDB) {return new WaiterService(dynamoDB);}

    @Singleton
    @Provides
    public FeedbackService providesFeedbackService(AmazonDynamoDB dynamoDB , AmazonSQS sqs) {
        return new FeedbackService(dynamoDB , sqs);
    }
    @Singleton
    @Provides
    public AmazonS3 provideAmazonS3(){
        return AmazonS3ClientBuilder.defaultClient();
    }


    @Singleton
    @Provides
    public ReportService providesReportService(AmazonDynamoDB dynamoDB , AmazonS3 client , UserService userService){
        return new ReportService(dynamoDB , client , userService);
    }


    @Singleton
    @Provides
    public AmazonSQS provideAmazonSqs(){
        return AmazonSQSClientBuilder.defaultClient();
    }

    @Singleton
    @Provides
    public OrderService providesOrderService(AmazonDynamoDB dynamoDB) {
        return new OrderService(dynamoDB);
    }

    @Singleton
    @Provides
    @Named("cors")
    Map<String, String> provideCorsHeaders() {
        return Map.of(
                "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token",
                "Access-Control-Allow-Origin", "*",
                "Access-Control-Allow-Methods", "*",
                "Accept-Version", "*"
        );
    }
}
