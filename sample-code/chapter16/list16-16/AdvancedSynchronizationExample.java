/**
 * リスト16-16
 * AdvancedSynchronizationExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (1293行目)
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
    
    // 2. Semaphore - リソース数制限
    static class ResourcePool {
        private final Semaphore semaphore;
        private final boolean[] resources;
        
        public ResourcePool(int size) {
            this.semaphore = new Semaphore(size);
            this.resources = new boolean[size];
        }
        
        public int acquire() throws InterruptedException {
            semaphore.acquire();
            synchronized (resources) {
                for (int i = 0; i < resources.length; i++) {
                    if (!resources[i]) {
                        resources[i] = true;
                        System.out.println(Thread.currentThread().getName() + 
                            " がリソース " + i + " を取得");
                        return i;
                    }
                }
            }
            throw new IllegalStateException("リソースが見つかりません");
        }
        
        public void release(int resourceId) {
            synchronized (resources) {
                resources[resourceId] = false;
                System.out.println(Thread.currentThread().getName() + 
                    " がリソース " + resourceId + " を解放");
            }
            semaphore.release();
        }
    }
    
    // 3. CountDownLatch - カウントダウン同期
    static class RaceExample {
        public static void demonstrate() throws InterruptedException {
            int runnerCount = 5;
            CountDownLatch startSignal = new CountDownLatch(1);
            CountDownLatch finishSignal = new CountDownLatch(runnerCount);
            
            for (int i = 0; i < runnerCount; i++) {
                int runnerId = i + 1;
                new Thread(() -> {
                    try {
                        System.out.println("ランナー " + runnerId + " 準備完了");
                        startSignal.await(); // スタート信号を待つ
                        
                        System.out.println("ランナー " + runnerId + " スタート！");
                        Thread.sleep((long)(Math.random() * 3000)); // 走行時間
                        System.out.println("ランナー " + runnerId + " ゴール！");
                        
                        finishSignal.countDown(); // ゴールしたことを通知
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
            
            Thread.sleep(1000);
            System.out.println("位置について... よーい...");
            Thread.sleep(1000);
            System.out.println("ドン！");
            startSignal.countDown(); // 全ランナーをスタートさせる
            
            finishSignal.await(); // 全ランナーのゴールを待つ
            System.out.println("レース終了！");
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
        
        // Semaphoreのデモ
        System.out.println("\n=== Semaphore デモ ===");
        ResourcePool pool = new ResourcePool(3);
        ExecutorService poolExecutor = Executors.newFixedThreadPool(5);
        
        for (int i = 0; i < 5; i++) {
            poolExecutor.execute(() -> {
                try {
                    int resourceId = pool.acquire();
                    Thread.sleep(1000); // リソースを使用
                    pool.release(resourceId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        poolExecutor.shutdown();
        poolExecutor.awaitTermination(10, TimeUnit.SECONDS);
        
        // CountDownLatchのデモ
        System.out.println("\n=== CountDownLatch デモ ===");
        RaceExample.demonstrate();
    }
}