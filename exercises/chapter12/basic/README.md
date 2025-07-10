# 第12章 基本課題

## 🎯 学習目標
- Record クラスの基本概念と利点の理解
- 不変データ構造としての Record の活用
- 従来のクラスと Record の使い分け
- Record の制限事項と適用場面の理解
- Record を使ったデータ中心設計の実践
- データ転送オブジェクト（DTO）としてのRecordの効果的な活用

## 📝 課題一覧

### 課題1: 基本的なRecord定義と活用
**ファイル名**: `BasicRecord.java`, `BasicRecordTest.java`

基本的なRecordクラスを定義し、その特性を理解してください。

**要求仕様**:
- シンプルなRecord定義（Point、Person等）
- 自動生成されるメソッド（equals、hashCode、toString）の確認
- Record のアクセサメソッドの使用
- Record の不変性の確認
- 従来のクラスとの比較

**実行例**:
```
=== 基本的なRecord定義と活用 ===
座標Record:
point1: Point[x=3, y=4]
point2: Point[x=3, y=4]

アクセサメソッド:
X座標: 3
Y座標: 4

equals比較:
point1.equals(point2): true
point1 == point2: false

hashCode比較:
point1.hashCode(): 128
point2.hashCode(): 128

人物Record:
person: Person[name=田中太郎, age=30, email=tanaka@example.com]

不変性確認:
// 以下はコンパイルエラー
// person.name = "佐藤花子";  // フィールドは final

Record vs 従来クラス比較:
従来クラスの行数: 35行（getter、setter、equals、hashCode、toString）
Recordの行数: 1行（record Person(String name, int age) {}）
```

**評価ポイント**:
- Record の基本構文理解
- 自動生成メソッドの理解
- 不変性の確認



### 課題2: Recordを使ったデータ転送オブジェクト（DTO）
**ファイル名**: `UserDTO.java`, `OrderDTO.java`, `DTOTest.java`

RecordをDTOとして活用し、データ転送の実装を行ってください。

**要求仕様**:
- ユーザー情報のRecord DTO
- 注文情報のRecord DTO（ネストしたRecord含む）
- JSON風データ構造の表現
- Record のバリデーション機能
- Record の変換メソッド

**実行例**:
```
=== Recordを使ったデータ転送オブジェクト ===
ユーザーDTO:
user: UserDTO[id=1001, name=田中太郎, email=tanaka@example.com, active=true]

注文DTO（ネストしたRecord）:
order: OrderDTO[
  orderId=ORD-2024-001,
  user=UserDTO[id=1001, name=田中太郎, email=tanaka@example.com, active=true],
  items=[
    OrderItem[productName=ノートPC, quantity=1, price=150000],
    OrderItem[productName=マウス, quantity=2, price=3000]
  ],
  totalAmount=156000,
  orderDate=2024-07-04T10:30:00
]

データ変換:
CSV形式: "1001,田中太郎,tanaka@example.com,true"
Map形式: {id=1001, name=田中太郎, email=tanaka@example.com, active=true}

バリデーション:
有効なユーザー: UserDTO[id=1001, name=田中太郎, email=tanaka@example.com, active=true] ✓
無効なユーザー: UserDTO[id=0, name=, email=invalid-email, active=false] ✗
エラー: [IDは1以上である必要があります, 名前は必須です, メールアドレスの形式が正しくありません]
```

**評価ポイント**:
- Record のネスト構造活用
- DTO としての適切な設計
- データ変換処理の実装



### 課題3: Record と設定管理
**ファイル名**: `AppConfig.java`, `DatabaseConfig.java`, `ConfigManager.java`, `ConfigTest.java`

Recordを使ったアプリケーション設定管理システムを実装してください。

**要求仕様**:
- 階層的な設定構造（Record内にRecord）
- 設定の読み込みと保存
- デフォルト値の提供
- 設定の部分更新（新しいRecordインスタンス生成）
- 設定の検証機能

