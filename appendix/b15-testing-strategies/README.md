# 付録B.15 テストピラミッドと統合テスト戦略

効果的なテスト戦略の構築と高度なテスト技法について学習できるプロジェクトです。

## 概要

ソフトウェア開発では、単体テストだけでは見つからない問題があります。
テストピラミッドに基づくテスト戦略、Property-based testing、ミューテーションテスト、実際のデータベースを使った統合テストなどの技法を理解すると、バグを早く見つけ、品質の高いソフトウェアを作りやすくなります。

## なぜ高度なテスト戦略が重要なのか

### 単体テストだけでは見つからない問題

次のコードは、依存するサービスをモックにした単体テストでは正常に動きます。

```java
public class OrderService {
    private final PaymentService paymentService;
    private final InventoryService inventoryService;

    public Order processOrder(Order order) {
        paymentService.charge(order.getAmount());
        inventoryService.reserveItem(order.getItemId());
        return order;
    }
}
```

しかし、支払いの後で在庫の確保に失敗したときに支払いを取り消すか、といったトランザクションの境界や、依存するサービスが失敗したときの動作は、単体テストでは確かめられません。
本番で初めて、支払いだけが済んだ注文のようなデータの不整合が見つかることがあります。

### 例で選んだ値だけでは見つからない問題

手で選んだ入力値によるテストは、作った人が思いつかなかった境界値やエッジケースを見落とします。
また、テストが通っていても、そのテストがバグを本当に見つけられるかどうかは分かりません。
Property-based testingは多数の入力を自動で生成して前者を補い、ミューテーションテストは後者を測ります。

## サンプルコード構成

### 1. テストピラミッド実装
- `TestPyramidDemo.java`
  - 単体テスト: 個別のコンポーネントの高速なテスト（依存するサービスは手書きのモックに置き換える）
  - 統合テスト: サービス層の連携のテスト
  - E2Eテスト: ユーザーのシナリオ全体のテスト
  - ECサイトの注文処理を題材にする

### 2. Property-based Testing
- `PropertyBasedTestDemo.java`
  - ソートの性質（長さ、冪等性、順序、要素の保存）
  - 文字列操作の性質
  - データ構造の不変条件
  - 状態を持つオブジェクト（銀行口座）の性質
  - 1つの性質につき100〜1000件のランダムな入力で検証する。乱数のシードは固定（既定は42）で、`-Dseed=数値`で変えられる

### 3. ミューテーションテスト
- `MutationTestDemo.java`
  - ミュータント（わざと小さなバグを入れたコード）をサブクラスとして用意し、同じテストを元のコードと各ミュータントに対して実行する
  - 弱いテストスイートと強いテストスイートのミューテーションスコアを比べる
  - 等価ミュータント（元のコードと同じ動きをするため、どんなテストでも検出できないもの）の例も含む

### 4. データベースを使った統合テスト
- `TestContainersDemo.java`
  - CRUD操作、一意制約、集計のクエリ、トランザクション（コミットとロールバック）、性能をテストする
  - Testcontainersは、Dockerのコンテナーで本番と同じデータベースをテストのたびに起動するライブラリ。このデモはDockerなしで動かせるよう、Testcontainersは使わず、インメモリのH2データベースで代用している

## 実行方法

### コンパイルと実行
```bash
cd appendix/b15-testing-strategies
javac -d out src/main/java/com/example/testing/*.java

# テストピラミッドのデモ
java -cp out com.example.testing.TestPyramidDemo

# Property-based testingのデモ
java -cp out com.example.testing.PropertyBasedTestDemo

# ミューテーションテストのデモ
java -cp out com.example.testing.MutationTestDemo
```

`TestContainersDemo`はH2のJDBCドライバーを使います。
Maven Centralから`com.h2database:h2`のJAR（例: `h2-2.5.252.jar`）を入手し、クラスパスに加えて実行します。

```bash
curl -O https://repo1.maven.org/maven2/com/h2database/h2/2.5.252/h2-2.5.252.jar

# macOS・Linux
java -cp out:h2-2.5.252.jar com.example.testing.TestContainersDemo

# Windows（区切りは;）
java -cp "out;h2-2.5.252.jar" com.example.testing.TestContainersDemo
```

H2がクラスパスにないと、「Could not open the test database」と表示して終了します。

### テスト実行例

`TestPyramidDemo`の出力の一部です（注文番号は実行のたびに変わります）。

