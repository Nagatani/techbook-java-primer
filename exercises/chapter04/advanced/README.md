# 第4章 応用課題

## 🎯 学習目標
- アクセス修飾子の適切な使い分け
- セキュアなクラス設計
- カプセル化の実践的な応用
- パッケージを考慮した設計
- データの整合性保護

## 📝 課題一覧

### 課題1: セキュアユーザー管理システム
**ファイル名**: `SecureUserSystem.java`

ユーザーの機密情報を安全に管理するシステムを作成してください。

**要求仕様**:
- パスワードの暗号化保存
- 個人情報の適切な保護
- 権限レベルによるアクセス制御
- 不正アクセスの検出と防止

**セキュリティ要件**:
- パスワードは平文で保存しない
- 個人情報は直接アクセス不可
- ログイン試行回数の制限
- セッション管理

**実装すべきクラス**:

```java
class User {
    // private: パスワードハッシュ、個人情報
    // protected: ユーザーID、権限レベル  
    // public: 基本的な操作メソッドのみ
    // package-private: 管理用メソッド
}

class SecurityManager {
    // private: セキュリティポリシー
    // package-private: 内部検証メソッド
    // public: 認証・認可インターフェイス
}
```

**実行例**:
```
=== セキュアユーザー管理システム ===

🔒 SecureCloud Enterprise v2.0

=== ユーザー登録 ===
✓ ユーザー登録: admin@company.com
  権限レベル: ADMIN
  パスワード: [暗号化済み]
  
✓ ユーザー登録: user@company.com  
  権限レベル: USER
  パスワード: [暗号化済み]

=== ログイン試行 ===
ログイン試行: admin@company.com
パスワード: ********
✓ 認証成功 - ADMINセッション開始

ログイン試行: user@company.com
パスワード: wrongpass
✗ 認証失敗 (1/3回)

ログイン試行: user@company.com  
パスワード: wrongpass
✗ 認証失敗 (2/3回)

ログイン試行: user@company.com
パスワード: wrongpass
✗ 認証失敗 (3/3回)
🚨 アカウントロック (5分間)

=== アクセス制御 ===
ADMINセッション:
✓ ユーザー一覧表示: 許可
✓ システム設定変更: 許可
✓ ログ参照: 許可

USERセッション:
✓ プロフィール表示: 許可
✗ ユーザー一覧表示: 権限不足
✗ システム設定変更: 権限不足

=== セキュリティレポート ===
🛡️ セキュリティ統計:
- 総ユーザー数: 2名
- アクティブセッション: 1個
- 失敗ログイン: 3回
- ロックアカウント: 1個
- セキュリティアラート: 0件
```

**評価ポイント**:
- 機密データの適切な保護
- アクセス修飾子の戦略的使用
- セキュリティ機能の実装
- エラーハンドリングの充実



### 課題2: 設定管理システム
**ファイル名**: `ConfigurationSystem.java`

アプリケーションの設定を安全に管理するシステムを作成してください。

**要求仕様**:
- 設定値の階層管理
- 環境別設定の切り替え
- 設定変更の履歴管理
- 不正な設定値の検証

**設計要件**:
- 設定値は直接変更不可
- 検証済みの値のみ設定可能
- 環境固有の設定は保護
- デフォルト値の適切な管理

**実装すべきクラス**:

```java
class Configuration {
    // private: 実際の設定値
    // protected: 環境別設定
    // public: 取得メソッドのみ
    // package-private: 管理メソッド
}

class ConfigValidator {
    // private: 検証ルール
    // package-private: 検証メソッド
    // public: 検証インターフェイス
}
```

