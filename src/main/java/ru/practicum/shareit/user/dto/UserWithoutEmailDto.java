package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class UserWithoutEmailDto {

    @NotNull
    @Positive
    private long id;
    private String name;


}
