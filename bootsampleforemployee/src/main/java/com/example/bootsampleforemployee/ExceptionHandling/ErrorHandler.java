package com.example.bootsampleforemployee.ExceptionHandling;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger("ErrorHandler");
    @ExceptionHandler({EmployeeNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(Exception e, WebRequest webRequest) {

        logger.error(e.getMessage());
        return handleExceptionInternal(
                e, e.getMessage(),
                new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest
        );
    }

    @ExceptionHandler({
            EmployeeIdMismatchException.class,
            DepartmentNotFoundException.class,
            NullPointerException.class,
            IllegalArgumentException.class,
    })
    public ResponseEntity<Object> handleBadRequest(Exception e, WebRequest webRequest) {

        logger.error(e.getMessage());
        return handleExceptionInternal(
                e, e.getMessage(),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest
        );
    }

}
