# 第20章 ユニットテストと品質保証

## 章末演習

本章で学んだユニットテストと品質保証の概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
ユニットテストとテスト駆動開発の実践技術を習得します。

### 技術的背景：ソフトウェアテストの重要性と現代的アプローチ

**なぜテストが必要なのか：**

ソフトウェア開発において、テストは単なる「バグ探し」以上の重要な役割を担っています：

1. **早期バグ発見によるコスト削減**
   - 開発段階でのバグ修正コスト: 1
   - リリース後のバグ修正コスト: 100〜1000倍
   - 有名な事例: アリアン5ロケットの爆発（整数オーバーフロー）

2. **リファクタリングの安全網**
   - 既存機能を壊していないことの保証
   - 設計改善への心理的障壁の除去
   - 継続的な品質向上の実現

3. **生きたドキュメントとしての価値**
   - テストコードが仕様書の役割
   - 新メンバーの学習材料
   - APIの使用例の提供

**テスト駆動開発（TDD）の実践：**

```java
// Red: 失敗するテストを書く
@Test
void testCalculateTax() {
    TaxCalculator calc = new TaxCalculator();
    assertEquals(1100, calc.calculateWithTax(1000));
}

// Green: テストを通す最小限のコード
public int calculateWithTax(int price) {
    return (int)(price * 1.1);
}

// Refactor: コードを改善
public int calculateWithTax(int price) {
    return applyTaxRate(price, getTaxRate());
}
```

**現代的なテスティング戦略：**

1. **テストピラミッド**
   ```
   ／＼     E2Eテスト（少量）
  ／  ＼    統合テスト（中量）
 ／____＼   ユニットテスト（大量）
   ```

2. **継続的インテグレーション（CI）**
   - コミット毎の自動テスト実行
   - 品質ゲートの設定
   - テストカバレッジの可視化

3. **モックとスタブの使い分け**
   - モック: 振る舞いの検証
   - スタブ: 固定値の返却
   - スパイ: 実装の一部を置き換え

**実務でのテスト設計パターン：**

```java
// Given-When-Then パターン
@Test
void testBankTransfer() {
    // Given: 事前条件の設定
    Account sender = new Account(1000);
    Account receiver = new Account(0);
    
    // When: 実行
    transferService.transfer(sender, receiver, 300);
    
    // Then: 結果の検証
    assertEquals(700, sender.getBalance());
    assertEquals(300, receiver.getBalance());
}
```

この演習では、これらの実践的なテスト技術を身につけます。

### 課題の場所
演習課題は `exercises/chapter20/` ディレクトリに用意されています：

```
exercises/chapter20/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── JUnitBasics.java
│   └── TestDrivenCalculator.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```


## 本章の学習目標

### 前提知識

本章を学習するための必須前提として、第19章までに習得した総合的なJava開発能力が必要です。これには、オブジェクト指向の基本から、コレクションフレームワーク、例外処理、ファイルI/O、マルチスレッド、GUIプログラミングまで、Javaの主要な技術要素を実践的に使いこなせるレベルが含まれます。特に、オブジェクト指向設計の実践経験は重要で、クラス間の責任分担、継承とコンポジションの使い分け、インターフェイスを使った抽象化など、設計上の判断を行った経験があることが求められます。また、依存関係とインターフェイスの理解も不可欠です。あるクラスが別のクラスに依存するとはどういうことか、なぜインターフェイスを使って依存関係を抽象化することが重要なのかを理解していることで、本章で学ぶテスト容易な設計の価値を深く理解できます。

開発経験の前提として、中規模なJavaアプリケーションの開発経験があることが望ましいです。数十のクラスからなるアプリケーションを開発し、機能追加や修正を行った経験があれば、テストの必要性を実感を持って理解できます。また、デバッグとエラー解決の経験も重要です。手動でのデバッグに時間を費やし、同じバグが再発して困った経験があれば、自動化されたテストの価値をより深く理解できるでしょう。

### 学習目標

本章では、ソフトウェアの品質を保証する上で不可欠なユニットテストと、それを支える設計手法について体系的に学習します。知識理解の面では、まずユニットテストの本質的な目的と重要性を理解します。ユニットテストは単にバグを見つけるためのものではなく、コードの設計を改善し、リファクタリングを安全に行い、ドキュメントとしての役割も果たす多面的な価値を持つことを学びます。特に、開発初期段階でのバグ発見によるコスト削減効果と、長期的な保守性向上への貢献について深く理解します。

