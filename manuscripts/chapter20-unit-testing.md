# 第20章 単体テストと品質保証

## 🎯総合演習プロジェクトへのステップ

本章で学ぶ「ユニットテスト」と「疎結合設計（DI）」は、**総合演習プロジェクト「ToDoリストアプリケーション」** の品質を保証し、将来の機能追加や変更を容易にするためのプロフェッショナルな開発技術です。

- **ロジックの品質保証**: `Task`クラスのメソッド（例：期限切れかどうかの判定）や、`TaskList`クラスのメソッド（例：タスクの追加・削除ロジック）が、意図通りに正しく動作することを保証するために、ユニットテストを作成します。
- **テスト容易性の向上**: `TaskList`がタスクの保存・読み込みを行う`TaskRepository`に依存している場合、この依存関係をDI（コンストラクタインジェクション）を使って疎結合にします。
- **スタブによるテスト**: テスト時には、実際のファイルにアクセスする`FileTaskRepository`の代わりに、メモリ上で動作するテスト用の偽物（`InMemoryTaskRepository`スタブ）を注入します。これにより、ファイルシステムの存在しない環境でも、`TaskList`のロジックだけを独立して、高速かつ安定してテストできます。

## 📋 本章の学習目標

### 前提知識
**必須前提**：
- 第19章までの総合的なJava開発能力
- オブジェクト指向設計の実践経験
- 依存関係とインターフェイスの理解

**開発経験前提**：
- 中規模なJavaアプリケーションの開発経験
- デバッグとエラー解決の経験

### 学習目標
**知識理解目標**：
- ユニットテストの目的と重要性
- テスト駆動開発（TDD）の基本概念
- 依存性注入（DI）とテスト容易性の関係
- モックとスタブの概念と使い分け

**技能習得目標**：
- JUnitを使った効果的なテストケース作成
- Mockitoを使ったモックオブジェクトの活用
- テスト容易な設計の実装
- 継続的テストの実践

**設計能力目標**：
- テスト容易性を考慮した設計思考
- 疎結合で保守性の高いアーキテクチャ設計
- 品質を重視した開発プロセスの構築

**到達レベルの指標**：
- 独立性の高いユニットテストが効率的に作成できる
- 依存関係を適切に分離したテスト容易な設計ができる
- モックやスタブを活用した高品質なテストが実装できる
- 継続的な品質保証プロセスが構築できる

---

## 20.1 なぜテストは重要なのか

これまでの章で、Javaの文法やオブジェクト指向の概念を学び、プログラムを「動かす」方法を習得してきました。しかし、プロフェッショナルなソフトウェア開発において、「動く」ことと同じくらい、あるいはそれ以上に重要なのは、そのソフトウェアが「**正しく動き続ける**」ことを保証することです。

**ユニットテスト（ユニットテスト）**とは、ソフトウェアを構成する **最小単位**（クラスやメソッドなど）が、それぞれ意図通りに正しく動作するかを検証するテストです。部品1つ一つの品質を保証する、品質管理の第一歩です。

### ユニットテストがもたらす4つのメリット

1.  **バグの早期発見**: 開発の初期段階でバグを発見できれば、修正は比較的簡単です。ユニットテストは、**コードを書いた直後に品質を検証する**ことで、手戻りのコストを大幅に削減します。
2.  **変更を恐れないための「安全網」**: 一度作成したコードの構造を、後からより良く改善する作業を**リフ��クタリング**と呼びます。ユニットテストという安全網があれば、「動いているコードを壊すかもしれない」という恐怖心なく、安心してリファクタリングに挑戦できます。
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

問題は、クラスがほかのクラスに**依存**している場合です。特に、あるクラスが別の**具体的なクラス**を内部で直接生成している状態（**密結合**）は、��ストを著しく困難にします。

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

DIの原則は、「**あるオブジェクトが必要とする別のオブジェクト（依存オブジェクト）を、内部で生成するのではなく、外部から与える（注入���る）**」というものです。これは、オブジェクトの生成と管理の責任を外部に委ねる**制御の反転 (Inversion of Control - IoC)** という、より大きな設計原則の具体的な実現方法です。

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