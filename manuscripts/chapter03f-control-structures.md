## 第3章 オブジェクト指向の考え方 - Part F: 制御構造

### 3.3 制御構造

プログラミングにおいて、最も重要で最もバグを出しやすい部分です。

#### if文による条件分岐

##### if – 基本的な書き方

括弧内の条件（*boolean型*）に合致する（値が`true`）の場合に、波括弧`{ }`内のブロックの処理を行います。

```java
if ( 条件 ) {
    ここは条件に合致した場合にのみ実行される
}
```

波括弧のブロックは、実行する処理が1行の場合のみ省略が可能ですが、波括弧は省略しない方が良いです。

もし、条件に合致した場合実行する処理が1行だけのとき、次の書き方のように改行を挟まず1行で書くようにすると分かりやすいです。

```java
if ( 条件 ) ここは条件に合致した場合にのみ実行される
```

波括弧を省略してほしくない理由は、次のようなパターンが考えられるからです。

```java
if ( 条件 )
    最初にif文を書いた人が書いた条件内のコード
```

これは正常に動きます。  
次に、このコードを改修する際、以下のようなコードの修正を行ってしまいやすい点が波括弧を省略した際に起こりやすいです。

```java
if ( 条件 )
    最初にif文を書いた人が書いた条件内のコード
    追加された条件内コードのつもりで書かれたコード
```

このとき、追加されたコードは、ifの条件にまったく関係なく処理が実行されてしまい、これが意図しない処理としてバグにつながります。
すぐに気付けるようだったら良いのですが、追加されたコードの影響が、かなり後の方（たとえばリリース後など）で気付く状況も少なからずあります。
そもそも波括弧を省略していなければ防げているバグを発生させないためにも、必ず波括弧を書きましょう。

少し逸れますがバグの混入を防ぐ話として、インデントをそろえることも重要です。  
インデントはタブでもスペースでもかまいませんが、コードブロックが分かりやすくなるように適切な字下げをしましょう。  
また、インデントに関していえば、IDEに搭載されているコードフォーマッタを使うのもよいです。自動的にコードの整形を行ってくれるため、先ほどの追加コードのような不具合もコードの整形によって気付きやすくなります。

##### if-else – どちらかを実行

ifの条件に合致しなかった場合のみ実行される処理を書ける

```java
if ( 条件 ) {
    ここは条件に合致した場合にのみ実行される
} else {
    条件に合致しなかった場合にのみ実行される
}
```

##### if-elseif-else – 複数の条件

条件に合致しなかった場合、再度評価を行うこともできる

```java
if ( 条件1 ) {
    条件1に合致した場合にのみ実行される
} else if ( 条件2 ) {
    条件1に合致せず、条件2に合致した場合
} else {
    上記すべての条件に合致しなかった場合
}
```

##### 比較演算子と論理演算子を組み合わせた複数条件指定

if文の条件は、条件1かつ条件2と言ったように、1つのif文で複数の条件を入れることが可能です。

###### AND（〜かつ〜）

```java
if ( 条件１ && 条件２ ) {
    条件１と条件２どちらにも合致した場合にのみ実行される
}
```

###### OR（〜または〜）

```java
if ( 条件１ || 条件２ ) {
    条件１か条件２のどちらかに合致した場合にのみ実行される
}
```

##### 条件分岐の実践例

論理演算子を使った複雑な条件分岐は、実際のビジネスロジックを実装する際に頻繁に使用されます。以下のプログラムは、会員システムの割り引き判定という実用的な例を通じて、論理演算子の効果的な使用方法を学習するための材料です：

ファイル名： MembershipDiscount.java

