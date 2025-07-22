# <b>21章</b> <span>ユニットテストと品質保証</span> <small>信頼性の高いソフトウェアを作る</small>

## 本章の学習目標

### 前提知識

必須
- 第4章のクラスとインスタンス（メソッド、コンストラクタ）
- 第7章の抽象クラスとインターフェイス（依存関係の理解）
- 第14章の例外処理（try-catch、例外の種類）
- 基本的なJavaプログラミング経験

推奨
- 第16章のマルチスレッドプログラミング（テストの独立性）
- 第23章の外部ライブラリ活用（Maven、Gradle）
- デザインパターンの基礎知識（Strategy、Factoryパターン）
- ソフトウェア開発プロセスの基本理解

### 学習目標

#### 知識理解目標
- 単体テストの重要性と品質保証における役割
- テスト駆動開発（TDD）の思想と実践方法
- 疎結合設計とDependency Injection（DI）の概念
- JUnitとAssertJなどのテストフレームワークの理解

#### 技能習得目標
- JUnitを使った単体テストの作成と実行
- AAA（Arrange-Act-Assert）パターンによるテスト設計
- モックオブジェクトを活用したテストの分離
- テストしやすいコード設計の実装

#### 実践的な活用目標
- 継続的インテグレーション（CI）環境でのテスト自動化
- レガシーコードへのテスト追加とリファクタリング
- テストカバレッジの測定と品質向上
- BDD（行動駆動開発）によるテスト仕様の記述

#### 到達レベルの指標
- 品質の高いテストコードを継続的に作成できる
- テスト駆動開発の流れで機能開発ができる
- テストしやすい設計原則に基づいてアーキテクチャを構築できる
- チーム開発において品質保証のプロセスを主導できる

## はじめに

ソフトウェア開発において、品質の高いコードを維持することは極めて重要です。
本章では、単体テスト（ユニットテスト）を主題とし、なぜテストが重要なのか、
そして良いテストを書くために必要な「疎結合」という設計思想と、
それを実現する「Dependency Injection (DI)」について体系的に学習します。

## 単体テストの基礎

### 単体テストとは

単体テスト（ユニットテスト）とは、ソフトウェアを構成する最小単位（クラスやメソッドなど）が、それぞれ意図通りに正しく動作するかを検証するテストです。部品一つひとつの品質を保証する、品質管理の第一歩です。

### 品質の高いソフトウェアのための4つのメリット

単体テストは、単にバグを見つけるだけの作業ではありません。品質の高いソフトウェアを効率的に生み出すための、積極的な活動です。

#### バグの早期発見

開発の初期段階でバグを発見できれば、修正は比較的簡単です。単体テストは、コードを書いた直後に品質を検証することで、手戻りコストをリリース後の100分の1以下に抑えます。

<span class="listing-number">**サンプルコード21-1**</span>

```java
// バグ修正のコスト
// 開発段階でのバグ修正コスト： 1
// リリース後のバグ修正コスト： 100〜1000倍
```

#### 変更を恐れないための「安全網」

一度作成したコードの構造を、後からより良く改善する作業をリファクタリングと呼びます。単体テストという安全網があれば、「動いているコードを壊してしまう」という恐怖心なく、安心してリファクタリングに挑戦できます。

#### 動く「仕様書」

テストコードは「このメソッドは、こういう入力に対して、こう動作するとよい」という仕様をコードで表現したものです。常に最新の正しい仕様を示す「生きたドキュメント」として機能します。

#### 開発サイクルの高速化

手動での動作確認は、時間がかかり、ミスも発生しやすい作業です。自動化された単体テストは、何百もの検証を自動かつ一瞬で完了させ、開発のリズムを向上させます。

## テストの基本形（AAAパターン）

テストコードは、AAA（Arrange-Act-Assert）という3ステップのパターンで記述するのが基本です。これにより、テストの意図が明確になります。

- Arrange（準備）: テストに必要な条件を整える
- Act（実行）: テストしたいメソッドを呼び出す
- Assert（検証）: 実行結果が期待通りかを確認する

シンプルな`Calculator`クラスを例に、テストの基本形を見てみましょう。

### テスト対象のクラス

<span class="listing-number">**サンプルコード21-2**</span>

```java
// Calculator.java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```

### テストコードの実践

以下の例では、JUnitを使わずに手動でテストを実装して、AAAパターンの構造を明確に示します。各ステップが明確に分離され、テストの意図が一目で理解できるようになっています。

<span class="listing-number">**サンプルコード21-3**</span>

