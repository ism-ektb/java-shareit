package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;


    @Test
    public void saveUserAndReturnWithId() {
        User user = User.builder().name("Имя").email("e@e.e").build();
        assertEquals(repository.save(user).getName(), user.getName());
    }

    @Test
    public void save_whenEmailIsNotUniq() {
        User user = User.builder().name("Имя").email("e@e.e").build();
        assertEquals(repository.save(user).getName(), user.getName());
        User user3 = User.builder().name("Имя2").email("e@e.e").build();
        final DataIntegrityViolationException e =
                assertThrows(DataIntegrityViolationException.class, () -> repository.save(user3));

    }
}
