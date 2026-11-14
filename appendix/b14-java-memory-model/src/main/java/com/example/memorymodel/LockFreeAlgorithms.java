package com.example.memorymodel;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ロックフリーアルゴリズムの実装例
 * Compare-And-Swap (CAS) 操作と ABA 問題の解決方法を学習
 */
public class LockFreeAlgorithms {
    
    /**
     * ロックフリースタックの実装
     */
    public static class LockFreeStack<T> {
        private static class Node<T> {
            final T item;
            final Node<T> next;
            
            Node(T item, Node<T> next) {
                this.item = item;
                this.next = next;
            }
        }
        
        private final AtomicReference<Node<T>> top = new AtomicReference<>();
        private final AtomicInteger operationCount = new AtomicInteger(0);
        
        /**
         * スタックに要素を追加
         */
        public void push(T item) {
            Node<T> newHead = new Node<>(item, null);
            while (true) {
                Node<T> currentHead = top.get();
                newHead = new Node<>(item, currentHead);
                
                // CAS操作で原子的に更新
                if (top.compareAndSet(currentHead, newHead)) {
                    operationCount.incrementAndGet();
                    return;
                }
                // CAS失敗時はリトライ
            }
        }
        
        /**
         * スタックから要素を取り出し
         */
        public T pop() {
            while (true) {
                Node<T> currentHead = top.get();
                if (currentHead == null) {
                    return null; // スタックが空
                }
                
                Node<T> newHead = currentHead.next;
                
                // CAS操作で原子的に更新
                if (top.compareAndSet(currentHead, newHead)) {
                    operationCount.incrementAndGet();
                    return currentHead.item;
                }
                // CAS失敗時はリトライ
            }
        }
        
        /**
         * スタックが空かチェック
         */
        public boolean isEmpty() {
            return top.get() == null;
        }
        
        /**
         * 操作回数を取得（統計用）
         */
        public int getOperationCount() {
            return operationCount.get();
        }
        
        /**
         * スタックの内容を表示（デバッグ用）
         */
        public void printStack() {
            Node<T> current = top.get();
            IO.print("Stack: [");
            while (current != null) {
                IO.print(current.item);
                current = current.next;
                if (current != null) {
                    IO.print(", ");
                }
            }
            IO.println("]");
        }
    }
    
    /**
     * ABA問題のデモンストレーション
     */
    public static class ABAProblematicStack<T> {
        private static class Node<T> {
            final T item;
            volatile Node<T> next;
            
            Node(T item, Node<T> next) {
                this.item = item;
                this.next = next;
            }
        }
        
        private final AtomicReference<Node<T>> top = new AtomicReference<>();
        
        public void push(T item) {
            Node<T> newHead = new Node<>(item, null);
            while (true) {
                Node<T> currentHead = top.get();
                newHead.next = currentHead;
                
                if (top.compareAndSet(currentHead, newHead)) {
                    return;
                }
            }
        }
        
        public T pop() {
            while (true) {
                Node<T> currentHead = top.get();
                if (currentHead == null) {
                    return null;
                }
                
                Node<T> newHead = currentHead.next;
                
                // ABA問題：この間に他のスレッドがpop/pushを行う可能性
                if (top.compareAndSet(currentHead, newHead)) {
                    return currentHead.item;
                }
            }
        }
        
        /**
         * 先頭のノードを取り出す（ノードの再利用をシミュレートするため、ノードごと返す）
         */
        private Node<T> popNode() {
            while (true) {
                Node<T> currentHead = top.get();
                if (currentHead == null) {
                    return null;
                }
                if (top.compareAndSet(currentHead, currentHead.next)) {
                    return currentHead;
                }
            }
        }
        
        /**
         * 既存のノードを先頭に戻す
         */
        private void pushNode(Node<T> node) {
            while (true) {
                Node<T> currentHead = top.get();
                node.next = currentHead;
                if (top.compareAndSet(currentHead, node)) {
                    return;
                }
            }
        }
        
        /**
         * ABA問題をシミュレートするメソッド
         */
        public void simulateABAProblem() {
            // 初期状態: A -> B -> C
            push((T) "C");
            push((T) "B");
            push((T) "A");
            
            IO.println("Initial state:");
            printStack();
            
            // スレッド1がpopを開始（A を取得）
            Node<T> thread1Head = top.get();
            IO.println("Thread1 sees head: " + thread1Head.item);
            
            // スレッド1は、次の先頭になるはずのBを読んだところで中断されたとする
            Node<T> newHead = thread1Head.next; // B
            
            // スレッド2がA, Bをpopしてから、取り出したAのノードを再利用してpush（A -> C）
            // Javaでは新しいノードを作ればGCのおかげで参照が一致せずABAは起きない。
            // ノードをプールして再利用すると、同じ参照が戻ってきてABAが起きる
            Node<T> nodeA = popNode(); // A を pop（ノードを保持して再利用する）
            popNode();                 // B を pop（スタックから取り除かれた）
            pushNode(nodeA);           // 同じAのノードを push
            
            IO.println("After thread2 operations:");
            printStack();
            
            // スレッド1がCASを実行する。先頭は同じAのノードなので成功してしまう
            boolean casResult = top.compareAndSet(thread1Head, newHead);
            
            IO.println("Thread1 CAS result: " + casResult);
            IO.println("Final state (problematic: removed B is back on the stack):");
            printStack();
        }
        
