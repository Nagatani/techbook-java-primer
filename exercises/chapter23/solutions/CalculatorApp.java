package chapter23.solutions;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * コマンドライン計算機アプリケーション
 * 実行可能JARとして配布される基本的な計算機能を提供します。
 * 
 * 使用例:
 * java -jar calculator.jar add 10 20
 * java -jar calculator.jar multiply 5.5 3.2
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 */
public class CalculatorApp {
    
    /**
     * メインメソッド
     * コマンドライン引数から操作と数値を受け取り、計算を実行します。
     * 
     * @param args コマンドライン引数 [操作] [数値1] [数値2]
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            printUsage();
            System.exit(1);
        }
        
        String operation = args[0].toLowerCase();
        
        try {
            // BigDecimalを使用して精度の高い計算を行う
            BigDecimal num1 = new BigDecimal(args[1]);
            BigDecimal num2 = new BigDecimal(args[2]);
            
            BigDecimal result = calculate(operation, num1, num2);
            
            // 結果の表示
            System.out.println(formatResult(operation, num1, num2, result));
            
        } catch (NumberFormatException e) {
            System.err.println("エラー: 無効な数値形式です。");
            System.err.println("入力された値: " + args[1] + ", " + args[2]);
            System.exit(1);
        } catch (ArithmeticException e) {
            System.err.println("エラー: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("エラー: " + e.getMessage());
            printUsage();
            System.exit(1);
        }
    }
    
    /**
     * 計算を実行します。
     * 
     * @param operation 操作名（add, subtract, multiply, divide）
     * @param num1 第1オペランド
     * @param num2 第2オペランド
     * @return 計算結果
     * @throws IllegalArgumentException 無効な操作が指定された場合
     * @throws ArithmeticException ゼロ除算の場合
     */
    private static BigDecimal calculate(String operation, BigDecimal num1, BigDecimal num2) {
        switch (operation) {
            case "add":
            case "+":
                return num1.add(num2);
                
            case "subtract":
            case "-":
                return num1.subtract(num2);
                
            case "multiply":
            case "*":
                return num1.multiply(num2);
                
            case "divide":
            case "/":
                if (num2.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("ゼロで除算することはできません");
                }
                // 小数点以下10桁まで、四捨五入
                return num1.divide(num2, 10, RoundingMode.HALF_UP);
                
            default:
                throw new IllegalArgumentException("無効な操作: " + operation);
        }
    }
    
    /**
     * 計算結果をフォーマットして返します。
     * 
     * @param operation 操作名
     * @param num1 第1オペランド
     * @param num2 第2オペランド
     * @param result 計算結果
     * @return フォーマットされた結果文字列
     */
    private static String formatResult(String operation, BigDecimal num1, 
                                     BigDecimal num2, BigDecimal result) {
        String operator = getOperatorSymbol(operation);
        
        // 不要な末尾のゼロを削除
        String num1Str = num1.stripTrailingZeros().toPlainString();
        String num2Str = num2.stripTrailingZeros().toPlainString();
        String resultStr = result.stripTrailingZeros().toPlainString();
        
        return String.format("%s %s %s = %s", num1Str, operator, num2Str, resultStr);
    }
    
    /**
     * 操作名から演算子記号を取得します。
     * 
     * @param operation 操作名
     * @return 演算子記号
     */
    private static String getOperatorSymbol(String operation) {
        switch (operation) {
            case "add":
            case "+":
                return "+";
            case "subtract":
            case "-":
                return "-";
            case "multiply":
            case "*":
                return "×";
            case "divide":
            case "/":
                return "÷";
            default:
                return "?";
        }
    }
    
    /**
     * 使用方法を表示します。
     */
    private static void printUsage() {
        System.out.println("使用方法: java -jar calculator.jar [操作] [数値1] [数値2]");
        System.out.println();
        System.out.println("操作:");
        System.out.println("  add, +      : 加算");
        System.out.println("  subtract, - : 減算");
        System.out.println("  multiply, * : 乗算");
        System.out.println("  divide, /   : 除算");
        System.out.println();
        System.out.println("例:");
        System.out.println("  java -jar calculator.jar add 10 20");
        System.out.println("  java -jar calculator.jar multiply 5.5 3.2");
        System.out.println("  java -jar calculator.jar divide 100 3");
    }
    
    /**
     * バージョン情報を表示します（オプション機能）
     */
    private static void printVersion() {
        System.out.println("Calculator App v1.0");
        System.out.println("(c) 2024 Hidehiro Nagatani");
    }
}