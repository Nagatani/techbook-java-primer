# 第13章 基本課題

## 🎯 学習目標
- Enum（列挙型）の基本概念と利点の理解
- 定数としてのEnumの適切な使用
- Enumのメソッドとフィールドの活用
- 状態マシンパターンの実装
- EnumとSwitch文の効果的な組み合わせ

## 📝 課題一覧

### 課題1: 基本的なEnum定義と活用
**ファイル名**: `DayOfWeek.java`, `Priority.java`, `BasicEnumTest.java`

基本的なEnumクラスを定義し、その特性を理解してください。

**要求仕様**:
- 曜日を表すEnum（メソッド付き）
- 優先度を表すEnum（数値付き）
- Enumの基本メソッド（values、valueOf、ordinal等）
- Switch文でのEnum活用
- Enumの比較とソート

**実行例**:
```
=== 基本的なEnum定義と活用 ===
曜日Enum:
今日: MONDAY
明日: TUESDAY
今日は平日です: true
今日は週末です: false

全曜日の表示:
MONDAY: 月曜日（平日）
TUESDAY: 火曜日（平日）
WEDNESDAY: 水曜日（平日）
THURSDAY: 木曜日（平日）
FRIDAY: 金曜日（平日）
SATURDAY: 土曜日（週末）
SUNDAY: 日曜日（週末）

優先度Enum:
緊急: URGENT（レベル: 1）
高: HIGH（レベル: 2）
中: MEDIUM（レベル: 3）
低: LOW（レベル: 4）

優先度ソート:
[URGENT(1), HIGH(2), MEDIUM(3), LOW(4)]

Switch文での活用:
URGENT → 即座に対応が必要です
HIGH → 今日中に対応してください
MEDIUM → 今週中に対応してください
LOW → 時間があるときに対応してください

Enum比較:
URGENT.compareTo(LOW): -3
URGENT < LOW: true（数値が小さいほう が優先度高）
```

**評価ポイント**:
- Enumの基本構文理解
- メソッドとフィールドの追加
- Switch文での適切な使用

---

### 課題2: 注文状態管理システム
**ファイル名**: `OrderStatus.java`, `Order.java`, `OrderStatusTest.java`

注文の状態をEnumで管理し、状態遷移を実装してください。

**要求仕様**:
- 注文状態を表すEnum（状態遷移メソッド付き）
- 各状態での可能なアクション定義
- 状態遷移の検証機能
- 状態履歴の管理
- ビジネスロジックの実装

**実行例**:
```
=== 注文状態管理システム ===
注文作成:
注文ID: ORD-001
初期状態: PENDING（注文受付中）

状態遷移テスト:
PENDING → CONFIRMED（注文確認済み）
CONFIRMED → PREPARING（準備中）
PREPARING → SHIPPED（発送済み）
SHIPPED → DELIVERED（配達完了）

各状態での可能なアクション:
PENDING: [確認, キャンセル]
CONFIRMED: [準備開始, キャンセル]
PREPARING: [発送, キャンセル]
SHIPPED: [配達完了]
DELIVERED: [返品受付]

無効な状態遷移テスト:
PENDING → SHIPPED: ✗ 無効な遷移です
DELIVERED → PENDING: ✗ 無効な遷移です

状態履歴:
2024-07-04 10:00:00 - PENDING
2024-07-04 10:30:00 - CONFIRMED
2024-07-04 14:00:00 - PREPARING
2024-07-04 17:00:00 - SHIPPED
2024-07-05 09:00:00 - DELIVERED

ビジネスルール:
キャンセル可能期間: PENDING, CONFIRMED, PREPARING
返品可能状態: DELIVERED
追跡可能状態: SHIPPED, DELIVERED
```

**評価ポイント**:
- 状態パターンの実装
- ビジネスロジックのEnum内実装
- 状態遷移の検証

---

### 課題3: 計算機の演算子Enum
**ファイル名**: `Operation.java`, `Calculator.java`, `OperationTest.java`

計算機の演算子をEnumで実装し、関数型インターフェイスと組み合わせてください。

**要求仕様**:
- 四則演算を表すEnum（関数実装付き）
- 関数型インターフェイスとの組み合わせ
- 演算子の優先度実装
- 複雑な計算式の処理
- 演算履歴の管理