**実行例**:
```
=== 設定管理システム ===

⚙️ AppConfig Manager v1.5

=== 環境設定 ===
現在の環境: DEVELOPMENT
設定ファイル: config-dev.properties

=== 基本設定読み込み ===
✓ データベース接続: jdbc:h2:mem:testdb
✓ サーバーポート: 8080
✓ ログレベル: DEBUG
✓ 最大ユーザー数: 100

=== 設定変更試行 ===
設定変更: サーバーポート → 9090
✓ 検証OK: ポート番号範囲内
✓ 設定更新完了

設定変更: 最大ユーザー数 → -1
✗ 検証エラー: 正の整数が必要
設定変更却下

設定変更: データベースURL → invalid-url
✗ 検証エラー: 無効なURL形式
設定変更却下

=== 環境切り替え ===
環境変更: DEVELOPMENT → PRODUCTION

PRODUCTION環境の設定:
✓ データベース接続: jdbc:mysql://prod-db:3306/app
✓ サーバーポート: 80
✓ ログレベル: INFO
✓ 最大ユーザー数: 10000

=== 設定履歴 ===
変更履歴:
2024-07-05 10:30:15 | server.port: 8080 → 9090 | admin
2024-07-05 10:32:22 | environment: DEV → PROD | admin
2024-07-05 10:35:45 | log.level: DEBUG → INFO | system

=== セキュリティ検証 ===
🔒 保護設定へのアクセス試行:
✗ database.password: アクセス拒否
✗ secret.key: アクセス拒否
✓ public.api.endpoint: 参照許可

=== 設定バックアップ ===
✓ 設定スナップショット作成: config-backup-20240705.json
✓ 復元ポイント登録: snapshot-001
```

**評価ポイント**:
- 設定の安全な管理
- バリデーション機能
- 履歴管理の実装
- 環境別設定の分離



### 課題3: 金融取引システム
**ファイル名**: `FinancialTransaction.java`

金融取引の安全性を確保した取引システムを作成してください。

**要求仕様**:
- 取引データの改ざん防止
- 残高の整合性保証
- 取引の原子性確保
- 監査ログの完全性

**セキュリティ要件**:
- 取引一度確定したら変更不可
- 残高は計算結果のみ参照可能
- 取引履歴は追記のみ
- 不正な操作の検出

**実装すべきクラス**:

```java
class Account {
    // private: 残高、取引履歴
    // protected: 口座情報
    // public: 取引インターフェイスのみ
}

class Transaction {
    // private final: 取引詳細（イミュータブル）
    // package-private: 検証メソッド
    // public: 参照メソッドのみ
}

class TransactionProcessor {
    // private: 処理ロジック
    // package-private: 内部処理
    // public: 取引実行インターフェイス
}
```

**実行例**:
```
=== 金融取引システム ===

🏦 SecureBank Transaction Engine v3.0

=== 口座開設 ===
✓ 口座開設: ACC-001 (山田太郎)
  初期残高: ¥100,000
  口座種別: 普通預金

✓ 口座開設: ACC-002 (佐藤花子)
  初期残高: ¥50,000
  口座種別: 普通預金

=== 取引実行 ===
取引1: 振込処理
送金者: ACC-001 (山田太郎)
受取者: ACC-002 (佐藤花子)  
金額: ¥30,000

✓ 送金者残高確認: ¥100,000 >= ¥30,000
✓ 手数料計算: ¥200
✓ 取引認証: 成功
✓ 原子的処理実行:
  - ACC-001: ¥100,000 → ¥69,800
  - ACC-002: ¥50,000 → ¥80,000
✓ 取引完了: TXN-20240705-001

取引2: 不正な取引試行
送金者: ACC-002 (佐藤花子)
受取者: ACC-001 (山田太郎)
金額: ¥100,000

✗ 残高不足: ¥80,000 < ¥100,000
✗ 取引拒否: TXN-20240705-002

=== 残高照会 ===
口座: ACC-001 (山田太郎)
現在残高: ¥69,800
利用可能残高: ¥69,800
保留中: ¥0

口座: ACC-002 (佐藤花子)
現在残高: ¥80,000
利用可能残高: ¥80,000
保留中: ¥0

=== 取引履歴 ===
ACC-001の取引履歴:
2024-07-05 10:00:00 | 開設     | +¥100,000 | 残高: ¥100,000
2024-07-05 10:15:30 | 振込送金 | -¥30,200  | 残高: ¥69,800

ACC-002の取引履歴:
2024-07-05 10:05:00 | 開設     | +¥50,000  | 残高: ¥50,000
2024-07-05 10:15:30 | 振込受取 | +¥30,000  | 残高: ¥80,000

=== セキュリティ監査 ===
🔍 監査結果:
- 総取引件数: 4件
- 成功取引: 3件
- 失敗取引: 1件
- データ整合性: ✓正常
- 不正な操作: 0件検出
- システム健全性: ✓良好
```

