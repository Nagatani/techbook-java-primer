<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 8/11
行範囲: 1291 - 1487
作成日時: 2025-08-02 21:08:55

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

### 学習した重要概念

1. カプセル化
   - データと処理を1つのクラスにまとめる
   - privateフィールドとpublicメソッドによるアクセス制御
   - データの整合性と安全性の確保

2. アクセス修飾子
   - `private`: 同じクラス内のみ
   - パッケージプライベート（デフォルト）: 同じパッケージ内
   - `protected`: 同じパッケージかサブクラス
   - `public`: どこからでもアクセス可能

3. 実践的なクラス設計
   - BankAccountの段階的改善（V0→V1→V2→V3）
   - getter/setterによる安全なデータアクセス
   - バリデーションによるデータ検証
   - 防御的コピーによる内部状態の保護

4. コンストラクタとthisキーワード
   - デフォルトコンストラクタとカスタムコンストラクタ
   - コンストラクタオーバーロード
   - thisキーワードの3つの用法
   - メソッドオーバーロードによる柔軟なAPI設計

5. パッケージシステム
   - 機能別・層別によるクラスの組織化
   - ドメイン名を逆にした一意な名前空間
   - import文による明示的な依存関係の表現

### 次章への展望

第5章「継承とポリモーフィズム」では、本章で学んだクラス設計の技術をさらに発展させ、クラス間の関係性を表現する方法を学びます。継承にから既存のクラスを拡張し、ポリモーフィズムにから柔軟で拡張性の高いプログラムを作成する技術を習得します。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されます。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter04/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

## 設計上やってしまいやすい問題とその解決方法

オブジェクト指向プログラミングを学び始めた開発者が陥りやすい設計上の問題と、その解決方法について詳しく説明します。これらは実行時エラーではなく、設計品質に関わる問題です。

### 1. 神クラス（Godクラス）の問題

#### 問題の概要
神クラスとは、あまりにも多くの責任を持ち、多くの機能を詰め込んだ巨大なクラスのことです。
初心者は「すべてを1つのクラスにまとめれば管理が楽」と考えがちですが、実際には逆効果となります。

#### 問題のあるコード例

<span class="listing-number">**サンプルコード4-26**</span>

```java
// 一つのクラスに責任を詰め込みすぎた例
public class UserManager {
    private String username;
    private String password;
    private String email;
    
    // データベース操作
    public void saveToDatabase() { /* ... */ }
    public void deleteFromDatabase() { /* ... */ }
    
    // メール送信
    public void sendWelcomeEmail() { /* ... */ }
    public void sendPasswordResetEmail() { /* ... */ }
    
    // ログイン処理
    public boolean authenticate(String password) { /* ... */ }
    public void generateSession() { /* ... */ }
    
    // レポート生成
    public void generateUserReport() { /* ... */ }
    public void exportToCSV() { /* ... */ }
}
```

#### なぜこれが問題なのか

1. 保守性の低下
    + 1つのクラスが大きくなりすぎて、どこに何があるか把握が困難
2. テストの困難さ
    + 多くの依存関係があり、単体テストが書きにくい
3. 再利用性の欠如
    + 機能が密結合しているため、一部だけを再利用できない
4. 変更の影響範囲
    + 1つの変更が予期しない箇所に影響を与える可能性
5. 並行開発の困難
    + 複数の開発者が同じクラスを修正すると競合が発生

#### 解決方法：責任の分離
<span class="listing-number">**サンプルコード4-27**</span>

```java
// 責任を分離したクラス設計
public class User {
    private String username;
    private String password;
    private String email;
    
    // コンストラクタ、getter、setter
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    // 基本的なユーザー操作のみ
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
}

public class UserRepository {
    public void save(User user) { /* データベース操作 */ }
    public void delete(String username) { /* データベース操作 */ }
}

public class EmailService {
    public void sendWelcomeEmail(User user) { /* メール送信 */ }
    public void sendPasswordResetEmail(User user) { /* メール送信 */ }
}

public class AuthenticationService {
    public boolean authenticate(User user, String password) { /* 認証処理 */ }
    public String generateSession(User user) { /* セッション生成 */ }
}
```

#### 解決策のメリット

1. 高い保守性
    + 各クラスの責任が明確で、修正箇所が特定しやすい
2. テストの容易さ
    + 各クラスを独立してテスト可能
3. 再利用性の向上
    + EmailServiceは他のエンティティでも利用可能
4. 変更の局所化
    + データベースの変更はUserRepositoryのみに影響
5. 並行開発の促進
    + 異なる開発者が異なるクラスを担当できる

#### 解決策のデメリット

1. クラス数の増加
    + 管理すべきファイル数が増える
2. 初期開発の複雑さ
    + 最初の設計に時間がかかる
3. 過度な分割のリスク
    + 細かすぎる分割は逆に複雑性を増す
4. パッケージ構成の必要性
    + 明確なパッケージ分けが必要

#### 最適なバランスの見つけ方

- 凝集度を高く保つ
-    + 関連する機能は同じクラスに
- 結合度を低く保つ
-    + クラス間の依存は最小限に
- 将来の拡張性を考慮
-    + 新機能追加時の影響を予測
- チームの規模に応じた設計
-    + 小規模なら適度な分割で十分

### 2. 過度なgetter/setterの使用

#### 問題の概要
すべてのフィールドに機械的にgetterとsetterを作成することは、カプセル化の意味を失わせ、単なるデータ構造と変わらなくなってしまいます。

#### 問題のあるコード例

<span class="listing-number">**サンプルコード4-28**</span>

```java
public class BankAccount {
    private double balance;
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}

// 使用例での問題
BankAccount account = new BankAccount();
account.setBalance(1000.0);
double currentBalance = account.getBalance();
account.setBalance(currentBalance - 100.0);  // 直接残高操作
```



<!-- 
================
チャンク 8/11 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
