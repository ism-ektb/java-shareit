package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onFoundException(final NoFoundException e) {
        return new ErrorResponse(List.of(new Error("error", e.getMessage())));
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onValidationException(final ValidationException e) {
        return new ErrorResponse(List.of(new Error("error", e.getMessage())));
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrResponse onIllegalArgumentException(final ConversionFailedException e) {
        log.warn("Unknown state: {} ", e.getMessage());
        return new ErrResponse("Unknown state: " + e.getValue());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onConstraintValidationException(MethodArgumentNotValidException e) {
        final List<Error> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Error(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        if (!(errors.isEmpty())) {
            log.warn(errors.toString());
            return new ErrorResponse(errors);
        }
        final List<Error> errorsOther = e.getAllErrors().stream()
                .map(error -> new Error(error.getCode(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.warn(errorsOther.toString());
        return new ErrorResponse(errorsOther);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onConstraintViolationException(ConstraintViolationException e) {
        final List<Error> errors = e.getConstraintViolations().stream()
                .map(error -> new Error(error.getPropertyPath().toString(), error.getMessage()))
                .collect(Collectors.toList());
        log.warn(errors.toString());
        return new ErrorResponse(errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(SQLException e) {
        log.info("Нарушение условмя уникальности поля {}", e.getMessage());
        return new ErrorResponse(List.of(new Error("Нарушение " +
                "условмя уникальности поля {}", e.getMessage())));
    }


}
