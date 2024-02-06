package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.OnCreateGroup;
import ru.practicum.shareit.item.dto.OnPatchGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * DTO - класс для описания сущности User
 */
@Data
@Builder
public class UserDto {

    private Long id;
    @NotBlank(groups = OnCreateGroup.class)
    private String name;
    @Email(groups = {OnCreateGroup.class, OnPatchGroup.class})
    @NotBlank(groups = OnCreateGroup.class)
    private String email;

}
