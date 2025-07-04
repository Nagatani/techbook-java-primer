# 第15章 品質とテスト：ユニットテストと疎結合設計

## 始めに：なぜテストは重要なのか

これまでの章で、Javaの文法やオブジェクト指向の概念を学び、プログラムを「動かす」方法を習得してきました。しかし、プロフェッショナルなソフトウェア開発において、「動く」ことと同じくらい、あるいはそれ以上に重要なのは、そのソフトウェアが「**正しく動き続ける**」ことを保証することです。

本章では、ソフトウェアの品質を保証するための根幹となる「ユニットテスト」の概念と、テストを容易にするための設計原則「疎結合」および「Dependency Injection (DI)」について学びます。

## 15.1 ユニットテストとは

ユニットテスト（単体テスト）とは、ソフトウェアを構成する **最小単位**（クラスやメソッドなど）が、それぞれ意図通りに正しく動作するかを検証するテストです。部品一つ一つの品質を保証する、品質管理の第一歩です。

### 品質の高いソフトウェアのための4つのメリット

ユニットテストは、単にバグを見つけるだけの作業ではありません。品質の高いソフトウェアを効率的に生み出すための、積極的な活動です。

1.  **バグの早期発見**
    開発の初期段階でバグを発見できれば、修正は比較的簡単です。ユニットテストは、**コードを書いた直後に品質を検証する** ことで、手戻りのコストを大幅に削減します。

2.  **変更を恐れないための「安全網」**
    一度作成したコードの構造を、後からより良く改善する作業を**リファクタリング**と呼びます。ユニットテストという安全網があれば、「動いているコードを壊すかもしれない」という恐怖心なく、安心してリファクタリングに挑戦できます。

3.  **動く「仕様書」**
    テストコードは「このメソッドは、こういう入力に対して、こう動作するべきだ」という仕様をコードで表現したものです。 **常に最新の正しい仕様を示す「生きたドキュメント」** として機能します。

4.  **開発サイクルの高速化**
    手動での動作確認は、時間がかかり、ミスも発生しやすい作業です。自動化されたユニットテストは、何百もの検証を自動かつ一瞬で完了させ、開発のリズムを向上させます。

## 13.2 テストしやすいコード、しにくいコード
## 15.2 テストしやすいコード、しにくいコード

### テストの基本形 (AAAパターン)

テストコードは、**AAA（Arrange-Act-Assert）** という3ステップのパターンで記述するのが基本です。これにより、テストの意図が明確になります。

*   **Arrange（準備）**: テストに必要な条件を整えます。
*   **Act（実行）**: テストしたいメソッドを呼び出します。
*   **Assert（検証）**: 実行結果が期待通りかを確認します。

シンプルな`Calculator`クラスを例に、テストの基本形を見てみましょう。

```java
// Calculator.java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
}
```

```java
// CalculatorTest.java (JUnitを使用)
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {
    @Test
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

### テストを妨げる「依存関係」

問題は、クラスがほかのクラスに**依存**している場合です。特に、あるクラスが別の**具体的なクラス**を内部で直接生成している状態（**密結合**）は、テストを著しく困難にします。

```java
// UserRepository.java - DBからユーザー情報を取得するクラス
public class UserRepository {
    public String findById(String id) {
        // データベースへの接続と検索処理...
        return "Taro Yamada";
    }
}

// UserService.java - テストしにくい密結合なクラス
public class UserService {
    private final UserRepository userRepository = new UserRepository();

    public String getUserProfile(String id) {
        String name = userRepository.findById(id);
        return "【Profile】" + name;
    }
}
```

この`UserService`をテストしようとすると、`new UserRepository()`によって、必ずデータベース接続処理が実行されてしまいます。これでは、`UserService`のロジックだけを独立して検証できません。

## 15.3 疎結合設計とDependency Injection (DI)

この密結合の問題を解決するのが「疎結合」設計と、それを実現する「**Dependency Injection (DI)**」です。

DIの原則は、「**あるオブジェクトが必要とする別のオブジェクト（依存オブジェクト）を、内部で生成するのではなく、外部から与える（注入する）**」というものです。

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
        UserRepository stub = new UserRepositoryStub();
        UserService userService = new UserService(stub); // スタブを注入

        // Act（実行）
        String actual = userService.getUserProfile("123");

        // Assert（検証）
        assertEquals("【Profile】Test User", actual);
    }
}
```

`UserService`のコードを一切変更することなく、その振る舞いをDBから完全に独立させてテストできました。これがDIの力です。

## 15.4 テスト駆動開発 (TDD)

テスト駆動開発（TDD）とは、**プロダクトコードを実装する前に、まずテストコードを書く**開発手法です。

TDDでは、以下の短いサイクルを繰り返します。

1.  **🔴 Red**: まず、これから実装する機能に対する**失敗するテストコード**を書きます。
2.  **🟢 Green**: そのテストを**成功させるための最小限のコード**を実装します。
3.  **🔵 Refactoring**: テストが成功する状態を保ったまま、**コードをよりきれいに、効率的に**書き直します。

TDDを実践するには、テストがしやすい疎結合なコードが不可欠なため、TDDとDIは非常に相性の良い組み合わせです。