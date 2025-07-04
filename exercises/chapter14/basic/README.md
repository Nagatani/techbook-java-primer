# 第14章 基本課題

## 🎯 学習目標
- 例外処理の基本概念（例外の種類、try-catch-finally）
- 検査例外と非検査例外の理解と使い分け
- カスタム例外クラスの設計と実装
- try-with-resources によるリソース管理
- 例外処理のベストプラクティス
- 例外チェーン（Exception Chaining）による根本原因の追跡技法

## 📝 課題一覧

### 課題1: 基本的な例外処理
**ファイル名**: `BasicExceptionHandling.java`, `BasicExceptionTest.java`

基本的な例外処理を実装し、try-catch-finallyの動作を理解してください。

**要求仕様**:
- 様々な種類の例外処理（ArithmeticException、NullPointerException等）
- 複数catch文による例外の使い分け
- finallyブロックの動作確認
- 例外情報の取得と表示
- 例外の再発生（re-throw）

**実行例**:
```
=== 基本的な例外処理 ===
ゼロ除算テスト:
計算実行: 10 / 0
例外発生: java.lang.ArithmeticException: / by zero
finally実行: 計算処理を終了します

null参照テスト:
文字列操作: null.length()
例外発生: java.lang.NullPointerException
スタックトレース:
  at BasicExceptionHandling.stringOperation(BasicExceptionHandling.java:25)
  at BasicExceptionHandling.main(BasicExceptionHandling.java:15)

配列範囲外テスト:
配列アクセス: array[10] (配列サイズ: 5)
例外発生: java.lang.ArrayIndexOutOfBoundsException: Index 10 out of bounds for length 5
finally実行: 配列処理を終了します

複数catch文テスト:
入力値: "abc"
NumberFormatException発生: 数値変換に失敗しました
代替値を使用: 0

例外の再発生テスト:
内部メソッドで例外処理 → 外部メソッドで再処理
最終的な例外処理: 処理を中断しました
```

**評価ポイント**:
- try-catch-finallyの正しい使用
- 例外の種類に応じた適切な処理
- 例外情報の有効活用

---

### 課題2: カスタム例外クラス設計
**ファイル名**: `BankAccountException.java`, `InsufficientFundsException.java`, `InvalidAccountException.java`, `BankAccount.java`, `CustomExceptionTest.java`

銀行口座システムのカスタム例外を設計し、ビジネスロジックに応じた例外処理を実装してください。

**要求仕様**:
- 銀行業務固有の例外クラス（残高不足、無効口座等）
- 例外クラスの継承関係設計
- 例外に詳細情報を含める仕組み
- エラーコードとメッセージの管理
- 業務的な例外処理フロー

**実行例**:
```
=== カスタム例外クラス設計 ===
銀行口座作成:
口座1: 12345（残高: 100,000円）
口座2: 67890（残高: 50,000円）

通常取引テスト:
口座12345から30,000円出金: 成功
残高: 70,000円

残高不足例外テスト:
口座67890から80,000円出金:
例外発生: InsufficientFundsException
エラーコード: INSUFFICIENT_FUNDS
メッセージ: 残高が不足しています
詳細: 要求額: 80,000円, 現在残高: 50,000円, 不足額: 30,000円

無効口座例外テスト:
存在しない口座 99999 への振込:
例外発生: InvalidAccountException
エラーコード: ACCOUNT_NOT_FOUND
メッセージ: 指定された口座が見つかりません
詳細: 口座番号: 99999

振込処理例外テスト:
口座12345から口座67890へ80,000円振込:
例外発生: InsufficientFundsException
取引がロールバックされました
口座12345残高: 70,000円（変更なし）
口座67890残高: 50,000円（変更なし）

例外チェーン:
原因: InsufficientFundsException: 残高が不足しています
結果: TransactionFailedException: 取引処理に失敗しました
理由: 出金処理でエラーが発生
```

**評価ポイント**:
- ビジネスドメイン固有の例外設計
- 例外クラスの継承関係の理解
- 詳細な例外情報の提供

---

### 課題3: ファイル操作とリソース管理
**ファイル名**: `FileProcessor.java`, `ResourceManagerTest.java`

ファイル操作におけるリソース管理と例外処理を実装してください。

**要求仕様**:
- try-with-resources によるリソース管理
- ファイル読み込み時の例外処理
- ファイル書き込み時の例外処理
- 複数リソースの同時管理
- カスタムリソースクラスの実装