**実行例**:
```
=== 計算機の演算子Enum ===
基本演算テスト:
10 + 5 = 15.0
10 - 5 = 5.0
10 * 5 = 50.0
10 / 5 = 2.0

演算子の優先度:
MULTIPLY: 2
DIVIDE: 2
ADD: 1
SUBTRACT: 1

複雑な計算（優先度考慮）:
式: 2 + 3 * 4
解析順序: [3 * 4 = 12, 2 + 12 = 14]
結果: 14.0

連続計算:
初期値: 10
+5 = 15.0
*2 = 30.0
-8 = 22.0
/2 = 11.0

演算履歴:
10.0 + 5.0 = 15.0
15.0 * 2.0 = 30.0
30.0 - 8.0 = 22.0
22.0 / 2.0 = 11.0

特殊ケース処理:
10 / 0 = エラー: ゼロ除算は実行できません
√-1 = エラー: 負の数の平方根は計算できません
```

**評価ポイント**:
- 関数型インターフェイスとの統合
- 複雑なビジネスロジックの実装
- エラーハンドリング

---

### 課題4: ゲーム設定のEnum活用
**ファイル名**: `Difficulty.java`, `GameMode.java`, `PowerUp.java`, `GameConfig.java`, `GameConfigTest.java`

ゲーム設定を管理するEnumシステムを実装してください。

**要求仕様**:
- 難易度レベルを表すEnum（パラメータ付き）
- ゲームモードを表すEnum（機能設定付き）
- パワーアップアイテムのEnum（効果実装付き）
- 設定の組み合わせ検証
- 動的な設定変更システム

**実行例**:
```
=== ゲーム設定のEnum活用 ===
難易度設定:
EASY: 敵HP×0.5, プレイヤーHP×2.0, 経験値×1.0
NORMAL: 敵HP×1.0, プレイヤーHP×1.0, 経験値×1.0
HARD: 敵HP×1.5, プレイヤーHP×0.7, 経験値×1.5
EXTREME: 敵HP×2.0, プレイヤーHP×0.5, 経験値×2.0

ゲームモード:
STORY: ストーリーモード（セーブ可能、チェックポイント有り）
SURVIVAL: サバイバルモード（セーブ不可、連続プレイ）
CHALLENGE: チャレンジモード（特殊ルール、リーダーボード）
MULTIPLAYER: マルチプレイヤー（オンライン、最大4人）

パワーアップアイテム:
SPEED_BOOST: 速度アップ（効果時間: 30秒）
DOUBLE_DAMAGE: ダメージ2倍（効果時間: 20秒）
INVINCIBLE: 無敵状態（効果時間: 10秒）
HEALTH_REGEN: HP回復（効果時間: 60秒）

設定組み合わせ検証:
✓ EASY + STORY: 初心者向け設定
✓ HARD + SURVIVAL: 上級者向け設定
✗ EXTREME + MULTIPLAYER: 不適切な組み合わせ
理由: EXTREMEは単人プレイ専用です

現在の設定:
難易度: NORMAL
モード: STORY
有効なパワーアップ: [SPEED_BOOST, HEALTH_REGEN]
設定スコア: 100点（バランス良好）

設定推奨:
初心者: EASY + STORY
経験者: NORMAL + CHALLENGE
エキスパート: HARD + SURVIVAL
```

**評価ポイント**:
- 複数Enumの組み合わせ設計
- 動的な設定システム
- ビジネスルールの検証

## 💡 ヒント

### 課題1のヒント
- Enum定義: enum Day { MONDAY, TUESDAY, ... }
- メソッド追加: public boolean isWeekend() { ... }
- ordinal()で定義順序を取得

### 課題2のヒント
- 状態遷移メソッド: public OrderStatus nextStatus() { ... }
- Set<OrderStatus>で許可する遷移先を定義
- EnumMap で状態別の動作を管理

### 課題3のヒント
- 関数型フィールド: private final BinaryOperator<Double> operation
- コンストラクタで関数を受け取り
- apply()メソッドで計算実行

### 課題4のヒント
- 複数のパラメータをEnum要素に持たせる
- EnumSet で組み合わせ管理
- 設定の妥当性をstaticメソッドで検証

## 🔍 Enumのポイント

1. **型安全性**: コンパイル時の値チェック
2. **メソッド追加**: Enumに独自メソッドを実装可能
3. **シングルトン**: 各Enum要素は唯一のインスタンス
4. **比較**: ==での比較が安全
5. **Switch文**: 網羅性チェックの恩恵
6. **継承不可**: Enumは拡張できない（設計上の制約）

## ✅ 完了チェックリスト

- [ ] 課題1: 基本的なEnumの定義と使用ができている
- [ ] 課題2: 状態管理パターンが実装できている
- [ ] 課題3: 関数型インターフェイスとの組み合わせができている
- [ ] 課題4: 複雑なEnum設計システムが構築できている
- [ ] Enumの利点と制限を理解している
- [ ] 適切な設計パターンでEnumを活用できている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより高度なEnum活用に挑戦しましょう！