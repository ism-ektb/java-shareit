package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.dto.UserWithoutEmailDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * DTO Item. В поле owner конфеденциальные данные (email) не передается
 */
@Data
@Builder
@EqualsAndHashCode
public class ItemDto {

    private long id;
    @NotBlank(groups = {OnCreateGroup.class})
    private String name;
    @NotBlank(groups = {OnCreateGroup.class})
    private String description;
    @NotNull(groups = OnCreateGroup.class)
    private Boolean available;
    @Valid
    private UserWithoutEmailDto owner;
    private Long requestId;
}
