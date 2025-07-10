# 第19章 高度なGUIコンポーネント

## 章末演習

本章で学んだ高度なGUIコンポーネントとMVCパターンを活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
高度なGUIコンポーネントとMVCパターンを使った実用的なアプリケーションの作成技術を習得します。




## 基礎レベル課題（必須）

### 課題1: 高度なテーブル操作システム

データの表示・編集・ソート・フィルタリングができる高機能テーブルを作成してください。

**技術的背景：JTableのMVCアーキテクチャとエンタープライズアプリケーション**

JTableは、Swingの中で最も複雑で強力なコンポーネントの一つです：

**JTableのMVCアーキテクチャ：**
```java
// Model：データの管理
TableModel model = new AbstractTableModel() {
    public int getRowCount() { return data.size(); }
    public int getColumnCount() { return columns.length; }
    public Object getValueAt(int row, int col) { 
        return data.get(row).get(col); 
    }
};

// View：表示のカスタマイズ
TableCellRenderer renderer = new DefaultTableCellRenderer() {
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        // セルの外観をカスタマイズ
    }
};

// Controller：イベント処理
TableCellEditor editor = new DefaultCellEditor(textField);
```

**エンタープライズアプリケーションでの実例：**
- **ERPシステム**：在庫管理、受注管理
- **CRMシステム**：顧客情報管理
- **金融システム**：取引履歴表示
- **分析ツール**：データ集計表示

**パフォーマンス最適化の技術：**
```java
// 仮想化（Virtual Scrolling）
// 表示領域のみのデータをロード
public Object getValueAt(int row, int col) {
    if (!isDataLoaded(row)) {
        loadDataAsync(row);  // 非同期読み込み
        return "Loading...";
    }
    return cache.get(row, col);
}
```

**ソートとフィルタリングの高度な実装：**
- **複数列ソート**：RowSorterのチェーン
- **カスタムフィルタ**：RowFilterの拡張
- **リアルタイムフィルタ**：DocumentListener連携
- **ファセット検索**：複数条件の組み合わせ

**データ編集のベストプラクティス：**
- **トランザクション**：一括確定/キャンセル
- **検証**：セルエディタでの入力チェック
- **アンドゥ/リドゥ**：編集履歴の管理
- **ロック**：同時編集の制御

**セルレンダラーの高度な活用：**
```java
// 条件付き書式
if (value instanceof Number && ((Number)value).doubleValue() > 1000000) {
    setBackground(Color.GREEN);  // 高額を緑色で強調
}

// プログレスバー表示
JProgressBar progress = new JProgressBar(0, 100);
progress.setValue((Integer)value);
return progress;  // セルにプログレスバー表示
```

**実際のシステムでの課題と解決策：**
- **大量データ**：ページング、遅延ロード
- **メモリ管理**：WeakReferenceでキャッシュ
- **レスポンシブネス**：SwingWorkerで非同期処理
- **アクセシビリティ**：AccessibleTable実装

この演習では、業務アプリケーションレベルのテーブル実装を学びます。

**要求仕様：**
- カスタムテーブルModelの実装
- セルレンダラーとエディタのカスタマイズ
- 複数列ソート機能
- 動的フィルタリング機能
- データの検証とエラー表示

**実行例：**
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
│001│田中太郎│営業部│ 30│¥5,000,000│2020-04-01│****-│    o    │
│002│佐藤花子│開発部│ 25│¥4,500,000│2022-07-15│*****│    o    │
│003│鈴木一郎│開発部│ 35│¥6,000,000│2018-03-10│****-│    o    │
│004│山田次郎│総務部│ 28│¥4,200,000│2021-09-01│***--│    o    │
│005│中村美咲│営業部│ 32│¥5,200,000│2019-11-20│****-│    x    │
└─────────────────────────────────────────────────────────┘

カスタムレンダラー機能:
1. 給与列: 
   - 金額表示形式: ¥5,000,000
   - 色分け: 600万以上(緑), 500万以上(青), 400万未満(赤)

2. 評価列:
   - 星形アイコン表示: ****-
   - ツールチップ: "評価: 4/5"

