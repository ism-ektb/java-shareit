package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
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

    @ExceptionHandler(FormatDataException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse onFormatError(final FormatDataException e) {
        return new ErrorResponse(List.of(new Error("error", e.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onConstraintValidationException(MethodArgumentNotValidException e) {
        final List<Error> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Error(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.warn(errors.toString());
        return new ErrorResponse(errors);
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


}
