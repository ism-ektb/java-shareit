package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NoFoundException;
import ru.practicum.shareit.user.dto.OnCreateGroup;
import ru.practicum.shareit.user.dto.OnPatchGroup;
import ru.practicum.shareit.user.dto.UserDto;


/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient client;

    /**
     * запись в репозитория нового объекта класса User с присвоением нового id
     *
     * @throws java.sql.SQLException если объект с таким емаил уже существует
     */
    @PostMapping
    public ResponseEntity<Object> createUserDto(@Validated(OnCreateGroup.class) @RequestBody UserDto userDto) {
        return client.createUser(userDto);
    }

    /**
     * получить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserDtoById(@PathVariable("id") long userId) {
        return client.findUserById(userId);
    }

    /**
     * получение списка всех юзеров
     */
    @GetMapping
    public ResponseEntity<Object> getUserDtos() {
        return client.findAllUsers();
    }

    /**
     * обновление данных объекта класса User
     *
     * @throws java.sql.SQLException если объект с таким емаил уже существует
     * @throws NoFoundException    передан id не существующего объекта
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUserDto(@PathVariable("id") long userId,
                                                @Validated(OnPatchGroup.class) @RequestBody UserDto userDto) {
        return client.updateUser(userId, userDto);
    }

    /**
     * удалить объект по переданному id
     *
     * @throws NoFoundException передан id не существующего объекта
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") long userId) {
        return  client.deleteUser(userId);
    }

}
