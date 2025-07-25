# 第19章 基本課題

## 🎯 学習目標
- 高度なGUIコンポーネント（JTable、JTree、JList等）の活用
- MVC（Model-View-Controller）パターンの実装
- カスタムレンダラーとエディターの作成
- 複雑なレイアウト管理とGUIアーキテクチャ
- データバインディングとGUIの同期
- オブザーバーパターンによるリアルタイムなデータ更新システム

## 📝 課題一覧

### 課題1: 高度なテーブル操作システム
**ファイル名**: `AdvancedTableSystem.java`, `CustomTableModel.java`, `TableCellRenderer.java`, `AdvancedTableTest.java`

高機能なテーブルシステムを作成し、データの表示・編集・ソート・フィルタリングを実装してください。

**要求仕様**:
- カスタムTableModelの実装
- セルレンダラーとエディターのカスタマイズ
- 複数列ソート機能
- 動的フィルタリング機能
- データの検証とエラー表示

**実行例**:
```
=== 高度なテーブル操作システム ===
ウィンドウ表示: "Advanced Table System"
サイズ: 900x700ピクセル

社員管理テーブル:
┌─────────────────────────────┐
│ [追加] [編集] [削除] [更新]   │ (ツールバー)
├─────────────────────────────┤
│ フィルタ: [部署▼][年齢▼][給与▼]│ (フィルター行)
└─────────────────────────────┘
┌─────────────────────────────────────────────────────────┐
│ID │名前    │部署  │年齢│給与      │入社日    │評価│アクティブ│
├─────────────────────────────────────────────────────────┤
│001│田中太郎│営業部│ 30│¥5,000,000│2020-04-01│★★★★☆│    ✓    │
│002│佐藤花子│開発部│ 25│¥4,500,000│2022-07-15│★★★★★│    ✓    │
│003│鈴木一郎│開発部│ 35│¥6,000,000│2018-03-10│★★★★☆│    ✓    │
│004│山田次郎│総務部│ 28│¥4,200,000│2021-09-01│★★★☆☆│    ✓    │
│005│中村美咲│営業部│ 32│¥5,200,000│2019-11-20│★★★★☆│    ✗    │
└─────────────────────────────────────────────────────────┘

カスタムレンダラー機能:
1. 給与列: 
   - 金額表示形式: ¥5,000,000
   - 色分け: 600万以上(緑), 500万以上(青), 400万未満(赤)

2. 評価列:
   - 星形アイコン表示: ★★★★☆
   - ツールチップ: "評価: 4/5"

3. アクティブ列:
   - チェックボックス: ✓/✗
   - 背景色: アクティブ(白), 非アクティブ(灰色)

4. 入社日列:
   - 日付形式: yyyy-MM-dd
   - 勤続年数表示: "4年2ヶ月"

ソート機能:
単一列ソート:
- 名前列クリック: あいうえお順
- 給与列クリック: 金額順（高い→低い）
- 入社日列クリック: 新しい→古い

複数列ソート:
1次ソート: 部署（昇順）
2次ソート: 給与（降順）
結果: 部署内で給与順にソート

フィルタリング機能:
部署フィルタ: "開発部" 選択
→ 佐藤花子、鈴木一郎のみ表示

年齢フィルタ: "30歳以上" 選択
→ 田中太郎、鈴木一郎、中村美咲

給与フィルタ: "500万円以上" 選択
→ 田中太郎、鈴木一郎、中村美咲

複合フィルタ: "開発部 AND 500万円以上"
→ 鈴木一郎のみ表示

編集機能:
セル編集:
- ダブルクリック: インプレース編集開始
- Enter: 編集確定
- Esc: 編集キャンセル

データ検証:
名前: 必須入力、50文字以内
年齢: 18-70の範囲
給与: 正の数値のみ
入社日: 有効な日付形式

エラー表示:
不正データ: セル背景を赤色表示
ツールチップ: "年齢は18-70の範囲で入力してください"

統計情報表示:
┌─────────────────────────────┐
│ 統計情報                     │
│ 総従業員数: 5名              │
│ アクティブ: 4名              │
│ 平均年齢: 30.0歳             │
│ 平均給与: ¥4,980,000         │
│ 最新入社: 佐藤花子(2022-07)  │
└─────────────────────────────┘

パフォーマンス最適化:
- 大量データ対応: 仮想化による高速表示
- 遅延読み込み: 必要な時にデータ取得
- キャッシュ機能: フィルタ結果のキャッシュ

データ同期:
- 自動更新: 5秒間隔でデータ更新チェック
- 競合解決: 他ユーザーの変更を検出
- ロック機能: 編集中の行をロック

エクスポート機能:
- CSV出力: 現在の表示データ
- Excel出力: 書式付きエクスポート
- PDF出力: レポート形式
```

