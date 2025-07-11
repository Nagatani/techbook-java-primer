# 第21章 発展課題: TDDとモックの活用

## 課題1: TDD（テスト駆動開発）の実践

### 背景
Red-Green-Refactorサイクルを実際に体験し、TDDの利点を理解します。

### 要件
以下の仕様を満たす`PasswordValidator`クラスをTDDで実装してください。

#### パスワードの要件
1. 8文字以上
2. 大文字を1つ以上含む
3. 小文字を1つ以上含む
4. 数字を1つ以上含む
5. 特殊文字（!@#$%^&*）を1つ以上含む

### 実装手順
1. **Red**: 失敗するテストを1つ書く
2. **Green**: テストを通す最小限のコードを書く
3. **Refactor**: コードを改善する
4. 1-3を繰り返して、すべての要件を満たす

### 提出物
- 各ステップでのコミット履歴
- 最終的なテストコードと実装コード
- TDDの体験レポート（各ステップで感じたこと）

---

## 課題2: 依存性注入とモックの活用

### 背景
外部サービスに依存するクラスのテスト方法を学びます。

### 要件
以下のインターフェースと実装クラスを作成し、モックを使ったテストを書いてください。

```java
// メール送信サービスのインターフェース
public interface EmailService {
    void sendEmail(String to, String subject, String body);
}

// ユーザー登録サービス
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    public UserRegistrationService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    
    public void registerUser(String username, String email, String password) {
        // 実装してください
        // 1. ユーザー名の重複チェック
        // 2. ユーザーの保存
        // 3. ウェルカムメールの送信
    }
}
```

### 実装すべきテスト
1. 正常な登録フロー
2. ユーザー名重複時の例外処理
3. メール送信の検証（送信内容の確認）

### 使用技術
- Mockitoを使用してモックオブジェクトを作成
- `verify()`でメソッド呼び出しを検証
- `when().thenReturn()`でモックの振る舞いを定義

---

## 課題3: パラメータ化テスト

### 背景
同じロジックに対して複数のテストデータでテストする効率的な方法を学びます。

### 要件
以下の`DateFormatter`クラスに対してパラメータ化テストを作成してください。

```java
public class DateFormatter {
    public String format(LocalDate date, String pattern) {
        // 実装してください
        // サポートするパターン:
        // - "yyyy-MM-dd": 2024-01-15
        // - "dd/MM/yyyy": 15/01/2024
        // - "MMM dd, yyyy": Jan 15, 2024
        // - "EEEE, MMMM dd, yyyy": Monday, January 15, 2024
    }
}
```

### 実装内容
1. `@ParameterizedTest`を使用
2. `@CsvSource`または`@MethodSource`でテストデータを提供
3. 各パターンに対して複数の日付でテスト

### 評価ポイント
- テストデータの網羅性
- エッジケース（月末、年末など）の考慮
- テストコードの保守性

---

## チャレンジ課題: 統合テストの作成

### 背景
複数のコンポーネントを組み合わせた統合テストを作成します。

### 要件
簡単なTODOアプリケーションのバックエンドを想定し、以下の統合テストを作成してください。

```java
// TODOアプリケーションのサービス層
public class TodoService {
    private final TodoRepository repository;
    private final NotificationService notificationService;
    private final UserService userService;
    
    // コンストラクタと実装を追加
    
    public Todo createTodo(String userId, String title, LocalDate dueDate) {
        // 1. ユーザーの存在確認
        // 2. TODOの作成と保存
        // 3. 期限が近い場合は通知を送信
        // 実装してください
    }
    
    public List<Todo> getUserTodos(String userId) {
        // 実装してください
    }
    
    public void completeTodo(String userId, Long todoId) {
        // 1. TODOの所有者確認
        // 2. 完了状態に更新
        // 3. 完了通知の送信
        // 実装してください
    }
}
```

### テストシナリオ
1. **エンドツーエンドフロー**: TODO作成→一覧取得→完了までの一連の流れ
2. **エラーシナリオ**: 存在しないユーザー、権限のないTODO操作など
3. **並行処理**: 複数ユーザーが同時にTODOを操作する場合

### 高度な要件
- H2インメモリデータベースを使用した実際のDB操作
- `@SpringBootTest`を使用（Spring使用の場合）
- テストフィクスチャの適切な管理

## 提出方法
1. 各課題のソースコードとテストコードを提出
2. テスト実行結果のスクリーンショット
3. 工夫した点や学んだことをまとめたレポート（500字程度）

## 評価基準
- テストの網羅性と品質
- モックの適切な使用
- テストコードの可読性と保守性
- TDDプロセスの理解と実践