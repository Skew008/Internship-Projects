# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2025-04-08
### Added
    -  Added items

### Changed
    ReservationService
    PostReservationHandler
    HandlerModule
    WaiterService
### Removed
    -  Removed items 

## [2.1.0] - 2025-04-08
### Added
    -  Added GetAllDishesHandler
    -  Added GetDishByIdHandler
    -  Added getAllDishesByTypeAndSort service in DishService

### Changed
    -  Changed the sales table
    -  Changed getPopularDishes service
    -  Changed deployment_resources

## [2.2.0] - 2025-04-08
### Added
    -  Added PutProfileHandler
    -  Added updateUserInfo method in userService

### Changed
    -  Changed the userService

## [2.3.0] - 2025-04-09
### Added
    -  Added PostFeedbackHandler
    -  Added Feedback DTO

### Changed
    -  changed FeedbackService
    -  changed handlerModule

## [2.3.1] - 2025-04-09
### Changed
    - changed cuisine feedback condition in postFeedback method

## [2.3.2] - 2025-04-08
### Added
    - Added sqs queue in the deployment_resources
### Changed
    - Changed CognitoSupport for customized exceptions messages

## [2.3.2] - 2025-04-09
### Added
    - Added PostReservationByWaiterHandler
### Changed
    - Changed HandlerModule

## [2.3.3] - 2025-04-10
### Changed
    - Fixed the dish handler
    - Changed the logic for reservation status 

## [2.3.4] - 2025-04-10
### Changed
    - Updated name of sqs queue
    - Added env variables to api-handler

## [2.3.5] - 2025-04-10
### Changed
    - Modified sales table and changed dish service for popular dishes

## [2.4.5] - 2025-04-11
### Added
    - GetReservationOfWaiterHandler
### Changed
    -ReservationService
    -Handler Mofdule
    -deployment_resources.json

## [2.4.6] - 2025-04-11
### Added
    - Added OnStop Logic 
### Changed
    - Changed DishService and GetDishByHandler
    - Handled edge cases for specialty dishes

## [2.5.0] - 2025-04-11
### Added
    - Added  GetAvailableDishesHandler 
### Changed
    - Changed Reservation and Dish services
    - Updated deployment_resources.json

## [2.5.1] - 2025-04-11
### Changed
    - Changed Error Messages for password

## [2.6.0] - 2025-04-11
### Added
    - Added order service and dish to cart handler
### Changed
    - Updated service module and handler module
    - Changed reservation service

## [2.6.1] - 2025-04-11
### Added
    - Added get cart handler
### Changed
    - Updated service module and handler modules
    - Changed reservation, order and dish services

## [2.6.2] - 2025-04-12
### Added
    - Added put cart handler
### Changed
    - Updated service module and handler modules
    - Changed reservation, order and dish services

## [2.7.0] - 2025-04-14
### Added
    - Added GetWaiterDetailsByReservationHandler
### Changed
    - added getWaiterDetailsByReservationId method in waiter service 

## [2.7.1] - 2025-04-14
### Changed
    - Updated waiter service and feedback service for feedback updation
    - Fixed PostFeedbackHandler

## [2.8.0] - 2025-04-15
### Added
    - Added S3Service
    - Added PutUserProfileHandler 
### Changed
    - added updateUserInfo method in UserService
    - added Environment variable USER_PROFILE_IMAGE_BUCKET in ApiHandler
    - added AmazonS3 and S3Service in Service Module
## [2.9.0] - 2025-04-16
### Added
    - Added GetCartOfParticularResrvationIdHandler

## [2.10.0] - 2025-04-16

### Changed
    - updated getUserProfileHandler
    - added getBase64ImageFromS3 method in s3Service

## [2.11.0] - 2025-04-16

### Changed
    - added getFeedbackByReservationId method in Feedback Service
    -added providesFeedbackByReservationId method in HandlerModule class
    -updated deployment_resources.json 
### Added
    -added getFeedbackByReservationIdHandler class

## [2.12.0] - 2025-04-16

### Added
    - added PasswordChangeHandler class
### Changed
    - Updated cognito support for the change password functionality

## [2.13.0] - 2025-04-17

### Added
    - added Report Service class
    - added Report Handler
    - added Report Sender Handler
### Changed
    - API Handler
    - Service Module
    - Handler Module
    - Feedback Service

## [2.13.1] - 2025-04-18

### Changed
    - Minor fixes in Report Service

## [2.13.2] - 2025-04-21

### Added
    - GetReservationByIdHandler 
    - PutReservationHandler
### Changed
    - Reservation Service
    - Handler Module
    - Deployment Resources