**評価ポイント**:
- データの改ざん防止
- 取引の原子性実装
- セキュリティ監査機能
- エラー処理の完全性

## 💡 実装のヒント

### 課題1のヒント
```java
class User {
    private String passwordHash;  // 暗号化されたパスワード
    private String email;         // 機密情報
    protected int userId;         // サブクラスでアクセス可能
    protected AccessLevel level;  // 権限レベル
    
    // private メソッド - 内部でのみ使用
    private String hashPassword(String password) {
        // 暗号化ロジック
        return Integer.toString(password.hashCode()); // 簡易実装
    }
    
    // package-private - 同パッケージの管理クラスのみ
    boolean authenticate(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }
    
    // public - 外部からの安全なインターフェイス
    public boolean changePassword(String oldPassword, String newPassword) {
        if (authenticate(oldPassword)) {
            this.passwordHash = hashPassword(newPassword);
            return true;
        }
        return false;
    }
}
```

### 課題2のヒント
```java
class Configuration {
    private final Map<String, Object> settings;
    private final Environment environment;
    protected final Map<String, Object> defaults;
    
    // private - 直接的な設定変更を防ぐ
    private void setInternal(String key, Object value) {
        settings.put(key, value);
    }
    
    // package-private - 設定管理クラスからのみアクセス
    boolean updateSetting(String key, Object value) {
        if (ConfigValidator.validate(key, value)) {
            setInternal(key, value);
            logChange(key, value);
            return true;
        }
        return false;
    }
    
    // public - 安全な読み取り専用アクセス
    public <T> T getSetting(String key, Class<T> type) {
        Object value = settings.get(key);
        return type.cast(value != null ? value : defaults.get(key));
    }
}
```

### 課題3のヒント
```java
public final class Transaction {
    private final String transactionId;
    private final String fromAccount;
    private final String toAccount;
    private final double amount;
    private final long timestamp;
    private final TransactionType type;
    
    // package-private コンストラクタ - TransactionProcessorのみが作成可能
    Transaction(String id, String from, String to, double amount, TransactionType type) {
        this.transactionId = id;
        this.fromAccount = from;
        this.toAccount = to;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.type = type;
    }
    
    // public - 取引情報の読み取り専用アクセス
    public String getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public long getTimestamp() { return timestamp; }
    
    // private - 内部検証
    private boolean isValid() {
        return amount > 0 && fromAccount != null && toAccount != null;
    }
}
```

## 🔍 応用のポイント

1. **セキュリティ設計**: 機密データの適切な保護とアクセス制御
2. **カプセル化**: データの整合性を保つインターフェイス設計
3. **パッケージ設計**: 関連クラス間の適切な協調関係
4. **不変性**: 重要なデータの改ざん防止
5. **検証**: 入力データの妥当性確認と例外処理

## ✅ 完了チェックリスト

- [ ] 課題1: セキュアユーザー管理システムが正常に動作する
- [ ] 課題2: 設定管理システムが実装できた
- [ ] 課題3: 金融取引システムが動作する
- [ ] アクセス修飾子が適切に使い分けられている
- [ ] セキュリティ要件が満たされている
- [ ] データの整合性が保たれている

**次のステップ**: 応用課題が完了したら、challenge/のチャレンジ課題に挑戦してみましょう！