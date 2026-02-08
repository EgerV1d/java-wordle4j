package ru.yandex.practicum;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordleDictionaryLoader {
    private final PrintWriter log;
    private final int wordLength;

    public WordleDictionaryLoader(PrintWriter log, int wordLength) {
        this.log = log;
        this.wordLength = wordLength;
    }

    public WordleDictionary loadDictionary(String filename) throws IOException {
        log.println("Начинаю загрузку словаря");

        Set<String> uniqueWords = new HashSet<>();
        int lineCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {

                lineCount++;
                String normalized = normalizeWord(line);

                if (!normalized.isEmpty()) {
                    if (normalized.length() == wordLength) {
                        uniqueWords.add(normalized);
                    }
                }
            }

            List<String> words = new ArrayList<>(uniqueWords);
            log.println("Осталось слов: " + words.size());

            return new WordleDictionary(words);
        }
    }

    private String normalizeWord(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }

        return word.toLowerCase()
                .replace('ё', 'е')
                .trim();
    }
}
