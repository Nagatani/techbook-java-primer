/**
 * リスト16-24
 * CopyOnWriteExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (2126行目)
 */

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class CopyOnWriteExample {
    public static void main(String[] args) throws InterruptedException {
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
    }
}