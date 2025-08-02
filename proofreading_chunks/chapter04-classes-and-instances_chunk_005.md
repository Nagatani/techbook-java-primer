<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 5/11
行範囲: 746 - 933
作成日時: 2025-08-02 21:08:55

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

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

これはimport文の使用例です。

import文の種類。

1. 単一型インポート。
<span class="listing-number">**サンプルコード4-24**</span>

```java
import java.util.Scanner;  // Scannerクラスのみインポート
```

これは単一型インポートの例です。

2. オンデマンドインポート。
<span class="listing-number">**サンプルコード4-25**</span>

```java
import java.util.*;  // java.utilパッケージのすべてのクラスをインポート
```

これはオンデマンドインポートの例です。

3. 静的インポート。
<span class="listing-number">**サンプルコード4-14**</span>

```java
import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

double circumference = 2 * PI * radius;  // Math.PI と書く必要がない
double result = sqrt(16);                 // Math.sqrt と書く必要がない
```

これはstatic importの使用例です。

### import文の注意点

異なるパッケージに同名のクラスが存在する場合、明示的な指定が必要です。

<span class="listing-number">**サンプルコード4-15**</span>

```java
import java.util.*;
import java.awt.*;

public class Example {
    // コンパイルエラー: List はどちらのパッケージか不明
    // List myList;  
    
    // 解決策1: 完全限定名を使用
    java.util.List<String> utilList;
    java.awt.List awtList;
    
    // 解決策2: 片方のみimport
    // import java.util.List;
    // List<String> utilList;  // java.util.List
    // java.awt.List awtList;  // 完全限定名
}
```

これは同名クラスの競合解決の例です。

### パッケージ構成のベストプラクティス

効果的なパッケージ構成は、プロジェクトの保守性と拡張性を大きく向上させます。

<span class="listing-number">**サンプルコード4-16**</span>

```java
com.example.myapp/
├── model/          // ドメインモデル（エンティティ）
├── service/        // ビジネスロジック（処理の中核となる業務ルール）
├── repository/     // データアクセス層
├── controller/     // UIコントローラ
├── util/          // ユーティリティクラス
└── exception/     // カスタム例外
```

これはパッケージ構造の例です。



<!-- 
================
チャンク 5/11 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
