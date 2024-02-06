package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;

@SpringBootTest
class ErrorHandlerTest {

    @Autowired
    private ErrorHandler errorHandler;

    @Test
    void sQLExceptionTest() {
        ErrorResponse error = errorHandler.handleException(new SQLException("message"));
        Assertions.assertEquals(error.getErrors().get(0).getMessage(), "message");
    }
}