```java
public class MembershipDiscount {
    public static void main(String[] args) {
        // 顧客情報の設定
        int age = 25;
        boolean isPremiumMember = true;
        int purchaseAmount = 5000;
        int membershipYears = 3;
        
        System.out.println("=== 顧客情報 ===");
        System.out.println("年齢: " + age + "歳");
        System.out.println("プレミアム会員: " + (isPremiumMember ? "はい" : "いいえ"));
        System.out.println("購入金額: " + purchaseAmount + "円");
        System.out.println("会員歴: " + membershipYears + "年");
        System.out.println();
        
        // 複数の割引条件を論理演算子で組み合わせた判定
        System.out.println("=== 割引判定結果 ===");
        
        // 条件1: シニア割引（65歳以上）
        if (age >= 65) {
            System.out.println("シニア割引が適用されます（20%オフ）");
        }
        
        // 条件2: プレミアム会員かつ高額購入
        if (isPremiumMember && purchaseAmount >= 3000) {
            System.out.println("プレミアム会員高額購入割引が適用されます（15%オフ）");
        }
        
        // 条件3: 若年層または長期会員
        if (age <= 25 || membershipYears >= 5) {
            System.out.println("若年層・長期会員割引が適用されます（10%オフ）");
        }
        
        // 条件4: 複雑な組み合わせ条件
        if ((isPremiumMember && purchaseAmount >= 5000) || 
            (age >= 60 && membershipYears >= 3)) {
            System.out.println("特別VIP割引が適用されます（25%オフ）");
        }
        
        // 条件5: 除外条件を含む複雑な判定
        if (purchaseAmount >= 10000 && age >= 20 && age <= 60 && !isPremiumMember) {
            System.out.println("一般会員高額購入割引が適用されます（5%オフ）");
        }
    }
}
```

**このプログラムから学ぶ重要な概念：**

1. **論理AND演算子（&&）の実用性**：「プレミアム会員かつ高額購入」のように、複数の条件を同時に満たす場合の判定に使用します。ビジネスルールでは「すべての条件を満たす」ケースが頻繁にあります。

2. **論理OR演算子（||）の柔軟性**：「若年層または長期会員」のように、いずれかの条件を満たせば良い場合に使用します。顧客の多様なニーズに対応するための重要なしくみです。

3. **複雑な条件の構築**：括弧を使用することで、より複雑なビジネスロジックを表現できます。例：「（プレミアム会員で高額購入）または（シニアで長期会員）」

4. **論理NOT演算子（!）の活用**：`!isPremiumMember`のように、特定の条件を除外する際に使用します。「プレミアム会員ではない一般会員に対する特別オファー」などの実装に有用です。

5. **短絡評価（Short-circuit evaluation）の重要性**：`&&`演算子では、左の条件がfalseの場合、右の条件は評価されません。`||`演算子では、左の条件がtrueの場合、右の条件は評価されません。これにより、効率的で安全な条件判定が可能になります。

**実用的な応用場面：**

- **ECサイトの価格計算システム**：顧客属性にもとづいた動的割り引き計算
- **アクセス制御システム**：ユーザーの権限や属性にもとづいた機能制限
- **ゲームの進行条件**：プレイヤーのレベル、アイテム所持状況にもとづいた分岐
- **在庫管理システム**：商品の種類、在庫量、季節性を考慮した発注判定

**論理演算子使用時の注意点：**

1. **可読性の確保**：複雑な条件は適切に括弧でグループ化し、意図を明確にする
2. **ド・モルガンの法則**：`!(A && B)` = `!A || !B`、`!(A || B)` = `!A && !B`の理解
3. **null安全性**：オブジェクトの参照がnullでないことを最初に確認する習慣

##### 三項演算子

三項演算子は、`if...else...`を式として扱えます。

```java
System.out.println(条件 ? "true" : "false");
//条件に合致していればtrueと表示されます。合致していなければfalseと表示されます。

//↑の処理と同じように書く場合↓
if ( 条件 ) {
    System.out.println("true");
} else {
    System.out.println("false");
}
```

#### switch文による複数の選択肢の比較

「`==`」を条件としたif文を複数書くような場合は、switch文を使って記述できます。

switchの括弧内の変数に入っている値がcaseの値に該当した時、処理を実行します。

```java
switch ( 検査対象の値 ) {
    case 値1:
        検査対象の値が値１だった場合の処理
        break;
    case 値2:
        検査対象の値が値２だった場合の処理
        break;
    default:
        いずれのcaseに合致しない場合
        break;
}
```

breakが書かれていない場合は、その下にあるcaseの処理も実行されます。  
これは多くのプログラマにとって期待していない処理ですので、必ずcaseにはbreakを入れましょう。

