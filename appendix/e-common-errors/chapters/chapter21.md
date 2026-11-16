# 第21章 ユニットテストと品質保証：よくあるエラーと対処法

書籍『Java入門』第21章「ユニットテストと品質保証」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

テストの誤りは、テストが成功しているのにバグを見逃している、という形で現れるため気づきにくいものです。

## テストケースが不十分

期待どおりの入力の1組だけを確かめ、0や負の数、境界にある値をテストしていません。

### 対処法

```java
@Test
void testAdd() {
    Calculator calc = new Calculator();
    
    // 正常値のテスト
    assertEquals(5, calc.add(2, 3));
    
    // 0を含む値のテスト
    assertEquals(0, calc.add(0, 0));
    
    // 負数のテスト
    assertEquals(-1, calc.add(-3, 2));
}
```

## アサーションの使い方の誤り

浮動小数点数を誤差の許容範囲なしで比較し、計算は正しいのにテストが失敗します。
`0.1 + 0.2`は`0.30000000000000004`になるため、`assertEquals(0.3, 0.1 + 0.2)`は失敗します。

### 対処法

```java
@Test
void testDoubleCalculation() {
    double result = 0.1 + 0.2;
    assertEquals(0.3, result, 0.0001); // 許容誤差を指定
}
```

## モックの設定不足

テスト対象が呼び出すメソッドの振る舞いをモックに設定していません。
Mockitoのモックは、設定していないメソッドに対して`null`や0などの既定値を返すため、期待と違う結果になります。

### 対処法

テスト対象が呼び出すメソッドの戻り値を`when().thenReturn()`で設定します（本文のサンプルコード21-15と同じ`UserRepository`と`UserService`を使います）。

```java
@Test
void testGetUserProfile() {
    UserRepository mockRepo = mock(UserRepository.class);
    
    // モックの振る舞いを設定
    when(mockRepo.findById("123")).thenReturn("Test User");
    
    UserService service = new UserService(mockRepo);
    assertEquals("【Profile】Test User", service.getUserProfile("123"));
}
```

## テスト間の依存関係

複数のテストが同じオブジェクトを共有し、前のテストが残した状態に結果が左右されます。
JUnitはテストメソッドの実行順序を保証しないため、実行のたびに結果が変わることがあります。

### エラー例

```java
public class CartTest {
    private static List<String> cart = new ArrayList<>();  // すべてのテストで共有される

    @Test
    void testAddItem() {
        cart.add("りんご");
        assertEquals(1, cart.size());  // 後に実行されたほうのテストが失敗する
    }

    @Test
    void testAddTwoItems() {
        cart.add("みかん");
        cart.add("ぶどう");
        assertEquals(2, cart.size());
    }
}
```

`testAddItem`が先に動くと、`testAddTwoItems`では要素が3個になって失敗します。
逆の順序なら、`testAddItem`で要素が3個になって失敗します。
どちらの順序でも、後に実行されたほうのテストが失敗します。

### 対処法

`@BeforeEach`で、テストごとに新しいオブジェクトを用意します。

```java
public class CartTest {
    private List<String> cart;

    @BeforeEach
    void setUp() {
        cart = new ArrayList<>();  // 各テストの前に初期化
    }

    @Test
    void testAddItem() {
        cart.add("りんご");
        assertEquals(1, cart.size());
    }

    @Test
    void testAddTwoItems() {
        cart.add("みかん");
        cart.add("ぶどう");
        assertEquals(2, cart.size());
    }
}
```
