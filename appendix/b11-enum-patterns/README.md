# 付録B.11 Java Enumパターン詳解

Javaにおける高度なenumデザインパターンを集めたプロジェクトです。単純な定数を超えて、型安全で保守しやすい設計をenumで作る方法を実演します。

## 概要

このプロジェクトは、実際のアプリケーションで出会う問題をenumで解決するパターンを紹介します。各パターンは実行できる例とともに実装しています。

## なぜEnumパターンが重要なのか

### 従来のアプローチの問題点

1. 文字列による状態管理
   - タイプミスが実行時まで見つからない
   - コンパイル時の検証がない
   - 有効な遷移を追跡しにくい

2. マジックナンバー
   - 意味が分かりにくい
   - 検証のロジックが散らばる
   - 保守しにくい

3. 分散したビジネスロジック
   - 状態のチェックがコードのあちこちで重複する
   - ルールを変えるときにバグが入りやすい
   - テストしにくい

### Enumパターンの利点

1. 型安全性
   - 定義した定数以外の値を渡すと、コンパイルエラーになる
2. パフォーマンス
   - EnumSetとEnumMapは内部でビット列や配列を使い、HashSetやHashMapより速く省メモリ
3. 保守性
   - 定数に関係するロジックをenumの中に集められる
4. インスタンスの一意性
   - 各定数のインスタンスは、JVMがスレッドセーフに一度だけ生成する。ただし、インスタンスが可変の状態を持つなら、その状態のスレッドセーフ性は別に確保する必要がある

## プロジェクト構成

```
b11-enum-patterns/
├── src/main/java/com/example/enumpatterns/
│   ├── Main.java          # デモを選んで実行するメニュー
│   ├── statemachine/      # ステートマシン実装
│   ├── strategy/          # enumを使用したStrategyパターン
│   ├── permission/        # パーミッションとセキュリティパターン
│   ├── configuration/     # 設定キーと設定の管理
│   ├── factory/           # enumによるSingleton（コネクションプール）
│   ├── performance/       # パフォーマンスの比較
│   └── examples/          # デモと総合的な例
├── build.sh               # javacでコンパイルする
├── run.sh                 # コンパイルしてMainを実行する
└── pom.xml
```

## 含まれるパターン

### 1. ステートマシンパターン

注文の状態遷移をenumで実装します（`statemachine/OrderState.java`）。

```java
OrderState state = OrderState.PENDING;
state = state.confirm();  // CONFIRMEDを返す
state = state.ship();     // SHIPPEDを返す
state = state.cancel();   // IllegalStateExceptionを投げる
```

各状態が、許される遷移のメソッドだけをオーバーライドします。
不正な遷移は、コンパイル時ではなく実行時に`IllegalStateException`として検出されます。
不正な遷移をコンパイル時に防ぐには、状態ごとに別の型を用意し、その型に許される操作だけを持たせる設計（sealedインターフェイスなど）が必要です。

ユースケースは次のとおりです。

- 注文処理のワークフロー
- ユーザーのライフサイクル管理
- ゲームの状態管理
- 文書の承認プロセス

### 2. Strategyパターン

if-elseの連鎖を、定数ごとに異なる振る舞いを持つenumに置き換えます（`strategy/PricingStrategy.java`）。

```java
PricingStrategy strategy = PricingStrategy.PROGRESSIVE;
BigDecimal tax = strategy.calculateTax(amount, context);
```

ユースケースは次のとおりです。

- 税金の計算
- 価格設定のアルゴリズム
- データ処理のパイプライン

### 3. Singletonパターン

enumを使うと、スレッドセーフなシングルトンを簡潔に書けます（`factory/DatabaseConnection.java`）。

```java
public enum DatabaseConnection {
    INSTANCE;

    private final BlockingQueue<Connection> connectionPool;
    // ...

    public Connection getConnection() throws SQLException {
        // 最初の呼び出しでプールを作り、プールから接続を取り出す
    }
}
```

enumが保証するのは`INSTANCE`の生成がスレッドセーフであることまでです。
このクラスは可変の状態（コネクションプール）を持つため、`BlockingQueue`と`synchronized`でスレッドセーフ性を確保しています。
実行にはH2データベースのJDBCドライバーが必要です（`pom.xml`に`runtime`の依存として入れています）。
デモのメニューからは呼び出していません。