##### 拡張switch文（switch式）：モダンJavaの新機能

Java 14以降、switch文が大幅に強化され、より安全で簡潔な記述が可能になりました。

```java
// 従来のswitch文
String dayType;
switch (day) {
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
        dayType = "平日";
        break;
    case 6:
    case 7:
        dayType = "休日";
        break;
    default:
        dayType = "無効な曜日";
}

// 新しいswitch式（Java 14以降）
int day = 3;
String dayType = switch (day) {
    case 1, 2, 3, 4, 5 -> "平日";
    case 6, 7 -> "休日";
    default -> "無効な曜日";
};
```

**switch式のポイント:**

*   **`->`（アロー）ラベル:** `case`の後にコロン`:`の代わりにアロー`->`を使います。
*   **`break`不要:** アローの右側が単一の式かブロックの場合、`break`は不要です。これにより、`break`の書き忘れによるバグ（フォールスルー）を完全に防ぐことができます。
*   **値を返す:** `switch`式全体が値を返すため、結果を直接変数に代入できます。
*   **網羅性のチェック:** `default`節がない場合など、すべての可能性を網羅していないとコンパイルエラーになります（`enum`型を`switch`する場合など）。これにより、条件漏れを防ぐことができます。
*   **複数行の処理と`yield`**: `case`の処理が複数行にわたる場合は、ブロック`{}`で囲み、`yield`キーワードを使って値を返します。

```java
// yieldを使った例
String message = switch (day) {
    case 1, 2, 3, 4, 5 -> {
        System.out.println("今日は頑張る日！");
        yield "平日"; // ブロックから値を返す
    }
    case 6, 7 -> "休日";
    default -> "無効な曜日";
};
```

**パターンマッチング for switch (Java 17以降):**
`switch`で変数の型をチェックし、そのままその型の変数として利用できます。

```java
Object obj = "Hello";
String formatted = switch (obj) {
    case Integer i -> String.format("Integer: %d", i);
    case String s -> String.format("String: %s", s);
    case null -> "It's null"; // nullもcaseとして扱える
    default -> "Unknown type";
};
System.out.println(formatted); // "String: Hello"
```
この機能により、冗長な`if-else`の`instanceof`チェインを、安全で読みやすい`switch`式に置き換えることができます。

##### switch文の実践例

switch文は、特定の値にもとづいた分岐処理に優れており、特に列挙型のような限定された値セットを扱う場合に威力を発揮します。以下のプログラムは、成績管理システムという実用的な例を通じて、switch文の効果的な使用方法を学習するための材料です：

ファイル名： GradeCalculator.java

```java
public class GradeCalculator {
    public static void main(String[] args) {
        // テストケースとして複数の成績を評価
        char[] grades = {'A', 'B', 'C', 'D', 'F', 'B'};
        int totalGradePoints = 0;
        int courseCount = grades.length;
        
        System.out.println("=== 成績評価システム ===");
        System.out.println("履修科目数: " + courseCount + "科目");
        System.out.println();
        
        for (int i = 0; i < grades.length; i++) {
            char grade = grades[i];
            int gradePoint;
            String evaluation;
            
            // switch文による成績の詳細評価
            switch (grade) {
                case 'A':
                    gradePoint = 4;
                    evaluation = "優秀（Excellent）";
                    break;
                case 'B':
                    gradePoint = 3;
                    evaluation = "良好（Good）";
                    break;
                case 'C':
                    gradePoint = 2;
                    evaluation = "普通（Average）";
                    break;
                case 'D':
                    gradePoint = 1;
                    evaluation = "要努力（Below Average）";
                    break;
                case 'F':
                    gradePoint = 0;
                    evaluation = "不合格（Fail）";
                    break;
                default:
                    gradePoint = 0;
                    evaluation = "無効な成績";
                    System.out.println("警告: 無効な成績 '" + grade + "' が検出されました");
                    break;
            }
            
            totalGradePoints += gradePoint;
            System.out.println("科目" + (i + 1) + ": " + grade + "(" + gradePoint + "点) - " + evaluation);
        }
        
        System.out.println();
        
        // GPA計算と総合評価
        double gpa = (double) totalGradePoints / courseCount;
        System.out.println("総ポイント: " + totalGradePoints + "点");
        System.out.printf("GPA: %.2f\n", gpa);
        
        // GPAにもとづく総合評価（switch文の応用）
        int gpaCategory = (int) gpa; // 小数点以下切り捨て
        
        System.out.print("総合評価: ");
        switch (gpaCategory) {
            case 4:
                System.out.println("最優秀（Summa Cum Laude）- 学長表彰対象");
                break;
            case 3:
                System.out.println("優秀（Magna Cum Laude）- 学部長表彰対象");
                break;
            case 2:
                System.out.println("良好（Cum Laude）- 追加指導不要");
                break;
            case 1:
                System.out.println("要注意（Academic Warning）- 学習指導が必要");
                break;
            case 0:
                System.out.println("要改善（Academic Probation）- 緊急学習支援が必要");
                break;
            default:
                System.out.println("計算エラー");
                break;
        }
        
        // 複数caseラベルの活用例（奨学金適用条件）
        System.out.print("奨学金適用: ");
        switch (gpaCategory) {
            case 4:
            case 3:
                System.out.println("特待生奨学金適用対象");
                break;
            case 2:
                System.out.println("一般奨学金適用対象");
                break;
            default:
                System.out.println("奨学金適用対象外");
                break;
        }
    }
}
```

