# 第21章 ユニットテストと品質保証 - 演習課題

## 📋 章の概要

この章では、テスト駆動開発（TDD）の手法とJUnit 5を使用した効果的なテスト作成について学習します。単体テストから統合テスト、モックの活用まで、品質の高いソフトウェア開発に必要なテスト技術を習得します。

## 🎯 学習目標

- テスト駆動開発（TDD）の概念と手法を理解する
- JUnit 5の機能を効果的に活用できる
- モックオブジェクトを使用したテストができる
- パラメータ化テストによる効率的なテストが書ける
- テストスイートによる体系的なテスト管理ができる

## 📁 課題構成

### basic/
テスト作成の基礎スキルを習得するための課題です。

- **SimpleCalculatorTest.java** - 基本的な単体テストの作成
- **StringUtilsTest.java** - 文字列処理のテスト
- **ValidationTest.java** - バリデーション機能のテスト

### advanced/
より高度なテスト技法を学習する課題です。

- **MockObjectTest.java** - モックオブジェクトの活用
- **ParameterizedTestDemo.java** - パラメータ化テストの実装
- **IntegrationTest.java** - 統合テストの作成

### challenge/
実践的で包括的なテストスキルが求められる挑戦課題です。

- **TDDWorkflow.java** - 完全なTDDサイクルの実践
- **TestSuiteDesign.java** - 大規模テストスイートの設計
- **PerformanceTest.java** - パフォーマンステストの実装

### solutions/
各課題の完全な解答例と詳細な解説です。

- **TestDrivenCalculator.java** - TDD手法で実装された計算機クラス
- **TestDrivenCalculatorTest.java** - JUnit 5を使用した包括的なテストスイート
- **MockExample.java** - モッキングフレームワークを使用したテスト実装
- **ParameterizedTestExamples.java** - パラメータ化テストの効率的な活用例
- **TestSuite.java** - テストスイートによる体系的なテスト管理

## 🚀 課題の進め方

### 1. テストの基礎から開始
```bash
# JUnit 5の基本機能を理解
cd basic/
# SimpleCalculatorTest.java から始めることを推奨
```

### 2. TDDサイクルの実践
```bash
# Red-Green-Refactorサイクルを体験
cd advanced/
# 実際にTDDでコードを作成してみる
```

### 3. 高度なテスト技法の習得
```bash
# モックやパラメータ化テストを活用
cd challenge/
# 実務レベルのテスト設計を実践
```

## 🔄 TDDサイクル

### 1. Red（失敗するテストを書く）
```java
@Test
void testAddition() {
    Calculator calc = new Calculator();
    // まだ実装されていないメソッドをテスト
    assertEquals(5, calc.add(2, 3));
}
```

### 2. Green（テストを通す最小実装）
```java
public class Calculator {
    public int add(int a, int b) {
        return 5; // とりあえずテストを通すだけ
    }
}
```

### 3. Refactor（コードを改善）
```java
public class Calculator {
    public int add(int a, int b) {
        return a + b; // 適切な実装に改善
    }
}
```

## 🧪 JUnit 5の主要機能

### 基本的なアノテーション
```java
@Test                    // テストメソッド
@BeforeEach             // 各テスト前に実行
@AfterEach              // 各テスト後に実行
@BeforeAll              // クラス実行前に1回実行
@AfterAll               // クラス実行後に1回実行
@DisplayName("テスト名")  // テストの表示名
@Disabled               // テストを無効化
```

### アサーション
```java
// 基本的なアサーション
assertEquals(expected, actual);
assertNotEquals(unexpected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(object);
assertNotNull(object);

// 例外のアサーション
assertThrows(IllegalArgumentException.class, () -> {
    someMethod();
});

// 複数条件のアサーション
assertAll(
    () -> assertEquals(expected1, actual1),
    () -> assertEquals(expected2, actual2)
);
```

### パラメータ化テスト
```java
@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 4, 5})
void testIsPositive(int number) {
    assertTrue(number > 0);
}

@ParameterizedTest
@CsvSource({
    "1, 1, 2",
    "2, 3, 5",
    "3, 5, 8"
})
void testAddition(int a, int b, int expected) {
    assertEquals(expected, calculator.add(a, b));
}
```

## 🎭 モックオブジェクトの活用

### Mockitoの基本使用法
```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
    
    @Test
    void testServiceMethod() {
        // モックの振る舞いを定義
        when(repository.findById(1)).thenReturn(userData);
        
        // テスト実行
        User result = service.getUser(1);
        
        // 検証
        assertEquals(expectedUser, result);
        verify(repository).findById(1);
    }
}
```

