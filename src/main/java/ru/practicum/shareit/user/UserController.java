package ru.practicum.shareit.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.FormatDataException;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.item.dto.OnCreateGroup;
import ru.practicum.shareit.item.dto.OnPatchGroup;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /**
     * запись в репозитория нового объекта класса User с присвоением нового id
     *
     * @throws java.sql.SQLException если объект с таким емаил уже существует
     */
    @PostMapping
    public UserDto createUserDto(@Validated(OnCreateGroup.class) @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    /**
     * получить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    @GetMapping("/{id}")
    public UserDto getUserDtoById(@PathVariable("id") long userId) {
        return userService.findUserById(userId);
    }

    /**
     * получение списка всех юзеров
     */
    @GetMapping
    public List<UserDto> getUserDtos() {
        return userService.findAllUsers();
    }

    /**
     * обновление данных объекта класса User
     *
     * @throws java.sql.SQLException если объект с таким емаил уже существует
     * @throws NoFoundException    передан id не существующего объекта
     */
    @PatchMapping("/{id}")
    public UserDto updateUserDto(@PathVariable("id") long userId,
                                 @Validated(OnPatchGroup.class) @RequestBody UserDto userDto) {
        return userService.updateUser(userDto, userId);
    }

    /**
     * удалить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@NonNull @PathVariable("id") @NotNull long userId) {
        userService.deleteUser(userId);
    }

}
