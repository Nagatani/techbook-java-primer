# 第21章 ユニットテストと品質保証

## 本章の学習目標

### 前提知識

**必須前提**：
- 第4章のクラスとインスタンス（メソッド、コンストラクタ）
- 第7章の抽象クラスとインターフェイス（依存関係の理解）
- 第14章の例外処理（try-catch、例外の種類）
- 基本的なJavaプログラミング経験

**望ましい前提**：
- 第16章のマルチスレッドプログラミング（テストの独立性）
- 第22章の外部ライブラリ活用（Maven、Gradle）
- デザインパターンの基礎知識（Strategy、Factory パターン）
- ソフトウェア開発プロセスの基本理解

### 学習目標

**知識理解目標**：
- 単体テストの重要性と品質保証における役割
- テスト駆動開発（TDD）の思想と実践方法
- 疎結合設計とDependency Injection（DI）の概念
- JUnitとAssertJなどのテストフレームワークの理解

**技能習得目標**：
- JUnitを使った単体テストの作成と実行
- AAA（Arrange-Act-Assert）パターンによるテスト設計
- モックオブジェクトを活用したテストの分離
- テストしやすいコード設計の実装

**実践的な活用目標**：
- 継続的インテグレーション（CI）環境でのテスト自動化
- レガシーコードへのテスト追加とリファクタリング
- テストカバレッジの測定と品質向上
- BDD（行動駆動開発）によるテスト仕様の記述

**到達レベルの指標**：
- 品質の高いテストコードを継続的に作成できる
- テスト駆動開発の流れで機能開発ができる
- テストしやすい設計原則に基づいてアーキテクチャを構築できる
- チーム開発において品質保証のプロセスを主導できる

## 21.1 はじめに

ソフトウェア開発において、品質の高いコードを維持することは極めて重要です。本章では、単体テスト（ユニットテスト）を主題とし、なぜテストが重要なのか、そして良いテストを書くために不可欠な「疎結合」という設計思想と、それを実現する「Dependency Injection (DI)」について体系的に学習します。

## 21.2 単体テストの基礎

### 21.2.1 単体テストとは

単体テスト（ユニットテスト）とは、ソフトウェアを構成する**最小単位**（クラスやメソッドなど）が、それぞれ意図通りに正しく動作するかを検証するテストです。部品一つひとつの品質を保証する、品質管理の第一歩です。

### 21.2.2 品質の高いソフトウェアのための4つのメリット

単体テストは、単にバグを見つけるだけの作業ではありません。品質の高いソフトウェアを効率的に生み出すための、積極的な活動です。

#### 1. バグの早期発見

開発の初期段階でバグを発見できれば、修正は比較的簡単です。単体テストは、**コードを書いた直後に品質を検証する**ことで、手戻りのコストを大幅に削減します。

<span class="listing-number">**サンプルコード21-1**</span>
```java
// バグ修正のコスト
// 開発段階でのバグ修正コスト： 1
// リリース後のバグ修正コスト： 100〜1000倍
```

#### 2. 変更を恐れないための「安全網」

一度作成したコードの構造を、後からより良く改善する作業を**リファクタリング**と呼びます。単体テストという安全網があれば、「動いているコードを壊すかもしれない」という恐怖心なく、安心してリファクタリングに挑戦できます。

#### 3. 動く「仕様書」

テストコードは「このメソッドは、こういう入力に対して、こう動作するべきだ」という仕様をコードで表現したものです。**常に最新の正しい仕様を示す「生きたドキュメント」**として機能します。

#### 4. 開発サイクルの高速化

手動での動作確認は、時間がかかり、ミスも発生しやすい作業です。自動化された単体テストは、何百もの検証を自動かつ一瞬で完了させ、開発のリズムを向上させます。

## 21.3 テストの基本形（AAAパターン）

テストコードは、**AAA（Arrange-Act-Assert）**という3ステップのパターンで記述するのが基本です。これにより、テストの意図が明確になります。

- **Arrange（準備）**: テストに必要な条件を整えます
- **Act（実行）**: テストしたいメソッドを呼び出します
- **Assert（検証）**: 実行結果が期待通りかを確認します

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

## 21.4 テストを妨げる「依存関係」

問題は、クラスが他のクラスに**依存**している場合です。特に、あるクラスが別の**具体的なクラス**を内部で直接生成している状態（**密結合**）は、テストを著しく困難にします。

### テストしにくいコードの例

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

この`UserService`を単体テストしようとすると、`new UserRepository()`によって、必ずデータベース接続処理が実行されてしまいます。これでは、`UserService`のロジック（文字列を連結する処理）だけを独立して検証できません。**テスト対象をその依存先から切り離せない**ことが、問題の根源です。

## 21.5 疎結合設計（Dependency Injection）

### 21.5.1 解決策としての「疎結合」

良い単体テストのためには、クラス同士の関係が緩やかである**疎結合**な設計が不可欠です。疎結合を実現する鍵は、具体的な実装クラスに依存するのではなく、その**「役割」を定義したインターフェースに依存する**ことです。

### 21.5.2 Dependency Injection (DI) とは

