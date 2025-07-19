# <b>5章</b> <span>継承とポリモーフィズム</span> <small>コードの再利用と柔軟性の実現</small>

## 本章の学習目標

### 前提知識

必須
- 第4章のカプセル化とアクセス制御
- `public`、`private`、`protected`のアクセス修飾子
- クラスの基本設計（フィールド、メソッド、コンストラクタ）

推奨
- クラス設計の実践経験
- オブジェクト間の関係性の理解

### 到達目標

#### 知識理解目標

本章では、Javaの重要な機能である継承とポリモーフィズムの概念を理解します。まず、継承の概念とis-a関係について学びます。継承は単なるコードの再利用技術ではなく、オブジェクト間の意味的な関係を表現するための強力な機能です。`extends`キーワードを使ってクラスの継承関係を定義し、`super`キーワードを使って親クラスの機能にアクセスする方法を習得します。

メソッドオーバーライドのルールと目的も大切な学習ポイントです。オーバーライドによって、子クラスは親クラスの動作を特殊化し、より具体的な振る舞いを実装できます。さらに、ポリモーフィズム（多態性）の概念を理解することで、同じインターフェイスを通じて異なる実装を透過的に扱うことができ、これがプログラムに柔軟性と拡張性をもたらします。最後に、アップキャストとダウンキャスト、そして`instanceof`演算子の役割を学び、実行時の型判定と型変換の技術を身につけます。

#### 技能習得目標

本章では、継承とポリモーフィズムを実装するための具体的な技術を習得します。まず、`extends`キーワードを使って既存のクラスを継承し、新しいクラスを定義する方法を学びます。この際、親クラスの機能を適切に継承しながら、子クラス独自の機能を追加する方法を理解します。

次に、親クラスのメソッドを`@Override`アノテーションを使って正しくオーバーライドする技術を身につけます。このアノテーションはコンパイラによるチェックを可能にし、オーバーライドの正確性を保証します。また、親クラスのコンストラクタを`super()`で呼びだす方法も重要で、これにより親クラスの初期化処理を適切に実行し、オブジェクトの整合性を保ちます。

さらに、親クラスの型で子クラスのインスタンスを扱うことで、ポリモーフィズムを実践します。この技術により、柔軟で拡張性の高いコードを書くことができます。最後に、`instanceof`演算子とキャストを使って、実行時にオブジェクトの実際の型を判定し、特定のサブクラスの機能を利用する方法を学びます。

#### 到達レベルの指標

| 到達レベル |
| :--- |
| 継承関係を適切に設計し、コードの再利用性を高めることができる |
| ポリモーフィズムを活用し、拡張しやすく保守性の高いコードを記述できる |
| is-a関係とhas-a関係を理解し、継承とコンポジションを適切に使い分けられる |
| 実行時のオブジェクトの型に応じて動的に振る舞いが変わるプログラムを作成できる |



## 実践的な継承設計パターン

第3章でオブジェクト指向の基本概念として継承を学び、第4章では実際のクラス設計で継承を活用しました。本章では、これらの基礎知識を発展させ、より高度で実践的な継承設計パターンと、ポリモーフィズムを活用した柔軟なプログラム構造について学習します。

継承は単なるコードの再利用手段ではなく、システムの拡張性と保守性を向上させる設計技法です。本章では、現実的なビジネス要件に対応できる継承階層の設計方法と、それを支えるポリモーフィズムの実践的な活用法を習得します。

### 高度な継承設計における共通課題

実践的なソフトウェア開発では、単純な継承から複雑なドメインモデルまで、様々なレベルの継承設計が求められます。特に企業システムやWebアプリケーションでは、ビジネスルールの変化に対応できる柔軟な継承階層の設計が大切です。

#### 本章では、これまで学んだ基礎知識を活用して、以下のような実践的な設計課題に取り組みます

- テンプレートメソッドパターン共通アルゴリズムの骨格を定義し、詳細を子クラスに委ねる設計
- 戦略パターンとの組み合わせ継承と委譲を適切に使い分ける設計判断
- リスコフ置換原則の実践安全で予測可能な継承階層の構築
- 拡張性を考慮した設計将来の要件変更に対応できる柔軟な構造

### 継承設計の実践例：決済システム

実際のビジネスシステムでよく見られる決済処理を例に、段階的な継承設計の改善プロセスを見ていきましょう。ここでは、抽象クラスを使わずに、具象クラスの継承とメソッドのオーバーライドによってテンプレートメソッドパターンを実現する方法を示します。

<span class="listing-number">**サンプルコード5-1**</span>

```java
import java.math.BigDecimal;

// 決済結果を保持するクラス
public class PaymentResult {
    private boolean success;
    private String authorizationCode;
    private String message;
    
    public PaymentResult(boolean success, String authorizationCode, String message) {
        this.success = success;
        this.authorizationCode = authorizationCode;
        this.message = message;
    }
    
    public boolean isSuccess() { return success; }
    public String getAuthorizationCode() { return authorizationCode; }
    public String getMessage() { return message; }
}

// 決済処理の基底クラス：テンプレートメソッドパターンの実装
public class PaymentProcessor {
    protected String transactionId;
    protected BigDecimal amount;
    protected String currency;
    protected String errorMessage; // エラーメッセージを保持するフィールド
    
    public PaymentProcessor(String transactionId, BigDecimal amount, String currency) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.errorMessage = null;
    }
    
    // テンプレートメソッド：決済処理の共通フロー
    public final PaymentResult processPayment() {
        // 1. 事前検証（共通処理）
        if (!validateCommonParameters()) {
            logError();
            return new PaymentResult(false, null, errorMessage);
        }
        
        // 2. 決済手段固有の検証（子クラスでオーバーライド可能）
        if (!validatePaymentSpecific()) {
            logError();
            return new PaymentResult(false, null, errorMessage);
        }
        
        // 3. 外部API呼び出し（子クラスでオーバーライド可能）
        String authorizationCode = callExternalAPI();
        if (authorizationCode == null) {
            logError();
            return new PaymentResult(false, null, errorMessage);
        }
        
        // 4. 処理結果の記録（共通処理）
        logTransaction(authorizationCode);
        
        return new PaymentResult(true, authorizationCode, "決済完了");
    }
    
    // 共通の検証ロジック（boolean返却、エラーメッセージはフィールドに設定）
    private boolean validateCommonParameters() {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            errorMessage = "金額は正の値である必要があります";
            return false;
        }
        if (amount.compareTo(new BigDecimal("1000000")) > 0) {
            errorMessage = "決済上限額を超えています";
            return false;
        }
        return true;
    }
    
    // 子クラスでオーバーライドされることを想定したメソッド（デフォルト実装を提供）
    protected boolean validatePaymentSpecific() {
        // デフォルトでは追加の検証なし
        // 子クラスで必要に応じてオーバーライド
        return true;
    }
    
    protected String callExternalAPI() {
        // デフォルトでは基本的な決済処理を実行
        // 実際の実装では外部決済サービスのAPIを呼び出す
        // エラーの場合はnullを返し、errorMessageを設定
        return "DEFAULT-" + System.currentTimeMillis();
    }
    
    // フックメソッド：子クラスで必要に応じてオーバーライド
    protected void logTransaction(String authCode) {
        System.out.println("取引ID: " + transactionId + " 承認コード: " + authCode);
    }
    
    protected void logError() {
        System.err.println("決済エラー[" + transactionId + "]: " + errorMessage);
    }
    
    // エラーメッセージを設定するヘルパーメソッド（子クラスから利用可能）
    protected void setErrorMessage(String message) {
        this.errorMessage = message;
    }
}
```