テスト駆動開発（TDD）の基本概念についても学習します。「まずテストを書き、それを通すための最小限のコードを書き、その後リファクタリングする」というRed-Green-Refactorサイクルの意味と価値を理解します。この開発手法が、より良い設計と高い品質のコードを生み出す理由を、実践的な例を通じて学びます。また、依存性注入（DI）とテスト容易性の密接な関係についても理解を深めます。密結合なコードがなぜテストしにくいのか、DIを使うことでどのようにテスト容易性が向上するのかを、具体的なコード例を通じて学習します。

技能習得の観点では、業界標準のテスティングフレームワークであるJUnitを使った効果的なテストケース作成技術を習得します。@Test、@BeforeEach、@AfterEachなどの基本的なアノテーションから、@ParameterizedTest、@Nestedなどの高度な機能まで、JUnit 5の強力な機能を活用したテスト作成方法を学びます。また、Mockitoを使ったモックオブジェクトの活用技術も重要な学習項目です。外部システムやデータベースに依存するコードをテストする際に、モックを使って依存関係を切り離し、高速で安定したテストを作成する方法を習得します。

設計能力の面では、テスト容易性を最初から考慮した設計思考を養います。「このクラスはテストしやすいか？」という問いを常に持ち、単一責任の原則、依存関係逆転の原則などのSOLID原則を実践的に適用する能力を身につけます。疎結合で保守性の高いアーキテクチャ設計では、インターフェイスを使った抽象化、依存性注入コンテナの活用、レイヤードアーキテクチャなど、大規模なアプリケーションでも品質を保ちやすい設計パターンを学びます。

最終的な到達レベルとして、独立性の高いユニットテストを効率的に作成できるようになることを目指します。これには、適切なテストケースの粒度の判断、境界値テストやエラーケースのカバー、そして可読性の高いテストコードの作成が含まれます。依存関係を適切に分離したテスト容易な設計により、変更に強く、バグの少ないアプリケーションを構築できるようになります。モックやスタブを適切に使い分け、外部依存を持つ複雑なコードでも確実にテストできる技術を身につけます。最終的には、継続的インテグレーション（CI）環境でのテスト自動化など、継続的な品質保証プロセスを構築できる実力を養います。

---

## 20.1 なぜテストは重要なのか

これまでの章で、Javaの文法やオブジェクト指向の概念を学び、プログラムを「動かす」方法を習得してきました。しかし、プロフェッショナルなソフトウェア開発において、「動く」ことと同じくらい、あるいはそれ以上に重要なのは、そのソフトウェアが「**正しく動き続ける**」ことを保証することです。

**ユニットテスト（ユニットテスト）**とは、ソフトウェアを構成する **最小単位**（クラスやメソッドなど）が、それぞれ意図通りに正しく動作するかを検証するテストです。部品一つ一つの品質を保証する、品質管理の第一歩です。

### ユニットテストがもたらす4つのメリット

1.  **バグの早期発見**: 開発の初期段階でバグを発見できれば、修正は比較的簡単です。ユニットテストは、**コードを書いた直後に品質を検証する**ことで、手戻りのコストを大幅に削減します。
2.  **変更を恐れないための「安全網」**: 一度作成したコードの構造を、後からより良く改善する作業を**リファクタリング**と呼びます。ユニットテストという安全網があれば、「動いているコードを壊すかもしれない」という恐怖心なく、安心してリファクタリングに挑戦できます。
3.  **動く「仕様書」**: テストコードは「このメソッドは、こういう入力に対して、こう動作するべきだ」という仕様をコードで表現したものです。**常に最新の正しい仕様を示す「生きたドキュメント」**として機能します。
4.  **開発サイクルの高速化**: 手動での動作確認は、時間がかかり、ミスも発生しやすい作業です。自動化されたユニットテストは、何百もの検証を自動かつ一瞬で完了させ、開発のリズムを向上させます。

## 20.2 テストしやすいコード、しにくいコード

### テストの基本形 (AAAパターン)

テストコードは、**AAA（Arrange-Act-Assert）** という3ステップのパターンで記述するのが基本です。これにより、テストの意図が明確になります。

