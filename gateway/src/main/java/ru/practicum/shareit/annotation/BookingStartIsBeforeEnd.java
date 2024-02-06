package ru.practicum.shareit.annotation;

import ru.practicum.shareit.booking.dto.SimpleBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingStartIsBeforeEnd implements ConstraintValidator<ValidStartIsBeforeEnd, SimpleBookingDto> {

    public void initialize(ValidStartIsBeforeEnd constraintAnnotation) {
    }

    public boolean isValid(SimpleBookingDto simpleBookingDto, ConstraintValidatorContext constraintContext) {
        return simpleBookingDto.getStart() == null || simpleBookingDto.getEnd() == null ||
             simpleBookingDto.getStart().isBefore(simpleBookingDto.getEnd());
    }
}