```java
// CalculatorManualTest.java
public class CalculatorManualTest {
    public static void main(String[] args) {
        // Arrange（準備）
        Calculator calculator = new Calculator();
        int inputA = 2;
        int inputB = 3;
        int expected = 5;

        // Act（実行）
        int actual = calculator.add(inputA, inputB);

        // Assert（検証）
        if (actual == expected) {
            System.out.println("✅ テスト成功！");
        } else {
            System.err.println("❌ テスト失敗！");
        }
    }
}
```

この`Calculator`のように、他に依存しない自己完結したクラスは非常にテストしやすいです。

## テストを妨げる「依存関係」

問題は、クラスが他のクラスに依存している場合です。特に、あるクラスが別の具体的なクラスを内部で直接生成している状態（密結合）は、テストを著しく困難にします。

### テストしにくいコードの例

以下の例では、密結合によってテストが困難になる典型的なケースを示します。UserServiceがUserRepositoryを内部で直接生成しているため、テスト時にデータベース接続が必要となってしまいます。

<span class="listing-number">**サンプルコード21-4**</span>

```java
// UserRepository.java - DBからユーザー情報を取得するクラス
public class UserRepository {
    public String findById(String id) {
        // データベースへの接続と検索処理が行われます
        System.out.println("...データベースに接続中...");
        return "Taro Yamada";
    }
}

// UserService.java - ビジネスロジックを担当するクラス
public class UserService {
    // 内部で直接UserRepositoryを生成しています（密結合）
    private final UserRepository userRepository = new UserRepository();

    public String getUserProfile(String id) {
        String name = userRepository.findById(id);
        return "【Profile】" + name;
    }
}
```

### なぜ密結合コードはテストが難しいのか？

この`UserService`を単体テストしようとすると、`new UserRepository()`によって、
必ずデータベース接続処理が実行されてしまいます。
これでは、`UserService`のロジック（文字列を連結する処理）だけを独立して検証できません。
テスト対象をその依存先から切り離せないことが、問題の根源です。

## 疎結合設計（Dependency Injection）

### 解決策としての「疎結合」

良い単体テストのためには、クラス同士の関係が緩やかである疎結合な設計が必要です。疎結合を実現する鍵は、具体的な実装クラスに依存するのではなく、その「役割」を定義したインターフェイスに依存することです。

### Dependency Injection (DI) とは

Dependency Injection (DI)は、疎結合を実現するための具体的な設計テクニックです。
その原則は、「あるオブジェクトが必要とする別のオブジェクト（依存オブジェクト）を、
内部で生成するのではなく、外部から与える（注入する）」というものです。

この考え方は、制御の反転 (Inversion of Control - IoC)という、より大きな設計原則にもとづいています。

### DIの主な種類

依存性を注入するには、主に3つの方法があります。

#### コンストラクタインジェクション（推奨）

コンストラクタの引数を通じて依存性を注入します。最も推奨される方法です。

以下の例では、UserServiceがUserRepositoryを外部から受け取るように変更しています。これにより、テスト時にはモックオブジェクトを、本番時には実際の実装を注入できるようになります。

<span class="listing-number">**サンプルコード21-5**</span>

