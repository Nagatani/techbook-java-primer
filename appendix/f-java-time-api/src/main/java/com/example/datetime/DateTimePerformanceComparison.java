import java.time.*;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class DateTimePerformanceComparison {
    private static final int ITERATIONS = 100_000;
    private static final int THREAD_COUNT = 10;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("性能比較: レガシーAPI vs java.time API");
        System.out.println("反復回数: " + ITERATIONS);
        System.out.println("スレッド数: " + THREAD_COUNT);
        System.out.println();
        
        // 1. オブジェクト生成の性能比較
        compareObjectCreation();
        
        // 2. フォーマット処理の性能比較
        compareFormatting();
        
        // 3. パース処理の性能比較
        compareParsing();
        
        // 4. 日付計算の性能比較
        compareDateCalculation();
        
        // 5. マルチスレッド環境での性能比較
        compareMultithreadedFormatting();
    }
    
    private static void compareObjectCreation() {
        System.out.println("=== オブジェクト生成の性能比較 ===");
        
        // レガシーAPI
        long startLegacy = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
        long legacyTime = System.nanoTime() - startLegacy;
        
        // java.time API
        long startModern = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            LocalDateTime dateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = ZonedDateTime.now();
        }
        long modernTime = System.nanoTime() - startModern;
        
        printResults("オブジェクト生成", legacyTime, modernTime);
    }
    
    private static void compareFormatting() {
        System.out.println("\n=== フォーマット処理の性能比較 ===");
        
        Date legacyDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        LocalDateTime modernDate = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // レガシーAPI
        long startLegacy = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String formatted = sdf.format(legacyDate);
        }
        long legacyTime = System.nanoTime() - startLegacy;
        
        // java.time API
        long startModern = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            String formatted = modernDate.format(dtf);
        }
        long modernTime = System.nanoTime() - startModern;
        
        printResults("フォーマット処理", legacyTime, modernTime);
    }
    
    private static void compareParsing() {
        System.out.println("\n=== パース処理の性能比較 ===");
        
        String dateString = "2024-12-25 14:30:45";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // レガシーAPI
        long startLegacy = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            try {
                Date parsed = sdf.parse(dateString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long legacyTime = System.nanoTime() - startLegacy;
        
        // java.time API
        long startModern = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            LocalDateTime parsed = LocalDateTime.parse(dateString, dtf);
        }
        long modernTime = System.nanoTime() - startModern;
        
        printResults("パース処理", legacyTime, modernTime);
    }
    
    private static void compareDateCalculation() {
        System.out.println("\n=== 日付計算の性能比較 ===");
        
        // レガシーAPI
        Calendar calendar = Calendar.getInstance();
        long startLegacy = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.YEAR, -1);
            Date result = calendar.getTime();
        }
        long legacyTime = System.nanoTime() - startLegacy;
        
        // java.time API
        long startModern = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            LocalDateTime dateTime = LocalDateTime.now()
                .plusDays(7)
                .plusMonths(1)
                .minusYears(1);
        }
        long modernTime = System.nanoTime() - startModern;
        
        printResults("日付計算", legacyTime, modernTime);
    }
    
    private static void compareMultithreadedFormatting() throws InterruptedException {
        System.out.println("\n=== マルチスレッド環境でのフォーマット性能比較 ===");
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        
        // レガシーAPI（スレッドセーフでない）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date legacyDate = new Date();
        AtomicInteger legacyErrors = new AtomicInteger(0);
        
        long startLegacy = System.nanoTime();
        for (int t = 0; t < THREAD_COUNT; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < ITERATIONS / THREAD_COUNT; i++) {
                        try {
                            String formatted = sdf.format(legacyDate);
                            Date parsed = sdf.parse(formatted);
                        } catch (Exception e) {
                            legacyErrors.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        long legacyTime = System.nanoTime() - startLegacy;
        
        // java.time API（スレッドセーフ）
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime modernDate = LocalDateTime.now();
        CountDownLatch latch2 = new CountDownLatch(THREAD_COUNT);
        AtomicInteger modernErrors = new AtomicInteger(0);
        
        long startModern = System.nanoTime();
        for (int t = 0; t < THREAD_COUNT; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < ITERATIONS / THREAD_COUNT; i++) {
                        try {
                            String formatted = modernDate.format(dtf);
                            LocalDateTime parsed = LocalDateTime.parse(formatted, dtf);
                        } catch (Exception e) {
                            modernErrors.incrementAndGet();
                        }
                    }
                } finally {
                    latch2.countDown();
                }
            });
        }
        latch2.await();
        long modernTime = System.nanoTime() - startModern;
        
        executor.shutdown();
        
        System.out.println("レガシーAPI: " + formatTime(legacyTime) + 
                          " (エラー数: " + legacyErrors.get() + ")");
        System.out.println("java.time API: " + formatTime(modernTime) + 
                          " (エラー数: " + modernErrors.get() + ")");
        
        double improvement = ((double) legacyTime / modernTime - 1) * 100;
        System.out.println("改善率: " + String.format("%.1f%%", improvement));
        
        if (legacyErrors.get() > 0) {
            System.out.println("警告: レガシーAPIでスレッドセーフ性の問題が発生しました！");
        }
    }
    
    private static void printResults(String operation, long legacyTime, long modernTime) {
        System.out.println("レガシーAPI: " + formatTime(legacyTime));
        System.out.println("java.time API: " + formatTime(modernTime));
        
        double improvement = ((double) legacyTime / modernTime - 1) * 100;
        System.out.println("改善率: " + String.format("%.1f%%", improvement));
    }
    
    private static String formatTime(long nanos) {
        double millis = nanos / 1_000_000.0;
        return String.format("%.2f ms", millis);
    }
}