#### テンプレートメソッドパターンの重要なポイント

1. 共通フローの定義： `processPayment()`メソッドで決済処理の標準的な流れを定義
2. 拡張ポイントの明確化： 子クラスがオーバーライド可能なメソッドでカスタマイズポイントを提供
3. 不変性の保証： `final`キーワードでテンプレートメソッドの改変を防止
4. デフォルト実装： 基本的な動作を提供しつつ、特殊な処理が必要な場合は子クラスで特化した実装を可能にする

#### 具体的な決済処理の実装例

<span class="listing-number">**サンプルコード5-2**</span>

```java
// クレジットカード決済の実装
public class CreditCardPayment extends PaymentProcessor {
    private String cardNumber;
    private String cvv;
    
    public CreditCardPayment(String transactionId, BigDecimal amount, 
                           String currency, String cardNumber, String cvv) {
        super(transactionId, amount, currency);
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }
    
    @Override
    protected boolean validatePaymentSpecific() {
        // クレジットカード固有の検証
        if (cardNumber == null || cardNumber.length() != 16) {
            setErrorMessage("無効なカード番号です");
            return false;
        }
        if (cvv == null || cvv.length() != 3) {
            setErrorMessage("無効なCVVです");
            return false;
        }
        return true;
    }
    
    @Override
    protected String callExternalAPI() {
        // クレジットカード決済API呼び出しのシミュレーション
        System.out.println("クレジットカード決済APIを呼び出し中...");
        
        // 実際の実装では外部APIを呼び出す
        // ここではシミュレーションとして、まれにエラーが発生する例を示す
        if (Math.random() < 0.1) {  // 10%の確率でエラー
            setErrorMessage("クレジットカード決済が拒否されました");
            return null;
        }
        
        return "CC-" + System.currentTimeMillis();
    }
}

// PayPal決済の実装
public class PayPalPayment extends PaymentProcessor {
    private String email;
    private String password;
    
    public PayPalPayment(String transactionId, BigDecimal amount, 
                        String currency, String email, String password) {
        super(transactionId, amount, currency);
        this.email = email;
        this.password = password;
    }
    
    @Override
    protected boolean validatePaymentSpecific() {
        // PayPal固有の検証
        if (email == null || !email.contains("@")) {
            setErrorMessage("無効なメールアドレスです");
            return false;
        }
        if (password == null || password.length() < 8) {
            setErrorMessage("パスワードが短すぎます");
            return false;
        }
        return true;
    }
    
    @Override
    protected String callExternalAPI() {
        // PayPal API呼び出しのシミュレーション
        System.out.println("PayPal APIを呼び出し中...");
        
        // 実際の実装では外部APIを呼び出す
        // エラーが発生した場合はnullを返す
        if (email.equals("error@example.com")) {  // テスト用のエラー条件
            setErrorMessage("PayPalアカウントが一時的に利用できません");
            return null;
        }
        
        return "PP-" + System.currentTimeMillis();
    }
    
    @Override
    protected void logTransaction(String authCode) {
        // PayPalでは詳細なログを記録
        super.logTransaction(authCode);
        System.out.println("PayPalアカウント: " + email);
    }
}
```

このアプローチでは、`PaymentProcessor`は抽象クラスではなく具象クラスとして実装されており、すべてのメソッドにデフォルト実装が提供されています。子クラスはクレジットカード検証、PayPal認証、銀行口座確認などの特有の処理が必要な場合に、これらのメソッドをオーバーライドして独自の振る舞いを実装できます。

#### 例外を使わない決済処理の使用例

<span class="listing-number">**サンプルコード5-3**</span>

```java
public class PaymentExample {
    public static void main(String[] args) {
        // クレジットカード決済の例
        CreditCardPayment ccPayment = new CreditCardPayment(
            "TXN001", 
            new BigDecimal("5000"), 
            "JPY",
            "1234567890123456",
            "123"
        );
        
        PaymentResult ccResult = ccPayment.processPayment();
        if (ccResult.isSuccess()) {
            System.out.println("決済成功: " + ccResult.getAuthorizationCode());
        } else {
            System.out.println("決済失敗: " + ccResult.getMessage());
        }
        
        // PayPal決済の例（エラーケース）
        PayPalPayment ppPayment = new PayPalPayment(
            "TXN002",
            new BigDecimal("10000"),
            "JPY",
            "invalid-email",  // 無効なメールアドレス
            "pass123"
        );
        
        PaymentResult ppResult = ppPayment.processPayment();
        if (ppResult.isSuccess()) {
            System.out.println("決済成功: " + ppResult.getAuthorizationCode());
        } else {
            System.out.println("決済失敗: " + ppResult.getMessage());
        }
    }
}
```

#### エラー処理の設計ポイント

1. boolean戻り値： 検証メソッドは成功/失敗をbooleanで返す
2. エラーメッセージの保持： errorMessageフィールドにエラー情報を格納
3. nullチェック： API呼び出しの結果がnullの場合をエラーとして扱う
4. PaymentResultクラス： 処理結果とエラー情報をカプセル化

この設計により、例外処理を学ぶ前でも、エラー状態を適切に管理できるテンプレートメソッドパターンを実装できます。

### 段階的リファクタリング：重複コードから継承へ

実際の開発では、最初から完璧な継承構造を設計することは難しく、重複コードを発見してから継承を導入することがよくあります。その過程を段階的に見てみましょう。

#### ステップ1：重複コードの発見

実際の開発現場でよく見られる、独立して作成されたクラス間でのコード重複を示します。

<span class="listing-number">**サンプルコード5-4**</span>

```java
public class Car {
    private String model;     // ①
    private String color;     // ①
    private int speed;        // ①
    
    public void start() {     // ②
        System.out.println(model + " のエンジンを始動");
    }
    
    public void accelerate() {  // ③
        speed += 10;
        System.out.println(model + " が加速: " + speed + "km/h");
    }
    
    public void brake() {       // ④
        speed = Math.max(0, speed - 10);
        System.out.println(model + " が減速: " + speed + "km/h");
    }
}

public class Truck {
    private String model;       // ①と重複
    private String color;       // ①と重複
    private int speed;          // ①と重複
    private int loadCapacity;   // ⑤
    
    public void start() {       // ②と完全重複
        System.out.println(model + " のエンジンを始動");
    }
    
    public void accelerate() {  // ③と完全重複
        speed += 10;
        System.out.println(model + " が加速: " + speed + "km/h");
    }
    
    public void brake() {       // ④と完全重複
        speed = Math.max(0, speed - 10);
        System.out.println(model + " が減速: " + speed + "km/h");
    }
    
    public void loadCargo(int weight) {  // ⑥
        System.out.println(weight + "kg の荷物を積載");
    }
}
```

#### 重複コードの分析

①　共通フィールド： model、color、speedという車両の基本属性がCarとTruckで重複して定義されている。

②　エンジン始動処理： start()メソッドの実装が両クラスで同一。

③　加速処理： accelerate()メソッドでspeedを10増加させる処理とメッセージ出力が重複。

④　ブレーキ処理： brake()メソッドでspeedを10減少させ、負の値を防ぐロジックが重複。

