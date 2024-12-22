import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class BookParser {
    public static void main(String[] args) {
        // Путь к текстовому файлу
        String filePath = "book.txt";

        // Набор стоп-слов
        Set<String> stopWords = Set.of("the", "and", "is", "a", "an", "of", "to", "in", "on", "for", "with", "at", "by");

        try {
            // Читаем содержимое файла в строку
            String content = Files.readString(Paths.get(filePath));

            // 1. Подсчет слов с игнорированием стоп-слов (используем параллельный поток)
            Map<String, Long> wordCount = Arrays.stream(content.split("\\W+"))
                    .parallel()
                    .map(String::toLowerCase)
                    .filter(word -> !stopWords.contains(word))
                    .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

            // 2. Поиск самого длинного слова и его количество вхождений
            String longestWord = wordCount.keySet().stream()
                    .filter(word -> word.length() > 0) // Убедиться, что слово не пустое
                    .max(Comparator.comparingInt(String::length))
                    .orElse("");

            long longestWordCount = wordCount.getOrDefault(longestWord, 0L);
            int longestWordLength = longestWord.length();

            // 3. Вывод информации о самом длинном слове
            System.out.println("Самое длинное слово: " + longestWord);
            System.out.println("Длина самого длинного слова: " + longestWordLength);
            System.out.println("Количество его вхождений: " + longestWordCount);
            System.out.println();

            // 4. Подсчет среднего количества слов в предложении
            String[] sentences = content.split("[.!?]");
            double avgWordsPerSentence = Arrays.stream(sentences)
                    .parallel()
                    .mapToInt(sentence -> sentence.split("\\W+").length)
                    .average()
                    .orElse(0.0);

            System.out.printf("Среднее количество слов в предложении: %.2f%n", avgWordsPerSentence);
            System.out.println();

            // 5. Слова длиной больше 10 символов
            System.out.println("Слова длиной более 10 символов:");
            wordCount.entrySet().stream()
                    .filter(entry -> entry.getKey().length() > 10)
                    .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}
