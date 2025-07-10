# 第3章 コンストラクタとthisキーワード - 解答例

## 📚 本章の学習目標

この章では、Javaにおけるコンストラクタとthisキーワードの重要な概念を学習します。

### 主要な学習ポイント
1. **コンストラクタの基本概念**
   - オブジェクトの初期化処理
   - デフォルトコンストラクタとカスタムコンストラクタ
   - コンストラクタのオーバーロード

2. **thisキーワードの活用**
   - インスタンス変数と引数の区別
   - thisを使ったコンストラクタチェーン
   - メソッド内でのthisの使用

3. **メソッドオーバーロード**
   - 同じ名前のメソッドで異なる引数
   - 引数の型・数・順序による区別
   - 適切なオーバーロードの設計

## 🔧 解答例の構成

### 基本解答パターン
各課題について、以下の3つのレベルで解答例を提供しています：

1. **基本解答**: 課題の要求を満たす最小限の実装
2. **応用解答**: 実用性を高める機能の追加
3. **発展解答**: 高度な機能とベストプラクティス

## 📝 課題別解答例

### 課題1: Student クラス
**ファイル**: `Student.java`, `StudentTest.java`

#### 実装のポイント
- **コンストラクタチェーン**: `this()`を使用した効率的な初期化
- **thisキーワード**: 引数名と変数名の区別
- **妥当性チェック**: 年齢の負の値チェック

```java
// 【基本解答】コンストラクタチェーンの例
public Student() {
    this("未設定", 0, "未設定");
}

public Student(String name) {
    this(name, 0, "未設定");
}

public Student(String name, int age, String studentId) {
    this.name = name;
    this.age = age;
    this.studentId = studentId;
}
```

#### よくある間違いと対策
1. **this()の位置**: コンストラクタ内の最初に記述する必要がある
2. **無限ループ**: コンストラクタ同士が循環参照しないよう注意
3. **thisキーワードの省略**: 引数名と変数名が同じ場合は必須

### 課題2: Rectangle クラス
**ファイル**: `Rectangle.java`, `RectangleTest.java`

#### 実装のポイント
- **メソッドオーバーロード**: `setSize()`メソッドの複数バージョン
- **正方形の扱い**: 幅と高さが同じ特殊ケース
- **計算の正確性**: 浮動小数点数の精度問題

```java
// 【基本解答】メソッドオーバーロードの例
public void setSize(double size) {
    this.width = Math.abs(size);
    this.height = Math.abs(size);
}

public void setSize(double width, double height) {
    this.width = Math.abs(width);
    this.height = Math.abs(height);
}
```

#### よくある間違いと対策
1. **負の値の処理**: Math.abs()を使用して負の値を防ぐ
2. **浮動小数点の比較**: 完全一致ではなく誤差を考慮した比較
3. **オーバーロードの条件**: 引数の型・数・順序が異なる必要がある

### 課題3: BankAccount クラス
**ファイル**: `BankAccount.java`, `BankAccountTest.java`

#### 実装のポイント
- **コンストラクタチェーン**: 複数の初期化パターン
- **取引メソッド**: 入金・出金のオーバーロード
- **エラーハンドリング**: 残高不足や不正な値の処理

```java
// 【基本解答】入金メソッドのオーバーロード
public void deposit(double amount) {
    if (amount > 0) {
        this.balance += amount;
    }
}

public void deposit(double amount, String message) {
    if (amount > 0) {
        this.balance += amount;
        System.out.println(amount + "円を入金しました。（" + message + "）");
    }
}
```

#### よくある間違いと対策
1. **残高チェック**: 出金時の残高確認を忘れない
2. **戻り値の活用**: boolean型で処理結果を返す
3. **thisキーワード**: 引数名と変数名の区別

### 課題4: Time クラス
**ファイル**: `Time.java`, `TimeTest.java`

#### 実装のポイント
- **時刻の正規化**: 60秒=1分、60分=1時間、24時間=1日
- **複雑な計算**: 時刻の加算・減算処理
- **境界値処理**: 23:59:59 → 00:00:00の変換

