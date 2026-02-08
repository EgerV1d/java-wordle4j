package ru.yandex.practicum;

public class InvalidWordLengthException extends GameException {
    public InvalidWordLengthException(int expected, int actual) {
        super("Слово должно содержать " + expected + " букв, а содержит " + actual);
    }
}
