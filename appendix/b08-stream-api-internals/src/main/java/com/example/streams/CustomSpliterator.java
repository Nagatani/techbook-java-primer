package com.example.streams;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * カスタムSpliteratorの実装例
 * 分割可能イテレーターの仕組みと最適化を学習するためのサンプル
 */
public class CustomSpliterator {
    
    /**
     * ArrayListのSpliterator実装（簡略版）
     * 実際のArrayListSpliteratorの考え方（範囲を半分ずつに分ける）を理解するためのサンプル。
     * 本物は要素が1つになるまで分割できるが、ここでは16件未満で分割をやめる
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
            IO.println("Spliterator[" + origin + ", " + fence + ") size=" + estimateSize());
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
     * 行のリスト用のSpliterator
     * ファイルから読み込んだ行（List<String>）を、大きめの単位で分けて並列処理する例
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
        IO.println("=== Spliterator Split Behavior Demo ===");
        
        // テストデータの準備
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            numbers.add(i);
        }
        
        ArrayListSpliterator<Integer> spliterator = new ArrayListSpliterator<>(numbers);
        
        IO.println("Original:");
        spliterator.printSplitInfo();
        
        // 分割のデモンストレーション
        Spliterator<Integer> split1 = spliterator.trySplit();
        if (split1 instanceof ArrayListSpliterator) {
            IO.println("After first split:");
            ((ArrayListSpliterator<Integer>) split1).printSplitInfo();
            spliterator.printSplitInfo();
            
            // さらに分割
            Spliterator<Integer> split2 = split1.trySplit();
            if (split2 instanceof ArrayListSpliterator) {
                IO.println("After second split:");
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
        IO.println("\n=== Range Spliterator Demo ===");
        
        RangeSpliterator rangeSpliterator = new RangeSpliterator(0, 100, 1);
        
        // IntStreamとして使用（Spliterator.OfIntはintStreamに渡せばボクシングが起きない）
        IntStream rangeStream = StreamSupport.intStream(rangeSpliterator, false);
        
        int sum = rangeStream
            .filter(n -> n % 2 == 0)
            .sum();
        
        IO.println("Sum of even numbers 0-99: " + sum);
        
        // 並列処理での使用
        RangeSpliterator parallelRange = new RangeSpliterator(0, 1000000, 1);
        IntStream parallelStream = StreamSupport.intStream(parallelRange, true);
        
        long startTime = System.nanoTime();
        long parallelSum = parallelStream
            .mapToLong(n -> (long) n * n)
            .sum();
        long endTime = System.nanoTime();
        
        IO.println("Parallel sum of squares: " + parallelSum);
        IO.println("Execution time: %.3fms (depends on the environment)".formatted((endTime - startTime) / 1_000_000.0));
    }
    
    /**
     * 行のリスト用Spliteratorのデモ
     */
    public static void demonstrateLineSpliterator() {
        IO.println("\n=== Line Spliterator Demo ===");
        
        // ファイルから読み込んだ行の代わりに、1000行のリストを作る
        List<String> lines = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            lines.add((i % 10 == 0 ? "ERROR " : "INFO ") + "line " + i);
        }
        
        LineSpliterator spliterator = new LineSpliterator(lines);
        Spliterator<String> firstHalf = spliterator.trySplit();
        IO.println("After split: " + firstHalf.estimateSize() + " + " + spliterator.estimateSize() + " lines");
        
        long errorCount = StreamSupport.stream(new LineSpliterator(lines), true)
            .filter(line -> line.startsWith("ERROR"))
            .count();
        IO.println("ERROR lines (parallel): " + errorCount);
    }
    
    /**
     * メイン実行メソッド
     */
    public static void main(String[] args) {
        demonstrateSplitBehavior();
        demonstrateRangeSpliterator();
        demonstrateLineSpliterator();
    }
}