3. アクティブ列:
   - チェックボックス: o/x
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

**評価ポイント：**
- テーブルModelの高度な実装
- カスタムレンダラーの効果的活用
- 複雑なデータ操作機能

**実装ヒント：**
- AbstractTableModelを継承してfireTableDataChanged()で更新通知
- TableCellRenderer.getTableCellRendererComponent()で描画カスタマイズ
- RowSorterでソート機能、RowFilterでフィルタリング



### 課題2: ツリー構造データブラウザ

**学習目標：** TreeModelの柔軟な実装、大量データの効率的処理、階層データの直感的な操作

**問題説明：**
階層データを管理するツリーブラウザを作成し、ファイルシステムやXMLデータの表示を実装してください。

**技術的背景：JTreeの柔軟性と階層データの可視化**

JTreeは階層構造データの表現に最適なコンポーネントです：

**TreeModelの設計パターン：**
```java
// 遅延読み込みパターン
class LazyTreeNode extends DefaultMutableTreeNode {
    private boolean loaded = false;
    
    public void loadChildren() {
        if (!loaded) {
            // 必要時に子ノードをロード
            SwingUtilities.invokeLater(() -> {
                List<Node> children = fetchChildren();
                children.forEach(child -> 
                    add(new LazyTreeNode(child)));
                loaded = true;
            });
        }
    }
}
```

**実際のアプリケーションでの使用例：**
- **ファイルエクスプローラ**：Windows Explorer、Finder
- **IDEのプロジェクトビュー**：IntelliJ、Eclipse
- **データベースブラウザ**：テーブル、スキーマ表示
- **XML/JSONエディタ**：構造化データの編集

**パフォーマンス最適化技術：**
```java
// 大量ノードへの対応
tree.addTreeWillExpandListener(new TreeWillExpandListener() {
    public void treeWillExpand(TreeExpansionEvent e) {
        TreePath path = e.getPath();
        LazyTreeNode node = (LazyTreeNode)path.getLastPathComponent();
        node.loadChildren();  // 展開時にロード
    }
});
```

**ドラッグ＆ドロップの実装：**
- **TransferHandler**：データ転送の管理
- **ドロップ位置の計算**：ノード間、ノード内
- **視覚的フィードバック**：ドロップ可能位置の強調
- **アンドゥ対応**：移動操作の取り消し

**カスタムレンダラーの活用：**
```java
// アイコンと色分け
class FileTreeCellRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean selected,
            boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(
            tree, value, selected, expanded, leaf, row, hasFocus);
        if (value instanceof FileNode) {
            FileNode node = (FileNode)value;
            setIcon(getIconForFile(node.getFile()));
            if (node.isModified()) {
                setForeground(Color.BLUE);
            }
        }
        return this;
    }
}
```

**階層データの編集機能：**
- **インプレース編集**：F2キーで名前変更
- **コンテキストメニュー**：右クリック操作
- **キーボードナビゲーション**：矢印キーでの移動
- **マルチ選択**：Ctrl+クリック

**実装上の課題と解決策：**
- **循環参照**：WeakReferenceの活用
- **更新通知**：TreeModelListenerの適切な実装
- **展開状態の保存**：TreePathのシリアライズ
- **検索機能**：深さ優先/幅優先探索

この演習では、複雑な階層データの効率的な管理と表示技術を習得します。

**要求仕様：**
- カスタムTreeModelの実装
- ノードの動的読み込み（遅延読み込み）
- ツリーセルレンダラーのカスタマイズ
- 編集可能なツリーノード
- ドラッグ&ドロップによるノード移動

