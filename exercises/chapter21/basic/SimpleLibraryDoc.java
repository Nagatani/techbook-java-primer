/**
 * 第21章 基本課題1: シンプルなライブラリのドキュメント作成
 * 
 * 基本的なJavadocコメントを作成して、ライブラリのAPIドキュメントを生成してください。
 * 
 * 要件:
 * 1. クラス、メソッド、フィールドに適切なJavadocコメントを追加
 * 2. @param、@return、@throws タグを正しく使用
 * 3. 使用例を含むサンプルコードをドキュメントに含める
 * 4. @since、@author、@version タグを使用
 * 5. HTMLタグを使用した見やすいドキュメント
 * 
 * 学習ポイント:
 * - Javadocコメントの基本的な書き方
 * - 標準的なJavadocタグの使用方法
 * - APIドキュメントの品質を向上させる技法
 * - 利用者にとって分かりやすいドキュメントの作成
 */

// TODO: クラスレベルのJavadocコメントを追加してください
// 以下の情報を含めてください：
// - クラスの目的と概要
// - 簡単な使用例
// - @author タグ
// - @version タグ
// - @since タグ
public class SimpleLibraryDoc {
    
    // TODO: 定数のJavadocコメントを追加してください
    public static final String LIBRARY_NAME = "SimpleLibrary";
    
    // TODO: 定数のJavadocコメントを追加してください
    public static final String VERSION = "1.0.0";
    
    // TODO: デフォルトコンストラクタのJavadocコメントを追加してください
    public SimpleLibraryDoc() {
        // 初期化処理
    }
    
    // TODO: このメソッドに詳細なJavadocコメントを追加してください
    // 以下を含めてください：
    // - メソッドの機能説明
    // - @param タグ（各パラメータの説明）
    // - @return タグ（戻り値の説明）
    // - @throws タグ（例外の説明）
    // - 使用例
    public String formatMessage(String template, Object... args) {
        if (template == null) {
            throw new IllegalArgumentException("テンプレートはnullにできません");
        }
        
        // 簡単なテンプレート処理（{0}, {1}などを置換）
        String result = template;
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            String replacement = args[i] != null ? args[i].toString() : "null";
            result = result.replace(placeholder, replacement);
        }
        return result;
    }
    
    // TODO: このメソッドにJavadocコメントを追加してください
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // 簡単なメールアドレス検証
        return email.contains("@") && email.contains(".") && 
               email.indexOf("@") < email.lastIndexOf(".");
    }
    
    // TODO: このメソッドにJavadocコメントを追加してください
    public double calculatePercentage(double value, double total) {
        if (total == 0) {
            throw new ArithmeticException("合計値は0にできません");
        }
        return (value / total) * 100.0;
    }
    
    // TODO: このメソッドにJavadocコメントを追加してください
    public String[] splitAndTrim(String input, String delimiter) {
        if (input == null) {
            return new String[0];
        }
        
        String[] parts = input.split(delimiter);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }
    
    // TODO: このメソッドにJavadocコメントを追加してください
    // このメソッドは将来のバージョンで削除予定とする
    public String oldMethod(String input) {
        return input.toLowerCase();
    }
    
    // TODO: 内部クラスのJavadocコメントを追加してください
    public static class Result {
        
        // TODO: フィールドのJavadocコメントを追加してください
        private final boolean success;
        
        // TODO: フィールドのJavadocコメントを追加してください
        private final String message;
        
        // TODO: コンストラクタのJavadocコメントを追加してください
        public Result(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        // TODO: getterメソッドのJavadocコメントを追加してください
        public boolean isSuccess() {
            return success;
        }
        
        // TODO: getterメソッドのJavadocコメントを追加してください
        public String getMessage() {
            return message;
        }
    }
    
    // TODO: このメソッドにJavadocコメントを追加してください
    public Result validateInput(String input) {
        if (input == null) {
            return new Result(false, "入力はnullにできません");
        }
        
        if (input.trim().isEmpty()) {
            return new Result(false, "入力は空にできません");
        }
        
        if (input.length() > 100) {
            return new Result(false, "入力は100文字以内にしてください");
        }
        
        return new Result(true, "入力は有効です");
    }
    
    // TODO: mainメソッドのJavadocコメントを追加してください
    public static void main(String[] args) {
        SimpleLibraryDoc library = new SimpleLibraryDoc();
        
        // 使用例のデモンストレーション
        System.out.println("=== " + LIBRARY_NAME + " v" + VERSION + " デモ ===");
        
        // メッセージフォーマットのテスト
        String formatted = library.formatMessage("こんにちは、{0}さん！今日は{1}です。", "田中", "晴れ");
        System.out.println("フォーマット結果: " + formatted);
        
        // メールアドレス検証のテスト
        boolean isValid = library.isValidEmail("test@example.com");
        System.out.println("メール検証: " + isValid);
        
        // パーセンテージ計算のテスト
        double percentage = library.calculatePercentage(25, 100);
        System.out.println("パーセンテージ: " + percentage + "%");
        
        // 入力検証のテスト
        Result result = library.validateInput("有効な入力");
        System.out.println("検証結果: " + result.isSuccess() + " - " + result.getMessage());
    }
}