```java
// 【基本解答】時刻の正規化処理
private void normalize() {
    while (second >= 60) {
        second -= 60;
        minute++;
    }
    while (minute >= 60) {
        minute -= 60;
        hour++;
    }
    while (hour >= 24) {
        hour -= 24;
    }
}
```

#### よくある間違いと対策
1. **正規化の順序**: 秒→分→時の順序で処理
2. **負の値の処理**: 減算時の借り上げ処理
3. **24時間制**: 0-23時の範囲での処理

## 🔍 重要な学習ポイント

### 1. コンストラクタの設計原則
- **単一責任**: 各コンストラクタは明確な初期化パターンを持つ
- **DRY原則**: コンストラクタチェーンで重複コードを削減
- **妥当性チェック**: 不正な値の早期発見

### 2. thisキーワードの使用場面
- **変数の区別**: 引数名と変数名が同じ場合
- **コンストラクタチェーン**: this()による他のコンストラクタ呼び出し
- **メソッドチェーン**: メソッドの戻り値としてthisを返す

### 3. メソッドオーバーロードの原則
- **明確な区別**: 引数の型・数・順序で区別
- **一貫性**: 同じ名前のメソッドは関連する処理を行う
- **可読性**: 呼び出し側が理解しやすい設計

## 🚀 発展的な実装例

### オブジェクトの比較
```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    
    Student student = (Student) obj;
    return age == student.age &&
           name.equals(student.name) &&
           studentId.equals(student.studentId);
}
```

### 文字列表現の提供
```java
@Override
public String toString() {
    return String.format("Student{name='%s', age=%d, studentId='%s'}", 
                       name, age, studentId);
}
```

### 静的メソッドの活用
```java
public static double getTotalArea(Rectangle r1, Rectangle r2) {
    return r1.getArea() + r2.getArea();
}
```

## 🧪 テストコードの設計

### テストの種類
1. **正常系テスト**: 期待される動作の確認
2. **異常系テスト**: エラーケースの処理確認
3. **境界値テスト**: 限界値での動作確認
4. **統合テスト**: 複数のメソッドの連携確認

### テストの実装例
```java
// 正常系テスト
Student student = new Student("テスト太郎", 20, "S2024001");
assert student.getName().equals("テスト太郎");
assert student.getAge() == 20;

// 異常系テスト
BankAccount account = new BankAccount("12345", 1000.0, "テスト");
boolean result = account.withdraw(2000.0);  // 残高不足
assert !result;  // 出金が失敗することを確認
```

## 📊 学習効果の測定

### 理解度チェックリスト
- [ ] コンストラクタの基本概念を理解している
- [ ] thisキーワードの使い方を把握している
- [ ] メソッドオーバーロードを適切に実装できる
- [ ] エラーハンドリングを考慮した設計ができる
- [ ] テストコードを書いて動作確認ができる

### 実践的スキル
- [ ] 実際のプロジェクトでコンストラクタを設計できる
- [ ] 適切なメソッドオーバーロードを実装できる
- [ ] オブジェクトの状態管理を適切に行える
- [ ] バグの発生しにくいコードを書ける

## 🎯 次のステップ

### 第4章への準備
1. **カプセル化の理解**: privateフィールドとpublicメソッドの使い分け
2. **アクセス修飾子**: public, private, protectedの適切な使用
3. **データの保護**: 外部からの不正アクセスの防止

### 発展的な学習
1. **デザインパターン**: BuilderパターンやFactoryパターンの学習
2. **リファクタリング**: 既存コードの改善技法
3. **単体テスト**: JUnitを使った本格的なテスト実装

## 💡 実践的なヒント

### コードの品質向上
1. **命名規則**: 分かりやすい変数名・メソッド名
2. **コメント**: 実装の意図を明確にする
3. **エラーメッセージ**: 問題の特定が容易になるメッセージ

### デバッグのコツ
1. **段階的な実装**: 少しずつ機能を追加してテスト
2. **ログ出力**: 処理の流れを追跡できるログ
3. **単体テスト**: 個別の機能を独立してテスト



**注意**: この解答例は学習目的で作成されています。実際の開発では、プロジェクトの要件や規約に従って適切に修正してください。