        private void printStack() {
            Node<T> current = top.get();
            IO.print("Stack: [");
            int count = 0;
            while (current != null && count < 10) { // 無限ループ防止
                IO.print(current.item);
                current = current.next;
                count++;
                if (current != null && count < 10) {
                    IO.print(", ");
                }
            }
            if (count >= 10) {
                IO.print("...");
            }
            IO.println("]");
        }
    }
    
    /**
     * ABA問題を解決するスタンプ付きスタック
     */
    public static class ABASafeStack<T> {
        private static class Node<T> {
            final T item;
            final Node<T> next;
            
            Node(T item, Node<T> next) {
                this.item = item;
                this.next = next;
            }
        }
        
        // ノードと一緒にスタンプ（バージョン）も管理
        private final AtomicStampedReference<Node<T>> top = 
            new AtomicStampedReference<>(null, 0);
        
        public void push(T item) {
            while (true) {
                int[] stampHolder = new int[1];
                Node<T> currentHead = top.get(stampHolder);
                int currentStamp = stampHolder[0];
                
                Node<T> newHead = new Node<>(item, currentHead);
                
                // スタンプを更新してCAS
                if (top.compareAndSet(currentHead, newHead, 
                                     currentStamp, currentStamp + 1)) {
                    return;
                }
            }
        }
        
        public T pop() {
            while (true) {
                int[] stampHolder = new int[1];
                Node<T> currentHead = top.get(stampHolder);
                int currentStamp = stampHolder[0];
                
                if (currentHead == null) {
                    return null;
                }
                
                Node<T> newHead = currentHead.next;
                
                // スタンプも一緒にチェックすることでABA問題を防ぐ
                if (top.compareAndSet(currentHead, newHead,
                                     currentStamp, currentStamp + 1)) {
                    return currentHead.item;
                }
            }
        }
        
        public boolean isEmpty() {
            return top.getReference() == null;
        }
        
        public void printStack() {
            int[] stampHolder = new int[1];
            Node<T> current = top.get(stampHolder);
            int stamp = stampHolder[0];
            
            IO.print("Stack (stamp=" + stamp + "): [");
            while (current != null) {
                IO.print(current.item);
                current = current.next;
                if (current != null) {
                    IO.print(", ");
                }
            }
            IO.println("]");
        }
    }
    
    /**
     * ロックフリーカウンター
     */
    public static class LockFreeCounter {
        private final AtomicInteger counter = new AtomicInteger(0);
        private final AtomicInteger casFailures = new AtomicInteger(0);
        
        /**
         * カウンターをインクリメント
         */
        public int incrementAndGet() {
            while (true) {
                int current = counter.get();
                int next = current + 1;
                
                if (counter.compareAndSet(current, next)) {
                    return next;
                } else {
                    casFailures.incrementAndGet();
                    // CAS失敗時はリトライ
                }
            }
        }
        
        /**
         * カウンターをデクリメント
         */
        public int decrementAndGet() {
            while (true) {
                int current = counter.get();
                int next = current - 1;
                
                if (counter.compareAndSet(current, next)) {
                    return next;
                } else {
                    casFailures.incrementAndGet();
                    // CAS失敗時はリトライ
                }
            }
        }
        
        /**
         * 現在の値を取得
         */
        public int get() {
            return counter.get();
        }
        
        /**
         * CAS失敗回数を取得
         */
        public int getCasFailures() {
            return casFailures.get();
        }
        
        /**
         * カウンターをリセット
         */
        public void reset() {
            counter.set(0);
            casFailures.set(0);
        }
    }
    