**Dependency Injection (DI)**は、疎結合を実現するための具体的な設計テクニックです。その原則は、「**あるオブジェクトが必要とする別のオブジェクト（依存オブジェクト）を、内部で生成するのではなく、外部から与える（注入する）**」というものです。

この考え方は、**制御の反転 (Inversion of Control - IoC)**という、より大きな設計原則にもとづいています。

### 21.5.3 DIの主な種類

依存性を注入するには、主に3つの方法があります。

#### 1. コンストラクタインジェクション（推奨）

コンストラクタの引数を通じて依存性を注入します。**最も推奨される方法**です。

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

**メリット:**
- `final`キーワードを付与でき、依存関係が不変になります
- オブジェクト生成時に、必要な依存性がすべて揃っていることが保証されます

#### 2. セッターインジェクション

セッターメソッドを通じて依存性を注入します。

<span class="listing-number">**サンプルコード21-6**</span>
```java
public class UserService {
    private UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

**メリット:**
- 依存性の注入が任意（オプショナル）にできます
- 後から依存性を変更できます

**デメリット:**
- 依存性が注入されないままメソッドが呼ばれる可能性があり、`NullPointerException`のリスクがあります

#### 3. フィールドインジェクション

クラスのフィールドに直接依存性を注入します。この方法は主にフレームワークが利用するもので、手動での利用は推奨されません。

## 21.6 DIによるテスト容易性の向上

### 21.6.1 依存関係の差し替え

DIの核心は、依存関係を外部から制御できる点にあります。これにより、アプリケーションの実行時には**本番用の実装**を、単体テストの実行時には**テスト用の偽物（スタブ）**を、同じクラスに注入できます。

#### DIを適用したサービスクラス

<span class="listing-number">**サンプルコード21-7**</span>
```java
// UserRepository.java - インターフェース
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

### 21.6.2 スタブを使った単体テストの実践

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

`UserService`のコードを一切変更することなく、その振る舞いをDBから完全に独立させてテストできました。

## 21.7 テスト駆動開発（TDD）

### 21.7.1 TDDの基本サイクル

テスト駆動開発（TDD）とは、**プロダクトコードを実装する前に、まずテストコードを書く**開発手法です。

TDDでは、以下の短いサイクルを繰り返します：

1. **🔴 Red**: まず、これから実装する機能に対する**失敗するテストコード**を書きます
2. **🟢 Green**: そのテストを**成功させるための最小限のコード**を実装します
3. **🔵 Refactoring**: テストが成功する状態を保ったまま、**コードをより綺麗に、効率的に**書き直します

### 21.7.2 TDDの実践例

#### ステップ1: Red - 失敗するテストを書く

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

## 21.8 JUnitによる効率的なテスト

### 21.8.1 JUnitの基本的な使い方

実際の開発では、JUnitのようなテストフレームワークを使用します。

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

### 21.8.2 モックフレームワークの活用

Mockitoなどのモックフレームワークを使うと、より柔軟なテストが可能になります。

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

## 21.9 テストのベストプラクティス

### 21.9.1 良いテストの特徴（FIRST原則）

- **Fast（高速）**: テストは素早く実行される
- **Independent（独立）**: 各テストは他のテストに依存しない
- **Repeatable（再現可能）**: どの環境でも同じ結果が得られる
- **Self-Validating（自己検証）**: 成功/失敗が自動的に判定される
- **Timely（適時）**: プロダクトコードと同時、または先に書かれる

### 21.9.2 テストの構造化

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

## 21.10 継続的インテグレーション（CI）との統合

### 21.10.1 自動テストの重要性

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

### 21.10.2 テストカバレッジ

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

## 21.11 まとめ

本章では、単体テストの重要性から始まり、テストしやすいコードの設計、Dependency Injection、テスト駆動開発、そして実践的なテストフレームワークの使い方まで学習しました。

### 重要なポイント

1. **単体テストは品質保証の基盤**: バグの早期発見、安全なリファクタリング、生きたドキュメント
2. **疎結合設計の重要性**: テストしやすいコードは良い設計のコード
3. **DIによる依存関係の管理**: 外部から依存を注入することで柔軟性を確保
4. **TDDのサイクル**: Red-Green-Refactoringによる着実な開発
5. **ツールの活用**: JUnit、Mockitoなどで効率的なテスト作成

品質の高いソフトウェアを開発するためには、テストは不可欠です。本章で学んだ知識を活用して、堅牢で保守しやすいコードを書く習慣を身につけてください。

## 章末演習

本章で学んだユニットテストと品質保証を実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

### 第21章の課題構成

```
exercises/chapter21/
├── basic/              # 基礎課題（必須）
│   ├── README.md       # 詳細な課題説明
│   └── [関連ファイル名]
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- JUnitを使った効果的なユニットテストの作成
- テスト駆動開発（TDD）の実践
- モックオブジェクトを使った依存関係の分離

### 課題の概要

1. **基礎課題**: JUnitの基本的な使い方とテストケース作成
2. **発展課題**: TDDサイクルを使った機能実装
3. **チャレンジ課題**: モックフレームワークを使った高度なテスト

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

**次のステップ**: 基礎課題が完了したら、第22章「ドキュメントとライブラリ作成」に進みましょう。
