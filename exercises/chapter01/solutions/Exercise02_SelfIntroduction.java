/**
 * 第1章 課題2: 変数を使った自己紹介 - 解答例
 * 
 * 変数を使って自己紹介プログラムを作成してください。
 * 
 * 学習ポイント:
 * - 変数の宣言と初期化
 * - 適切なデータ型の選択
 * - 文字列の連結
 */
public class Exercise02_SelfIntroduction {
    public static void main(String[] args) {
        // 解答例 1: 基本的な実装
        String name = "田中太郎";
        int age = 20;
        double height = 170.5;
        String favoriteSubject = "数学";
        
        System.out.println("=== 自己紹介 ===");
        System.out.println("名前: " + name);
        System.out.println("年齢: " + age + "歳");
        System.out.println("身長: " + height + "cm");
        System.out.println("好きな科目: " + favoriteSubject);
        
        System.out.println(); // 空行
        
        // 解答例 2: 別の書き方（変数宣言と代入を分離）
        String name2;
        int age2;
        double height2;
        String favoriteSubject2;
        
        name2 = "佐藤花子";
        age2 = 22;
        height2 = 165.0;
        favoriteSubject2 = "物理学";
        
        System.out.println("=== 自己紹介（その2）===");
        System.out.println("名前: " + name2);
        System.out.println("年齢: " + age2 + "歳");
        System.out.println("身長: " + height2 + "cm");
        System.out.println("好きな科目: " + favoriteSubject2);
        
        System.out.println(); // 空行
        
        // 解答例 3: より多くの情報を含む実装
        String firstName = "鈴木";
        String lastName = "一郎";
        int birthYear = 2003;
        int currentYear = 2025;
        double weight = 65.5;
        boolean hasGlasses = true;
        char bloodType = 'A';
        
        // 計算して年齢を求める
        int calculatedAge = currentYear - birthYear;
        
        System.out.println("=== 詳細な自己紹介 ===");
        System.out.println("氏名: " + firstName + " " + lastName);
        System.out.println("年齢: " + calculatedAge + "歳（" + birthYear + "年生まれ）");
        System.out.println("体重: " + weight + "kg");
        System.out.println("血液型: " + bloodType + "型");
        System.out.println("眼鏡着用: " + (hasGlasses ? "あり" : "なし"));
        
        // BMI計算（応用例）
        double bmi = weight / ((height / 100) * (height / 100));
        System.out.println("BMI: " + String.format("%.1f", bmi));
    }
}

/*
 * データ型の選択指針:
 * 
 * String: 文字列データ
 * - 名前、住所、メールアドレスなど
 * - "で囲んで記述
 * 
 * int: 整数データ
 * - 年齢、個数、年度など
 * - 範囲: -2,147,483,648 ～ 2,147,483,647
 * 
 * double: 小数点を含む数値
 * - 身長、体重、価格など
 * - 精度が必要な計算に使用
 * 
 * boolean: 真偽値
 * - true または false のみ
 * - フラグや状態表現に使用
 * 
 * char: 1文字
 * - 'A', '1', '漢' など
 * - 'で囲んで記述
 * 
 * よくある間違い:
 * 
 * 1. データ型の不適切な選択
 *    × String age = "20";     // 数値なのに文字列
 *    ○ int age = 20;          // 数値はint型
 * 
 * 2. 変数名の命名規則違反
 *    × int 2age = 20;         // 数字で始まってはいけない
 *    × int my-age = 20;       // ハイフンは使えない
 *    ○ int myAge = 20;        // キャメルケース推奨
 * 
 * 3. 初期化忘れ
 *    × int age;
 *      System.out.println(age); // 初期化されていない変数を使用
 *    ○ int age = 20;
 *      System.out.println(age); // 使用前に初期化
 * 
 * プログラミングのコツ:
 * - 変数名は意味のある名前にする
 * - 一つの変数には一つの意味を持たせる
 * - 適切なデータ型を選択する
 */