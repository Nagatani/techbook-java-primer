package com.example.theory.complexity;

/**
 * 循環的複雑度（Cyclomatic Complexity）の例
 * リストAC-1とリストAC-2を統合
 */
public class ComplexityExample {
    
    /**
     * 複雑度が高いコード例（複雑度: 6）
     * リストAC-1
     */
    public String processDataComplex(int type, boolean flag, String data) {
        if (type == 1) {                    // +1
            if (flag) {                     // +1
                if (data.length() > 10) {   // +1
                    return "Type1-Long";
                } else {                    // +1
                    return "Type1-Short";
                }
            } else {                        // +1
                return "Type1-Default";
            }
        } else if (type == 2) {             // +1
            return "Type2";
        }
        return "Unknown";
    }
    
    /**
     * 改善後のコード（複雑度: 3）
     * リストAC-2
     */
    public String processData(int type, boolean flag, String data) {
        switch (type) {                     // +1
            case 1:
                return processType1(flag, data);
            case 2:
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