**このプログラムから学ぶ重要な概念：**

1. **switch文の適用場面**：文字や整数などの離散的な値による分岐において、if-else文よりも読みやすく、効率的な処理が可能です。特に「成績」「曜日」「月」「状態」などの列挙可能な値に最適です。

2. **break文の重要性**：各caseの最後にbreak文を配置することで、意図しないfall-through動作を防ぎます。break文を忘れると、次のcaseも実行されてしまう重大なバグの原因となります。

3. **default句の活用**：予期しない値に対する安全な処理を提供します。入力検証やエラーハンドリングの重要な要素として機能します。

4. **複数caseラベルの実用性**：同じ処理を複数の値で実行したい場合、case文を連続して記述することで、コードの重複を避けられます。

5. **型安全性とパフォーマンス**：switch文は、コンパイル時に値のチェックが行われ、実行時は効率的なジャンプテーブルとして最適化される場合があります。

**実用的な応用場面：**

- **状態管理システム**：注文状態（待機中、処理中、発送済み等）の処理分岐
- **ゲーム開発**：プレイヤーの行動（移動、攻撃、防御等）による処理分岐
- **ユーザーインターフェイス**：メニュー選択やボタンクリックにもとづいた機能実行
- **データ変換処理**：ファイル形式やデータ型による変換処理の分岐

**switch文使用時のベストプラクティス：**

1. **必ずdefault句を含める**：予期しない値に対する適切な処理を提供
2. **各caseにbreak文を配置**：意図しないfall-throughを防ぐ
3. **複雑な処理はメソッドに分割**：各caseの処理が長くなる場合は、別メソッドに委譲
4. **列挙型（enum）との組み合わせ**：型安全性をさらに向上させるため、可能な限りenum型を使用

#### String型の内容評価について

JavaのString型は、プログラミング初心者が遇遇する最も細かい罠の1つであり、同時にJavaのメモリ管理とオブジェクト指向の本質を理解するための絶好の教材でもあります。以下のプログラムは、String型の特別な性質と、参照比較と値比較の違いを具体的に示しています。

このサンプルを通じて、Javaの文字列プール機能、オブジェクトの同一性、メモリ管理のしくみを理解しましょう：

```java
public class StringEval {
  public static void main(String[] args) {

    String a = "Hello";
    String b = "Hello";

    if (a == b) {
      System.out.println("同じだよ！");  // こっち
    } else {
      System.out.println("違うよ！");
    }

    // a,b両方に処理を加えて値を変化させる
    a += 1;
    b += 1;
    System.out.println("a:" + System.identityHashCode(a));
    System.out.println("b:" + System.identityHashCode(b));

    if (a == b) {
      System.out.println("同じだよ！");
    } else {
      System.out.println("違うよ！");  // こっち
    }

    if (a.equals(b)) {
      System.out.println("equalsなら同じだよ！");  // こっち
    } else {
      System.out.println("equalsでも違うよ！");
    }
  }
}
```