### 4. パーミッションシステム

EnumSetを使って権限を管理します（`permission/`）。

```java
EnumSet<Permission> userPerms = EnumSet.of(READ, WRITE);
boolean canDelete = userPerms.contains(DELETE);  // false
```

ユースケースは次のとおりです。

- ロールベースのアクセス制御
- フィーチャーフラグ
- APIのアクセス制御

### 5. 設定管理

設定キーをenumで定義し、既定値・型・検証の規則をキーに持たせます（`configuration/`）。

```java
int port = config.getServerPort();
boolean featureEnabled = config.isFeatureNewUiEnabled();
```

汎用の`config.get(ConfigKey.SERVER_PORT)`もありますが、戻り値の型はコンパイル時に検査されません。
enumは型パラメーターを持てないため、`String s = config.get(ConfigKey.SERVER_PORT)`のような誤りもコンパイルでき、実行時に`ClassCastException`になります。
型を確実にするには、`getServerPort()`のような型付きのメソッドを使います。

ユースケースは次のとおりです。

- アプリケーションの設定
- 環境ごとの設定
- フィーチャートグル

## パフォーマンスの比較

`performance/PerformanceComparison.java`は、EnumSetとHashSet、EnumMapとHashMap、enumのswitchとif-elseなどを`System.nanoTime()`で比べます。
EnumSetとEnumMapは、HashSetやHashMapより速い傾向があります。
ただし、ウォームアップを少し行うだけの簡易な測定なので、結果は実行のたびに、また環境によって大きく変わります。
正確に比べるには、JMHなどのベンチマークツールを使います（付録B.12のプロジェクトでJMHを使っています）。

### メモリ使用量

- EnumSet
  - 定数が64個以下なら、1つの`long`のビット列で要素を表す（`RegularEnumSet`）
- HashSet
  - 要素ごとにハッシュテーブルのエントリーを持つ

## サンプルの実行

### 前提条件

- Java 25以上
- Maven 3.9以上（Mavenを使う場合）

### javacで実行する

```bash
cd appendix/b11-enum-patterns

# コンパイル
./build.sh

# メニューから選んで実行
java -cp out com.example.enumpatterns.Main

# デモを直接実行
java -cp out com.example.enumpatterns.examples.StateMachineDemo
java -cp out com.example.enumpatterns.examples.PermissionDemo
java -cp out com.example.enumpatterns.examples.StrategyPatternDemo
java -cp out com.example.enumpatterns.examples.ConfigurationDemo
java -cp out com.example.enumpatterns.examples.ComprehensiveExample
java -cp out com.example.enumpatterns.performance.PerformanceComparison
```

`Main`には、`state`、`strategy`、`permission`、`config`、`comprehensive`、`performance`、`all`のいずれかを引数として渡せます。

### Mavenで実行する

```bash
mvn clean package
mvn exec:java -Dexec.args=all
```

## ベストプラクティス

1. 列挙型のコレクションにはEnumSetとEnumMapを使う
   - 型安全で、速くメモリ効率がよい

2. Enumにビジネスロジックを実装する
   - 関連するロジックを集約する
   - 定数ごとのメソッドのオーバーライドでポリモーフィズムを活用する

3. 固定された集合にはEnumを優先する
   - 設定キー
   - ステートマシン
   - パーミッション

4. モダンなJavaの機能と組み合わせる
   - switch式
   - パターンマッチング
   - データの受け渡しにはRecord

## 避けるべき一般的な落とし穴

1. 序数（`ordinal()`）に意味を持たせない
   - 代わりに明示的なフィールドを使う
   - 序数は定数の並べ替えで変わる

2. Enumに可変の状態をむやみに持たせない
   - 全体で共有される1つのインスタンスなので、状態を持つとスレッドセーフ性を考える必要がある
   - 持たせる場合は、`DatabaseConnection`のようにスレッドセーフな仕組みで守る

3. Enumを使いすぎない
   - 実行時に増減する集合には向かない

## ライセンス

このプロジェクトはJava入門技術書の一部であり、教育目的で提供されています。
