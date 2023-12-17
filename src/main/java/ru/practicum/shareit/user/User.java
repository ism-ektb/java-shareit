package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * POJO класс для описания сущности User
 */
@Data
@Builder
public class User {
    @EqualsAndHashCode.Exclude
    private Long id;
    @EqualsAndHashCode.Exclude
    private String name;
    private String email;
}