**実行例：**
```
=== ツリー構造データブラウザ ===
ウィンドウ表示: "Tree Data Browser"
サイズ: 800x600ピクセル

ファイルシステムブラウザ:
┌─────────────────────────────┐
│ [更新] [新規フォルダ] [削除]  │ (ツールバー)
├─────────────────────────────┤
│ ┌─ファイルツリー─┐ ┌─詳細─┐  │
│ │├[DIR] C:\        │ │名前:   │  │
│ ││├[DIR] Program.. │ │Documents│  │
│ ││├[DIR] Users     │ │種類:   │  │
│ │││├[DIR] Public  │ │フォルダ │  │
│ │││└[DIR] Admin   │ │サイズ: │  │
│ ││└[DIR] Windows   │ │125 MB  │  │
│ │├[DIR] D:\        │ │更新日: │  │
│ ││├[FILE] file1.txt │ │2024-07 │  │
│ ││├[FILE] file2.doc │ │-04     │  │
│ ││└[IMG] image.jpg │ └───────┘  │
│ │└[USB] E:\ (USB)  │            │
│ └──────────────┘            │
└─────────────────────────────┘

XMLデータブラウザ:
┌─────────────────────────────┐
│ [XML読み込み] [検証] [保存]   │
├─────────────────────────────┤
│ ├[FILE] configuration.xml        │
│ │├[FOLDER] database               │ 
│ ││├[LINK] host: localhost       │
│ ││├[LINK] port: 3306           │
│ ││└[LINK] name: myapp          │
│ │├[FOLDER] logging                │
│ ││├[LINK] level: DEBUG         │
│ ││└[LINK] file: app.log        │
│ │└[FOLDER] features               │
│ │ ├[x] feature1: enabled    │
│ │ └[ ] feature2: disabled    │
│ └[FILE] users.xml              │
│  ├[USER] user[id=1]           │
│  │├[NOTE] name: 田中太郎       │
│  │├[EMAIL] email: tanaka@...   │
│  │└[DATE] created: 2024-01   │
│  └[USER] user[id=2]           │
│   └[NOTE] name: 佐藤花子       │
└─────────────────────────────┘

ツリー操作機能:
1. ノード展開/折りたたみ:
   クリック: [DIR] → [FOLDER] (展開)
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
- フォルダ: [DIR]/[FOLDER] (閉じた/開いた)
- ファイル: 拡張子別アイコン
  - .txt: [TXT], .doc: [DOC], .jpg: [CAMERA]
  - .exe: [SETTING], .zip: [PACKAGE]
- 特殊: [USB](USBドライブ), [NET](ネットワーク)

状態表示:
- 読み取り専用: [LOCK] アイコン
- 隠しファイル: 半透明表示
- 最近更新: [STAR] マーク
- エラー: [ERROR] アイコン

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

**評価ポイント：**
- TreeModelの柔軟な実装
- 大量データの効率的な処理
- 階層データの直感的な操作

**実装ヒント：**
- DefaultMutableTreeNodeでノード作成
- TreeWillExpandListenerで遅延読み込み
- DefaultTreeCellRendererを継承してアイコンと表示をカスタマイズ



### 課題3: データバインディングシステム

**学習目標：** PropertyChangeListenerによる双方向バインディング、複雑な依存関係の管理、リアルタイム更新の最適化

**問題説明：**
データモデルとGUIコンポーネントを自動同期するバインディングシステムを実装してください。

**技術的背景：MVCパターンとリアクティブプログラミング**

データバインディングは、モダンなGUIフレームワークの中核技術です：

**双方向バインディングの仕組み：**
```java
// JavaBeansプロパティ変更通知
public class Person {
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private String name;
    
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        pcs.firePropertyChange("name", oldName, name);
    }
    
    public void addPropertyChangeListener(
            PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
}
```

**データバインディングの利点：**
- **宣言的プログラミング**：何をするかを記述
- **自動同期**：手動更新コードの削減
- **保守性向上**：UIとロジックの分離
- **テスタビリティ**：UIなしでモデルテスト可能

**実際のフレームワークでの実装：**
- **JavaFX**：Property APIとバインディング
- **Angular**：双方向データバインディング
- **React**：単方向データフロー+setState
- **Vue.js**：リアクティブシステム

**リアクティブプログラミングの概念：**
```java
// 計算プロパティの例
class Invoice {
    private DoubleProperty price = new SimpleDoubleProperty();
    private DoubleProperty quantity = new SimpleDoubleProperty();
    private DoubleBinding total;
    