-   **Arrange（準備）**: テストに必要なオブジェクトを生成し、条件を整えます。
-   **Act（実行）**: テストしたいメソッドを呼び出します。
-   **Assert（検証）**: 実行結果が期待通りかを確認します。

シンプルな`Calculator`クラスを例に、テストの基本形を見てみましょう。

```java
// Calculator.java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}

// CalculatorTest.java (JUnit 5を使用)
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {
    @Test // このメソッドがテストケースであることを示すアノテーション
    void testAdd() {
        // Arrange（準備）
        Calculator calculator = new Calculator();
        int inputA = 2;
        int inputB = 3;
        int expected = 5;

        // Act（実行）
        int actual = calculator.add(inputA, inputB);

        // Assert（検証）
        assertEquals(expected, actual, "2 + 3 は 5 になるべきです");
    }
}
```
`@Test`は、**JUnit**というテストフレームワークのアノテーションです。JUnitは、テストの記述と実行を強力に支援する、Java開発の標準的なツールです。

### テストを妨げる「密結合」

問題は、クラスがほかのクラスに**依存**している場合です。特に、あるクラスが別の**具体的なクラス**を内部で直接生成している状態（**密結合**）は、テストを著しく困難にします。

```java
// UserRepository.java - DBからユーザー情報を取得するクラス
public class UserRepository {
    public String findById(String id) {
        // データベースへの接続と検索処理...
        System.out.println("...データベースに接続中...");
        return "Taro Yamada";
    }
}

// UserService.java - テストしにくい密結合なクラス
public class UserService {
    private final UserRepository userRepository = new UserRepository(); // 内部で直接生成！

    public String getUserProfile(String id) {
        String name = userRepository.findById(id);
        return "【Profile】" + name;
    }
}
```
この`UserService`をテストしようとすると、`new UserRepository()`によって、必ずデータベース接続処理が実行されてしまいます。これでは、`UserService`のロジックだけを独立して検証できません。

## 20.3 疎結合設計とDependency Injection (DI)

この密結合の問題を解決するのが「疎結合」設計と、それを実現する「**Dependency Injection (DI)**」です。

DIの原則は、「**あるオブジェクトが必要とする別のオブジェクト（依存オブジェクト）を、内部で生成するのではなく、外部から与える（注入する）**」というものです。これは、オブジェクトの生成と管理の責任を外部に委ねる**制御の反転 (Inversion of Control - IoC)** という、より大きな設計原則の具体的な実現方法です。

### インターフェイスによる疎結合

疎結合を実現する鍵は、具体的な実装クラスに依存するのではなく、その **「役割」を定義したインターフェイスに依存する** ことです。

```java
// UserRepository.java (インターフェース)
public interface UserRepository {
    String findById(String id);
}

// UserServiceImpl.java (本番用の実装)
public class UserServiceImpl implements UserRepository {
    @Override
    public String findById(String id) {
        // 実際のDB接続処理
        System.out.println("...データベースに接続中...");
        return "Taro Yamada";
    }
}

// UserService.java (DIを適用)
public class UserService {
    private final UserRepository userRepository;

    // コンストラクタで依存性を受け取る（コンストラクタインジェクション）
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUserProfile(String id) {
        String name = userRepository.findById(id);
        return "【Profile】" + name;
    }
}
```
`UserService`は、具体的な`UserServiceImpl`を知りません。`UserRepository`という「役割」だけを知っています。

### スタブを使ったユニットテスト

DIによって、テスト時には本物のDB接続クラスの代わりに、テスト用の偽物（**スタブ**）を注入できます。

```java
// UserRepositoryStub.java - テスト用の偽物リポジトリ
public class UserRepositoryStub implements UserRepository {
    @Override
    public String findById(String id) {
        // DBには接続せず、テスト用の固定値を返す
        return "Test User";
    }
}

// UserServiceTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {
    @Test
    void testGetUserProfile() {
        // Arrange（準備）
        UserRepository stub = new UserRepositoryStub(); // テスト用のスタブを生成
        UserService userService = new UserService(stub); // スタブを注入！

        // Act（実行）
        String actual = userService.getUserProfile("123");

        // Assert（検証）
        assertEquals("【Profile】Test User", actual);
    }
}
```
`UserService`のコードを一切変更することなく、その振る舞いをDBから完全に独立させてテストできました。これがDIの力です。

## 20.4 テスト駆動開発 (TDD)

