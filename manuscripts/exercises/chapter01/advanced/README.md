# 第1章 発展課題

## 概要
基礎課題を完了した学習者向けの発展的な課題です。より実践的なプログラムを作成し、Javaプログラミングの理解を深めます。

## 学習目標
- 複数の概念を組み合わせたプログラムを作成できる
- ユーザー入力を扱うプログラムを実装できる
- エラー処理の基礎を理解する
- より複雑なロジックを実装できる

## 課題一覧

### 課題A-1: 電卓プログラム
**ファイル名**: `Calculator.java`

#### 要求仕様
1. 2つの数値と演算子を変数に格納し、計算結果を表示する
2. 対応する演算：加算(+)、減算(-)、乗算(*)、除算(/)
3. 0による除算の場合はエラーメッセージを表示
4. 小数の計算にも対応する（double型を使用）

#### 実装のヒント
- switch文またはif-else文で演算子を判定
- 除算前に除数が0でないかチェック
- 結果は小数点以下2桁まで表示

#### 実装例の骨組み
```java
public class Calculator {
    public static void main(String[] args) {
        double num1 = 10.5;
        double num2 = 3.2;
        char operator = '+';
        double result = 0;
        
        // 演算子に応じた計算
        switch (operator) {
            case '+':
                result = num1 + num2;
                break;
            // 他の演算子も実装
        }
        
        // 結果の表示
        System.out.printf("%.1f %c %.1f = %.2f%n", num1, operator, num2, result);
    }
}
```

#### 評価ポイント
- すべての演算が正しく実装されている
- 0除算のエラー処理が適切
- 出力形式が整っている

#### 発展学習
- 剰余演算（%）も追加する
- 累乗計算を実装する
- 三項演算子を使った実装を試す

---

### 課題A-2: 個人情報管理プログラム
**ファイル名**: `PersonalInfo.java`

#### 要求仕様
1. 以下の個人情報を変数で管理し、整形して表示する：
   - 氏名（姓・名を別々に管理）
   - 生年月日（年・月・日を別々に管理）
   - 住所（都道府県・市区町村を別々に管理）
   - 電話番号
   - メールアドレス
2. 現在の年齢を計算して表示（2025年基準）
3. すべての情報を見やすい形式で出力

#### 実装のヒント
- 文字列の連結と書式設定を活用
- 年齢計算は現在年から生年を引く
- 情報を区切り線などで見やすく整形

#### 実装例の骨組み
```java
public class PersonalInfo {
    public static void main(String[] args) {
        // 個人情報の変数
        String lastName = "山田";
        String firstName = "太郎";
        int birthYear = 2000;
        int birthMonth = 4;
        int birthDay = 15;
        // 他の情報も定義
        
        // 年齢計算
        int currentYear = 2025;
        int age = currentYear - birthYear;
        
        // 情報の表示
        System.out.println("===== 個人情報 =====");
        System.out.println("氏名: " + lastName + " " + firstName);
        // 残りの情報も表示
    }
}
```

#### 評価ポイント
- すべての情報が適切に管理されている
- 年齢計算が正しい
- 出力が見やすく整形されている

#### 発展学習
- 誕生日が来ているかを考慮した正確な年齢計算
- 郵便番号から住所の一部を推定する機能
- 入力値の妥当性チェック

---

### 課題A-3: 時間計算プログラム
**ファイル名**: `TimeCalculation.java`

#### 要求仕様
1. 開始時刻と終了時刻を時・分で管理
2. 経過時間を計算して表示
3. 経過時間を以下の形式で表示：
   - 「○時間○分」形式
   - 合計分数
   - 合計秒数
4. 日をまたぐ場合も考慮（24時間表記）

#### 実装のヒント
- 時刻を分に変換してから計算
- 商と余りを使って時間と分を分離
- 24時間を超える場合の処理

#### 実装例の骨組み
```java
public class TimeCalculation {
    public static void main(String[] args) {
        // 開始時刻
        int startHour = 9;
        int startMinute = 30;
        
        // 終了時刻
        int endHour = 15;
        int endMinute = 45;
        
        // 分に変換
        int startTotalMinutes = startHour * 60 + startMinute;
        int endTotalMinutes = endHour * 60 + endMinute;
        
        // 経過時間の計算
        int elapsedMinutes = endTotalMinutes - startTotalMinutes;
        
        // 結果の表示
        // 実装を完成させる
    }
}
```

#### 評価ポイント
- 時間計算が正確
- 複数の形式で結果を表示
- 日をまたぐケースの処理

#### 発展学習
- 12時間表記（AM/PM）への変換
- 複数の時間帯の合計を計算
- 時差を考慮した計算

## 提出方法
1. 各課題のJavaファイルを作成
2. 動作確認を十分に行う
3. コメントを適切に追加してコードの意図を明確にする
4. ソースコード（.javaファイル）のみを提出

## 注意事項
- 変数名は内容がわかりやすい名前を使用
- 計算ロジックにコメントを追加
- 出力は実行例のとおりになるよう注意