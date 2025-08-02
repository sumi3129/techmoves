package com.example.techmoves.exception;
import com.example.techmoves.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse("Validation failed", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({OrderNotFoundException.class, InvalidCoordinateException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderAlreadyTakenException.class)
    public ResponseEntity<ErrorResponse> handleOrderAlreadyTaken(OrderAlreadyTakenException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({IllegalArgumentException.class, GoogleApiException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponse("An unexpected error occurred"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}