テスト駆動開発（TDD）とは、**プロダクトコードを実装する前に、まずテストコードを書く**開発手法です。

TDDでは、以下の短いサイクルを繰り返します。

1.  **🔴 Red**: まず、これから実装する機能に対する**失敗するテストコード**を書きます。この時点では、プロダクトコードが存在しないため、コンパイルエラーになるか、テストが失敗します。
2.  **🟢 Green**: そのテストを**成功させるための最小限のコード**を実装します。余計な機能は作らず、とにかくテストをパスさせることだけを考えます。
3.  **🔵 Refactoring**: テストが成功する状態を保ったまま、**コードをよりきれいに、効率的に**書き直します。

TDDを実践するには、テストがしやすい疎結合なコードが不可欠なため、TDDとDIは非常に相性の良い組み合わせです。

## 20.5 高度なテストテクニック

### パラメータ化テスト

同じテスト論理を複数の入力パラメータで実行したい場合、JUnit 5の`@ParameterizedTest`を使用します。

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParameterizedTestExample {
    
    @ParameterizedTest
    @ValueSource(strings = {"racecar", "radar", "level", "noon"})
    void testPalindrome(String word) {
        assertTrue(isPalindrome(word));
    }
    
    @ParameterizedTest
    @CsvSource({
        "1, 1, 2",
        "2, 3, 5", 
        "5, 7, 12",
        "10, 15, 25"
    })
    void testAddition(int a, int b, int expected) {
        assertEquals(expected, a + b);
    }
    
    private boolean isPalindrome(String str) {
        String reversed = new StringBuilder(str).reverse().toString();
        return str.equals(reversed);
    }
}
```

### テストダブル（モック、スタブ、スパイ）

テストダブルは、テスト時に本物のオブジェクトの代わりに使用する偽物のオブジェクトです。

#### スタブ (Stub)
決まった値を返すだけのシンプルな偽物です。

```java
public class EmailServiceStub implements EmailService {
    @Override
    public boolean sendEmail(String to, String subject, String body) {
        // 実際には送信せず、常にtrueを返す
        return true;
    }
}
```

#### モック (Mock)
呼び出しの検証も行う高機能な偽物です。Mockitoを使用した例：

```java
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Test
    void testGetUserProfile() {
        // Arrange
        when(userRepository.findById("123")).thenReturn("Test User");
        UserService userService = new UserService(userRepository);
        
        // Act
        String result = userService.getUserProfile("123");
        
        // Assert
        assertEquals("【Profile】Test User", result);
        verify(userRepository).findById("123"); // 呼び出しの検証
    }
}
```

#### スパイ (Spy)
実際のオブジェクトの一部メソッドだけをモック化します。

```java
@Test
void testPartialMocking() {
    List<String> list = new ArrayList<>();
    List<String> spyList = spy(list);
    
    // 一部のメソッドだけモック化
    when(spyList.size()).thenReturn(100);
    
    spyList.add("one");
    assertEquals(1, spyList.size()); // 実際の動作
    
    when(spyList.size()).thenReturn(100);
    assertEquals(100, spyList.size()); // モック化された動作
}
```

### テストダブルの使い分け

| 種類 | 用途 | 特徴 |
|------|------|------|
| **スタブ** | 決まった値を返す | シンプル、状態の確認が主目的 |
| **モック** | 呼び出しの検証 | 相互作用の検証が主目的 |
| **スパイ** | 部分的なモック化 | 実オブジェクトの拡張 |

選択指針：
- **状態の確認**が主目的 → スタブ
- **相互作用の検証**が主目的 → モック
- **実オブジェクトの部分的な置き換え** → スパイ

## より深い理解のために

本章で学んだ基本的なテスト技法をさらに発展させたい方は、付録B.13「テストピラミッドと統合テスト戦略」を参照してください。この付録では以下の高度なトピックを扱います：

- **テストピラミッド**: 単体テスト、統合テスト、E2Eテストの適切なバランス
- **Testcontainers**: 実際のデータベースやメッセージキューを使った統合テスト
- **Property-based Testing**: ランダムな入力による包括的なテスト
- **ミューテーションテスト**: テストケースの品質を検証する手法
- **高度なテスト戦略**: Contract Testing、Chaos Engineering、パフォーマンステスト

これらの技術を習得することで、より堅牢で信頼性の高いシステムを構築できるようになります。