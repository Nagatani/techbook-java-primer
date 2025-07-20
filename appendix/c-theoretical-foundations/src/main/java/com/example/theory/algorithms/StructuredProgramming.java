package com.example.theory.algorithms;

/**
 * 構造化プログラミングの理論的基盤
 * Dijkstraの構造化定理の実装例
 * リストAC-3, AC-4, AC-5, AC-6を含む
 */
public class StructuredProgramming {
    
    /**
     * 1. 順次処理（Sequence）の例
     * リストAC-3
     */
    public void sequenceExample() {
        int statement1 = 1;  // statement1;
        int statement2 = 2;  // statement2;
        int statement3 = 3;  // statement3;
        
        System.out.println("Sequential execution: " + statement1 + ", " + statement2 + ", " + statement3);
    }
    
    /**
     * 2. 分岐処理（Selection）の例
     * リストAC-4
     */
    public void selectionExample(boolean condition) {
        if (condition) {
            // statementA;
            System.out.println("Statement A executed");
        } else {
            // statementB;
            System.out.println("Statement B executed");
        }
    }
    
    /**
     * 3. 反復処理（Iteration）の例
     * リストAC-5
     */
    public void iterationExample(int count) {
        int i = 0;
        while (i < count) {
            // statements;
            System.out.println("Iteration " + (i + 1));
            i++;
        }
    }
    
    /**
     * ループ不変条件の例：配列の最大値を求めるアルゴリズム
     * リストAC-6
     */
    public int findMax(int[] array) {
        if (array.length == 0) throw new IllegalArgumentException("Array cannot be empty");
        
        int max = array[0];
        int i = 1;
        
        // ループ不変条件: max は array[0..i-1] の最大値
        while (i < array.length) {
            if (array[i] > max) {
                max = array[i];
            }
            i++;
            // 不変条件は保持される
        }
        
        return max; // max は array[0..length-1] の最大値
    }
    
    /**
     * ループ不変条件の検証を含むバージョン
     */
    public int findMaxWithInvariantCheck(int[] array) {
        if (array.length == 0) throw new IllegalArgumentException("Array cannot be empty");
        
        int max = array[0];
        int i = 1;
        
        while (i < array.length) {
            // ループ不変条件の事前チェック
            assert isMaxOfRange(array, 0, i - 1, max) : "Invariant violated before iteration";
            
            if (array[i] > max) {
                max = array[i];
            }
            i++;
            
            // ループ不変条件の事後チェック
            assert isMaxOfRange(array, 0, i - 1, max) : "Invariant violated after iteration";
        }
        
        return max;
    }
    
    /**
     * 配列の指定範囲での最大値かどうかを検証
     */
    private boolean isMaxOfRange(int[] array, int start, int end, int value) {
        for (int i = start; i <= end; i++) {
            if (array[i] > value) {
                return false;
            }
        }
        return true;
    }
}