⑤　固有フィールド： loadCapacity（積載量）はトラック特有の属性として追加。

⑥　固有メソッド： loadCargo()はトラック特有の機能として実装。

public class Motorcycle {
    private String model;
    private String color;
    private int speed;
    
    // また同じメソッドの重複！
    public void start() {
        System.out.println(model + " のエンジンを始動");
    }
    
    public void accelerate() {
        speed += 10;
        System.out.println(model + " が加速: " + speed + "km/h");
    }
    
    public void brake() {
        speed = Math.max(0, speed - 10);
        System.out.println(model + " が減速: " + speed + "km/h");
    }
}
```

#### ステップ2：共通部分の抽出

<span class="listing-number">**サンプルコード5-5**</span>

```java
// 共通部分を親クラスとして抽出
public class Vehicle {
    protected String model;
    protected String color;
    protected int speed;
    
    public Vehicle(String model, String color) {
        this.model = model;
        this.color = color;
        this.speed = 0;
    }
    
    // 共通メソッドを親クラスに移動
    public void start() {
        System.out.println(model + " のエンジンを始動");
    }
    
    public void accelerate() {
        speed += 10;
        System.out.println(model + " が加速: " + speed + "km/h");
    }
    
    public void brake() {
        speed = Math.max(0, speed - 10);
        System.out.println(model + " が減速: " + speed + "km/h");
    }
    
    // ゲッターとセッター
    public String getModel() { return model; }
    public int getSpeed() { return speed; }
}
```

#### ステップ3：子クラスの再実装

<span class="listing-number">**サンプルコード5-6**</span>

```java
// リファクタリング後：重複が除去された
public class Car extends Vehicle {
    public Car(String model, String color) {
        super(model, color);
    }
    
    // Car固有の機能があれば追加
    public void openTrunk() {
        System.out.println(model + " のトランクを開く");
    }
}

public class Truck extends Vehicle {
    private int loadCapacity;
    
    public Truck(String model, String color, int loadCapacity) {
        super(model, color);
        this.loadCapacity = loadCapacity;
    }
    
    // accelerateメソッドをオーバーライド（重い車両は加速が遅い）
    @Override
    public void accelerate() {
        speed += 5; // トラックは加速が遅い
        System.out.println(model + " がゆっくり加速: " + speed + "km/h");
    }
    
    public void loadCargo(int weight) {
        if (weight <= loadCapacity) {
            System.out.println(weight + "kg の荷物を積載");
        } else {
            System.out.println("積載量オーバー！");
        }
    }
}

public class Motorcycle extends Vehicle {
    public Motorcycle(String model, String color) {
        super(model, color);  // ①
    }
    
    @Override  // ②
    public void accelerate() {
        speed += 20;  // ③
        System.out.println(model + " が素早く加速: " + speed + "km/h");
    }
    
    public void wheelie() {  // ④
        System.out.println(model + " がウィリー！");
    }
}
```

#### オーバーライドと特化機能の実装

①　親クラスのコンストラクタ呼び出し： super()により、親クラスの初期化処理を適切に実行

②　オーバーライドアノテーション： @Overrideによりコンパイラが親クラスのメソッドを正しくオーバーライドしていることを検証

③　特化した振る舞い： 親クラスではspeed += 10だが、バイクの特性として20増加するよう変更

④　固有メソッドの追加： wheelie()はバイク特有の機能として、親クラスには存在しないメソッドを追加
```

#### リファクタリングの効果

継承を導入したリファクタリングによって、コードの重複を削減できます。まず、コードの重複が除去されることで、保守性が向上します。同じコードを複数の場所に書く必要がなくなるため、バグの発生率が減り、修正も簡単になります。共通機能の変更が1箇所で済むことも大きな利点です。親クラスを修正するだけで、すべての子クラスに変更が反映されます。

また、各車両の特性をオーバーライドで表現できることで、柔軟な設計が可能になります。共通のインターフェイスを保ちながら、各子クラスが独自の実装を持つことができます。さらに、新しい車両タイプの追加が容易になります。親クラスを継承し、必要な部分だけをカスタマイズすることで、短時間で新しい機能を実装できます。このように、継承はソフトウェアの拡張性と保守性に寄与する技術の一つです。ただし、継承階層が3層以上になるとコードの理解が難しくなるため、コンポジションやインターフェイスの活用も検討すべきです。

### 継承の書き方：`extends`

Javaで継承を行うには、子クラスの宣言時に`extends`キーワードを使います。

#### 継承の基本構文と継承される要素

<span class="listing-number">**サンプルコード5-7**</span>

```java
public class Character {  // 親クラス（スーパークラス）
    String name;         // ①
    int hp;              // ①

    void attack() {      // ②
        System.out.println(this.name + "の攻撃！");
    }
}

public class Hero extends Character {  // ③
    void specialMove() {  // ④
        System.out.println(this.name + "の必殺技！");
    }
}

public class Wizard extends Character {  // ③
    int mp;  // ⑤

    void castSpell() {  // ④
        System.out.println(this.name + "は魔法を唱えた！");
    }
}
```

#### 継承の仕組みと効果

①　フィールドの継承： name、hpというフィールドは自動的にすべての子クラスに引き継がれる。

②　メソッドの継承： attack()メソッドも同様に、すべての子クラスで利用可能になる。

③　extends宣言： `extends Character`により、HeroとWizardはCharacterクラスのすべての非privateメンバを継承。

④　固有メソッドの追加： 各子クラスは継承した機能に加えて、独自のメソッドを定義可能。

⑤　固有フィールドの追加： Wizardクラスは継承したフィールドに加えて、mp（マジックポイント）を独自に持つ。

### is-a関係

継承は、クラス間に「is-a関係」（〜は〜の一種である）が成り立つ場合に使うのが適切です。

- 「勇者（Hero） is aキャラクタ(Character)」
- 「魔法使い（Wizard） is aキャラクタ(Character)」

このような関係が成り立つ場合、継承の利用を検討します。一方、「車（Car） has aエンジン(Engine)」のような「has-a関係」の場合は、継承ではなく、フィールドとして持つ（コンポジション）方が適切です。

### 継承の誤用例：よくある間違い

継承を誤用すると、論理的に破綻したコードになってしまいます。実際の開発現場でよく見られる誤用パターンをいくつか見てみましょう。

#### 誤用例1：スタックがArrayListを継承

<span class="listing-number">**サンプルコード5-8**</span>

```java
// 悪い例：実装の詳細を継承してしまう
public class MyStack<E> extends ArrayList<E> {
    public void push(E item) {
        add(item);
    }
    
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return remove(size() - 1);
    }
    
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return get(size() - 1);
    }
}

// 問題点が露呈する使用例
public class StackProblem {
    public static void main(String[] args) {
        MyStack<String> stack = new MyStack<>();
        stack.push("first");
        stack.push("second");
        stack.push("third");
        
        // ArrayListのメソッドが露出してしまう
        stack.add(1, "WRONG!"); // スタックの途中に要素を挿入できてしまう
        stack.remove(0); // スタックの底から要素を削除できてしまう
        stack.clear(); // スタック全体をクリアできてしまう
        
        // スタックの約束（LIFO）が破られる
    }
}
```

#### 解決策：コンポジションを使用

<span class="listing-number">**サンプルコード5-9**</span>