    /**
     * ロックフリースタックのパフォーマンステスト
     */
    public static void testLockFreeStackPerformance() {
        IO.println("=== Lock-Free Stack Performance Test ===");
        
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        final int threadCount = 4;
        final int operationsPerThread = 1000;
        
        Thread[] threads = new Thread[threadCount];
        long startTime = System.nanoTime();
        
        // 各スレッドでpush/pop操作を実行
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    stack.push(threadId * operationsPerThread + j);
                    
                    if (j % 2 == 0) { // 半分をpop
                        stack.pop();
                    }
                }
            });
        }
        
        // すべてのスレッドを開始
        for (Thread thread : threads) {
            thread.start();
        }
        
        // すべてのスレッドの完了を待つ
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        
        IO.println("Threads: " + threadCount);
        IO.println("Pushes per thread: " + operationsPerThread + " (and a pop after every other push)");
        IO.println("Total operations (push + pop): " + stack.getOperationCount());
        IO.println("Elapsed time: " + String.format("%.2f", elapsedMs) + "ms");
        IO.println("Operations per second: " + 
                         String.format("%.0f", stack.getOperationCount() / elapsedMs * 1000));
        
        // 最終的なスタックの状態
        int remainingItems = 0;
        while (!stack.isEmpty()) {
            stack.pop();
            remainingItems++;
        }
        IO.println("Remaining items in stack: " + remainingItems);
    }
    
    /**
     * ABA問題のデモンストレーション
     */
    public static void demonstrateABAProblem() {
        IO.println("\n=== ABA Problem Demonstration ===");
        
        ABAProblematicStack<String> problematicStack = new ABAProblematicStack<>();
        problematicStack.simulateABAProblem();
        
        IO.println("\n--- ABA-Safe Stack ---");
        ABASafeStack<String> safeStack = new ABASafeStack<>();
        
        // 同じ操作をABA-safeスタックで実行
        safeStack.push("C");
        safeStack.push("B");
        safeStack.push("A");
        
        IO.println("Initial state:");
        safeStack.printStack();
        
        // 通常の操作
        String item = safeStack.pop();
        IO.println("Popped: " + item);
        safeStack.printStack();
        
        safeStack.push("D");
        IO.println("Pushed: D");
        safeStack.printStack();
    }
    
    /**
     * ロックフリーカウンターのテスト
     */
    public static void testLockFreeCounter() {
        IO.println("\n=== Lock-Free Counter Test ===");
        
        LockFreeCounter counter = new LockFreeCounter();
        final int threadCount = 8;
        final int incrementsPerThread = 1000;
        
        Thread[] threads = new Thread[threadCount];
        long startTime = System.nanoTime();
        
        // 各スレッドでカウンターを操作
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    if (j % 2 == 0) {
                        counter.incrementAndGet();
                    } else {
                        counter.decrementAndGet();
                    }
                }
            });
        }
        
        // すべてのスレッドを開始
        for (Thread thread : threads) {
            thread.start();
        }
        
        // すべてのスレッドの完了を待つ
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        long endTime = System.nanoTime();
        double elapsedMs = (endTime - startTime) / 1_000_000.0;
        
        IO.println("Threads: " + threadCount);
        IO.println("Operations per thread: " + incrementsPerThread + " (half increments, half decrements)");
        IO.println("Expected final value: 0");
        IO.println("Final counter value: " + counter.get());
        IO.println("CAS failures: " + counter.getCasFailures());
        IO.println("Elapsed time: " + String.format("%.2f", elapsedMs) + "ms");
        
        int totalOperations = threadCount * incrementsPerThread;
        IO.println("Total operations: " + totalOperations);
        IO.println("Operations per second: " + 
                         String.format("%.0f", totalOperations / elapsedMs * 1000));
        IO.println("CAS failures per operation: " + 
                         String.format("%.2f", counter.getCasFailures() / (double) totalOperations));
    }
    
    /**
     * CAS操作の基本デモ
     */
    public static void demonstrateBasicCAS() {
        IO.println("\n=== Basic CAS Operation Demo ===");
        
        AtomicInteger atomicInt = new AtomicInteger(10);
        
        IO.println("Initial value: " + atomicInt.get());
        
        // 成功するCAS
        boolean success1 = atomicInt.compareAndSet(10, 20);
        IO.println("CAS(10, 20): " + success1 + ", new value: " + atomicInt.get());
        
        // 失敗するCAS
        boolean success2 = atomicInt.compareAndSet(10, 30);
        IO.println("CAS(10, 30): " + success2 + ", value: " + atomicInt.get());
        
        // 成功するCAS
        boolean success3 = atomicInt.compareAndSet(20, 30);
        IO.println("CAS(20, 30): " + success3 + ", new value: " + atomicInt.get());
        
        // getAndXXX操作の実装例
        IO.println("\ngetAndIncrement implementation:");
        int oldValue = atomicInt.get();
        while (!atomicInt.compareAndSet(oldValue, oldValue + 1)) {
            oldValue = atomicInt.get();
            IO.println("  CAS retry with old value: " + oldValue);
        }
        IO.println("Final value: " + atomicInt.get());
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateBasicCAS();
        testLockFreeStackPerformance();
        demonstrateABAProblem();
        testLockFreeCounter();
        
        IO.println("\n=== Lock-Free Programming Summary ===");
        IO.println("1. CAS (Compare-And-Swap) enables lock-free atomic operations");
        IO.println("2. ABA problem can occur when memory is reused");
        IO.println("3. Stamped references or hazard pointers can solve ABA problem");
        IO.println("4. Lock-free algorithms provide better scalability and avoid deadlocks");
        IO.println("5. Implementation complexity is higher than lock-based approaches");
        IO.println("6. Performance benefits are most apparent under high contention");
    }
}