**実行例**:
```
=== Record と設定管理 ===
デフォルト設定:
AppConfig[
  appName=MyApplication,
  version=1.0.0,
  database=DatabaseConfig[
    url=jdbc:h2:mem:testdb,
    username=sa,
    password=,
    maxConnections=10,
    timeout=30
  ],
  features=[logging, caching],
  debug=true
]

設定更新（部分更新）:
データベース接続数を20に変更:
新しい設定: AppConfig[
  appName=MyApplication,
  version=1.0.0,
  database=DatabaseConfig[
    url=jdbc:h2:mem:testdb,
    username=sa,
    password=,
    maxConnections=20,
    timeout=30
  ],
  features=[logging, caching],
  debug=true
]

設定ファイル形式での出力:
app.name=MyApplication
app.version=1.0.0
database.url=jdbc:h2:mem:testdb
database.username=sa
database.password=
database.maxConnections=20
database.timeout=30
features=logging,caching
debug=true

設定検証:
✓ アプリケーション名が設定されています
✓ データベースURLが有効です
✓ 接続数が適切な範囲内です
✗ パスワードが設定されていません（警告）
```

**評価ポイント**:
- 複雑なRecord構造の設計
- 不変性を保った部分更新
- 設定管理システムの実装



### 課題4: Record とパターンマッチング（Java 17+）
**ファイル名**: `Shape.java`, `PatternMatching.java`, `PatternMatchingTest.java`

Recordとパターンマッチングを組み合わせた処理を実装してください。

**要求仕様**:
- 図形を表すRecord（Circle、Rectangle、Triangle）
- sealed interface との組み合わせ
- パターンマッチング（switch式）の活用
- Record のデコンストラクション
- 型安全な処理の実装

**実行例**:
```
=== Record とパターンマッチング ===
図形Record定義:
circle: Circle[radius=5.0]
rectangle: Rectangle[width=4.0, height=3.0]
triangle: Triangle[base=6.0, height=4.0]

面積計算（パターンマッチング）:
円の面積: 78.54
長方形の面積: 12.0
三角形の面積: 12.0

周囲計算（パターンマッチング）:
円の周囲: 31.42
長方形の周囲: 14.0
三角形の周囲: （計算複雑）

Record デコンストラクション:
circle(5.0) → 半径: 5.0
rectangle(4.0, 3.0) → 幅: 4.0, 高さ: 3.0
triangle(6.0, 4.0) → 底辺: 6.0, 高さ: 4.0

図形分類:
小さい図形（面積20未満）: [circle, rectangle, triangle]
中くらいの図形（面積20-100）: []
大きい図形（面積100以上）: []

型安全性確認:
switch 文で全ケースを網羅
コンパイル時に型安全性を保証
```

**評価ポイント**:
- sealed interface と Record の組み合わせ
- パターンマッチングの活用
- 型安全性の確保

## 💡 ヒント

### 課題1のヒント
- Record 定義: record Point(int x, int y) {}
- アクセサメソッド: point.x(), point.y()
- equals() と hashCode() は自動生成

### 課題2のヒント
- ネストしたRecord: record Order(User user, List<Item> items) {}
- データ変換にはStream APIを活用
- バリデーションは静的メソッドで実装

### 課題3のヒント
- with メソッドパターンで部分更新を実現
- record.field() で値取得、新しいRecordで更新
- Properties ファイル形式での出力

### 課題4のヒント
- sealed interface で型を限定
- switch式でパターンマッチング
- var でRecord要素を取得

## 🔍 Record のポイント

1. **不変データ**: Record は本質的に不変
2. **簡潔性**: boilerplate コードの大幅削減
3. **透明性**: データの構造が明確
4. **値ベース**: equals/hashCode が自動で正しく実装
5. **制限事項**: 継承不可、追加フィールド不可
6. **適用場面**: DTO、設定、値オブジェクト

## ✅ 完了チェックリスト

- [ ] 課題1: 基本的なRecordの定義と特性が理解できている
- [ ] 課題2: RecordをDTOとして適切に活用できている
- [ ] 課題3: 複雑なRecord構造と設定管理ができている
- [ ] 課題4: パターンマッチングと組み合わせて使えている
- [ ] Record の利点と制限を理解している
- [ ] 従来のクラスとの使い分けができている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより複雑なRecord活用に挑戦しましょう！