    public Invoice() {
        // 自動計算バインディング
        total = price.multiply(quantity);
    }
}
```

**バインディングの種類：**
1. **単純バインディング**：1対1のプロパティ接続
2. **式バインディング**：計算式による結合
3. **条件バインディング**：if-then-else的な結合
4. **コレクションバインディング**：リスト同期

**パフォーマンス最適化技術：**
- **遅延評価**：必要時まで計算を遅延
- **バッチ更新**：複数変更を一括処理
- **弱参照**：メモリリーク防止
- **変更通知の最適化**：不要な通知の抑制

**実装上の注意点：**
- **循環参照**：A→B→A の依存関係
- **メモリリーク**：リスナーの削除忘れ
- **スレッド安全性**：EDT外からの更新
- **無限ループ**：相互更新の制御

この演習では、プロダクションレベルのデータバインディングシステムを構築します。

**要求仕様：**
- PropertyChangeListenerによる双方向バインディング
- データ検証とエラー表示の自動化
- 計算プロパティとリアルタイム更新
- バインディング式の実装
- 依存関係の管理

**実行例：**
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
- 空文字: [ERROR] "名前は必須です"
- 長すぎ: [ERROR] "名前は50文字以内です"
- 正常: [OK] 緑色枠線

年齢フィールド:
- 負の値: [ERROR] "年齢は0以上です"
- 範囲外: [ERROR] "年齢は150以下です"
- 正常: [OK] 緑色枠線

給与フィールド:
- 文字列: [ERROR] "数値を入力してください"
- 負の値: [ERROR] "給与は0以上です"
- 正常: [OK] 緑色枠線

複数コンポーネント同期:
┌─────────────────────────────┐
│ 給与設定                     │
│ スライダー: ├──●───┤ 600万   │
│ スピナー: [6000000] [UP][DOWN]      │
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
salaryRank = salary < 400万 ? "初級" : 
             salary < 600万 ? "中級" : "上級"

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

**評価ポイント：**
- 双方向バインディングの実装
- 複雑な依存関係の管理
- リアルタイム更新の最適化

**実装ヒント：**
- PropertyChangeSupportでイベント通知
- DocumentListenerでテキストフィールドの変更検出
- WeakReferenceで循環参照を防止



### 課題4: ダッシュボードアプリケーション

**学習目標：** 複雑なレイアウトシステムの構築、動的なコンポーネント管理、高度な視覚化機能の実装

**問題説明：**
複数のウィジェットを配置できるダッシュボードシステムを作成してください。

**要求仕様：**
- 動的なウィジェット配置システム
- リサイズ可能なパネル
- チャート・グラフコンポーネント
- ウィジェットの設定とカスタマイズ
- レイアウトの保存・読み込み

**実行例：**
```
=== ダッシュボードアプリケーション ===
ウィンドウ表示: "Executive Dashboard"
サイズ: 1200x800ピクセル

