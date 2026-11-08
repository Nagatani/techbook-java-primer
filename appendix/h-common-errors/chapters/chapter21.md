# 第21章 ユニットテストと品質保証：よくあるエラーと対処法

書籍『Java入門』第21章「ユニットテストと品質保証」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

単体テスト初学者がよく遭遇する基本的なエラーとその対処法を説明します。

## 1. テストケースが不十分

#### 問題
テストケースが期待値のみをチェックし、境界値や異常値をテストしていません。

#### 解決策

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

## 2. アサーション使用ミス

#### 問題
浮動小数点の厳密な比較によるテスト失敗。

#### 解決策

```java
@Test
void testDoubleCalculation() {
    double result = 0.1 + 0.2;
    assertEquals(0.3, result, 0.0001); // 許容誤差を指定
}
```

## 3. モックの設定不足

#### 問題
モックオブジェクトの振る舞いが正しく設定されていません。

#### 解決策

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

## 4. テスト間の依存関係

#### 問題
テストの実行順序に依存したテストケース。

#### 解決策

```java
@Test
void testIncrement() {
    int counter = 0;  // 各テストで初期化
    counter++;
    assertEquals(1, counter);
}
```