```java
public class UserService {
    private final UserRepository userRepository;

    // コンストラクタで依存性を受け取ります
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

##### メリット
- `final`キーワードを付与でき、依存関係が不変になる
- オブジェクト生成時に、必要な依存性がすべて揃っていることが保証される

#### セッターインジェクション

セッターメソッドを通じて依存性を注入します。

セッターインジェクションは、オプショナルな依存性や、オブジェクト生成後に依存性を変更したい場合に便利ですが、必須の依存性にはありません。

<span class="listing-number">**サンプルコード21-6**</span>

```java
public class UserService {
    private UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

##### メリット
- 依存性の注入が任意（オプショナル）にできる
- 後から依存性を変更できる

##### デメリット
- 依存性が注入されないままメソッドが呼ばれる可能性があり、`NullPointerException`のリスクがある

#### フィールドインジェクション

クラスのフィールドに直接依存性を注入します。この方法は主にフレームワークが利用するもので、手動での利用は推奨されません。

## DIによるテスト容易性の向上

### 依存関係の差し替え

DIの核心は、依存関係を外部から制御できる点にあります。これにより、アプリケーションの実行時には本番用の実装を、単体テストの実行時にはテスト用の偽物（スタブ）を、同じクラスに注入できます。

#### DIを適用したサービスクラス

以下の例では、UserRepositoryをインターフェイスとして定義し、UserServiceが具体的な実装ではなく抽象に依存するように変更しています。これが疎結合設計の核心です。

<span class="listing-number">**サンプルコード21-7**</span>

```java
// UserRepository.java - インターフェイス
public interface UserRepository {
    String findById(String id);
}

// UserService.java - DIを適用
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUserProfile(String id) {
        String name = userRepository.findById(id);
        return "【Profile】" + name;
    }
}
```

### スタブを使った単体テストの実践

スタブ（偽物オブジェクト）を使用することで、データベースに接続することなくUserServiceのロジックをテストできます。以下の例では、テスト用の偽物リポジトリを作成し、それを使ってUserServiceをテストしています。

<span class="listing-number">**サンプルコード21-8**</span>

```java
// UserRepositoryStub.java - テスト用の偽物リポジトリ
public class UserRepositoryStub implements UserRepository {
    @Override
    public String findById(String id) {
        // DBには接続せず、テスト用の固定値を返します
        return "Test User";
    }
}

// UserServiceManualTest.java
public class UserServiceManualTest {
    public static void main(String[] args) {
        // Arrange（準備）
        UserRepository stub = new UserRepositoryStub();
        UserService userService = new UserService(stub);

        // Act（実行）
        String actual = userService.getUserProfile("123");

        // Assert（検証）
        String expected = "【Profile】Test User";
        if (expected.equals(actual)) {
            System.out.println("✅ テスト成功！");
        } else {
            System.err.println("❌ テスト失敗！");
        }
    }
}
```

`UserService`のコードを一切変更することなく、その振る舞いをDBから切り離してテストできました。

## テスト駆動開発（TDD）

### TDDの基本サイクル

テスト駆動開発（TDD）とは、プロダクトコードを実装する前に、まずテストコードを書く開発手法です。

TDDでは、以下の短いサイクルを繰り返します。

1. 🔴 Red: まず、これから実装する機能に対する失敗するテストコードを書く
2. 🟢 Green: そのテストを成功させるための最小限のコードを実装する
3. 🔵 Refactoring: テストが成功する状態を保ったまま、コードをより綺麗に、効率的に書き直す

### TDDの実践例

#### ステップ1: Red - 失敗するテストを書く

TDDの最初のステップでは、まだ存在しないクラスやメソッドに対してテストを書きます。このテストは当然失敗しますが、これが「何を実装すべきか」を明確にする仕様となります。

<span class="listing-number">**サンプルコード21-9**</span>

```java
public class StringCalculatorTest {
    @Test
    public void testAdd_EmptyString_ReturnsZero() {
        // まだ存在しないクラスとメソッドのテストを書く
        StringCalculator calc = new StringCalculator();
        assertEquals(0, calc.add(""));
    }
}
```

#### ステップ2: Green - テストを通す最小限のコード

次に、テストが通る最小限の実装を行います。この段階では、ハードコーディングでも構いません。重要なのは、テストを緑（成功）にすることです。

<span class="listing-number">**サンプルコード21-10**</span>

```java
public class StringCalculator {
    public int add(String numbers) {
        return 0; // 最小限の実装
    }
}
```

#### ステップ3: Refactoring - 必要に応じてコードを改善

この段階では特にリファクタリングは不要です。次のテストに進みます。

## JUnitによる効率的なテスト

### JUnitの基本的な使い方

実際の開発では、JUnitのようなテストフレームワークを使用します。

JUnit 5（Jupiter）を使用した基本的なテストの例を示します。@Testアノテーションでテストメソッドをマークし、@BeforeEachで各テスト前の初期化処理を定義し、Assertionsクラスのメソッドで検証を行います。

<span class="listing-number">**サンプルコード21-11**</span>

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    void testAdd() {
        assertEquals(5, calculator.add(2, 3));
    }
    
    @Test
    void testDivide() {
        assertEquals(2.0, calculator.divide(10, 5), 0.001);
    }
    
    @Test
    void testDivideByZero() {
        assertThrows(ArithmeticException.class, () -> {
            calculator.divide(10, 0);
        });
    }
}
```

### モックフレームワークの活用

Mockitoなどのモックフレームワークを使うと、より柔軟なテストが可能になります。

Mockitoを使用すると、モックオブジェクトの振る舞いを詳細に制御でき、
メソッド呼び出しの検証も可能になります。
以下の例では、@Mockアノテーションでモックを宣言し、when-thenReturnで振る舞いを定義し、
verifyでメソッド呼び出しを検証しています。

<span class="listing-number">**サンプルコード21-12**</span>

```java
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository mockRepository;
    
    @Test
    void testGetUserProfile() {
        // モックの振る舞いを定義
        when(mockRepository.findById("123"))
            .thenReturn("Test User");
        
        UserService service = new UserService(mockRepository);
        String profile = service.getUserProfile("123");
        
        assertEquals("【Profile】Test User", profile);
        verify(mockRepository).findById("123"); // 呼び出しを検証
    }
}
```

## テストのベストプラクティス

### 良いテストの特徴（FIRST原則）

- Fast（高速）: テストは素早く実行される
- Independent（独立）: 各テストは他のテストに依存しない
- Repeatable（再現可能）: どの環境でも同じ結果が得られる
- Self-Validating（自己検証）: 成功/失敗が自動的に判定される
- Timely（適時）: プロダクトコードと同時、または先に書かれる

### テストの構造化

JUnit 5の@Nestedアノテーションを使用すると、テストを論理的にグループ化できます。
@DisplayNameを使用することで、テストレポートに読みやすい説明を表示できます。
以下の例では、正常系と異常系のテストを明確に分離しています。

<span class="listing-number">**サンプルコード21-13**</span>

```java
public class PaymentServiceTest {
    
