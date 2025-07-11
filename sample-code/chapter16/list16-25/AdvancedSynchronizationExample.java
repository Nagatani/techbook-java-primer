/**
 * リスト16-25
 * AdvancedSynchronizationExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (2178行目)
 */

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class AdvancedSynchronizationExample {
    
    // 1. ReadWriteLock - 読み取り/書き込みロック
    static class SharedResource {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Lock readLock = lock.readLock();
        private final Lock writeLock = lock.writeLock();
        private String data = "初期データ";
        
        public String read() {
            readLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 読み取り開始");
                Thread.sleep(100); // 読み取り処理をシミュレート
                return data;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } finally {
                System.out.println(Thread.currentThread().getName() + " 読み取り完了");
                readLock.unlock();
            }
        }
        
        public void write(String newData) {
            writeLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 書き込み開始");
                Thread.sleep(500); // 書き込み処理をシミュレート
                data = newData;
                System.out.println(Thread.currentThread().getName() + " 書き込み完了: " + newData);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                writeLock.unlock();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        // ReadWriteLockのデモ
        System.out.println("=== ReadWriteLock デモ ===");
        SharedResource resource = new SharedResource();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // 複数の読み取りスレッド（並行実行可能）
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                String data = resource.read();
                System.out.println("読み取りデータ: " + data);
            });
        }
        
        // 書き込みスレッド（排他実行）
        executor.execute(() -> resource.write("更新データ"));
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}