```java
// 良い例：内部実装を隠蔽
public class MyStack<E> {
    private final ArrayList<E> elements = new ArrayList<>(); // privateで隠蔽
    
    public void push(E item) {
        elements.add(item);
    }
    
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.remove(elements.size() - 1);
    }
    
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.get(elements.size() - 1);
    }
    
    public boolean isEmpty() {
        return elements.isEmpty();
    }
    
    public int size() {
        return elements.size();
    }
    // ArrayListの他のメソッドは公開されない
}
```

このMyStackクラスは、コンポジションを使用してArrayListを内部に保持し、スタックに必要な機能のみを公開しています。これにより、ArrayListの不適切なメソッド（add、removeなど）が外部に公開されることを防いでいます。

#### 誤用例2：鳥の階層での問題

以下の例は、継承設計の一般的な落とし穴である「すべてのサブクラスが親クラスの振る舞いを持つ」という誤った仮定を示しています。この設計はリスコフ置換原則に違反します。

<span class="listing-number">**サンプルコード5-10**</span>

```java
// 悪い例：すべての鳥が飛べるという誤った前提
public class Bird {
    protected String name;
    
    public Bird(String name) {
        this.name = name;
    }
    
    public void fly() {
        System.out.println(name + " が飛んでいる");
    }
}

public class Eagle extends Bird {
    public Eagle(String name) {
        super(name);
    }
    // flyメソッドを適切に継承
}

public class Penguin extends Bird {
    public Penguin(String name) {
        super(name);
    }
    
    @Override
    public void fly() {
        // ペンギンは飛べない！
        throw new UnsupportedOperationException("ペンギンは飛べません");
    }
}

// 使用時の問題
public class BirdPark {
    public static void makeBirdsFly(List<Bird> birds) {
        for (Bird bird : birds) {
            bird.fly(); // ペンギンで例外が発生！
        }
    }
}
```

この問題は、すべての鳥が飛べるという誤った仮定に基づいた継承設計の典型的な例です。ペンギンは鳥ですが飛べないため、`fly()`メソッドで例外を投げることになり、リスコフの置換原則に違反しています。

この問題を適切に解決するためには、継承階層を見直し、共通の振る舞いのみを親クラスに定義する必要があります。より良い解決策として、インターフェイスを使用した設計がありますが、これについては第7章「抽象クラスとインターフェイス」で詳しく説明します。

> 注意: 継承を使用する際は、「is-a」の関係だけでなく、すべての子クラスが親クラスの振る舞いを適切に実装できるかを慎重に検討する必要があります。

#### 誤用例3：正方形と長方形の問題

有名な例として「正方形と長方形」の問題も見てみましょう。

<span class="listing-number">**サンプルコード5-11**</span>

```java
// 問題のあるコード：数学的には正方形は長方形の一種だが...
public class Rectangle {
    protected int width;
    protected int height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}

// 正方形を長方形として継承すると問題が発生
public class Square extends Rectangle {
    public Square(int size) {
        super(size, size);
    }
    
    // 正方形では幅と高さは常に同じでなければならない
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // 高さも同じ値に！
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height; // 幅も同じ値に！
        this.height = height;
    }
}

// 使用例で問題が明らかに
public class GeometryTest {
    public static void main(String[] args) {
        Rectangle rect = new Square(5);
        
        // 長方形として扱うコード
        rect.setWidth(10);  // 幅を10に
        rect.setHeight(5);  // 高さを5に
        
        // 期待値：10 × 5 = 50
        // 実際の結果：5 × 5 = 25（最後のsetHeightで幅も5になってしまう）
        System.out.println("面積: " + rect.getArea()); // 25が出力される
    }
}
```

この例はリスコフの置換原則に違反しています。子クラスは親クラスと置き換え可能であることが大切ですが、`Square`は`Rectangle`の期待される振る舞いを破壊してしまっています。

この問題の根本的な原因は、数学的な関係（正方形は長方形の一種）とオブジェクト指向の継承関係が必ずしも一致しないことにあります。継承は「振る舞いの継承」であり、単なる概念的な関係ではありません。

この問題を解決する方法として、継承ではなくインターフェイスやコンポジションを使用する設計があります。これらの高度な設計手法については、第7章「抽象クラスとインターフェイス」で詳しく解説します。

> 重要: 継承を使用する際は、親クラスの契約（期待される振る舞い）を子クラスが満たせるかを確認することが重要です。

### そのほかの継承の誤用パターン

継承の誤用は「正方形と長方形」以外にも多くのパターンがあります。以下によくある間違いを示します。

#### 誤用例1：実装の都合だけで継承を使う

<span class="listing-number">**サンプルコード5-12**</span>

```java
// 悪い例：Stackを継承したMyStack
public class MyStack<E> extends ArrayList<E> {
    public void push(E item) {
        add(item);
    }
    
    public E pop() {
        return remove(size() - 1);
    }
    
    public E peek() {
        return get(size() - 1);
    }
}

// 問題：ArrayListのすべてのメソッドが公開されてしまう
MyStack<String> stack = new MyStack<>();
stack.push("A");
stack.push("B");
stack.add(0, "C");  // スタックの途中に挿入できてしまう！
stack.remove(1);    // スタックの途中から削除できてしまう！
```

この例では、継承を使用したためにMyStackがArrayListのすべてのメソッドを継承し、スタックの原則を破る不正な操作が可能になってしまいます。

#### 改善策：コンポジション（委譲）を使う

<span class="listing-number">**サンプルコード5-13**</span>

```java
// 良い例：ArrayListを内部で使用
public class MyStack<E> {
    private ArrayList<E> list = new ArrayList<>();
    
    public void push(E item) {
        list.add(item);
    }
    
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return list.remove(list.size() - 1);
    }
    
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return list.get(list.size() - 1);
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public int size() {
        return list.size();
    }
}
```

#### 誤用例2：多重継承の代替として無理な継承

Javaは多重継承をサポートしないため、複数の能力を持つオブジェクトを表現するために無理な継承階層を作ることがあります。これは設計の柔軟性を損ないます。

<span class="listing-number">**サンプルコード5-14**</span>

```java
// 悪い例：「飛べる鳥」と「泳げる鳥」を無理に継承で表現
public class Bird {
    public void eat() { /* ... */ }
    public void sleep() { /* ... */ }
}

public class FlyingBird extends Bird {
    public void fly() { /* ... */ }
}

public class SwimmingBird extends Bird {
    public void swim() { /* ... */ }
}

// 問題：ペンギンは飛べないが泳げる、鴨は飛べて泳げる
// どちらを継承すればよい？
```

この問題は、Javaが単一継承しかサポートしないことによる制約を示しています。複数の能力を持つオブジェクトを表現しようとすると、継承階層が複雑になり、設計の柔軟性が失われます。

例えば、鴨（Duck）は飛ぶこともできるし泳ぐこともできますが、`FlyingBird`と`SwimmingBird`のどちらか一方しか継承できません。結果として、どちらかの能力を諦めるか、複雑な継承階層を作ることになってしまいます。

この問題を解決する最も効果的な方法は、インターフェイスを使用して能力を表現することです。インターフェイスを使えば、クラスは必要な能力を選択的に実装でき、継承階層をシンプルに保つことができます。この設計手法については、第7章「抽象クラスとインターフェイス」で詳しく説明します。

> ポイント: 継承は強力な機能ですが、すべての問題を解決できるわけではありません。特に複数の異なる能力を持つオブジェクトを表現する場合は、継承以外の設計手法を検討する必要があります。

### 継承の実践例

