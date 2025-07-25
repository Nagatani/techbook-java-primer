# 第6章 基本課題

## 🎯 学習目標
- 不変性（Immutability）の概念と重要性の理解
- finalキーワードの適切な使用（変数、メソッド、クラス）
- 不変オブジェクトの設計と実装
- スレッドセーフティの基本概念
- 防御的コピーの実装
- 値オブジェクト（Value Object）パターンの実装と活用

## 📝 課題一覧

### 課題1: 不変の座標クラス設計
**ファイル名**: `ImmutablePoint.java`, `ImmutablePointTest.java`

不変の2D座標を表すクラスを作成し、不変性の概念を実装してください。

**要求仕様**:
- finalフィールド: x（double）, y（double）
- コンストラクタでの初期化のみ
- getterメソッドのみ提供（setterなし）
- 座標間の距離計算メソッド
- 新しい座標を返すメソッド（移動、回転など）

**実行例**:
```
=== 不変座標クラステスト ===
座標1: (3.0, 4.0)
座標2: (0.0, 0.0)

座標1から座標2への距離: 5.0

座標1を(2.0, 3.0)移動:
元の座標1: (3.0, 4.0)
新しい座標: (5.0, 7.0)

座標1を90度回転:
元の座標1: (3.0, 4.0)
回転後の座標: (-4.0, 3.0)
```

**評価ポイント**:
- finalフィールドの適切な使用
- 不変性の維持
- メソッドからの新しいインスタンス返却



### 課題2: 不変の人物情報クラス
**ファイル名**: `ImmutablePerson.java`, `ImmutablePersonTest.java`

不変の人物情報を表すクラスを作成し、複雑なデータの不変性を実装してください。

**要求仕様**:
- finalフィールド: name（String）, age（int）, hobbies（List<String>）
- コンストラクタでの防御的コピー
- コレクションの不変性保証
- 情報変更時の新しいインスタンス作成メソッド
- 年齢更新、趣味追加・削除の不変メソッド

**実行例**:
```
=== 不変人物情報クラステスト ===
元の人物情報:
名前: 田中太郎
年齢: 25歳
趣味: [読書, 映画鑑賞]

年齢を更新（26歳）:
元の人物: 田中太郎（25歳）
新しい人物: 田中太郎（26歳）

趣味を追加（プログラミング）:
元の趣味: [読書, 映画鑑賞]
新しい趣味: [読書, 映画鑑賞, プログラミング]
```

**評価ポイント**:
- コレクションの防御的コピー
- 不変性を保った更新メソッド
- 元のオブジェクトの保護



### 課題3: 設定値管理クラス
**ファイル名**: `ApplicationConfig.java`, `ApplicationConfigTest.java`

アプリケーション設定を管理する不変クラスを作成し、設定の安全な管理を実装してください。

**要求仕様**:
- finalフィールド: appName（String）, version（String）, properties（Map<String, String>）
- 設定の読み取り専用アクセス
- 設定変更時の新しいインスタンス作成
- デフォルト設定の提供
- 設定値の検証機能

**実行例**:
```
=== アプリケーション設定管理テスト ===
デフォルト設定:
アプリ名: MyApp
バージョン: 1.0.0
設定値: {timeout=30, debug=false}

設定を更新:
元の設定 - タイムアウト: 30
新しい設定 - タイムアウト: 60

設定を追加:
元の設定: {timeout=30, debug=false}
新しい設定: {timeout=30, debug=false, logging=true}
```

**評価ポイント**:
- Mapの不変性管理
- ビルダーパターンの活用
- 設定値の妥当性検証



### 課題4: 金額計算クラス
**ファイル名**: `Money.java`, `MoneyTest.java`

不変の金額クラスを作成し、金融計算の安全性を実装してください。

**要求仕様**:
- finalフィールド: amount（BigDecimal）, currency（String）
- 四則演算メソッド（新しいインスタンスを返す）
- 通貨変換メソッド
- 金額の比較メソッド
- 精度の正確な計算

**実行例**:
```
=== 金額計算クラステスト ===
金額1: 1000.00 JPY
金額2: 500.50 JPY

加算結果:
1000.00 JPY + 500.50 JPY = 1500.50 JPY

減算結果:
1000.00 JPY - 500.50 JPY = 499.50 JPY

乗算結果:
1000.00 JPY × 1.08 = 1080.00 JPY

比較結果:
1000.00 JPY > 500.50 JPY: true
```

**評価ポイント**:
- BigDecimalによる正確な計算
- 演算結果の新しいインスタンス返却
- 通貨の整合性チェック

## 💡 ヒント

### 課題1のヒント
- finalフィールドはコンストラクタでのみ設定可能
- 移動メソッドは元のオブジェクトを変更せず新しいインスタンスを返す
- Math.sqrt()で距離計算、Math.sin/cosで回転計算

### 課題2のヒント
- List.copyOf()やCollections.unmodifiableList()を活用
- コンストラクタで new ArrayList<>(hobbies) で防御的コピー
- 趣味の追加時は既存リストに新要素を加えた新しいリストを作成

### 課題3のヒント
- Map.copyOf()やCollections.unmodifiableMap()を使用
- builderパターンで設定の段階的構築
- withProperty()メソッドで設定追加

### 課題4のヒント
- BigDecimal.valueOf()で正確な数値表現
- setScale()で小数点以下の桁数制御
- compareTo()で金額比較

## 🔍 不変性とfinalのポイント

1. **final変数**: 一度初期化されると変更不可
2. **finalメソッド**: オーバーライド不可
3. **finalクラス**: 継承不可（例: String, Integer）
4. **不変オブジェクト**: 状態が変更されないオブジェクト
5. **防御的コピー**: 外部からの変更を防ぐためのコピー
6. **スレッドセーフ**: 不変オブジェクトは本質的にスレッドセーフ

## ✅ 完了チェックリスト

- [ ] 課題1: ImmutablePointで不変の座標クラスができている
- [ ] 課題2: ImmutablePersonで複雑データの不変性が実装されている
- [ ] 課題3: ApplicationConfigで設定管理ができている
- [ ] 課題4: Moneyクラスで正確な金額計算ができている
- [ ] finalキーワードの適切な使用ができている
- [ ] 不変性の原則を理解している

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより複雑な不変クラス設計に挑戦しましょう！