### スタブとモックの違い
```java
// スタブ: 決まった値を返す
when(service.getData()).thenReturn(testData);

// モック: 呼び出しを検証
verify(service).saveData(any());
verify(service, times(2)).processData();
verify(service, never()).deleteData();
```

## 📊 テストの分類と戦略

### テストピラミッド
```
    /\
   /  \     E2E Tests (少数)
  /    \
 /      \   Integration Tests (中程度)
/        \
----------  Unit Tests (大多数)
```

### テストの種類
- **単体テスト (Unit Test)** - 個別のクラス・メソッドのテスト
- **統合テスト (Integration Test)** - コンポーネント間の連携テスト
- **システムテスト (System Test)** - システム全体の動作テスト
- **受入テスト (Acceptance Test)** - ユーザー要件の確認テスト

## 🏗️ テストスイートの設計

### タグによる分類
```java
@Tag("fast")      // 高速テスト
@Tag("slow")      // 時間のかかるテスト
@Tag("integration") // 統合テスト
@Tag("unit")      // 単体テスト
```

### 条件付きテスト実行
```java
@EnabledOnOs(OS.WINDOWS)        // Windows でのみ実行
@EnabledOnJre(JRE.JAVA_11)      // Java 11 でのみ実行
@EnabledIfSystemProperty(       // システムプロパティ条件
    named = "os.arch", 
    matches = ".*64.*"
)
```

## 💡 テスト設計のベストプラクティス

### 1. FIRST原則
- **Fast** - 高速に実行される
- **Independent** - 他のテストに依存しない
- **Repeatable** - 繰り返し実行可能
- **Self-Validating** - 成功/失敗が明確
- **Timely** - 適切なタイミングで作成

### 2. AAA パターン
```java
@Test
void testCalculation() {
    // Arrange（準備）
    Calculator calculator = new Calculator();
    int a = 5, b = 3;
    
    // Act（実行）
    int result = calculator.add(a, b);
    
    // Assert（検証）
    assertEquals(8, result);
}
```

### 3. 適切なテスト名
```java
// ❌ 悪い例
@Test
void test1() { }

// ✅ 良い例
@Test
@DisplayName("正の数を加算した場合、正しい合計が返される")
void shouldReturnCorrectSumWhenAddingPositiveNumbers() { }
```

## 🔧 開発環境とツール

### 必要な依存関係

Java 25で動作を確認した版です（Mockito 4系とJaCoCo 0.8.13以前はJava 25に対応していません）。
```xml
<!-- pom.xml (Maven) -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.14.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.24.0</version>
    <scope>test</scope>
</dependency>
<!-- TestSuite.java（@Suite）を使う場合 -->
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-suite</artifactId>
    <version>1.14.4</version>
    <scope>test</scope>
</dependency>
```

### IDEでの実行
```bash
# Maven でテスト実行
mvn test

# Gradle でテスト実行
gradle test

# 特定のテストクラスのみ実行
mvn test -Dtest=CalculatorTest

# タグ別実行
mvn test -Dgroups=fast
```

## 📊 テストカバレッジ

### カバレッジの種類
- **行カバレッジ** - 実行された行の割合
- **分岐カバレッジ** - 実行された分岐の割合
- **条件カバレッジ** - 実行された条件の割合

### JaCoCo の活用
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.16</version>
</plugin>
```

## ✅ 評価基準

### 基本レベル (60-70点)
- [ ] 基本的な単体テストが書ける
- [ ] JUnit 5のアノテーションが使用できる
- [ ] アサーションが適切に使用できる

### 応用レベル (70-85点)
- [ ] TDDサイクルが実践できる
- [ ] パラメータ化テストが活用できる
- [ ] 例外処理のテストが書ける
- [ ] テストの可読性と保守性が考慮されている

### 発展レベル (85-100点)
- [ ] モックオブジェクトが効果的に活用されている
- [ ] テストスイートが体系的に設計されている
- [ ] パフォーマンステストが実装されている
- [ ] CI/CD での活用が考慮されている

## 🎯 実務での応用

### 継続的インテグレーション
```yaml
# GitHub Actions の例
name: CI
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Run tests
        run: mvn test
```

### 品質管理
- **コードレビュー** でのテスト確認
- **品質ゲート** でのカバレッジ基準
- **リグレッションテスト** での変更影響確認



💡 **ヒント**: TDDは最初は時間がかかるように感じますが、バグの早期発見とコード品質の向上により、長期的には開発効率が向上します。小さなステップで確実に進めることが重要です。