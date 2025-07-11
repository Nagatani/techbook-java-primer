/**
 * リスト16-15
 * ConcurrentCollectionsExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (1183行目)
 */

import java.util.concurrent.*;
import java.util.Map;
import java.util.List;
import java.util.Random;

public class ConcurrentCollectionsExample {
    
    public static void main(String[] args) throws InterruptedException {
        // 1. ConcurrentHashMap - スレッドセーフなマップ
        ConcurrentHashMap<String, Integer> wordCount = new ConcurrentHashMap<>();
        String[] words = {"apple", "banana", "apple", "cherry", "banana", "apple"};
        
        // 複数スレッドで単語をカウント
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (String word : words) {
            executor.execute(() -> {
                wordCount.merge(word, 1, Integer::sum);
                System.out.println(Thread.currentThread().getName() + 
                    " counted " + word + ": " + wordCount.get(word));
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("\n単語カウント結果: " + wordCount);
        
        // 2. CopyOnWriteArrayList - 読み取り最適化リスト
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        list.addAll(List.of("初期値1", "初期値2", "初期値3"));
        
        // 読み取りスレッド（高速・ロックなし）
        Runnable reader = () -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("読み取り: " + list);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        
        // 書き込みスレッド（コピーが発生）
        Runnable writer = () -> {
            for (int i = 0; i < 3; i++) {
                list.add("新規追加" + i);
                System.out.println("書き込み: 新規追加" + i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        
        Thread readerThread = new Thread(reader);
        Thread writerThread = new Thread(writer);
        readerThread.start();
        writerThread.start();
        readerThread.join();
        writerThread.join();
        
        // 3. ConcurrentLinkedQueue - ロックフリーキュー
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
        Random random = new Random();
        
        // 生産者
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                int value = random.nextInt(100);
                queue.offer(value);
                System.out.println("生産: " + value);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        // 消費者
        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                Integer value;
                while ((value = queue.poll()) == null) {
                    // キューが空の場合は待機
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println("消費: " + value);
            }
        });
        
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
}