RPGゲームのキャラクターシステムを例に、継承を実際に使用した実装例です。親クラスのフィールドやメソッドを子クラスで活用し、独自の機能を追加する方法を示しています。

<span class="listing-number">**サンプルコード5-15**</span>

```java
// Main.java
public class Main {
    public static void main(String[] args) {
        Hero hero = new Hero();
        hero.name = "勇者"; // 親クラスのフィールドを利用
        hero.hp = 100;

        Wizard wizard = new Wizard();
        wizard.name = "魔法使い";
        wizard.hp = 70;
        wizard.mp = 50; // 子クラス独自のフィールド

        hero.attack();       // 親クラスのメソッドを利用
        hero.specialMove();  // 子クラス独自のメソッド

        wizard.attack();     // 親クラスのメソッドを利用
        wizard.castSpell();  // 子クラス独自のメソッド
    }
}
```

## メソッドのオーバーライド

継承の強力な機能の1つが、メソッドのオーバーライド (Method Overriding) です。

オーバーライドとは、親クラスで定義されたメソッドを、子クラスで再定義（上書き）することです。これにより、子クラスは親クラスの基本的な振る舞いを引き継ぎつつ、自身の特性に合わせた具体的な振る舞いを実装できます。

たとえば、`Character`クラスの`attack()`メソッドは「〜の攻撃！」と表示するだけですが、「勇者」は剣で攻撃し、「魔法使い」は杖で攻撃するなど、職業によって攻撃方法は異なります。これをオーバーライドで表現します。

### オーバーライドのルール

メソッドオーバーライドを正しく行うためには、いくつかの大切なルールを守ることがポイントです。まず最も基本的なルールとして、メソッド名、引数の型・数・順序が親クラスのメソッドと一致していることが大切です。このシグネチャ（メソッド名とパラメータリスト）が一致しない場合、それはオーバーライドではなく、単なる新しいメソッドの定義とみなされます。

次に、戻り値の型についても制約があります。基本的には、親クラスのメソッドと同じ型を返す必要があります。ただし、Java 5以降では共変戻り値型（covariant return type）がサポートされ、より具体的な（サブクラスの）型を返すことも許可されています。これにより、型安全性を保ちながらより精密な設計が可能になります。

さらに、アクセス修飾子に関する大切なルールがあります。オーバーライドするメソッドのアクセス修飾子は、親クラスのメソッドよりも制限を緩くすることしかできません。たとえば、親クラスのメソッドが`protected`である場合、子クラスでは`protected`のままにするか、`public`に変更することはできますが、`private`に変更することはできません。この制約は、ポリモーフィズムの原則を守り、リスコフの置換原則を保証するためのポイントです。

### `@Override`アノテーション

オーバーライドを行う際は、メソッドの直前に`@Override`というアノテーションを付けることが強く推奨されます。

これは、コンパイラに対して「このメソッドは親クラスのメソッドをオーバーライドする意図で書いています」と伝える目印です。もし、タイプミスなどでオーバーライドのルールを満たせていない場合、コンパイラがエラーを検知してくれます。

### `super`キーワード

子クラスのオーバーライドしたメソッド内から、親クラスの同名メソッドを呼び出したい場合があります。その際に使うのが`super`キーワードです。

`super.メソッド名()`とすることで、親クラスのメソッドを呼びだすことができます。これにより、親クラスの共通処理を活かしつつ、子クラス独自の処理を追加できます。

#### 実践例：`attack`メソッドのオーバーライド

<span class="listing-number">**サンプルコード5-16**</span>

```java
// 親クラス
public class Character {
    String name;
    int hp;

    public Character(String name, int hp) {
        this.name = name;
        this.hp = hp;
    }

    public void attack() {
        System.out.println(this.name + "の攻撃！");
    }
}

// 子クラス
public class Wizard extends Character {
    int mp;

    public Wizard(String name, int hp, int mp) {
        // super()で親クラスのコンストラクタを呼び出す
        super(name, hp); 
        this.mp = mp;
    }

    // attackメソッドをオーバーライド
    @Override
    public void attack() {
        System.out.println(this.name + "は杖で殴った！");
    }

    public void castSpell() {
        System.out.println(this.name + "は魔法を唱えた！");
    }
}

// 子クラス
public class Knight extends Character {
    public Knight(String name, int hp) {
        super(name, hp);
    }

    // attackメソッドをオーバーライドし、親の処理も利用する
    @Override
    public void attack() {
        super.attack(); // 親クラスのattack()を呼び出す
        System.out.println("追加で剣を振るった！"); // 子クラス独自の処理を追加
    }
}
```

### コンストラクタと`super()`

子クラスのコンストラクタでは、その処理の一番初めに、親クラスのコンストラクタを呼びだす必要があります。これは`super()`を使って行います。

- もし、子クラスのコンストラクタで明示的に`super()`を呼び出さない場合、コンパイラは自動的に親クラスの引数なしのコンストラクタ (`super()`) を呼びだすコードを挿入しる
- 親クラスに引数なしのコンストラクタが存在しない場合は、子クラスのコンストラクタで必ず明示的に`super(...)`を呼び出さなければならず、さもないとコンパイルエラーになりる

## ポリモーフィズム（多態性）

ポリモーフィズム (Polymorphism) は、ギリシャ語で「多くの形を持つ」という意味で、プログラミングにおける柔軟性を実現する概念です。オブジェクト指向では継承とオーバーライドを通じて実現されますが、関数型プログラミングではパラメトリックポリモーフィズムやアドホックポリモーフィズムなど、異なる形で実現されます。

ポリモーフィズムとは、同じ型の変数や同じメソッド呼び出しが、実行時のオブジェクトの種類によって異なる振る舞いをする性質を指します。

継承とオーバーライドは、このポリモーフィズムを実現するための土台となります。

### 親クラスの型で子クラスのインスタンスを扱う

Javaでは、親クラス型の変数に、その子クラスのインスタンスを代入できます。これをアップキャストと呼びます。

<span class="listing-number">**サンプルコード5-17**</span>

```java
// 親クラス型の変数に、子クラスのインスタンスを代入
Character chara1 = new Hero("勇者", 100);
Character chara2 = new Wizard("魔法使い", 70, 50);
Character chara3 = new Knight("騎士", 120);
```

このとき、変数`chara1`, `chara2`, `chara3`はすべて`Character`型として扱われます。しかし、それぞれの変数が実際に指し示しているオブジェクトの実体は`Hero`, `Wizard`, `Knight`と異なります。

### 同じ呼び出しで、異なる振る舞いを実現する

ここからがポリモーフィズムの真骨頂です。これらの`Character`型の変数に対して`attack()`メソッドを呼びだすと、何が起こるでしょうか。

<span class="listing-number">**サンプルコード5-18**</span>

```java
chara1.attack(); // 実行結果: 勇者の攻撃！
chara2.attack(); // 実行結果: 魔法使いは杖で殴った！
chara3.attack(); // 実行結果: 騎士の攻撃！
                 //         追加で剣を振るった！
```

`chara2.attack()`という同じ呼び出し方にもかかわらず、Javaの実行環境は`chara2`が実際に`Wizard`オブジェクトを指していることを認識し、`Wizard`クラスでオーバーライドされた`attack()`メソッドを自動的に呼び出します。これがポリモーフィズムです。

### ポリモーフィズムの利点

