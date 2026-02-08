package ru.yandex.practicum;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) throws IOException {

        try (PrintWriter log = new PrintWriter("game.log", StandardCharsets.UTF_8)) {
            log.println("Старт игры");

            WordleDictionaryLoader loader = new WordleDictionaryLoader(log, 5);
            WordleDictionary dictionary = loader.loadDictionary("words_ru.txt");
            log.println("Проверка загрузки словаря. Слов: " + dictionary.size());

            if (dictionary.size() == 0) {
                throw new IOException("Словарь пуст");
            }

            WordleGame wordleGame = new WordleGame(dictionary);
            log.println("Загаданное слово: " + wordleGame.getAnswer());

            runGame(wordleGame, log);
        } catch (Exception e) {
            System.err.println("Критическая ошибка " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runGame(WordleGame wordleGame, PrintWriter log) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Начало игры.");
        System.out.println("Угадайте слово из 5 букв за 6 попыток");
        System.out.println("\"+\" - буква на месте, \"^\" - буква есть в слове, \"\" - буквы нет");

        try {
            while (!wordleGame.isGameOver() && !wordleGame.isWin()) {
                System.out.println("\nПопыток осталось: "+ wordleGame.getAttemptsLeft());
                System.out.print("Введите слово или Enter для подсказки: ");
                String guess = scanner.nextLine().trim();

                if (guess.isEmpty()) {
                    String hint = wordleGame.getHints();
                    System.out.println(hint);
                    log.println("Игрок запросил подсказку: " + hint);
                    continue;
                }

                try {
                    String result = wordleGame.makeGuess(guess);
                    System.out.println("Результат: " + result);

                    if (wordleGame.isWin()) {
                        System.out.println("Вы выиграли");
                        log.println("Игрок выиграл за " + (6 - wordleGame.getAttemptsLeft()) + " попыток");
                        break;
                    }
                } catch (GameException e) {
                    System.out.println("Ошибка ввода");
                    log.println("Ошибка ввода: " + guess + e.getMessage());
                }
            }

            if (!wordleGame.isWin() && wordleGame.isGameOver()) {
                System.out.println("Игра проиграна, закончились попытки");
                System.out.println("Загаданное слово: " + wordleGame.getAnswer());
                log.println("Проигрыш, слово: " + wordleGame.getAnswer());
            }
        } finally {
            scanner.close();
            log.println("Игра завершена");
        }
    }
}
