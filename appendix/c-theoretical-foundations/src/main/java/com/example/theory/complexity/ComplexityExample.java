package com.example.theory.complexity;

/**
 * 循環的複雑度（Cyclomatic Complexity）の例
 * 循環的複雑度 = 判定（if・else if・case・三項演算子・&&・||など）の数 + 1
 * elseは新しい判定ではないので数えない
 */
public class ComplexityExample {
    
    /**
     * 複雑度が高いコード例（判定4つで、複雑度: 5）
     * 判定が入れ子になっていて、読むときに条件を同時に覚えておく必要がある
     */
    public String processDataComplex(int type, boolean flag, String data) {
        if (type == 1) {                    // +1
            if (flag) {                     // +1
                if (data.length() > 10) {   // +1
                    return "Type1-Long";
                } else {
                    return "Type1-Short";
                }
            } else {
                return "Type1-Default";
            }
        } else if (type == 2) {             // +1
            return "Type2";
        }
        return "Unknown";
    }
    
    /**
     * 改善後のコード（processDataとprocessType1がそれぞれ複雑度: 3）
     * 合計は大きく減らないが、1つのメソッドで追う判定が減り、入れ子もなくなる
     */
    public String processData(int type, boolean flag, String data) {
        switch (type) {
            case 1:                         // +1
                return processType1(flag, data);
            case 2:                         // +1
                return "Type2";
            default:
                return "Unknown";
        }
    }
    
    private String processType1(boolean flag, String data) {
        if (!flag) return "Type1-Default"; // +1
        return data.length() > 10          // +1
            ? "Type1-Long" 
            : "Type1-Short";
    }
}