/*
 * Javadocコメント作成のヒント:
 * 
 * 1. クラスレベルのドキュメント例:
 * 
 * /**
 *  * 文字列処理とデータ検証のためのユーティリティクラス
 *  * 
 *  * <p>このクラスは一般的な文字列操作、データ検証、フォーマット処理のための
 *  * 便利なメソッドを提供します。すべてのメソッドはスレッドセーフです。</p>
 *  * 
 *  * <h2>使用例</h2>
 *  * <pre>{@code
 *  * SimpleLibraryDoc lib = new SimpleLibraryDoc();
 *  * String result = lib.formatMessage("Hello {0}", "World");
 *  * boolean valid = lib.isValidEmail("user@example.com");
 *  * }</pre>
 *  * 
 *  * @author あなたの名前
 *  * @version 1.0.0
 *  * @since 1.0
 *  /
 * 
 * 2. メソッドレベルのドキュメント例:
 * 
 * /**
 *  * テンプレート文字列内のプレースホルダーを指定された値で置換します
 *  * 
 *  * <p>プレースホルダーは {0}, {1}, {2}... の形式で指定します。
 *  * 対応する引数がない場合、プレースホルダーはそのまま残ります。</p>
 *  * 
 *  * @param template プレースホルダーを含むテンプレート文字列（null不可）
 *  * @param args 置換する値の配列
 *  * @return プレースホルダーが置換された文字列
 *  * @throws IllegalArgumentException templateがnullの場合
 *  * 
 *  * @since 1.0
 *  * @see String#replace(CharSequence, CharSequence)
 *  * 
 *  * @example
 *  * <pre>{@code
 *  * String result = formatMessage("Hello {0}, you have {1} messages", "John", 5);
 *  * // 結果: "Hello John, you have 5 messages"
 *  * }</pre>
 *  /
 * 
 * 3. 非推奨メソッドの例:
 * 
 * /**
 *  * 文字列を小文字に変換します
 *  * 
 *  * @param input 変換する文字列
 *  * @return 小文字に変換された文字列
 *  * @deprecated バージョン 2.0 以降は {@link String#toLowerCase()} を直接使用してください。
 *  *             このメソッドは 3.0 で削除予定です。
 *  /
 * @Deprecated(since = "2.0", forRemoval = true)
 * 
 * 4. 内部クラスのドキュメント例:
 * 
 * /**
 *  * 処理結果を表すイミュータブルクラス
 *  * 
 *  * <p>成功/失敗の状態とメッセージを保持します。</p>
 *  /
 * 
 * ドキュメント生成コマンド:
 * javadoc -d docs -cp . SimpleLibraryDoc.java
 * 
 * 確認ポイント:
 * - すべてのpublicメソッドにドキュメントがあるか
 * - パラメータの制約が明記されているか
 * - 例外条件が説明されているか
 * - 使用例が含まれているか
 * - HTMLタグが適切に使用されているか
 */