/**
 * 第2章 課題1: Personクラスのテスト - 解答例
 * 
 * Personクラスの動作を確認するテストクラス
 * 
 * 学習ポイント:
 * - オブジェクトの生成方法
 * - インスタンス変数への値の設定
 * - メソッドの呼び出し方法
 */
public class PersonTest {
    public static void main(String[] args) {
        System.out.println("=== Person クラステスト ===");
        
        // 解答例 1: 基本的な使用方法
        Person person1 = new Person();
        person1.name = "田中太郎";
        person1.age = 20;
        
        person1.introduce();
        person1.birthday();
        person1.introduce();
        
        System.out.println(); // 空行
        
        // 解答例 2: 複数のオブジェクトを作成
        Person person2 = new Person();
        person2.name = "佐藤花子";
        person2.age = 25;
        
        Person person3 = new Person();
        person3.name = "鈴木一郎";
        person3.age = 30;
        
        System.out.println("=== 複数人の情報 ===");
        person1.detailedIntroduce();
        System.out.println();
        
        person2.detailedIntroduce();
        System.out.println();
        
        person3.detailedIntroduce();
        System.out.println();
        
        // 解答例 3: メソッドの様々な使い方
        System.out.println("=== メソッドの活用例 ===");
        
        // 挨拶
        person1.greet();
        person2.greet();
        person3.greet();
        System.out.println();
        
        // 年齢比較
        System.out.println("=== 年齢比較 ===");
        if (person1.isSameAge(person2)) {
            System.out.println(person1.name + "と" + person2.name + "は同い年です。");
        } else {
            System.out.println(person1.name + "と" + person2.name + "は年齢が違います。");
        }
        
        if (person3.isOlder(person1)) {
            System.out.println(person3.name + "は" + person1.name + "より年上です。");
        }
        
        if (person2.isOlder(person3)) {
            System.out.println(person2.name + "は" + person3.name + "より年上です。");
        } else {
            System.out.println(person2.name + "は" + person3.name + "より年下です。");
        }
        System.out.println();
        
        // 解答例 4: 情報の更新
        System.out.println("=== 情報更新テスト ===");
        person1.displayInfo();
        person1.setInfo("田中二郎", 21);
        person1.displayInfo();
        System.out.println();
        
        // 無効な情報での更新テスト
        person1.setInfo("", -5);  // 無効な情報
        person1.setInfo(null, 100);  // 無効な情報
        System.out.println();
        
        // 解答例 5: 年数経過のテスト
        System.out.println("=== 年数経過テスト ===");
        person2.displayInfo();
        person2.ageYears(5);
        person2.displayInfo();
        person2.ageYears(-3);  // 無効な年数
        System.out.println();
        
        // 解答例 6: 未初期化オブジェクトのテスト
        System.out.println("=== 未初期化オブジェクトのテスト ===");
        Person person4 = new Person();
        person4.displayInfo();  // nameはnull、ageは0
        person4.introduce();    // nullが表示される
        System.out.println();
        
        // 部分的に初期化
        person4.name = "山田次郎";
        person4.displayInfo();
        person4.introduce();
        System.out.println();
        
        // 解答例 7: 配列を使った複数オブジェクトの管理
        System.out.println("=== 配列による複数オブジェクト管理 ===");
        Person[] people = {person1, person2, person3, person4};
        
        System.out.println("全員の情報:");
        for (int i = 0; i < people.length; i++) {
            System.out.println((i + 1) + "番目の人:");
            people[i].displayInfo();
            System.out.println();
        }
        
        // 最年長者を見つける
        Person oldest = people[0];
        for (int i = 1; i < people.length; i++) {
            if (people[i].isOlder(oldest)) {
                oldest = people[i];
            }
        }
        System.out.println("最年長者は " + oldest.name + " さん（" + oldest.age + "歳）です。");
        
        // 平均年齢を計算
        int totalAge = 0;
        int validCount = 0;
        for (Person person : people) {
            if (person.name != null) {  // 有効な人のみカウント
                totalAge += person.age;
                validCount++;
            }
        }
        
        if (validCount > 0) {
            double averageAge = (double) totalAge / validCount;
            System.out.println("平均年齢: " + String.format("%.1f", averageAge) + "歳");
        }
    }
}

/*
 * テストクラスの重要ポイント:
 * 
 * 1. オブジェクトの生成
 *    Person person1 = new Person();
 *    - new演算子でインスタンスを作成
 *    - 各オブジェクトは独立したデータを持つ
 * 
 * 2. フィールドへのアクセス
 *    person1.name = "田中太郎";
 *    - ドット記法でフィールドにアクセス
 *    - 各オブジェクト固有の値を設定
 * 
 * 3. メソッドの呼び出し
 *    person1.introduce();
 *    - ドット記法でメソッドを呼び出し
 *    - そのオブジェクトのデータで処理される
 * 
 * 4. オブジェクトの独立性
 *    - person1, person2, person3は別々のオブジェクト
 *    - 一つのオブジェクトの変更は他に影響しない
 * 
 * テストの観点:
 * 
 * 1. 正常ケース
 *    - 期待通りの動作をするか確認
 *    - 典型的な使用パターンをテスト
 * 
 * 2. 境界ケース
 *    - 未初期化のオブジェクト
 *    - null値の処理
 *    - 無効なデータの処理
 * 
 * 3. 異常ケース
 *    - 不正な引数の処理
 *    - エラーハンドリングの確認
 * 
 * よくある間違いとその確認:
 * 
 * 1. null参照エラー
 *    - 未初期化のnameフィールドを使用
 *    - nullチェックの重要性を理解
 * 
 * 2. オブジェクトの混同
 *    - person1とperson2は別のオブジェクト
 *    - 各オブジェクトが独立したデータを持つ
 * 
 * 3. メソッド呼び出しの間違い
 *    - Person.introduce(); ← 間違い（クラス名で呼び出し）
 *    - person1.introduce(); ← 正解（オブジェクトで呼び出し）
 */