この性質を利用すると、非常に柔軟で拡張性の高いプログラムを書くことができます。たとえば、さまざまなキャラクタをまとめて管理する配列を考えてみましょう。

<span class="listing-number">**サンプルコード5-19**</span>

```java
public class GameParty {
    public static void main(String[] args) {
        // 親クラスの配列に、様々な子クラスのインスタンスを格納できる
        Character[] party = new Character[3];
        party[0] = new Hero("勇者", 100);
        party[1] = new Wizard("魔法使い", 70, 50);
        party[2] = new Knight("騎士", 120);

        // パーティ全員で一斉攻撃！
        for (Character member : party) {
            // member変数の型はCharacterだが、実行時には
            // 実際のインスタンスのattack()が呼び出される
            member.attack(); 
        }
    }
}
```

この`for`ループの中では、`member`が`Hero`なのか`Wizard`なのかを一切気にする必要がありません。ただ`attack()`を呼びだすだけで、各キャラクタは自身の職業に合った攻撃を自動的に行ってくれます。

もし将来、「忍者 `Ninja`」という新しい職業クラスを追加したくなっても、`GameParty`クラスのコードは一切変更する必要がありません。`Ninja`クラスを作成し、`Character`を継承して`attack()`をオーバーライドし、`party`配列に追加するだけで、新しいキャラクタも問題なく動作します。これがポリモーフィズムがもたらす拡張性です。

### ポリモーフィズムのBefore/After比較

ポリモーフィズムを使わない場合と使った場合の違いを、実際のコードで比較してみましょう。

#### Before：ポリモーフィズムを使わない場合

<span class="listing-number">**サンプルコード5-20**</span>

```java
// 型ごとに別々の処理を書く必要がある
public class GamePartyBefore {
    public static void main(String[] args) {
        Hero hero = new Hero("勇者", 100);
        Wizard wizard = new Wizard("魔法使い", 70, 50);
        Knight knight = new Knight("騎士", 120);
        
        // それぞれの型に応じた攻撃処理
        System.out.println("=== パーティ全員の攻撃（ポリモーフィズムなし）===");
        
        // Heroの攻撃
        hero.attack();
        
        // Wizardの攻撃
        wizard.attack();
        
        // Knightの攻撃
        knight.attack();
        
        // 新しいメンバーを追加するたびに、ここにコードを追加する必要がある
        // Archer archer = new Archer("弓使い", 80);
        // archer.attack();
    }
    
    // 全員の合計ダメージを計算する場合も型別処理が必要
    public static int calculateTotalDamage(Hero hero, Wizard wizard, Knight knight) {
        int totalDamage = 0;
        
        // 各型ごとに個別に処理
        if (hero != null) {
            totalDamage += 50; // Heroの攻撃力
        }
        if (wizard != null) {
            totalDamage += 30; // Wizardの攻撃力
        }
        if (knight != null) {
            totalDamage += 70; // Knightの攻撃力
        }
        
        // 新しい型が増えるたびに、このメソッドも修正が必要
        return totalDamage;
    }
}
```

#### After：ポリモーフィズムを使った場合

<span class="listing-number">**サンプルコード5-21**</span>

```java
// 統一的な処理で全ての型を扱える
public class GamePartyAfter {
    public static void main(String[] args) {
        // ポリモーフィズム：親クラスの型で管理
        Character[] party = {
            new Hero("勇者", 100),
            new Wizard("魔法使い", 70, 50),
            new Knight("騎士", 120)
            // 新しいメンバーを追加しても、以下のコードは変更不要
            // new Archer("弓使い", 80)
        };
        
        System.out.println("=== パーティ全員の攻撃（ポリモーフィズムあり）===");
        
        // 統一的な処理で全員を扱える
        for (Character member : party) {
            member.attack(); // 実際の型に応じた攻撃が自動的に呼ばれる
        }
    }
    
    // 合計ダメージ計算も拡張性が高い
    public static int calculateTotalDamage(Character[] party) {
        int totalDamage = 0;
        
        // 型を意識せずに処理できる
        for (Character member : party) {
            totalDamage += member.getAttackPower(); // 各キャラクタの攻撃力を取得
        }
        
        return totalDamage;
    }
    
    // 特定の条件での処理も簡潔に書ける
    public static void healLowHpMembers(Character[] party) {
        for (Character member : party) {
            if (member.getHp() < 30) {
                member.heal(20); // HPが低いメンバーを回復
                System.out.println(member.getName() + " を回復しました");
            }
        }
    }
}

// 親クラスに共通インターフェースを定義
class Character {
    protected String name;
    protected int hp;
    
    public int getAttackPower() {
        // デフォルトの攻撃力
        return 10;
    }
    
    public void heal(int amount) {
        this.hp += amount;
    }
    
    public String getName() { return name; }
    public int getHp() { return hp; }
}
```

#### ポリモーフィズムの利点まとめ

1. コードの簡潔性： 型別の条件分岐が不要になり、コードがシンプルに
2. 拡張性： 新しい型を追加してもクライアントコードの変更が不要
3. 保守性： 処理の追加・変更が容易
4. 再利用性： 汎用的なメソッドが書きやすい
5. 型安全性： コンパイル時に型チェックが行われる

### ポリモーフィズムの実践例：図形描画システム

より実践的な例として、図形描画システムを考えてみましょう。

#### Before：ポリモーフィズムを使わない場合

<span class="listing-number">**サンプルコード5-22**</span>

```java
// 図形の種類を列挙型で管理
enum ShapeType {
    CIRCLE, RECTANGLE, TRIANGLE
}

// すべての図形データを1つのクラスで管理（悪い設計）
class ShapeData {
    ShapeType type;
    // 円の属性
    double radius;
    // 長方形の属性
    double width, height;
    // 三角形の属性
    double base, triangleHeight;
    
    double calculateArea() {
        switch (type) {
            case CIRCLE:
                return Math.PI * radius * radius;
            case RECTANGLE:
                return width * height;
            case TRIANGLE:
                return 0.5 * base * triangleHeight;
            default:
                return 0;
        }
    }
    
    void draw() {
        switch (type) {
            case CIRCLE:
                System.out.println("円を描画：半径 = " + radius);
                break;
            case RECTANGLE:
                System.out.println("長方形を描画：幅 = " + width + ", 高さ = " + height);
                break;
            case TRIANGLE:
                System.out.println("三角形を描画：底辺 = " + base + ", 高さ = " + triangleHeight);
                break;
        }
    }
}

// 使用例
public class DrawingAppBefore {
    public static void main(String[] args) {
        ShapeData[] shapes = new ShapeData[3];
        
        // 円を作成
        shapes[0] = new ShapeData();
        shapes[0].type = ShapeType.CIRCLE;
        shapes[0].radius = 5.0;
        
        // 長方形を作成
        shapes[1] = new ShapeData();
        shapes[1].type = ShapeType.RECTANGLE;
        shapes[1].width = 10.0;
        shapes[1].height = 20.0;
        
        // 三角形を作成
        shapes[2] = new ShapeData();
        shapes[2].type = ShapeType.TRIANGLE;
        shapes[2].base = 15.0;
        shapes[2].triangleHeight = 8.0;
        
        // すべての図形を描画
        for (ShapeData shape : shapes) {
            shape.draw();
            System.out.println("面積: " + shape.calculateArea());
        }
    }
}
```

#### After：ポリモーフィズムを使った場合

<span class="listing-number">**サンプルコード5-23**</span>