**評価ポイント**:
- TableModelの高度な実装
- カスタムレンダラーの効果的活用
- 複雑なデータ操作機能



### 課題2: ツリー構造データブラウザ
**ファイル名**: `TreeDataBrowser.java`, `CustomTreeModel.java`, `TreeCellRenderer.java`, `TreeBrowserTest.java`

階層データを管理するツリーブラウザを作成し、ファイルシステムやXMLデータの表示を実装してください。

**要求仕様**:
- カスタムTreeModelの実装
- ノードの動的読み込み（遅延読み込み）
- ツリーセルレンダラーのカスタマイズ
- 編集可能なツリーノード
- ドラッグ&ドロップによるノード移動

**実行例**:
```
=== ツリー構造データブラウザ ===
ウィンドウ表示: "Tree Data Browser"
サイズ: 800x600ピクセル

ファイルシステムブラウザ:
┌─────────────────────────────┐
│ [更新] [新規フォルダ] [削除]  │ (ツールバー)
├─────────────────────────────┤
│ ┌─ファイルツリー─┐ ┌─詳細─┐  │
│ │├📁 C:\        │ │名前:   │  │
│ ││├📁 Program.. │ │Documents│  │
│ ││├📁 Users     │ │種類:   │  │
│ │││├📁 Public  │ │フォルダ │  │
│ │││└📁 Admin   │ │サイズ: │  │
│ ││└📁 Windows   │ │125 MB  │  │
│ │├📁 D:\        │ │更新日: │  │
│ ││├📄 file1.txt │ │2024-07 │  │
│ ││├📄 file2.doc │ │-04     │  │
│ ││└📷 image.jpg │ └───────┘  │
│ │└🔌 E:\ (USB)  │            │
│ └──────────────┘            │
└─────────────────────────────┘

XMLデータブラウザ:
┌─────────────────────────────┐
│ [XML読み込み] [検証] [保存]   │
├─────────────────────────────┤
│ ├📋 configuration.xml        │
│ │├📂 database               │ 
│ ││├🔗 host: localhost       │
│ ││├🔗 port: 3306           │
│ ││└🔗 name: myapp          │
│ │├📂 logging                │
│ ││├🔗 level: DEBUG         │
│ ││└🔗 file: app.log        │
│ │└📂 features               │
│ │ ├☑️ feature1: enabled    │
│ │ └☐ feature2: disabled    │
│ └📋 users.xml              │
│  ├👤 user[id=1]           │
│  │├📝 name: 田中太郎       │
│  │├📧 email: tanaka@...   │
│  │└📅 created: 2024-01   │
│  └👤 user[id=2]           │
│   └📝 name: 佐藤花子       │
└─────────────────────────────┘

ツリー操作機能:
1. ノード展開/折りたたみ:
   クリック: 📁 → 📂 (展開)
   ダブルクリック: 深い階層まで展開
   Shift+クリック: 同レベル全展開

2. 動的読み込み:
   大きなディレクトリ: 必要時に子ノード読み込み
   プログレス表示: "読み込み中... 45%"
   キャンセル機能: 長時間処理の中断

3. 検索機能:
   検索ボックス: "*.java"
   結果ハイライト: 一致ノードを黄色表示
   階層表示: 検索結果の親ノードも表示

カスタムレンダラー機能:
アイコン表示:
- フォルダ: 📁/📂 (閉じた/開いた)
- ファイル: 拡張子別アイコン
  - .txt: 📄, .doc: 📃, .jpg: 📷
  - .exe: ⚙️, .zip: 📦
- 特殊: 🔌(USBドライブ), 🌐(ネットワーク)

状態表示:
- 読み取り専用: 🔒 アイコン
- 隠しファイル: 半透明表示
- 最近更新: ✨ マーク
- エラー: ❌ アイコン

編集機能:
名前変更:
- F2キー または 右クリック→名前変更
- インプレース編集: テキストフィールド表示
- 検証: 不正文字チェック
- 確定: Enter または フォーカス喪失

新規作成:
- 右クリック→新規フォルダ
- デフォルト名: "新しいフォルダ"
- 即座に編集モード

削除:
- Delete キーまたは右クリック→削除
- 確認ダイアログ: "3個のアイテムを削除しますか？"
- ゴミ箱移動: システム標準動作

ドラッグ&ドロップ:
ノード移動:
- ドラッグ開始: ノードを選択してドラッグ
- ドロップ先表示: 青色ハイライト
- 移動実行: ファイルシステム操作

コピー操作:
- Ctrl+ドラッグ: コピー操作
- カーソル表示: + アイコン付き
- 確認: "ファイルをコピーしますか？"

制約チェック:
- 移動先の権限チェック
- 循環参照防止
- ディスク容量チェック

パフォーマンス最適化:
遅延読み込み:
- 子ノードの動的生成
- メモリ効率: 表示中のノードのみ保持
- キャッシュ: 一度読み込んだデータは保持

バックグラウンド処理:
- 大きなディレクトリの非同期読み込み
- プログレス表示: 進行状況バー
- キャンセル: ユーザーによる中断可能

統計情報:
┌─────────────────────────────┐
│ 選択フォルダ: C:\Users\Admin │
│ ファイル数: 1,247           │
│ フォルダ数: 89              │
│ 総サイズ: 2.4 GB            │
│ 最新更新: 2024-07-04        │
└─────────────────────────────┘

イベント処理:
ツリー選択: 詳細情報更新
ノード展開: 子ノード読み込み
右クリック: コンテキストメニュー
ダブルクリック: デフォルトアクション実行
```

