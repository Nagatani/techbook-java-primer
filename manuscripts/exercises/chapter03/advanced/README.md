# 第3章 発展課題

## 概要
オブジェクト指向の概念をより深く理解し、実践的なシステム設計に挑戦します。複数のクラスが協調して動作する、より複雑なアプリケーションを開発します。

## 学習目標
- オブジェクト間の複雑な相互作用を設計できる
- 責任の分離を意識したクラス設計ができる
- 拡張性のあるシステムアーキテクチャを構築できる
- 実世界の問題をオブジェクト指向で解決できる

## 課題一覧

### 課題A-1: ホテル予約管理システム
**ファイル名**: `Room.java`、`Guest.java`、`Reservation.java`、`Hotel.java`、`HotelManagementSystem.java`

#### 要求仕様
1. `Room`クラス：
   - 部屋番号、タイプ（シングル/ダブル/スイート）、料金
   - 清掃状態、設備リスト
   - 稼働率の計算
2. `Guest`クラス：
   - ゲストID、氏名、連絡先、会員ランク
   - 宿泊履歴、ポイント管理
   - 会員特典の適用
3. `Reservation`クラス：
   - 予約番号、ゲスト、部屋、期間
   - チェックイン/チェックアウト処理
   - キャンセル処理（キャンセル料計算）
4. `Hotel`クラス：
   - 全部屋の管理、予約の管理
   - 空室検索機能
   - 売上集計、稼働率分析
5. システム機能：
   - 予約の作成・変更・キャンセル
   - 部屋割り当ての最適化
   - 料金計算（季節料金、長期割引）
   - 日次・月次レポート

#### 実装のヒント
- 日付は簡易的に整数で管理
- 部屋の重複予約を防ぐロジック
- 会員ランクによる割引率の適用

#### 実装例の骨組み
```java
// Room.java
public class Room {
    private String roomNumber;
    private RoomType type;
    private double basePrice;
    private boolean isClean;
    private String[] amenities;
    
    public boolean isAvailable(int startDate, int endDate) {
        // 指定期間の空室確認
    }
}

// Guest.java
public class Guest {
    private String guestId;
    private String name;
    private MembershipLevel level;
    private int points;
    private Reservation[] history;
    
    public double getDiscountRate() {
        // 会員レベルに応じた割引率
    }
}

// Reservation.java
public class Reservation {
    private String reservationId;
    private Guest guest;
    private Room room;
    private int checkInDate;
    private int checkOutDate;
    private ReservationStatus status;
    
    public double calculateTotalPrice() {
        // 料金計算（割引適用）
    }
}
```

#### 評価ポイント
- ビジネスロジックの完全性
- データの整合性維持
- 実用的な機能の充実度
- エラーハンドリング

#### 発展学習
- オンライン予約API
- 多言語対応
- 決済システム連携

---

### 課題A-2: 学校の時間割管理システム
**ファイル名**: `Teacher.java`、`Student.java`、`Course.java`、`Classroom.java`、`Schedule.java`、`TimetableSystem.java`

#### 要求仕様
1. `Teacher`クラス：
   - 教員ID、氏名、専門科目、担当可能コマ数
   - スケジュール管理
2. `Student`クラス：
   - 学生ID、氏名、学年、履修科目リスト
   - 単位取得状況
3. `Course`クラス：
   - 科目コード、科目名、単位数、必要時間数
   - 履修条件、定員
4. `Classroom`クラス：
   - 教室番号、収容人数、設備
   - 利用可能時間帯
5. `Schedule`クラス：
   - 曜日、時限、科目、教員、教室の組み合わせ
   - 時間割の妥当性チェック
6. システム機能：
   - 時間割の自動生成
   - 衝突検出（教員、教室、学生）
   - 履修登録管理
   - 出席管理

#### 実装のヒント
- 時間割は2次元配列で管理
- 制約条件を満たす割り当て
- バックトラッキングアルゴリズム

#### 評価ポイント
- 複雑な制約条件の処理
- アルゴリズムの効率性
- ユーザビリティ

#### 発展学習
- 最適化アルゴリズム
- Web インターフェース
- 成績管理連携

---

### 課題A-3: レストラン注文管理システム
**ファイル名**: `MenuItem.java`、`Order.java`、`Table.java`、`Kitchen.java`、`RestaurantSystem.java`

#### 要求仕様
1. `MenuItem`クラス：
   - メニューID、名前、価格、カテゴリ
   - 調理時間、在庫管理
   - アレルギー情報
2. `Order`クラス：
   - 注文番号、テーブル番号、注文項目リスト
   - 注文状態（受付/調理中/配膳済み）
   - 合計金額計算
3. `Table`クラス：
   - テーブル番号、座席数、利用状態
   - 現在の注文、会計状態
4. `Kitchen`クラス：
   - 注文キュー管理
   - 調理優先順位
   - 調理完了通知
5. システム機能：
   - 注文の受付・変更・キャンセル
   - 調理進捗管理
   - 売上分析
   - 人気メニューランキング

#### 実装のヒント
- 注文は時系列で管理
- FIFO/優先度付きキュー
- 状態遷移の管理

#### 評価ポイント
- リアルタイム性の考慮
- 業務フローの正確な実装
- 拡張性のある設計

#### 発展学習
- 在庫管理システム
- スタッフシフト管理
- 予約システム連携

## 提出方法
1. 選択したシステムのすべてのファイルを実装
2. クラス図を含む設計ドキュメント作成
3. 操作マニュアルの作成
4. サンプルデータでの動作例を提示

## 注意事項
- SOLID原則を意識した設計
- 各クラスの責任を明確に
- 将来の拡張を考慮した設計
- 十分なエラーハンドリング