```java
// 基底クラス
class Shape {
    double calculateArea() {
        // デフォルトの実装（面積0を返す）
        return 0.0;
    }
    
    void draw() {
        // デフォルトの実装
        System.out.println("基本的な図形を描画");
    }
}

// 円クラス
class Circle extends Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    void draw() {
        System.out.println("円を描画：半径 = " + radius);
    }
}

// 長方形クラス
class Rectangle extends Shape {
    private double width;
    private double height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    double calculateArea() {
        return width * height;
    }
    
    @Override
    void draw() {
        System.out.println("長方形を描画：幅 = " + width + ", 高さ = " + height);
    }
}

// 三角形クラス
class Triangle extends Shape {
    private double base;
    private double height;
    
    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }
    
    @Override
    double calculateArea() {
        return 0.5 * base * height;
    }
    
    @Override
    void draw() {
        System.out.println("三角形を描画：底辺 = " + base + ", 高さ = " + height);
    }
}

// 使用例
public class DrawingAppAfter {
    public static void main(String[] args) {
        Shape[] shapes = {
            new Circle(5.0),
            new Rectangle(10.0, 20.0),
            new Triangle(15.0, 8.0)
        };
        
        // すべての図形を描画（型を意識しない）
        for (Shape shape : shapes) {
            shape.draw();
            System.out.println("面積: " + shape.calculateArea());
        }
        
        // 新しい図形（五角形）を追加しても、このコードは変更不要
    }
}

// 新しい図形の追加が容易
class Pentagon extends Shape {
    private double side;
    
    public Pentagon(double side) {
        this.side = side;
    }
    
    @Override
    double calculateArea() {
        return 0.25 * Math.sqrt(5 * (5 + 2 * Math.sqrt(5))) * side * side;
    }
    
    @Override
    void draw() {
        System.out.println("五角形を描画：一辺 = " + side);
    }
}
```

#### ポリモーフィズムがもたらす設計上の利点

1. Open/Closed原則の実現： 拡張に対して開いており、修正に対して閉じている
2. 単一責任原則： 各図形クラスは自身の描画と面積計算のみに責任を持つ
3. 依存関係逆転の原則： 高レベルのコードが低レベルの詳細に依存しない
4. テストの容易性： 各図形クラスを独立してテスト可能
5. 並列開発： 複数の開発者が異なる図形クラスを同時に開発可能

## `instanceof`演算子とキャスト

親クラスの型でオブジェクトを扱っていると、そのオブジェクトがもともとどの具体的な子クラスのインスタンスだったかを知り、その子クラス独自の機能を使いたくなることがあります。

たとえば、`Character`型の変数`member`が、もし`Wizard`だったら`castSpell()`メソッドを呼びたい、という場合です。

```java
Character member = new Wizard("魔法使い", 70, 50);
// member.castSpell(); // コンパイルエラー！
```
これはコンパイルエラーになります。なぜなら、コンパイラは変数`member`を`Character`型としてしか認識しておらず、`Character`クラスには`castSpell()`メソッドが定義されていないからです。

### `instanceof`演算子：型の調査

`instanceof`演算子を使うと、あるオブジェクトが特定のクラス（またはそのサブクラス）のインスタンスであるかどうかを調べることができます。

`変数 instanceof クラス名` の形で使用し、結果は`boolean`値（`true`または`false`）で返されます。

### キャスト：型変換

オブジェクトが特定の子クラスのインスタンスであることが分かったら、その変数の型を一時的に子クラスの型に変換できます。これをダウンキャストと呼びます。

`(変換したい型)変数` のように記述します。

<span class="listing-number">**サンプルコード5-24**</span>

```java
public class GameParty {
    public static void main(String[] args) {
        Character[] party = {
            new Hero("勇者", 100),
            new Wizard("魔法使い", 70, 50),
            new Knight("騎士", 120)
        };

        for (Character member : party) {
            member.attack(); // これはポリモーフィズムでOK

            // もし、メンバーがWizardだったら、特別に魔法も使わせる
            if (member instanceof Wizard) {
                // memberをWizard型にダウンキャストする
                Wizard wizard = (Wizard) member; 
                wizard.castSpell();
            }
        }
    }
}
```

注意： `instanceof`でチェックせずにいきなりキャストしようとすると、もしオブジェクトがその型でなかった場合に`ClassCastException`という実行時エラーが発生します。必ず`instanceof`で確認してからキャストするのが安全です。

#### モダンJavaのパターンマッチング

Java 16から、`instanceof`とキャストをより簡潔に書ける「`instanceof`のパターンマッチング」が導入されました。

<span class="listing-number">**サンプルコード5-25**</span>

```java
// 従来の書き方
if (member instanceof Wizard) {
    Wizard wizard = (Wizard) member;
    wizard.castSpell();
}

// パターンマッチングを使った書き方 (Java 16以降)
if (member instanceof Wizard wizard) {
    // instanceofがtrueの場合、キャスト済みのwizard変数が使える
    wizard.castSpell();
}
```
この新しい書き方を使うと、より安全で読みやすいコードになります。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter05/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

- 継承を使ったクラス階層の設計と実装
- メソッドのオーバーライドによる振る舞いの特殊化
- ポリモーフィズムを活用した柔軟なプログラム設計
- instanceof演算子とキャストの適切な使用

### 基礎課題のヒント

PayrollSystem（給与計算システム）
- Employeeクラスを親クラスとして設計
- 正社員、アルバイト、派遣社員などの子クラスを実装
- calculateSalary()メソッドをオーバーライドして各雇用形態に応じた給与計算を実装
- 配列を使って異なる型の従業員を一括管理

### 実装のポイント

1. 親クラスの設計： 共通属性（名前、社員番号）を定義
2. 抽象メソッドの活用： 給与計算ロジックは子クラスで実装
3. ポリモーフィズムの実践： Employee[]配列で全従業員を管理
4. 型判定の活用： 必要に応じてinstanceofで特定の処理を実行

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

次のステップ： 基礎課題が完了したら、第6章「不変性とfinalキーワード」に進みましょう。

## 例外処理の基礎（補足）

本章では継承とポリモーフィズムに焦点を当てましたが、実際のプログラムでは例外処理も重要です。ここでは、基本的な例外処理について簡単に触れておきます。詳細は第14章で学習します。

### 例外とは

例外（Exception）は、プログラム実行中に発生する異常な状況を表すオブジェクトです。Javaでは、これらの状況を適切に処理することで、プログラムの安定性を向上させます。

### 基本的なtry-catch構文

```java
public class BasicExceptionExample {
    public static void main(String[] args) {
        try {
            // 例外が発生する可能性のあるコード
            int result = 10 / 0;  // ArithmeticException発生
            System.out.println("結果: " + result);
        } catch (ArithmeticException e) {
            // 例外が発生した場合の処理
            System.out.println("エラー: ゼロで除算はできません");
            System.out.println("詳細: " + e.getMessage());
        }
        
        System.out.println("プログラムは継続します");
    }
}
```

### よく遭遇する例外

1. NullPointerException: nullオブジェクトのメソッド呼び出し
```java
String str = null;
// str.length();  // NullPointerException発生
```

2. ArrayIndexOutOfBoundsException: 配列の範囲外アクセス
```java
int[] numbers = {1, 2, 3};
// numbers[5];  // ArrayIndexOutOfBoundsException発生
```