**評価ポイント**:
- TreeModelの柔軟な実装
- 大量データの効率的な処理
- 階層データの直感的な操作



### 課題3: データバインディングシステム
**ファイル名**: `DataBindingSystem.java`, `BindableModel.java`, `PropertyChangeManager.java`, `DataBindingTest.java`

データモデルとGUIコンポーネントを自動同期するバインディングシステムを実装してください。

**要求仕様**:
- PropertyChangeListenerによる双方向バインディング
- データ検証とエラー表示の自動化
- 計算プロパティとリアルタイム更新
- バインディング式の実装
- 依存関係の管理

**実行例**:
```
=== データバインディングシステム ===
ウィンドウ表示: "Data Binding System"
サイズ: 700x500ピクセル

個人情報フォーム:
┌─────────────────────────────┐
│ 基本情報                     │
│ 名前: [田中太郎_____________] │
│ 年齢: [30] 歳               │
│ 給与: [5000000] 円          │ 
├─────────────────────────────┤
│ 自動計算項目                 │
│ 月給: [416,667] 円          │ ← 給与÷12 (自動計算)
│ 税額: [1,000,000] 円        │ ← 給与×0.2 (自動計算)  
│ 手取: [4,000,000] 円        │ ← 給与-税額 (自動計算)
├─────────────────────────────┤
│ 年齢グループ: [30代]         │ ← 年齢による自動分類
│ 給与ランク: [中級]           │ ← 給与による自動分類
└─────────────────────────────┘

リアルタイム同期デモ:
給与フィールド変更: 5000000 → 6000000
即座に更新:
- 月給: 416,667 → 500,000
- 税額: 1,000,000 → 1,200,000  
- 手取: 4,000,000 → 4,800,000
- 給与ランク: 中級 → 上級

年齢フィールド変更: 30 → 25
即座に更新:
- 年齢グループ: 30代 → 20代

データ検証:
名前フィールド:
- 空文字: ❌ "名前は必須です"
- 長すぎ: ❌ "名前は50文字以内です"
- 正常: ✅ 緑色枠線

年齢フィールド:
- 負の値: ❌ "年齢は0以上です"
- 範囲外: ❌ "年齢は150以下です"
- 正常: ✅ 緑色枠線

給与フィールド:
- 文字列: ❌ "数値を入力してください"
- 負の値: ❌ "給与は0以上です"
- 正常: ✅ 緑色枠線

複数コンポーネント同期:
┌─────────────────────────────┐
│ 給与設定                     │
│ スライダー: ├──●───┤ 600万   │
│ スピナー: [6000000] ↑↓      │
│ テキスト: [6,000,000円]      │
│ プログレス: ████████░░ 60%   │ ← 1000万を満点とした割合
└─────────────────────────────┘
※すべて連動して同じ値を表示・編集

バインディング式:
式定義:
monthlyPay = salary / 12
tax = salary * 0.2
netPay = salary - tax
ageGroup = age < 30 ? "20代" : age < 40 ? "30代" : "40代以上"
salaryRank = salary < 400万 ? "初級" : salary < 600万 ? "中級" : "上級"

依存関係グラフ:
salary → monthlyPay
salary → tax
salary, tax → netPay
age → ageGroup
salary → salaryRank

変更伝播:
salary変更 → [monthlyPay, tax, salaryRank] 更新
tax変更 → [netPay] 更新

バインディングAPI使用例:
// 双方向バインディング
binder.bind(nameField).to(person, "name");
binder.bind(ageField).to(person, "age");

// 一方向バインディング（読み取り専用）
binder.bind(ageGroupLabel).toExpression(
    () -> person.getAge() < 30 ? "20代" : "30代以上"
);

// 計算プロパティ
binder.bind(monthlyPayField).toCalculation(
    () -> person.getSalary() / 12
).dependsOn(person, "salary");

// 検証ルール
binder.bind(nameField)
    .validate(name -> !name.isEmpty(), "名前は必須です")
    .validate(name -> name.length() <= 50, "50文字以内です");

イベント追跡:
┌─────────────────────────────┐
│ バインディングログ           │
│ 10:30:15 salary changed: 5000000 → 6000000
│ 10:30:15 monthlyPay updated: 416667 → 500000
│ 10:30:15 tax updated: 1000000 → 1200000
│ 10:30:15 netPay updated: 4000000 → 4800000
│ 10:30:15 salaryRank updated: 中級 → 上級
│ 10:30:20 age changed: 30 → 25
│ 10:30:20 ageGroup updated: 30代 → 20代
└─────────────────────────────┘

パフォーマンス統計:
バインディング数: 15
プロパティ変更: 127回
更新処理時間: 平均 0.5ms
メモリ使用量: 2.3MB
リスナー登録数: 45
```

