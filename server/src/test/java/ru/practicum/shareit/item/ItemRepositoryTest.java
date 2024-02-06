package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository repository;

    @Test
    void findItemsByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrueOrderById() {
    }

    @Test
    void findItemsByOwnerEqualsOrderById() {
    }

    @Test
    void findAllByRequestId() {
    }
}