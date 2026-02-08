package ru.yandex.practicum;

import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private String answer;
    private WordleDictionary dictionary;
    private int attemptsLeft;
    private List<String> guesses;
    private List<String> hints;

    private Set<Character> correctLetters;
    private Set<Character> wrongLetters;
    private Map<Integer, Character> knownPosition;
    private Map<Integer, Set<Character>> wrongPosition;

    public WordleGame(WordleDictionary dictionary) {
        this.answer = dictionary.getRandomWord();
        this.dictionary = dictionary;
        this.attemptsLeft = 6;
        this.guesses = new ArrayList<>();
        this.hints = new ArrayList<>();

        this.correctLetters = new HashSet<>();
        this.wrongLetters = new HashSet<>();
        this.knownPosition = new HashMap<>();
        this.wrongPosition = new HashMap<>();
    }

    public String getAnswer() {
        return answer;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public String getHints() {
        return getHint();
    }

    public boolean isGameOver() {
        return attemptsLeft <= 0;
    }

    public boolean isWin() {
        return !guesses.isEmpty() && guesses.get(guesses.size() - 1).equals(answer);
    }

    public String makeGuess(String userInput) throws GameException {
        String guess = normalizeInput(userInput);

        if (guess.length() != 5) {
            throw new InvalidWordLengthException(5, guess.length());
        }

        if (!dictionary.contains(guess)) {
            throw new WordNotInDictionaryException(guess);
        }

        attemptsLeft--;
        guesses.add(guess);

        String hint = generateHint(guess);
        hints.add(hint);

        hintInfo(guess, hint);

        return hint;
    }

    private void hintInfo(String guess, String hint) {
        for (int i = 0; i < hint.length(); i++) {
            char hintChar = hint.charAt(i);
            char guessChar = guess.charAt(i);

            if (hintChar == '+') {
                knownPosition.put(i, guessChar);
                correctLetters.add(guessChar);
            } else if (hintChar == '^') {
                correctLetters.add(guessChar);
                wrongPosition.computeIfAbsent(i, k -> new HashSet<>()).add(guessChar);
            } else if (hintChar == '-') {
                if (!correctLetters.contains(guessChar)) {
                    wrongLetters.add(guessChar);
                }
            }
        }
    }

    private String getHint() {
        List<String> possibleWords = findPossibleWords();

        if (possibleWords.isEmpty()) {
            return "Нет подходящих слов";
        }

        possibleWords.removeAll(guesses);

        Random random = new Random();
        String hintWord = possibleWords.get(random.nextInt(possibleWords.size()));
        return hintWord;
    }

    private List<String> findPossibleWords() {
        List<String> possible = new ArrayList<>();

        for (String word : dictionary.getWords()) {
            if (isWordPossible(word)) {
                possible.add(word);
            }
        }
        return possible;
    }

    //проверить буквы, которых нет
    //проверить известные позиции
    //проверить известные буквы
    private boolean isWordPossible(String word) {
        for (char c : word.toCharArray()) {
            if (wrongLetters.contains(c)) {
                return false;
            }
        }

        for (Map.Entry<Integer, Character> entry : knownPosition.entrySet()) {
            int position = entry.getKey();
            char expectedLetter = entry.getValue();

            if (word.charAt(position) != expectedLetter) {
                return false;
            }
        }

        for (char c : correctLetters) {
            if (word.indexOf(c) == -1) {
                return false;
            }
        }

        for (Map.Entry<Integer, Set<Character>> entry : wrongPosition.entrySet()) {
            int position = entry.getKey();
            Set<Character> chars = entry.getValue();

            if (chars.contains(word.charAt(position))) {
                return false;
            }
        }

        return true;
    }

    private String generateHint(String guess) {
        StringBuilder result = new StringBuilder();
        char[] answerChar = answer.toCharArray();
        char[] guessChar = guess.toCharArray();
        boolean[] used = new boolean[answer.length()];

        for (int i = 0; i < guess.length(); i++) {
            if (answerChar[i] == guessChar[i]) {
                result.append('+');
                used[i] = true;
            } else {
                result.append(' ');
            }
        }

        for (int i = 0; i < guess.length(); i++) {
            if (result.charAt(i) == '+') {
                continue;
            }

            char c = guessChar[i];
            boolean found = false;

            for (int j = 0; j < answer.length(); j++) {
                if (!used[j] && answerChar[j] == c) {
                    result.setCharAt(i, '^');
                    used[j] = true;
                    found = true;
                    break;
                }
            }

            if (!found) {
                result.setCharAt(i, '-');
            }
        }
        return result.toString();
    }

    private String normalizeInput(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.toLowerCase()
                .replace('ё', 'е')
                .trim();
    }
}
