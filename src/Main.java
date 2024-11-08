import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(25);
        List<Future<Integer>> futures = new ArrayList<>();

        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            final int index = i;// Зафиксируем значение индекса в отдельной переменной
            Callable<Integer> logic = () -> {
                texts[index] = generateText("aab", 30_000); // используем переменную с зафиксированным индексом
                return findMaxInterval(texts[index]);
            };
            futures.add(threadPool.submit(logic)); // отправляем задачу в пул потоков и добавляем Future в список
        }
        long startTs = System.currentTimeMillis();// start time
        threadPool.shutdown();

        int maxInterval = 0; // переменная максимального значения размеров интервалов
        for (Future future : futures) {
            int result = (int) future.get();
            maxInterval = Math.max(maxInterval, result);
        }
        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
        System.out.println("Max interval: " + maxInterval);
    }

    private static Integer findMaxInterval(String text) {
        int maxSize = 0;
        for (int i = 0; i < text.length(); i++) {
            for (int j = 0; j < text.length(); j++) {
                if (i >= j) {
                    continue;
                }
                boolean bFound = false;
                for (int k = i; k < j; k++) {
                    if (text.charAt(k) == 'b') {
                        bFound = true;
                        break;
                    }
                }
                if (!bFound && maxSize < j - i) {
                    maxSize = j - i;
                }
            }
        }
        return maxSize;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

}