**評価ポイント**:
- 双方向バインディングの実装
- 複雑な依存関係の管理
- リアルタイム更新の最適化



### 課題4: ダッシュボードアプリケーション
**ファイル名**: `Dashboard.java`, `WidgetManager.java`, `ChartComponent.java`, `DashboardTest.java`

複数のウィジェットを配置できるダッシュボードシステムを作成してください。

**要求仕様**:
- 動的なウィジェット配置システム
- リサイズ可能なパネル
- チャート・グラフコンポーネント
- ウィジェットの設定とカスタマイズ
- レイアウトの保存・読み込み

**実行例**:
```
=== ダッシュボードアプリケーション ===
ウィンドウ表示: "Executive Dashboard"
サイズ: 1200x800ピクセル

ダッシュボードレイアウト:
┌─────────────────────────────────────────────────────────┐
│ [+ ウィジェット追加] [レイアウト保存] [設定] [フルスクリーン] │
├─────────────────────────────────────────────────────────┤
│ ┌─売上グラフ────────┐ ┌─KPI指標─┐ ┌─タスク状況─┐   │
│ │     📊           │ │💰 月間売上│ │📋 完了: 85%│   │
│ │   /\    /\       │ │ ¥12.5M   │ │🔄 進行: 12%│   │
│ │  /  \  /  \      │ │          │ │⏸️ 保留: 3% │   │
│ │ /    \/    \     │ │👥 顧客数 │ └───────────┘   │
│ │/            \    │ │  1,247   │                 │
│ └─────────────────┘ │          │ ┌─通知センター─┐   │
│                     │📈 成長率  │ │🔔 新着: 5件 │   │
│ ┌─地域別売上────────┐ │ +15.3%   │ │📧 未読: 12件│   │
│ │🌏 地域マップ      │ └─────────┘ │⚠️ 警告: 2件 │   │
│ │ 北海道: 8%       │             │🎯 締切: 3件 │   │
│ │ 関東: 45%        │ ┌─株価情報─┐ └───────────┘   │
│ │ 関西: 25%        │ │📈 NIKKEI │                 │
│ │ 九州: 12%        │ │ 28,450   │ ┌─天気予報───┐   │
│ │ その他: 10%      │ │ +245 ↗️  │ │☀️ 東京      │   │
│ └─────────────────┘ └─────────┘ │ 25°C 晴れ   │   │
│                                   │ 湿度: 60%   │   │
│ ┌─システム状況──────────────────────┐ │ 風速: 3m/s  │   │
│ │💻 CPU: ████░░░░░░ 40%         │ └───────────┘   │
│ │💾 メモリ: ██████░░ 60%       │                 │
│ │💿 ディスク: ███░░░░ 30%      │                 │
│ │🌐 ネットワーク: ██████████ 100%│                 │
│ └─────────────────────────────────┘                 │
└─────────────────────────────────────────────────────────┘

ウィジェット操作:
1. 追加:
   [+ ウィジェット追加] → ウィジェットギャラリー表示
   ┌─ウィジェットギャラリー─┐
   │ 📊 チャート          │
   │ 📈 ラインチャート    │
   │ 🥧 円グラフ          │
   │ 📋 リスト            │
   │ 🗓️ カレンダー        │
   │ 🌡️ メーター          │
   │ 📰 ニュースフィード  │
   │ ⏰ 時計              │
   └─────────────────────┘

2. サイズ変更:
   ウィジェット端をドラッグ → リサイズ
   スナップ機能: グリッドに自動整列
   最小サイズ: 200x150px

3. 移動:
   ウィジェットをドラッグ → 位置変更
   他のウィジェットを自動押し下げ
   磁石効果: 近くのウィジェットに吸着

4. 設定:
   右クリック → 設定メニュー
   ├── 設定
   ├── 複製
   ├── 最前面に移動
   ├── 最背面に移動
   ├── ─────────
   └── 削除

チャートウィジェット詳細:
データソース設定:
- ファイル: CSV, Excel, JSON
- データベース: MySQL, PostgreSQL
- API: REST, GraphQL
- リアルタイム: WebSocket

グラフタイプ:
- 線グラフ: 時系列データ
- 棒グラフ: カテゴリ比較
- 円グラフ: 構成比
- 散布図: 相関関係
- ヒートマップ: 2次元データ

カスタマイズ:
色設定: カラーパレット選択
軸設定: 最小値、最大値、目盛り
凡例: 位置、表示/非表示
アニメーション: 有効/無効

データ更新:
自動更新: 1分、5分、15分、1時間
手動更新: 更新ボタンクリック
リアルタイム: データソース変更時

レイアウト管理:
保存機能:
- レイアウト名: "役員ダッシュボード"
- ウィジェット配置情報
- 各ウィジェットの設定
- ファイル形式: JSON

読み込み機能:
- 保存済みレイアウト一覧
- プリセットレイアウト
- インポート/エクスポート

テンプレート:
- 営業ダッシュボード
- 開発チームダッシュボード
- システム監視ダッシュボード
- 財務ダッシュボード

パフォーマンス最適化:
仮想化: 画面外ウィジェットの描画停止
キャッシュ: データの一時保存
圧縮: レイアウトファイルの最適化
遅延読み込み: 必要時にウィジェット作成

アクセシビリティ:
キーボード操作: Tab移動、Space選択
スクリーンリーダー対応: ARIA属性
ハイコントラスト: 視認性向上
拡大機能: ズームイン/アウト

統計情報:
┌─────────────────────────────┐
│ ダッシュボード統計           │
│ 総ウィジェット数: 12        │
│ データ更新回数: 1,547       │
│ 平均応答時間: 125ms         │
│ エラー発生数: 0             │
│ 最終更新: 2024-07-04 10:30  │
└─────────────────────────────┘
```

