package ru.yandex.practicum;

public class WordNotInDictionaryException extends GameException {
    public WordNotInDictionaryException(String word) {
        super("Слово "+ word + " не содержится в словаре");
    }
}
