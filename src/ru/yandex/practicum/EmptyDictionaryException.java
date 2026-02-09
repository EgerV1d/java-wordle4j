package ru.yandex.practicum;

import java.io.IOException;

public class EmptyDictionaryException extends IOException {
    public EmptyDictionaryException(String message) {
        super(message);
    }
}