ダッシュボードレイアウト:
┌─────────────────────────────────────────────────────────┐
│ [+ ウィジェット追加] [レイアウト保存] [設定] [フルスクリーン] │
├─────────────────────────────────────────────────────────┤
│ ┌─売上グラフ────────┐ ┌─KPI指標─┐ ┌─タスク状況─┐   │
│ │     [CHART]           │ │[MONEY] 月間売上│ │[FILE] 完了: 85%│   │
│ │   /\    /\       │ │ ¥12.5M   │ │[SYNC] 進行: 12%│   │
│ │  /  \  /  \      │ │          │ │[PAUSE] 保留: 3% │   │
│ │ /    \/    \     │ │[USERS] 顧客数 │ └───────────┘   │
│ │/            \    │ │  1,247   │                 │
│ └─────────────────┘ │          │ ┌─通知センター─┐   │
│                     │[GRAPH] 成長率  │ │[BELL] 新着: 5件 │   │
│ ┌─地域別売上────────┐ │ +15.3%   │ │[EMAIL] 未読: 12件│   │
│ │[WORLD] 地域マップ      │ └─────────┘ │[WARN] 警告: 2件 │   │
│ │ 北海道: 8%       │             │[TARGET] 締切: 3件 │   │
│ │ 関東: 45%        │ ┌─株価情報─┐ └───────────┘   │
│ │ 関西: 25%        │ │[GRAPH] NIKKEI │                 │
│ │ 九州: 12%        │ │ 28,450   │ ┌─天気予報───┐   │
│ │ その他: 10%      │ │ +245 [UP-RIGHT]  │ │[SUN] 東京      │   │
│ └─────────────────┘ └─────────┘ │ 25°C 晴れ   │   │
│                                   │ 湿度: 60%   │   │
│ ┌─システム状況──────────────────────┐ │ 風速: 3m/s  │   │
│ │[PC] CPU: ████░░░░░░ 40%         │ └───────────┘   │
│ │[MEM] メモリ: ██████░░ 60%       │                 │
│ │[DISK] ディスク: ███░░░░ 30%      │                 │
│ │[NET] ネットワーク: ██████████ 100%│                 │
│ └─────────────────────────────────┘                 │
└─────────────────────────────────────────────────────────┘

ウィジェット操作:
1. 追加:
   [+ ウィジェット追加] → ウィジェットギャラリー表示
   ┌─ウィジェットギャラリー─┐
   │ [CHART] チャート          │
   │ [GRAPH] ラインチャート    │
   │ [PIE] 円グラフ          │
   │ [FILE] リスト            │
   │ [CALENDAR] カレンダー        │
   │ [TEMP] メーター          │
   │ [NEWS] ニュースフィード  │
   │ [CLOCK] 時計              │
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

**評価ポイント：**
- 複雑なレイアウトシステムの構築
- 動的なコンポーネント管理
- 高度な視覚化機能の実装

**実装ヒント：**
- JLayeredPaneで重なり順管理
- ComponentListenerでリサイズ検出
- Graphics2Dで高品質なチャート描画



## 実装のヒント

### 高度なGUIのポイント

1. **MVCパターン**: モデル、ビュー、コントローラの分離
2. **カスタムレンダリング**: 独自の描画ロジック実装
3. **データバインディング**: モデルとビューの自動同期
4. **パフォーマンス**: 大量データの効率的な処理
5. **ユーザビリティ**: 直感的で使いやすいインターフェイス
6. **拡張性**: プラグイン機能やカスタマイズ対応

### よくある落とし穴
- モデルとビューの直接的な結合
- 大量データ表示時のパフォーマンス問題
- カスタムレンダラーのメモリリーク
- 複雑な依存関係による更新ループ

### 設計のベストプラクティス
- MVCパターンの徹底的な適用
- インターフェイスによる疎結合設計
- 観察者パターンによるイベント通知
- 遅延読み込みによるパフォーマンス最適化



## 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter19/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── AdvancedTableSystem.java
│   ├── TreeDataBrowser.java
│   ├── DataBindingSystem.java
│   └── Dashboard.java
├── advanced/       # 応用レベル課題
├── challenge/      # 発展レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```



## 完了確認チェックリスト

### 基礎レベル
- [ ] 高度なテーブル操作システムが実装できている
- [ ] ツリー構造データブラウザが作成できている
- [ ] データバインディングシステムが構築できている
- [ ] ダッシュボードアプリケーションが開発できている

### 技術要素
- [ ] MVCパターンを理解して実装できている
- [ ] カスタムレンダラーとエディタが作成できている
- [ ] データモデルとビューの適切な分離ができている
- [ ] 複雑なレイアウトとコンポーネント管理ができている

### 応用レベル
- [ ] 大量データの効率的な処理ができている
- [ ] 複雑な依存関係を持つシステムが構築できている
- [ ] ユーザビリティの高いインターフェイスが設計できている
- [ ] 拡張可能なアーキテクチャが実装できている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより高度なGUIアーキテクチャに挑戦しましょう！

## 本章の学習目標

### 前提知識

