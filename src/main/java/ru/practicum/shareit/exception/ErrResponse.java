package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter

public class ErrResponse {
    private final String error;

    public ErrResponse(String error) {
        this.error = error;
    }

}