**実行例**:
```
=== ファイル操作とリソース管理 ===
ファイル読み込みテスト:
ファイル: sample.txt
内容:
Hello, World!
Java Exception Handling
Try-with-resources example

リソース自動クローズ確認:
BufferedReader がクローズされました

存在しないファイル読み込み:
ファイル: nonexistent.txt
例外発生: java.io.FileNotFoundException: nonexistent.txt (No such file or directory)
処理を継続します

ファイル書き込みテスト:
出力ファイル: output.txt
書き込み内容: "Hello from Java Exception Handling!"
書き込み完了
リソース自動クローズ確認:
BufferedWriter がクローズされました

複数リソース管理テスト:
入力ファイル: input.txt
出力ファイル: copy.txt
ファイルコピー処理開始
コピー完了: 145バイト
両方のリソースがクローズされました

カスタムリソーステスト:
DatabaseConnection 作成
SQL実行: SELECT * FROM users
結果: 5件のレコードを取得
DatabaseConnection 自動クローズ

例外発生時のリソース管理:
処理中に IOException が発生
リソースは確実にクローズされました
例外: java.io.IOException: ネットワークエラー
```

**評価ポイント**:
- try-with-resources の適切な使用
- リソースリークの防止
- AutoCloseable インターフェイスの理解

---

### 課題4: 例外処理設計パターン
**ファイル名**: `ValidationResult.java`, `UserService.java`, `ExceptionDesignTest.java`

例外処理の設計パターンを実装し、保守性の高い例外処理システムを構築してください。

**要求仕様**:
- 例外 vs 戻り値による エラー表現の比較
- Optional を活用した安全なプログラミング
- バリデーション結果の集約
- ログ出力との連携
- 例外処理のパフォーマンス考慮

**実行例**:
```
=== 例外処理設計パターン ===
バリデーション結果パターン:
ユーザー作成: 田中太郎, tanaka@example.com, 25
バリデーション結果: 成功

ユーザー作成: , invalid-email, -5
バリデーション結果: 失敗
エラー詳細:
- 名前が空です
- メールアドレスの形式が正しくありません  
- 年齢は0以上である必要があります

Optional パターン:
ユーザー検索: ID=1001
結果: Optional[User{id=1001, name=田中太郎}]

ユーザー検索: ID=9999
結果: Optional.empty
デフォルトユーザーを使用: Guest User

例外 vs 戻り値の比較:
例外パターン:
  計算: 10 / 2 = 5.0
  計算: 10 / 0 → ArithmeticException

戻り値パターン:
  計算: 10 / 2 → Result{success=true, value=5.0}
  計算: 10 / 0 → Result{success=false, error="ゼロ除算"}

パフォーマンステスト:
正常処理（1,000,000回）:
例外なし: 150ms
例外あり: 2,450ms
結論: 例外は通常フローに使うべきではない

ログ連携:
[INFO] ユーザー作成開始: 佐藤花子
[ERROR] バリデーション失敗: メールアドレス不正
[WARN] 処理を中断しました
[INFO] 代替処理を実行
```

**評価ポイント**:
- 例外処理の設計判断
- パフォーマンスを考慮した実装
- 保守性の高いエラーハンドリング

## 💡 ヒント

### 課題1のヒント
- 複数catch文は具体的な例外から記述
- finally は必ず実行される（return があっても）
- printStackTrace() でデバッグ情報取得

### 課題2のヒント
- 検査例外は extends Exception
- 非検査例外は extends RuntimeException
- super(message, cause) で例外チェーン

### 課題3のヒント
- try-with-resources: try (Resource r = new Resource())
- AutoCloseable インターフェイスを実装
- 複数リソース: try (R1 r1 = ...; R2 r2 = ...)

### 課題4のヒント
- Optional.ofNullable() で null 安全性
- Result パターンで成功/失敗を表現
- バリデーションは早期リターンよりも集約

## 🔍 例外処理のポイント

1. **例外の種類**: 検査例外 vs 非検査例外
2. **リソース管理**: try-with-resources で確実なクローズ
3. **例外設計**: ビジネスロジックに応じたカスタム例外
4. **パフォーマンス**: 例外は例外的状況でのみ使用
5. **保守性**: 適切なログ出力と例外情報
6. **安全性**: null チェックと Optional の活用

## ✅ 完了チェックリスト

- [ ] 課題1: 基本的な例外処理構文が正しく使えている
- [ ] 課題2: ビジネスロジックに応じたカスタム例外が設計できている
- [ ] 課題3: リソース管理と例外処理が適切に実装できている
- [ ] 課題4: 例外処理の設計パターンが理解できている
- [ ] 検査例外と非検査例外の使い分けができている
- [ ] try-with-resources でリソース管理ができている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより高度な例外処理設計に挑戦しましょう！