本章を学習するための必須前提として、第18章で学んだGUIイベント処理を確実に習得していることが必要です。各種イベントリスナの実装、ラムダ式を使った簡潔な記述、イベント駆動プログラミングの本質的な理解など、前章で学んだすべての要素が本章の基礎となります。また、コレクションフレームワークの実践的な使用経験も重要で、List、Set、Mapなどのデータ構造を適切に選択し、効率的に操作できる能力が求められます。高度なGUIコンポーネントは内部的にこれらのデータ構造を活用しているため、その理解が不可欠です。さらに、Model-View-Controller（MVC）パターンの基本概念について理解していることも重要です。データ（Model）、表示（View）、制御（Controller）の役割分担という設計思想が、本章で学ぶ高度なコンポーネントの根幹をなしているからです。

設計経験の前提として、基本的なGUIアプリケーションの開発経験があることが望ましいです。単純なフォームやダイアログを作成し、イベント処理を実装した経験があれば、本章で扱う高度なコンポーネントの必要性をより深く理解できます。また、データと表示の分離に対する問題意識を持っていることも重要です。すべてのロジックを一つのクラスに詰め込んだ結果、コードが複雑になり保守が困難になった経験があれば、MVCパターンの価値を実感を持って理解できるでしょう。

### 学習目標

本章では、プロフェッショナルなGUIアプリケーション開発に不可欠な高度なコンポーネントとアーキテクチャについて学習します。知識理解の面では、まずJList、JTable、JTreeといった高度なGUIコンポーネントの内部的な仕組みを理解します。これらのコンポーネントは、単純なボタンやテキストフィールドとは異なり、大量のデータを効率的に表示し、ユーザーとの複雑なインタラクションを可能にする洗練された設計になっています。特に、データの管理と表示を分離するモデル・ビューアーキテクチャの実装方法を深く理解することが重要です。

SwingにおけるMVCアーキテクチャの実装についても詳しく学びます。Swingのコンポーネントは、厳密なMVCではなく、モデルとビューを分離し、コントローラの役割をビューとリスナが分担する変形MVCパターンを採用しています。この設計により、同じデータを複数の異なる方法で表示したり、データの変更を自動的にすべての表示に反映させたりすることが可能になります。また、カスタムレンダラーとエディタの概念を理解することで、標準的な表示では対応できない複雑な要求にも柔軟に対応できるようになります。

技能習得の観点では、データモデルを活用した動的なリストやテーブルの実装技術を習得します。DefaultListModel、DefaultTableModel、AbstractTableModelなどを使い分け、データの追加・削除・更新に応じて自動的に表示が更新される動的なUIを構築します。カスタムセルレンダラーを使った表示のカスタマイズでは、セルごとに異なる色、フォント、アイコンを表示したり、複雑なコンポーネントをセル内に配置したりする高度な技術を学びます。また、ツリー構造のような階層的データや、データベースから取得した大量のレコードなど、複雑なデータ構造をGUIで効果的に表現する方法も習得します。

アーキテクチャ設計能力の面では、MVCパターンを適用した保守性の高いGUI設計手法を学びます。ビジネスロジックをモデルに集約し、表示ロジックをビューに分離することで、変更に強く、テスト可能なアプリケーションを構築する方法を習得します。データ駆動型UIアプリケーションの設計では、データの変更が自動的にUIに反映される仕組みを活用し、コードの重複を削減しながら一貫性のある動作を実現します。また、拡張性と再利用性を考慮したコンポーネント設計により、新しい要求に対して既存のコードを最小限の変更で対応できるようになります。

最終的な到達レベルとして、複雑なデータをGUIで効果的に表現できるようになることを目指します。これには、大量のデータを扱う際のパフォーマンス最適化、ユーザビリティを考慮した直感的な操作性の実現、そして視覚的に分かりやすい情報の提示が含まれます。MVCパターンを活用した保守性の高いアプリケーション設計により、長期的なメンテナンスや機能拡張にも対応できる堅牢なシステムを構築できるようになります。カスタムコンポーネントとデータモデルの実装能力により、標準コンポーネントでは実現できない独自の要求にも対応でき、最終的には企業レベルのGUIアプリケーション設計ができる実力を身につけます。



