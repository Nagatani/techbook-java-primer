<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 8/10
行範囲: 1480 - 1679
作成日時: 2025-08-03 02:32:41

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->


#### なぜこれが問題なのか

1. ビジネスロジックの分散
    + 残高操作のルールが使用側に散らばる
2. 不正な状態の可能性
    + 負の残高など、ビジネス的に不正な値を設定可能
3. 変更の困難さ
    + 残高操作のルールを変更する際、全使用箇所を修正必要
4. トランザクション管理の欠如
    + 操作履歴や監査ログを残せない
5. 並行処理の問題
    + 複数スレッドからの同時アクセスで不整合が発生

#### 解決方法：意味のあるメソッドの提供
<span class="listing-number">**サンプルコード4-29**</span>

```java
public class BankAccount {
    private double balance;
    
    public BankAccount(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("初期残高は0以上である必要です");
        }
        this.balance = initialBalance;
    }
    
    // 残高の取得は許可
    public double getBalance() { return balance; }
    
    // 直接設定は不可、代わりにビジネスロジックメソッドを提供
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は0より大きい必要があります");
        }
        this.balance += amount;
    }
    
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("出金額は0より大きい必要があります");
        }
        if (this.balance < amount) {
            return false;  // 残高不足
        }
        this.balance -= amount;
        return true;
    }
}
```

#### 解決策のメリット

1. ビジネスロジックの集約
    + 残高操作のルールが1箇所に集中
2. データ整合性の保証
    + 不正な状態になることを防げる
3. 変更の容易さ
    + ルール変更時の修正箇所が限定的
4. 拡張性
    + 履歴記録や通知機能を簡単に追加可能
5. テストの簡潔さ
    + ビジネスロジックを集中的にテスト可能

#### 解決策のデメリット

1. 柔軟性の低下
    + 特殊なケースへの対応が困難な場合がある
2. メソッド数の増加
    + 操作の種類が増えるとメソッドも増える
3. 学習コスト
    + 使用可能な操作を把握する必要がある
4. 過度な制限のリスク
    + 必要な操作まで制限してしまう可能性

#### getter/setter設計のベストプラクティス

1. getterの設計指針
   - 内部状態を公開してもよいかを慎重に検討
   - 必要なら防御的コピーを返す（コレクションなど）
   - 計算結果を返すメソッドも検討（getTotal()など）

2. setterの設計指針
   - 本当に外部から変更可能にすべきか検討
   - 検証ロジックを必ず含める
   - イミュータブルオブジェクトの使用も検討

3. 代替案の検討
   - ビルダーパターンでの初期化
   - ファクトリメソッドでの生成
   - コンストラクタでの完全な初期化

### 3. 防御的プログラミングの欠如

#### 問題の概要
外部から渡されたオブジェクトをそのまま保持したり、内部のオブジェクトをそのまま返したりすると、意図しない変更を許してしまう問題です。

#### 問題のあるコード例

<span class="listing-number">**サンプルコード4-30**</span>

```java
public class Team {
    private List<String> members;
    
    public Team(List<String> members) {
        this.members = members;  // 参照をそのまま保存
    }
    
    public List<String> getMembers() {
        return members;  // 内部リストを直接返す
    }
}

// 使用例での問題
List<String> originalList = new ArrayList<>();
originalList.add("Alice");
Team team = new Team(originalList);
originalList.add("Bob");  // Teamの内部状態が変更される！

List<String> teamMembers = team.getMembers();
teamMembers.clear();  // Teamの内部状態が破壊される！
```

#### なぜこれが問題なのか

1. カプセル化の破壊
    + 外部から内部状態を直接操作可能
2. 予期しない副作用
    + 他の箇所での変更が影響する
3. 不変条件の破壊
    + クラスの整合性が保てない
4. デバッグの困難さ
    + 変更箇所の特定が困難
5. 並行処理での問題
    + スレッドセーフでない

#### 解決方法：防御的コピー

<span class="listing-number">**サンプルコード4-31**</span>

```java
public class Team {
    private List<String> members;
    
    public Team(List<String> members) {
        // 防御的コピー（コンストラクタ）
        this.members = new ArrayList<>(members);
    }
    
    public List<String> getMembers() {
        // 防御的コピー（getter）
        return new ArrayList<>(members);
    }
    
    // 正しい方法でメンバーを追加
    public void addMember(String member) {
        if (member != null && !member.trim().isEmpty()) {
            members.add(member);
        }
    }
    
    // 正しい方法でメンバーを削除
    public boolean removeMember(String member) {
        return members.remove(member);
    }
}
```

#### 解決策のメリット

1. 完全なカプセル化
    + 内部状態が外部から保護される
2. 予測可能な動作
    + 外部の変更に影響されない
3. 不変条件の維持
    + クラスの整合性が保証される
4. デバッグの容易さ
    + 変更箇所が限定的
5. スレッドセーフ性の向上
    + 明示的な同期化と組み合わせ可能

#### 解決策のデメリット

1. パフォーマンスコスト
    + コピー処理のオーバーヘッド
2. メモリ使用量の増加
    + オブジェクトの複製によるメモリ消費
3. 実装の複雑さ
    + 深いコピーが必要な場合の実装が複雑
4. 一貫性の確保
    + すべての箇所で防御的コピーを忘れずに実装する必要

#### 防御的プログラミングのガイドライン

1. **コンストラクタでの防御**
   - 可変オブジェクトは必ずコピー
   - nullチェックと検証を実施
   - 不変オブジェクトの使用を検討


<!-- 
================
チャンク 8/10 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