3. ClassCastException: 不適切な型変換
```java
Object obj = "文字列";
// Integer num = (Integer) obj;  // ClassCastException発生
```

### 継承における例外処理の注意点

メソッドをオーバーライドする際、親クラスのメソッドが投げる例外より広い例外を投げることはできません。

```java
class Parent {
    void doSomething() throws IOException {
        // 処理
    }
}

class Child extends Parent {
    @Override
    void doSomething() throws IOException {  // OK
        // IOException以下の例外のみ宣言可能
    }
    
    // @Override
    // void doSomething() throws Exception {  // コンパイルエラー
    //     // より広い例外は宣言できない
    // }
}
```

この基本的な例外処理の知識があれば、本書の残りの章でもより実践的なコードを理解できるでしょう。第14章では、例外処理の詳細な仕組みやベストプラクティスを学習します。

※ 本章の高度な内容については、付録B05「仮想メソッドテーブル」を参照してください。

## よくあるエラーと対処法

継承とポリモーフィズムを学習する際によく遭遇するエラーとその対処法を以下にまとめます。

### コンストラクタ関連のエラー

#### super()の呼び出し忘れ

#### エラーメッセージ
```
error: constructor Parent in class Parent cannot be applied to given types
```

#### 原因と対処

```java
// エラー例
class Parent {
    public Parent(String name) {
        // パラメータ付きコンストラクタのみ定義
    }
}

class Child extends Parent {
    public Child() {  // エラー：親のデフォルトコンストラクタが存在しない
        // super(); は暗黙的に呼ばれるが、Parent()は存在しない
    }
}

// 修正版
class Child extends Parent {
    public Child() {
        super("default");  // 明示的に親のコンストラクタを呼び出す
    }
    
    public Child(String name) {
        super(name);  // パラメータを渡して親のコンストラクタを呼び出す
    }
}
```

### メソッドオーバーライド関連のエラー

#### アクセス修飾子の制限違反

#### エラーメッセージ
```
error: doSomething() in Child cannot override doSomething() in Parent; attempting to assign weaker access privileges
```

#### 原因と対処

```java
// エラー例
class Parent {
    public void doSomething() {  // public
    }
}

class Child extends Parent {
    private void doSomething() {  // エラー：publicからprivateに制限を強化
    }
}

// 修正版
class Child extends Parent {
    @Override
    public void doSomething() {  // 同じかより緩いアクセス修飾子を使用
    }
}
```

#### 戻り値型の不一致

#### エラーメッセージ
```
error: doSomething() in Child cannot override doSomething() in Parent; return type String is not compatible with int
```

#### 原因と対処

```java
// エラー例
class Parent {
    public int doSomething() {
        return 0;
    }
}

class Child extends Parent {
    @Override
    public String doSomething() {  // エラー：戻り値型が異なる
        return "hello";
    }
}

// 修正版1：同じ戻り値型を使用
class Child extends Parent {
    @Override
    public int doSomething() {  // 同じ戻り値型
        return 42;
    }
}

// 修正版2：共変戻り値型を使用（参照型の場合）
class Parent {
    public Object getValue() {
        return new Object();
    }
}

class Child extends Parent {
    @Override
    public String getValue() {  // ObjectのサブクラスなのでOK
        return "hello";
    }
}
```

### キャスト関連のエラー

#### ClassCastException

#### エラーメッセージ
```
Exception in thread "main" java.lang.ClassCastException: Parent cannot be cast to Child
```

#### 原因と対処

```java
// エラー例
Parent p = new Parent();  // Parent型のインスタンス
Child c = (Child) p;      // 実行時エラー：ParentはChildではない

// 修正版：instanceof演算子を使用
Parent p = new Parent();
if (p instanceof Child) {
    Child c = (Child) p;  // 安全なキャスト
    // Child固有の処理
} else {
    System.out.println("pはChild型ではありません");
}

// より安全な方法：パターンマッチング（Java 14+）
if (p instanceof Child child) {
    // childは自動的にChild型として利用可能
    child.childSpecificMethod();
}
```

### メソッド呼び出し関連のエラー

#### メソッドが見つからない

#### エラーメッセージ
```
error: cannot find symbol - method childMethod()
```

#### 原因と対処

```java
// エラー例
Parent p = new Child();
p.childMethod();  // エラー：Parent型の変数からChild固有のメソッドは呼べない

// 修正版1：キャストして呼び出し
Parent p = new Child();
if (p instanceof Child) {
    ((Child) p).childMethod();  // キャストしてから呼び出し
}

// 修正版2：Child型の変数を使用
Child c = new Child();
c.childMethod();  // Child型なので直接呼び出し可能

// 修正版3：ポリモーフィズムを活用
// Parent側にabstractメソッドまたはインターフェイスを定義
abstract class Parent {
    public abstract void doSomething();  // 子クラスで実装必須
}

class Child extends Parent {
    @Override
    public void doSomething() {  // 実装
        // 処理
    }
}
```

### 抽象クラス・インターフェイス関連のエラー

#### 抽象クラスのインスタンス化

#### エラーメッセージ
```
error: Animal is abstract; cannot be instantiated
```

#### 原因と対処

```java
// エラー例
abstract class Animal {
    public abstract void makeSound();
}

Animal a = new Animal();  // エラー：抽象クラスはインスタンス化不可

// 修正版：具象クラスを作成
class Dog extends Animal {
    @Override
    public void makeSound() {
        System.out.println("ワンワン");
    }
}

Animal a = new Dog();  // OK：具象クラスのインスタンスを作成
```

#### 抽象メソッドの実装忘れ

#### エラーメッセージ
```
error: Dog is not abstract and does not override abstract method makeSound() in Animal
```

#### 原因と対処

```java
// エラー例
abstract class Animal {
    public abstract void makeSound();
}

class Dog extends Animal {
    // makeSound()の実装が不足
}

// 修正版：すべての抽象メソッドを実装
class Dog extends Animal {
    @Override
    public void makeSound() {  // 必須の実装
        System.out.println("ワンワン");
    }
}
```

### thisとsuperの混同

#### 一般的な間違い

```java
// 間違った使用例
class Parent {
    protected String name = "Parent";
}

class Child extends Parent {
    private String name = "Child";
    
    public void printNames() {
        System.out.println(name);        // Childのname
        System.out.println(this.name);   // 同じくChildのname
        System.out.println(super.name);  // Parentのname
    }
}

// 正しい理解での使用例
class Child extends Parent {
    private String name = "Child";
    
    public void printNames() {
        System.out.println("子クラス: " + this.name);   // "Child"
        System.out.println("親クラス: " + super.name);  // "Parent"
    }
    
    @Override
    public void someMethod() {
        super.someMethod();  // 親クラスのメソッドを呼び出し
        // 追加の処理
    }
}
```

### デバッグのコツ

1. エラーメッセージを注意深く読む
   - 行番号と具体的な問題を確認
   - 「cannot override」「cannot be cast」等のキーワードに注目

2. 継承階層を図式化する
   - どのクラスがどのクラスを継承しているか整理
   - どのメソッドがどこで定義・オーバーライドされているか確認

3. 段階的にテストする
   - 親クラス単体で動作確認
   - 子クラス単体で動作確認
   - 継承関係での動作確認

4. @Overrideアノテーションを活用
   - オーバーライドの意図を明確化
   - コンパイル時のチェックを強化

これらの対処法を参考に、継承とポリモーフィズムの学習を進めてください。