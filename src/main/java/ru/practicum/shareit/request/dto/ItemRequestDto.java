package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {

    private long id;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    @NotBlank
    private UserDto requestor;
    @Past
    private LocalDateTime created;
}
