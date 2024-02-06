package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository repository;

    @Autowired
    private UserRepository userRepository;

    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private User user1;
    private User user2;

    @BeforeEach
    void addDate() {
        user1 = userRepository.save(User.builder().name("name1").build());
        itemRequest1 = repository.save(ItemRequest.builder().description("описание").requestor(user1).build());

        user2 = userRepository.save(User.builder().name("name2").build());
        itemRequest2 = repository.save(ItemRequest.builder().description("описание2").requestor(user2).build());
    }


    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {

        List<ItemRequest> list = repository
                .findAllByRequestorIdOrderByCreatedDesc(1L, PageRequest.of(0, 10));

        assertEquals(1, list.size());
        assertEquals(itemRequest1, list.get(0));
    }

    @Test
    void findAllByRequestorIdIsNotOrderByCreatedDesc() {
        List<ItemRequest> list = repository
                .findAllByRequestorIdIsNotOrderByCreatedDesc(3L, PageRequest.of(0, 10));

        assertEquals(1, list.size());
        assertEquals(itemRequest2, list.get(0));
    }

    @AfterEach
    void deleteAll() {
        repository.deleteAll();
        userRepository.deleteAll();
    }

}