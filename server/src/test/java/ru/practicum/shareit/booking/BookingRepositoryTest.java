package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository repository;

    @Test
    void findAllByBookerIdOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndAndStatusEqualsOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndAndEndBeforeOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc() {
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndAndStatusEqualsOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndAndEndBeforeOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndStartAfterOrderByStartDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
    }

    @Test
    void findAllLast() {
    }

    @Test
    void findAllNext() {
    }

    @Test
    void findAllFinishByBookerIdByItemId() {
    }
}