```
Testing Pyramid Demonstration
==============================
=== Unit Test Level: Order Entity ===
Test 1: Normal order creation and item addition
✓ Order total correctly calculated: 35.0

Test 2: Edge cases
✓ Correctly rejected quantity 0: Quantity must be positive
✓ Correctly rejected negative price: Price cannot be negative
...

=== Integration Test Level: Service Layer ===

Test 1: End-to-end order workflow
Step 1: Order created - ORD-1794655472740-6950
Step 2: Items added, total: 65.0
Email sent: Order confirmation for ORD-1794655472740-6950 sent to CUST-001
Step 3: Order processed, status: PAID
Step 4: Email sent - Order confirmation for ORD-1794655472740-6950 sent to CUST-001
✓ Complete workflow executed successfully
...

=== E2E Test Level: Complete Order Scenario ===

Scenario: Complete online shopping flow
Step 1: Customer starts shopping
Created order: ORD-1794655472744-7601

Step 2: Adding items to cart
Cart total: $1479.97
...

All tests passed! Test pyramid executed successfully.
```

## 学習ポイント

### 1. テストピラミッドの実践

#### 3層構造の配分の目安
```
                    /\
                   /  \     E2E Tests（少数）
                  /    \    - 少数、遅い、高コスト
                 /------\
                /        \  Integration Tests（中程度）
               /          \ - 中程度の数、中速
              /------------\
             /              \ Unit Tests（多数）
            /________________\ - 多数、高速、低コスト
```

よく挙げられる目安は、単体テスト70%、統合テスト20%、E2Eテスト10%程度です。
適切な配分はシステムの性質によって変わります。

#### 各レイヤーの責任分担
- 単体テスト
  - ビジネスロジック、アルゴリズム、境界値
- 統合テスト
  - サービス間の連携、データアクセス層、外部API
- E2Eテスト
  - ユーザーのシナリオ、ワークフロー全体

### 2. Property-based Testing

#### 従来のテストとの比較

次の例は、Property-based testingのライブラリjqwikの書き方です（このプロジェクトのデモはライブラリを使わず、同じ考え方を簡単なフレームワークで実装しています）。

```java
// 従来のテスト（例示ベース）
@Test
void testSort() {
    List<Integer> input = Arrays.asList(3, 1, 4, 1, 5);
    List<Integer> sorted = sort(input);
    assertEquals(Arrays.asList(1, 1, 3, 4, 5), sorted);
}

// Property-based testing（性質ベース、jqwik）
@Property
void sortingPreservesLength(@ForAll List<Integer> list) {
    List<Integer> sorted = sort(list);
    assertEquals(list.size(), sorted.size());
}

@Property
void sortingIsIdempotent(@ForAll List<Integer> list) {
    List<Integer> sorted1 = sort(list);
    List<Integer> sorted2 = sort(sorted1);
    assertEquals(sorted1, sorted2);
}
```

#### 発見できるバグの種類
- エッジケース
  - 空のリスト、大きなデータ、重複する要素
- 境界値のエラー
  - オーバーフロー、アンダーフロー
- 一貫性の問題
  - 状態の不整合、不変条件の違反
- 性質そのものの誤り
  - 生成した入力が想定外だと、性質の書き方の誤りも見つかる（例: マップのキーが重複すると、後の値で上書きされる）

失敗した入力を再現できるよう、乱数のシードを記録しておくことが大切です。

### 3. ミューテーションテストによる品質改善

#### ミューテーションの種類
| 種類 | 例 | 検出するテストのすき |
|------|----|--------------------|
| 条件の境界の変更 | `<` → `<=` | 境界値のテストの不足 |
| 条件の否定 | `==` → `!=` | 等価性のテストの不備 |
| 算術演算子の変更 | `+` → `-` | 計算のロジックの検証不足 |
| 戻り値の変更 | `true` → `false` | 結果の検証の甘さ |
| メソッド呼び出しの削除 | `method()`を消す | 副作用のテストの欠如 |

#### デモの結果

`MutationTestDemo`を実行すると、次の結果になります（ミュータントとテストは固定なので、毎回同じです）。

| 対象 | ミュータントの数 | 検出した数 | スコア |
|---|---|---|---|
| TemperatureConverter（弱いテスト） | 10 | 5 | 50.0% |
| TemperatureConverter（強いテスト） | 10 | 9 | 90.0% |
| Calculator | 10 | 9 | 90.0% |
| UserValidator | 7 | 7 | 100.0% |

