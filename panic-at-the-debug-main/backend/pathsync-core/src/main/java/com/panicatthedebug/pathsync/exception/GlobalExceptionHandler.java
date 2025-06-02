package com.panicatthedebug.pathsync.exception;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.NonNullApi;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    static final String MESSAGE = "message";

//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            @NonNull MethodArgumentNotValidException ex,
//            @NonNull HttpHeaders headers,
//            @NonNull HttpStatusCode status,
//            @NonNull WebRequest request) {
//        return handleExceptionInternal(ex, Map.of(MESSAGE, ex.getMessage()),
//                headers,
//                HttpStatus.BAD_REQUEST,
//                request
//        );
//    }

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            ValidationException.class,
            InvalidOperationException.class,
            SurveyNotCompleteException.class,
            LearnPathAlreadyExistsException.class
    })
    public ResponseEntity<Object> handleBadRequest(Exception e, WebRequest webRequest) {
        System.out.println(e.getMessage());
        return handleExceptionInternal(e, Map.of(MESSAGE, e.getMessage()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                webRequest
        );
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            LearnPathNotFoundException.class,
            QuestionNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFound(Exception e, WebRequest webRequest) {
        return handleExceptionInternal(e, Map.of(MESSAGE, e.getMessage()),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                webRequest
        );
    }

    @ExceptionHandler({
            UnauthorizedException.class,
            AccessDeniedException.class
    })
    public ResponseEntity<Object> handleUnauthorized(Exception e, WebRequest webRequest) {
        return handleExceptionInternal(e, Map.of(MESSAGE, e.getMessage()),
                new HttpHeaders(),
                HttpStatusCode.valueOf(401),
                webRequest
        );
    }

    @ExceptionHandler({
            Exception.class,
    })
    public ResponseEntity<Object> handleServerError(Exception e, WebRequest webRequest) {
        System.out.println(e.getMessage());
        System.out.println(Arrays.toString(e.getStackTrace()));
        return handleExceptionInternal(e, Map.of(MESSAGE, "An unknown error occurred"),
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                webRequest
        );
    }
}
