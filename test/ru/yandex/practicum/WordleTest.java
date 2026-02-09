package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private static WordleDictionary dictionary;

    @BeforeAll
    static void setUp() throws IOException {
        File file = new File("words_ru.txt");
        PrintWriter log = new PrintWriter(System.out);
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log, 5);
        dictionary = loader.loadDictionary("words_ru.txt");

        if (dictionary == null) {
            System.out.println("Ошибка, словарь не загружен");
            return;
        }
    }

    @Test
    void testGameCreation() {

        WordleGame game = new WordleGame(dictionary);

        assertNotNull(game.getAnswer());
        assertEquals(WordleGame.WORD_LENGTH, game.getAnswer().length());
        assertEquals(WordleGame.MAX_ATTEMPTS, game.getAttemptsLeft());
        assertFalse(game.isGameOver());
        assertFalse(game.isWin());
    }

    @Test
    void testGameWinByOneStep() throws GameException {
        WordleGame game = new WordleGame(dictionary);
        String answer = game.getAnswer();
        String res = game.makeGuess(answer);

        StringBuilder expected = new StringBuilder();

        for (int i = 0; i < WordleGame.WORD_LENGTH; i++) {
            expected.append(WordleGame.CORRECT_POSITION);
        }
        assertEquals(expected.toString(), res);
        assertTrue(game.isWin());
    }

    @Test
    void testGameNotWinByOneStep() throws GameException {
        WordleGame game = new WordleGame(dictionary);
        String answer = game.getAnswer();

        List<String> allWords = dictionary.getWords();
        List<String> wrongWords = new ArrayList<>(allWords);
        wrongWords.remove(answer);

        Random random = new Random();
        String wrongWord = wrongWords.get(random.nextInt(wrongWords.size()));
        String res = game.makeGuess(wrongWord);

        StringBuilder unExpected = new StringBuilder();

        for (int i = 0; i < WordleGame.WORD_LENGTH; i++) {
            unExpected.append(WordleGame.CORRECT_POSITION);
        }

        assertNotEquals(unExpected.toString(), res);
        assertFalse(game.isWin());
    }

    @Test
    void testHints() {
        WordleGame game = new WordleGame(dictionary);

        String hint1 = game.getHints();
        assertNotNull(hint1);

        try {
            String randomWord = dictionary.getRandomWord();
            if (!randomWord.equals(game.getAnswer())) {
                game.makeGuess(randomWord);
                String hint2 = game.getHints();
                assertNotNull(hint2);
            }
        } catch (GameException e) {

        }
    }

    @Test
    void testGameOver() throws GameException {
        WordleGame game = new WordleGame(dictionary);
        String answer = game.getAnswer();

        String wrongWord = "";
        for (String word : dictionary.getWords()) {
            if (!word.equals(answer)) {
                wrongWord = word;
                break;
            }
        }

        for (int i = 0; i < WordleGame.MAX_ATTEMPTS; i++) {
            game.makeGuess(wrongWord);
        }

        assertTrue(game.isGameOver());
        assertEquals(0, game.getAttemptsLeft());
        assertFalse(game.isWin());
    }
}