## 13.1 モデル・ビュー・コントローラ（MVC）アーキテクチャ

これまでのGUIプログラミングでは、画面の見た目（ビュー）と、ボタンが押されたときの処理（ロジック）を1つのクラスにまとめて記述してきました。しかし、アプリケーションが複雑になると、この方法ではコードが非常に見通し悪く、修正が困難になります。

そこで、GUIアプリケーションの設計で広く用いられるのが**MVC（Model-View-Controller）**という設計パターンです。MVCは、アプリケーションを以下の3つの役割に分割します。

1.  **Model（モデル）**: アプリケーションの核となるデータと、そのデータを操作するビジネスロジックを担当します。ビューやコントローラについては何も知りません。
2.  **View（ビュー）**: 画面表示を担当します。モデルからデータを取得して表示しますが、データの加工やビジネスロジックは行いません。
3.  **Controller（コントローラ）**: ユーザーからの入力（イベント）を受け取り、それに応じてモデルを更新したり、ビューの表示を切り替えたりする「司令塔」の役割を担います。

この3つを分離することで、それぞれの独立性が高まり、変更やテストが容易になります。たとえば、見た目（View）だけを変更したい場合、ModelやControllerのコードに触る必要がありません。

Swingの`JList`や`JTable`といった高度なコンポーネントは、このMVCアーキテクチャが色濃く反映された設計になっています。

## 13.2 `JList`: 項目の一覧を表示する

`JList`は、複数の項目を一覧表示するためのコンポーネントです。ファイルリストや、メールの件名一覧など、さまざまな場所で使われます。

### `JList`と`DefaultListModel`

`JList`は、表示（View）そのものを担当し、リストのデータ（Model）は`ListModel`インターフェイスを実装したクラスが管理します。最も簡単に使えるモデルが`DefaultListModel`です。

```java
import javax.swing.*;
import java.awt.*;

public class JListExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JList Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 250);
        frame.setLayout(new BorderLayout());

        // 1. Modelの作成 (データを管理する)
        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("りんご");
        listModel.addElement("バナナ");
        listModel.addElement("みかん");

        // 2. Viewの作成 (モデルを渡して表示する)
        JList<String> fruitList = new JList<>(listModel);

        // --- 操作用パネル ---
        JPanel controlPanel = new JPanel(new FlowLayout());
        JTextField inputField = new JTextField(10);
        JButton addButton = new JButton("追加");
        JButton removeButton = new JButton("削除");
        controlPanel.add(inputField);
        controlPanel.add(addButton);
        controlPanel.add(removeButton);

        // 3. Controllerの役割 (イベント処理)
        // 追加ボタンの処理
        addButton.addActionListener(e -> {
            String newItem = inputField.getText();
            if (!newItem.isEmpty()) {
                listModel.addElement(newItem); // Modelを更新
                inputField.setText("");
            }
        });

        // 削除ボタンの処理
        removeButton.addActionListener(e -> {
            int selectedIndex = fruitList.getSelectedIndex();
            if (selectedIndex != -1) {
                listModel.remove(selectedIndex); // Modelを更新
            }
        });

        frame.add(new JScrollPane(fruitList), BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```
この例では、`listModel`が**Model**、`fruitList`が**View**、ボタンの`ActionListener`が**Controller**の役割を担っています。重要なのは、**イベント処理では`JList`を直接操作するのではなく、`DefaultListModel`を更新している**点です。モデルが変更されると、ビューである`JList`は自動的に再描画されます。

## 13.3 `JTable`: 表形式のデータを扱う

`JTable`は、スプレッドシートのような表形式でデータを表示・編集するための、非常に強力なコンポーネントです。

### `JTable`と`DefaultTableModel`

`JTable`も`JList`と同様にMVCにもとづいており、表示（View）を`JTable`が、データ（Model）を`TableModel`が担当します。