**評価ポイント**:
- 複雑なレイアウトシステムの構築
- 動的なコンポーネント管理
- 高度な視覚化機能の実装

## 💡 ヒント

### 課題1のヒント
- AbstractTableModel を継承して fireTableDataChanged() で更新通知
- TableCellRenderer.getTableCellRendererComponent() で描画カスタマイズ
- RowSorter でソート機能、RowFilter でフィルタリング

### 課題2のヒント
- DefaultMutableTreeNode でノード作成
- TreeWillExpandListener で遅延読み込み
- DefaultTreeCellRenderer を継承してアイコンと表示をカスタマイズ

### 課題3のヒント
- PropertyChangeSupport でイベント通知
- DocumentListener でテキストフィールドの変更検出
- WeakReference で循環参照を防止

### 課題4のヒント
- JLayeredPane で重なり順管理
- ComponentListener でリサイズ検出
- Graphics2D で高品質なチャート描画

## 🔍 高度なGUIのポイント

1. **MVCパターン**: モデル、ビュー、コントローラーの分離
2. **カスタムレンダリング**: 独自の描画ロジック実装
3. **データバインディング**: モデルとビューの自動同期
4. **パフォーマンス**: 大量データの効率的な処理
5. **ユーザビリティ**: 直感的で使いやすいインターフェイス
6. **拡張性**: プラグイン機能やカスタマイズ対応

## ✅ 完了チェックリスト

- [ ] 課題1: 高度なテーブル操作システムが実装できている
- [ ] 課題2: ツリー構造データブラウザが作成できている
- [ ] 課題3: データバインディングシステムが構築できている
- [ ] 課題4: ダッシュボードアプリケーションが開発できている
- [ ] MVCパターンを理解して実装できている
- [ ] カスタムレンダラーとエディターが作成できている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより高度なGUIアーキテクチャに挑戦しましょう！