    @Nested
    @DisplayName("正常系のテスト")
    class SuccessfulPayments {
        @Test
        @DisplayName("クレジットカードでの支払いが成功する")
        void testCreditCardPayment() {
            // テスト実装
        }
    }
    
    @Nested
    @DisplayName("異常系のテスト")
    class FailedPayments {
        @Test
        @DisplayName("残高不足の場合は例外が発生する")
        void testInsufficientBalance() {
            // テスト実装
        }
    }
}
```

## 継続的インテグレーション（CI）との統合

### 自動テストの重要性

継続的インテグレーション（CI）では、コードがリポジトリにプッシュされるたびに自動的にテストが実行されます。

```yaml
# GitHub Actions の例
name: Java CI

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
    - name: Run tests
      run: ./gradlew test
```

### テストカバレッジ

テストカバレッジは、コードのどの部分がテストされているかを示す指標です。

```gradle
// build.gradle
plugins {
    id 'jacoco'
}

jacocoTestReport {
    reports {
        xml.enabled true
        html.enabled true
    }
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.80 // 80%以上のカバレッジを要求
            }
        }
    }
}
```

## まとめ

本章では、単体テストの重要性から始まり、テストしやすいコードの設計、Dependency Injection、テスト駆動開発、そして実践的なテストフレームワークの使い方まで学習しました。

### 重要なポイント

1. 単体テストは品質保証の基盤： バグの早期発見、安全なリファクタリング、生きたドキュメント
2. 疎結合設計の重要性： テストしやすいコードは良い設計のコード
3. DIによる依存関係の管理： 外部から依存を注入することで柔軟性を確保
4. TDDのサイクル： Red-Green-Refactoringによる着実な開発
5. ツールの活用： JUnit、Mockitoなどでテストコードの記述量を削減し、モックを使った独立性の高いテストを作成

品質の高いソフトウェアを開発するためには、テストは必要です。本章で学んだ知識を活用して、堅牢で保守しやすいコードを書く習慣を身につけてください。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter21/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

1. 基礎課題： JUnitの基本的な使い方とテストケース作成
2. 発展課題： TDDサイクルを使った機能実装
3. チャレンジ課題： モックフレームワークを使った高度なテスト

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

次のステップ： 基礎課題が完了したら、第23章「ドキュメントと外部ライブラリ」に進みましょう。

## よくあるエラーと対処法

単体テスト初学者がよく遭遇する基本的なエラーとその対処法を説明します。

### 1. テストケースが不十分

##### 問題
テストケースが期待値のみをチェックし、境界値や異常値をテストしていない。

##### 解決策
```java
@Test
void testAdd() {
    Calculator calc = new Calculator();
    
    // 正常値のテスト
    assertEquals(5, calc.add(2, 3));
    
    // 境界値のテスト
    assertEquals(0, calc.add(0, 0));
    
    // 負数のテスト
    assertEquals(-1, calc.add(-3, 2));
}
```

### 2. アサーション使用ミス

##### 問題
浮動小数点の厳密な比較によるテスト失敗。

##### 解決策
```java
@Test
void testDoubleCalculation() {
    double result = 0.1 + 0.2;
    assertEquals(0.3, result, 0.0001); // 許容誤差を指定
}
```

### 3. モックの設定不足

##### 問題
モックオブジェクトの振る舞いが正しく設定されていない。

##### 解決策
```java
@Test
void testUserService() {
    UserRepository mockRepo = mock(UserRepository.class);
    
    // モックの振る舞いを設定
    when(mockRepo.findById(1L)).thenReturn(new User(1L, "Test User"));
    
    UserService service = new UserService(mockRepo);
    User user = service.findById(1L);
    assertNotNull(user);
}
```

### 4. テスト間の依存関係

##### 問題
テストの実行順序に依存したテストケース。

##### 解決策
```java
@Test
void testIncrement() {
    int counter = 0;  // 各テストで初期化
    counter++;
    assertEquals(1, counter);
}
```