**このプログラムが示す重要な概念：**

1. **文字列プール（String Pool）の存在**：最初の`a == b`がtrueになるのは、Javaが同じ文字列リテラル"Hello"を自動的に共有するためです。これは文字列プールというしくみにより、メモリ効率を向上させています。

2. **参照の同一性 vs 値の同等性**：
   - `==`演算子：2つの変数が同じオブジェクトを参照しているかをチェック（参照の同一性）
   - `equals()`メソッド：2つのオブジェクトの内容が同じかをチェック（値の同等性）

3. **Stringの不変性（Immutability）**：`a += 1`のような操作は、既存の文字列を変更するのではなく、新しい文字列オブジェクトを生成します。これがSystem.identityHashCode()で異なる値を示す理由です。

4. **メモリ効率の考慮**：文字列プールの存在により、同じ内容の文字列リテラルを複数ヵ所で使用してもメモリを節約できます。ただし、動的に生成された文字列（演算結果など）は自動的にプールには入りません。

5. **プログラミングのベストプラクティス**：文字列の比較には常に`equals()`メソッドを使用すべきです。`==`での比較は、意図しない結果を引き起こす可能性があります。

**実用的な注意点：**

```java
// 良い例
if (userInput.equals("yes")) {
    // 処理
}

// 悪い例（バグの原因）
if (userInput == "yes") {
    // userInputが動的に生成された場合、falseになる可能性大
}

// null安全な比較
if ("yes".equals(userInput)) {
    // userInputがnullでもNullPointerExceptionを回避
}
```

このような文字列の特性を理解することは、Javaプログラマとして必須の知識であり、多くのバグを未然に防ぐことができます。

#### 単純な繰り返し

##### while

while文の括弧内で指定された条件に、合致している間は処理を繰り返します。

```java
while ( 条件 ) {
    条件が真値(true)の間、繰り返す処理
}
```

###### 10回繰り返す

while文では、繰り返しの条件となるものが必要となります。

```java
int count = 1;
while ( count <= 10 ) {
    繰り返す処理をここに書きます。
    count += 1;
}
```

##### do…while

while文なんだけど、条件の検査が波括弧を閉じるタイミングで行われるため、波括弧内の処理は、条件にかかわらず一度は実行され繰り返し処理です。

```java
do {
    処理を実行後、条件が真値の場合には何度も繰り返す
} while ( 条件 ); //セミコロンを忘れずに付けましょう！
```

存在を忘れがちですので、覚えておきましょう。

###### 10回繰り返す

```java
int count = 1;
do {
    繰り返す処理 (countの初期値が11だった場合でも処理は実行されます)
    count += 1;
} while ( count <= 10 );
```

##### for

whileループの繰り返し条件用の数値型変数を簡略化して書けるループ処理です。

初期化された変数が、波括弧内の処理を繰り返す都度、指定された変化ルールに従って変化します。  
変化した値が繰り返し条件に合致しなくなった場合、繰り返しから抜けられます。

```java
for (変数の初期化; 繰り返し条件; 繰り返し時の変化) {
    繰り返す処理
}
```

###### 10回繰り返す

```java
for (int count = 1; count <= 10; count++) {
    繰り返す処理
}
```

#### 繰り返し処理中に例外的な処理を行う

##### 繰り返し中に別の条件でループを抜けたい - break

ループ処理に限らず、ブロックから抜け出すことができる命令としてbreakがあります。

for文を例にした場合：

```java
for (変数の初期化; 繰り返し条件; 繰り返し時の変化) {
    繰り返す処理
    if ( 別の条件 ) break;
}
```

##### 一度だけ処理を飛ばして、次の繰り返しを行う - continue

ループ処理中に、continueが実行されると、それ以降の処理は1回の繰り返し処理時のみ飛ばして次の繰り返し処理が実行されます。

```java
for (変数の初期化; 繰り返し条件; 繰り返し時の変化) {
    繰り返す処理
    if ( 別の条件 ) continue;
    別の条件に合致する場合、実行されない処理
}
```

---

次のパート：[Part G - 配列と文字列](chapter03g-arrays-strings.md)
