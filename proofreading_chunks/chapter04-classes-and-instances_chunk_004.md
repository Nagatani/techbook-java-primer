<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 4/10
行範囲: 654 - 853
作成日時: 2025-08-03 02:32:41

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->


#### 実行結果

```
口座番号: 123456
初期残高: 10000.0

5000円入金後の残高: 15000.0
3000円出金成功後の残高: 12000.0
20000円出金失敗（残高不足）

取引履歴:
  口座開設: 初期残高 10000.0円
  入金: 5000.0円
  出金: 3000.0円
  出金失敗（残高不足）: 20000.0円
```

#### 完成した設計の特徴
- 不変性
-    + 口座番号はfinalで変更不可
- 完全な検証
-    + すべての入力を検証
- 履歴管理
-    + すべての取引を記録
- 防御的コピー: 内部リストの参照を外部に漏らさない

### 銀行口座クラスのさらなる発展

BankAccountV3の設計を基に、本章ではさらに高度な設計パターンを適用した例を見てみましょう。これは、実際のエンタープライズアプリケーションで使用されるレベルの設計です。

<span class="listing-number">**サンプルコード4-11**</span>

```java
import java.time.LocalDateTime;

// BankAccountV3をさらに発展させた設計例
public class EnhancedBankAccount extends BankAccountV3 {
    // 追加のフィールド：口座の状態管理
    private String status;
    private LocalDateTime lastActivityDate;
    private int failedTransactionCount;
    
    // 口座状態を表す定数
    public static final String STATUS_ACTIVE = "ACTIVE";      // アクティブ
    public static final String STATUS_SUSPENDED = "SUSPENDED"; // 一時停止
    public static final String STATUS_CLOSED = "CLOSED";       // 閉鎖
    
    // 拡張されたコンストラクタ
    public EnhancedBankAccount(String accountNumber, String accountHolder, double initialBalance) {
        super(accountNumber, initialBalance);  // 親クラスのコンストラクタを呼び出し
        this.status = STATUS_ACTIVE;
        this.lastActivityDate = LocalDateTime.now();
        this.failedTransactionCount = 0;
    }
    
    // オーバーライドされた入金メソッド：状態チェックを追加
    @Override
    public void deposit(double amount) {
        validateAccountStatus();
        super.deposit(amount);
        updateLastActivity();
    }
    
    // オーバーライドされた出金メソッド：失敗回数の追跡
    @Override
    public boolean withdraw(double amount) {
        validateAccountStatus();
        boolean success = super.withdraw(amount);
        if (!success) {
            failedTransactionCount++;
            if (failedTransactionCount >= 3) {
                status = STATUS_SUSPENDED;
                System.out.println("連続した取引失敗により、口座が一時停止されました。");
            }
        } else {
            failedTransactionCount = 0;  // 成功したらリセット
        }
        updateLastActivity();
        return success;
    }
    
    // プライベートメソッド：内部ロジック
    private void validateAccountStatus() {
        if (!STATUS_ACTIVE.equals(status)) {
            throw new IllegalStateException("口座がアクティブではありません: " + status);
        }
    }
    
    private void updateLastActivity() {
        this.lastActivityDate = LocalDateTime.now();
    }
    
    // 口座の再アクティブ化
    public void reactivateAccount() {
        if (STATUS_SUSPENDED.equals(status)) {
            status = STATUS_ACTIVE;
            failedTransactionCount = 0;
            System.out.println("口座が再アクティブ化されました。");
        }
    }
}
```

この設計の要点。
- 継承の活用
-    + 第3章のBankAccountV3を基に機能を拡張
- 定数による状態管理
-    + 口座状態を文字列定数で管理
- 状態管理
-    + 失敗回数の追跡と自動停止機能
- テンプレートメソッドパターン
-    + 基本動作を保持しつつ拡張

### クラス設計のベストプラクティス

クラス設計は、ソフトウェアの品質向上に寄与する要素の1つです。オブジェクト指向設計の実践から生まれたベストプラクティスは、保守性、拡張性、信頼性の高いソフトウェアを構築するための指針を提供します。ただし、これらは唯一の解決策ではなく、バッチ処理やデータ変換処理では関数型プログラミングのイミュータブルなデータ構造、シンプルなスクリプトでは手続き型プログラミングの直接的な関数呼び出しが適している場合もあります。

もっとも基本的な原則は単一責任の原則です。1つのクラスは1つの明確な責任のみを持つべきであり、これにより変更の理由が限定され、クラスの理解と保守が容易になります。次に重要なのはデータの隠蔽で、クラスの内部状態をprivateで保護し、必要な操作のみをパブリックメソッドとして公開します。これにより、実装の詳細を変更してもクライアントコードに影響を与えません。

入力検証も欠かせない要素です。すべての外部入力は、setterメソッドやコンストラクタで厳密に検証し、不正なデータがオブジェクトの状態を破壊することを防ぎます。また、意味のある名前を使用することで、コードの可読性と保守性が向上します。クラス名は名詞、メソッド名は動詞で始まるという慣習に従い、その役割を明確に表現する名前を選びます。

最後に、不変条件の維持は、オブジェクトの整合性を保証するうえできわめて重要です。オブジェクトの生成から破棄まで、常に満たされるべき条件（不変条件）を明確に定義し、すべての操作でこれを維持するよう設計します。

これらの原則に従うことで、保守性が高く、バグの少ないJavaプログラムを作成できます。

## パッケージシステムとクラスの組織化

### パッケージの概念と必要性

Javaにおけるパッケージは、関連するクラスやインターフェイスをグループ化するための仕組みです。大規模なプロジェクトでは数百から数千のクラスが存在することがあり、フラットな構造ではクラス名の衝突や依存関係の把握が困難になります。パッケージは以下の重要な役割を果たします。

1. 名前空間の提供
    + 異なるパッケージに同じ名前のクラスを作成可能
2. アクセス制御
    + パッケージレベルでのアクセス制限を実現
3. 論理的な構造化
    + 機能や責任に基づいてクラスを整理
4. クラスの再利用
    + パッケージ単位での配布と利用

### パッケージの命名規則

Javaの言語仕様では、パッケージ名の衝突を避けるため、インターネットドメイン名を逆順にした命名規則が推奨されています。

<span class="listing-number">**サンプルコード4-12**</span>

```java
// ドメイン名: example.com
// パッケージ名: com.example.プロジェクト名.モジュール名
package com.example.myapp.service;

// ドメイン名: ac.jp（教育機関）
// パッケージ名: jp.ac.大学名.プロジェクト名
package jp.ac.university.research;
```

これはパッケージ名の命名規則の例です。

#### 命名規則のポイント
- すべて小文字を使用
- ドメイン名を逆順に記述
- 意味のある階層構造を作成
- Javaの予約語は使用しない

### パッケージとディレクトリ構造

パッケージ名はファイルシステムのディレクトリ構造と対応している必要があります。

```
src/
└── com/
    └── example/
        └── myapp/
            ├── model/
            │   ├── User.java
            │   └── Product.java
            ├── service/
            │   ├── UserService.java
            │   └── ProductService.java
            └── util/
                └── DateUtils.java
```

### import文の使い方

パッケージに含まれるクラスを使用する際は、完全限定名かimport文を使用します。

<span class="listing-number">**サンプルコード4-13**</span>

```java
// 完全限定名での使用
java.util.List<String> names = new java.util.ArrayList<>();

// import文を使用した場合
import java.util.List;
import java.util.ArrayList;

List<String> names = new ArrayList<>();
```



<!-- 
================
チャンク 4/10 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