弱いテストは、`isFreezing`の境界（0度）と`getTemperatureCategory`を検査していないため、境界の変更を見逃します。
強いテストとCalculatorのテストで生き残った1つずつは、等価ミュータント（`0.0`を`-0.0`に変える、`max`の`>`を`>=`に変える）です。
元のコードと同じ動きをするので、どんなテストでも検出できません。
スコアが100%でないことが、必ずしもテストの不足を意味しない理由です。

#### ミューテーションスコアを上げる指針
```java
// 改善前: 1つの例しか確かめていない
@Test
void testDivision() {
    assertEquals(5.0, calculator.divide(10, 2));
}

// 改善後: 割り切れない値、負の値、ゼロ除算も確かめる
@Test
void testDivision() {
    assertEquals(5.0, calculator.divide(10, 2));
    assertEquals(3.5, calculator.divide(7, 2));
    assertEquals(-2.5, calculator.divide(-5, 2));

    assertThrows(ArithmeticException.class,
        () -> calculator.divide(10, 0));
}
```

`MutationTestDemo`の`Calculator.divide`は、`b`が0のとき`ArithmeticException`を投げます（`double`の割り算は0で割っても例外にならないため、明示的に確かめています）。

### 4. 実際のデータベースを使う統合テスト

#### モックとの比較
| 項目 | Mock | Testcontainers |
|------|------|----------------|
| セットアップ | 高速 | やや低速（コンテナーの起動） |
| 現実性 | 低い | 高い |
| 複雑なSQL | 確かめられない | 確かめられる |
| DB固有の機能 | 使えない | 使える |
| データの整合性 | 擬似的 | 実際 |

H2のようなインメモリのデータベースは、Dockerがなくても速く動きます。
ただし、本番のデータベースとはSQLの方言や機能が異なるので、本番と同じデータベースで確かめたいときはTestcontainersを使います。

#### 適用場面
- リポジトリ層のテスト
  - 複雑なJOINのクエリ、集約関数
- マイグレーションのテスト
  - スキーマの変更の検証
- トランザクションのテスト
  - コミットとロールバックの確認

## 高度なテスト戦略の選択指針

### 1. プロジェクトの特性による使い分け

#### 高い信頼性が求められるシステム（金融、医療、航空など）
- ミューテーションテストで、テストの品質を継続的に確かめる
- Property-based testingで、満たすべき性質を検証する
- 本番と同じデータベースでの統合テスト（Testcontainers）
- 障害を注入して耐性を確かめる（カオスエンジニアリング）

#### 一般的なWebアプリケーション
- テストピラミッドに沿った配分
- 重要な機能でのProperty-based testing
- データアクセス層での統合テスト
- 定期的なミューテーションテスト

#### スタートアップ・プロトタイプ
- 単体テストを中心にする
- 重要なビジネスロジックだけProperty-based testing
- リリース前にE2Eテストを強化する

### 2. 導入の順序の例

#### すぐに始めやすい技法
1. テストピラミッドの構築
   - 遅いテストを減らし、CIを速くする
2. 境界値テストの強化
3. E2Eテストの自動化
   - 手動テストを減らす

#### 次に取り入れる技法
1. Property-based testing
   - エッジケースのバグを減らす
2. 実際のデータベースを使う統合テスト
   - 連携のバグを早く見つける
3. パフォーマンステストの自動化
   - 性能の低下を防ぐ

#### 長期的に取り組む技法
1. ミューテーションテスト
   - テストの品質を継続して改善する
2. カオスエンジニアリング
   - システムの回復力を高める
3. テスト駆動開発
   - 設計の品質を高める

## 関連技術とツール

### Javaのエコシステム
- JUnit
  - Javaの標準的なテストフレームワーク。最新の系列はJUnit 6（2025年9月公開）で、テストはJUnit 5と同じJupiterの書き方で書く
- Mockito
  - モックの作成
- Testcontainers
  - Dockerを使った統合テスト
- jqwik
  - Property-based testing
- PIT（Pitest）
  - ミューテーションテスト
- WireMock
  - HTTP APIのモック

### 品質測定ツール
- JaCoCo
  - コードカバレッジ
- SonarQube
  - 品質ゲート
- SpotBugs
  - 静的解析
- ArchUnit
  - アーキテクチャのテスト

### CI/CDとの統合
- GitHub Actions
  - ワークフローの自動化
- Jenkins
  - 継続的インテグレーション
- Docker
  - コンテナー化したテスト環境
- Gradle・Maven
  - ビルドの自動化

このプロジェクトを通じて、テストの書き方だけでなく、どのテストをどれだけ書くかという戦略的な考え方を身につけ、実際のプロジェクトで応用できるようになることが目標です。
