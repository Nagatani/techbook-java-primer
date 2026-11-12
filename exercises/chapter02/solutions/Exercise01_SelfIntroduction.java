/**
 * 第2章 課題1: 変数を使った自己紹介 - 解答例
 * 
 * 変数を使って自己紹介プログラムを作成してください。
 * 
 * 学習ポイント:
 * - 変数の宣言と初期化
 * - 適切なデータ型の選択
 * - 文字列の連結
 */
public class Exercise01_SelfIntroduction {
    public static void main(String[] args) {
        String name = "田中太郎";
        int age = 20;
        double height = 170.5;
        String favoriteSubject = "数学";
        
        IO.println("=== 自己紹介 ===");
        IO.println("名前: " + name);
        IO.println("年齢: " + age + "歳");
        IO.println("身長: " + height + "cm");
        IO.println("好きな科目: " + favoriteSubject);

        // 別の書き方: 宣言と代入を分けてもよい
        // String name;
        // name = "田中太郎";
        // boolean（眼鏡を使うか）やchar（血液型）の変数を足して、情報を増やしてもよい
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
 *      IO.println(age); // 初期化されていない変数を使用
 *    ○ int age = 20;
 *      IO.println(age); // 使用前に初期化
 * 
 * プログラミングのコツ:
 * - 変数名は意味のある名前にする
 * - 一つの変数には一つの意味を持たせる
 * - 適切なデータ型を選択する
 */