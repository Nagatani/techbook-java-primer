/**
 * 第2章 課題1: Personクラスの作成 - 解答例
 * 
 * 人を表すPersonクラスの実装
 * 
 * 学習ポイント:
 * - クラスの基本構造
 * - インスタンス変数の定義
 * - メソッドの実装
 */
public class Person {
    // インスタンス変数（フィールド）
    String name;
    int age;
    
    // 解答例 1: 基本的な実装
    
    /**
     * 自己紹介を行うメソッド
     */
    void introduce() {
        System.out.println("私の名前は" + name + "です。年齢は" + age + "歳です。");
    }
    
    /**
     * 誕生日処理（年齢を1つ増やす）
     */
    void birthday() {
        age++;
        System.out.println("誕生日おめでとう！");
    }
    
    // 解答例 2: より詳細な実装
    
    /**
     * 詳細な自己紹介を行うメソッド
     */
    void detailedIntroduce() {
        System.out.println("=== 自己紹介 ===");
        System.out.println("名前: " + name);
        System.out.println("年齢: " + age + "歳");
        
        // 年齢による分類
        if (age < 20) {
            System.out.println("分類: 未成年");
        } else if (age < 65) {
            System.out.println("分類: 成人");
        } else {
            System.out.println("分類: 高齢者");
        }
    }
    
    /**
     * 年を重ねる（複数年）
     * @param years 年数
     */
    void ageYears(int years) {
        if (years > 0) {
            age += years;
            System.out.println(years + "年経過しました。現在" + age + "歳です。");
        } else {
            System.out.println("正の年数を指定してください。");
        }
    }
    
    /**
     * 挨拶をするメソッド
     */
    void greet() {
        System.out.println("こんにちは！" + name + "です。よろしくお願いします。");
    }
    
    /**
     * 情報を設定するメソッド
     * @param newName 新しい名前
     * @param newAge 新しい年齢
     */
    void setInfo(String newName, int newAge) {
        if (newName != null && !newName.isEmpty() && newAge >= 0) {
            name = newName;
            age = newAge;
            System.out.println("情報を更新しました: " + name + ", " + age + "歳");
        } else {
            System.out.println("無効な情報です。更新できませんでした。");
        }
    }
    
    /**
     * 現在の情報を表示するメソッド
     */
    void displayInfo() {
        System.out.println("名前: " + (name != null ? name : "未設定"));
        System.out.println("年齢: " + age + "歳");
    }
    
    /**
     * 同い年かどうかを判定するメソッド
     * @param other 比較対象のPersonオブジェクト
     * @return 同い年の場合true
     */
    boolean isSameAge(Person other) {
        if (other != null) {
            return this.age == other.age;
        }
        return false;
    }
    
    /**
     * より年上かどうかを判定するメソッド
     * @param other 比較対象のPersonオブジェクト
     * @return より年上の場合true
     */
    boolean isOlder(Person other) {
        if (other != null) {
            return this.age > other.age;
        }
        return false;
    }
}

/*
 * クラス設計のポイント:
 * 
 * 1. インスタンス変数（フィールド）
 *    - クラスのオブジェクトが持つデータを定義
 *    - name: そのPersonの名前
 *    - age: そのPersonの年齢
 * 
 * 2. メソッド
 *    - オブジェクトに対して実行できる操作を定義
 *    - introduce(): 自己紹介の表示
 *    - birthday(): 年齢の増加処理
 * 
 * 3. メソッドの種類
 *    - void型: 戻り値なし、何かの処理を実行
 *    - boolean型: true/falseを返す判定メソッド
 *    - 引数あり: 外部からデータを受け取って処理
 * 
 * よくある間違い:
 * 
 * 1. インスタンス変数の未初期化
 *    - Stringは null、intは 0 で自動初期化される
 *    - 使用前に適切な値を設定することが重要
 * 
 * 2. nullチェックの忘れ
 *    - String型の変数はnullの可能性がある
 *    - メソッド内でnullチェックを行う
 * 
 * 3. メソッド名の命名
 *    - 動詞で始める: introduce, birthday, greet
 *    - 判定メソッドはis/hasで始める: isSameAge, isOlder
 * 
 * 設計の改善点:
 * - エラーハンドリングの追加
 * - データの妥当性チェック
 * - より多くの便利メソッドの提供
 */