package com.example.streams;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * カスタムSpliteratorの実装例
 * 分割可能イテレータの仕組みと最適化を学習するためのサンプル
 */
public class CustomSpliterator {
    
    /**
     * ArrayListのSpliterator実装（簡略版）
     * 実際のArrayListSpliteratorの動作を理解するためのサンプル
     */
    static class ArrayListSpliterator<E> implements Spliterator<E> {
        private final List<E> list;
        private int origin; // 開始インデックス
        private int fence;  // 終了インデックス（排他的）
        
        public ArrayListSpliterator(List<E> list, int origin, int fence) {
            this.list = list;
            this.origin = origin;
            this.fence = fence;
        }
        
        public ArrayListSpliterator(List<E> list) {
            this(list, 0, list.size());
        }
        
        @Override
        public Spliterator<E> trySplit() {
            int lo = origin;
            int mid = (lo + fence) >>> 1; // 2で割る（ビットシフト）
            
            // 十分な要素がある場合のみ分割（最小分割サイズを考慮）
            if (mid - lo < 16) {
                return null; // 分割しない
            }
            
            return new ArrayListSpliterator<>(list, lo, origin = mid);
        }
        
        @Override
        public boolean tryAdvance(Consumer<? super E> action) {
            if (origin < fence) {
                action.accept(list.get(origin++));
                return true;
            }
            return false;
        }
        
        @Override
        public long estimateSize() {
            return fence - origin;
        }
        
        @Override
        public int characteristics() {
            return ORDERED | SIZED | SUBSIZED;
        }
        
        /**
         * 分割過程を可視化するメソッド
         */
        public void printSplitInfo() {
            System.out.println("Spliterator[" + origin + ", " + fence + ") size=" + estimateSize());
        }
    }
    
    /**
     * 範囲生成器のSpliterator
     * 数値の範囲を効率的に並列処理するためのカスタム実装
     */
    static class RangeSpliterator implements Spliterator.OfInt {
        private int current;
        private final int end;
        private final int step;
        
        public RangeSpliterator(int start, int end, int step) {
            this.current = start;
            this.end = end;
            this.step = step;
        }
        
        @Override
        public Spliterator.OfInt trySplit() {
            int lo = current;
            int mid = lo + ((end - lo) / step / 2) * step;
            
            if (mid <= lo) {
                return null;
            }
            
            current = mid;
            return new RangeSpliterator(lo, mid, step);
        }
        
        @Override
        public boolean tryAdvance(Consumer<? super Integer> action) {
            if (current < end) {
                action.accept(current);
                current += step;
                return true;
            }
            return false;
        }
        
        @Override
        public boolean tryAdvance(java.util.function.IntConsumer action) {
            if (current < end) {
                action.accept(current);
                current += step;
                return true;
            }
            return false;
        }
        
        @Override
        public long estimateSize() {
            return Math.max(0, (end - current + step - 1) / step);
        }
        
        @Override
        public int characteristics() {
            return ORDERED | SIZED | SUBSIZED | IMMUTABLE | NONNULL | DISTINCT;
        }
    }
    
    /**
     * ファイル行読み込み用のSpliterator
     * 大きなファイルを効率的に並列処理するための実装
     */
    static class LineSpliterator implements Spliterator<String> {
        private final List<String> lines;
        private int origin;
        private int fence;
        
        public LineSpliterator(List<String> lines) {
            this(lines, 0, lines.size());
        }
        
        private LineSpliterator(List<String> lines, int origin, int fence) {
            this.lines = lines;
            this.origin = origin;
            this.fence = fence;
        }
        
        @Override
        public Spliterator<String> trySplit() {
            int lo = origin;
            int mid = (lo + fence) >>> 1;
            
            // ファイル処理では比較的大きな単位で分割
            if (mid - lo < 100) {
                return null;
            }
            
            return new LineSpliterator(lines, lo, origin = mid);
        }
        
        @Override
        public boolean tryAdvance(Consumer<? super String> action) {
            if (origin < fence) {
                action.accept(lines.get(origin++));
                return true;
            }
            return false;
        }
        
        @Override
        public long estimateSize() {
            return fence - origin;
        }
        
        @Override
        public int characteristics() {
            return ORDERED | SIZED | SUBSIZED | NONNULL;
        }
    }
    
    /**
     * Spliteratorの分割動作をデモンストレーション
     */
    public static void demonstrateSplitBehavior() {
        System.out.println("=== Spliterator Split Behavior Demo ===");
        
        // テストデータの準備
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            numbers.add(i);
        }
        
        ArrayListSpliterator<Integer> spliterator = new ArrayListSpliterator<>(numbers);
        
        System.out.println("Original:");
        spliterator.printSplitInfo();
        
        // 分割のデモンストレーション
        Spliterator<Integer> split1 = spliterator.trySplit();
        if (split1 instanceof ArrayListSpliterator) {
            System.out.println("After first split:");
            ((ArrayListSpliterator<Integer>) split1).printSplitInfo();
            spliterator.printSplitInfo();
            
            // さらに分割
            Spliterator<Integer> split2 = split1.trySplit();
            if (split2 instanceof ArrayListSpliterator) {
                System.out.println("After second split:");
                ((ArrayListSpliterator<Integer>) split2).printSplitInfo();
                ((ArrayListSpliterator<Integer>) split1).printSplitInfo();
                spliterator.printSplitInfo();
            }
        }
    }
    
    /**
     * 範囲生成器Spliteratorのデモ
     */
    public static void demonstrateRangeSpliterator() {
        System.out.println("\n=== Range Spliterator Demo ===");
        
        RangeSpliterator rangeSpliterator = new RangeSpliterator(0, 100, 1);
        
        // Streamとして使用
        Stream<Integer> rangeStream = StreamSupport.stream(rangeSpliterator, false);
        
        int sum = rangeStream
            .filter(n -> n % 2 == 0)
            .mapToInt(Integer::intValue)
            .sum();
        
        System.out.println("Sum of even numbers 0-99: " + sum);
        
        // 並列処理での使用
        RangeSpliterator parallelRange = new RangeSpliterator(0, 1000000, 1);
        Stream<Integer> parallelStream = StreamSupport.stream(parallelRange, true);
        
        long startTime = System.currentTimeMillis();
        long parallelSum = parallelStream
            .mapToLong(n -> (long) n * n)
            .sum();
        long endTime = System.currentTimeMillis();
        
        System.out.println("Parallel sum of squares: " + parallelSum);
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
    }
    
    /**
     * メイン実行メソッド
     */
    public static void main(String[] args) {
        demonstrateSplitBehavior();
        demonstrateRangeSpliterator();
    }
}