```java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class JTableExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JTable Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        // 1. Modelの作成
        // ヘッダーの定義
        String[] columnNames = {"名前", "年齢", "部署"};
        // データの定義
        Object[][] data = {
            {"山田 太郎", 30, "開発部"},
            {"佐藤 花子", 25, "営業部"},
            {"鈴木 一郎", 35, "人事部"}
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

        // 2. Viewの作成
        JTable table = new JTable(tableModel);

        // --- 操作用パネル ---
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("行を追加");
        
        // 3. Controllerの役割
        addButton.addActionListener(e -> {
            // Modelに行を追加
            Object[] newRow = {"新人 幸子", 22, "研修中"};
            tableModel.addRow(newRow);
        });
        controlPanel.add(addButton);

        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### カスタムテーブルModelの実装

`DefaultTableModel`は便利ですが、より高度な制御が必要な場合は`AbstractTableModel`を継承してカスタムモデルを作成します。

```java
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTableModel extends AbstractTableModel {
    private final String[] columnNames = {"ID", "名前", "年齢", "部署", "給与"};
    private final List<Employee> employees = new ArrayList<>();
    
    public static class Employee {
        private int id;
        private String name;
        private int age;
        private String department;
        private double salary;
        
        public Employee(int id, String name, int age, 
                String department, double salary) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.department = department;
            this.salary = salary;
        }
        
        // getters and setters...
        public int getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) {
            this.department = department;
        }
        public double getSalary() { return salary; }
        public void setSalary(double salary) {
            this.salary = salary;
        }
    }
    
    @Override
    public int getRowCount() {
        return employees.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Employee employee = employees.get(rowIndex);
        switch (columnIndex) {
            case 0: return employee.getId();
            case 1: return employee.getName();
            case 2: return employee.getAge();
            case 3: return employee.getDepartment();
            case 4: return employee.getSalary();
            default: return null;
        }
    }
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Employee employee = employees.get(rowIndex);
        switch (columnIndex) {
            case 1: employee.setName((String) value); break;
            case 2: employee.setAge((Integer) value); break;
            case 3: employee.setDepartment((String) value); break;
            case 4: employee.setSalary((Double) value); break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // IDは編集不可、その他は編集可能
        return columnIndex != 0;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: case 2: return Integer.class;
            case 1: case 3: return String.class;
            case 4: return Double.class;
            default: return Object.class;
        }
    }
    
    public void addEmployee(Employee employee) {
        employees.add(employee);
        int lastRow = employees.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public void removeEmployee(int rowIndex) {
        employees.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
}
```

### カスタムセルレンダラーの実装

表示をカスタマイズするために、セルレンダラーを作成できます。

```java
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SalaryRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        
        Component component = super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);
        
        if (value instanceof Double) {
            Double salary = (Double) value;
            
            // 給与に応じて背景色を変更
            if (salary >= 8000000) {
                // ライトグリーン
                component.setBackground(new Color(144, 238, 144));
            } else if (salary >= 5000000) {
                // ライトイエロー
                component.setBackground(new Color(255, 255, 224));
            } else {
                // ライトピンク
                component.setBackground(new Color(255, 182, 193));
            }
            
            // 通貨形式でフォーマット
            setText(String.format("¥%,.0f", salary));
        }
        
        return component;
    }
}

// 使用例
JTable table = new JTable(employeeTableModel);
table.getColumnModel().getColumn(4).setCellRenderer(new SalaryRenderer());
```

## まとめ

本章では、`JList`や`JTable`といった、より複雑なデータを扱うための高度なGUIコンポーネントについて学びました。

-   複雑なGUIアプリケーションを構築する際には、**MVCアーキテクチャ**にもとづいて役割を分離することが重要です。
-   Swingの高度なコンポーネントは、表示を担当する**View**（`JList`, `JTable`）と、データを管理する**Model**（`DefaultListModel`, `DefaultTableModel`）に分かれています。
-   イベント処理（**Controller**）では、Viewを直接操作するのではなく、**Modelを更新**することで、間接的にViewの表示を変化させます。

このMVCの考え方を身につけることで、変更に強く、テストしやすい、プロフェッショナルなGUIアプリケーションを構築するための基礎が固まります。
