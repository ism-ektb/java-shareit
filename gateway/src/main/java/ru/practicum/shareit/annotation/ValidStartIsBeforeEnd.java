package ru.practicum.shareit.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingStartIsBeforeEnd.class)
public @interface ValidStartIsBeforeEnd {
    String message() default "Начало бронирования должно быть раньше конца бронирования";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}