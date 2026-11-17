---
source: manuscripts/chapter20-advanced-gui.md
summarized: 2026-11-18
sections:
  - key: "本章の学習目標"
    hash: f30b2d60
  - key: "JTableのしくみと基本"
    hash: 41d25014
  - key: "テーブルのソートとフィルタリング"
    hash: ac8ffa96
  - key: "JTreeの実装"
    hash: 12dbd6fc
  - key: "JListの高度な使用法"
    hash: 0f734c49
  - key: "セルの編集に使う部品を変える"
    hash: d9b19aa4
  - key: "実践的なアプリケーション例"
    hash: b3ba887e
  - key: "よくあるエラーと対処法"
    hash: d2a7f7f8
  - key: "まとめ"
    hash: 6acf341a
  - key: "章末演習"
    hash: feb99734
---

# 第20章 JTable・JList・JTree 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter20-advanced-gui.md` を参照する。

## 章の概要

- 表・リスト・ツリーの形でデータを表示するSwingのコンポーネントとして、JTable（モデル・レンダラー・エディター・ソーターの役割分担、ソートとフィルタリング）、JTree（DefaultMutableTreeNodeによる構築）、JList（カスタムモデルとレンダラー）を扱う
- ツリーの動的な操作とカスタムセルエディターは要点だけを述べ、コード例は付録F（`appendix/f-swing-advanced/`）に置く（2026-11-16）
- 最後にJSplitPaneで3つを組み合わせたプロジェクト管理アプリ（20-5）を示す
- 前提は第17章のSwing基礎、第18・19章のイベント処理、第9章のコレクション、第7章のインターフェイス
- サンプルは20-1〜20-5の5つ

## 構成上の問題

- 節題「JTableの詳細な実装」が中身（役割分担の説明と基本例20-1）と合っていなかったので、2026-11-18に「JTableのしくみと基本」に改めた
- 次の問題は解決済み
    + 旧章題「JTableとJListの基礎」・旧学習目標と本文の水準のずれ（2026-11-14に章題を「JTable・JList・JTree」に改め、学習目標を本文に合わせた）
    + まとめの「AbstractTableModelによるカスタムモデル」「ツールバー」が本文と合わなかった（2026-11-12）
    + 第17〜19章との重複（エラー集のpaintComponent・EDT・SwingWorker）（2026-11-11に付録へ移した。2026-11-14によくあるエラーを本章の内容の7項目に書き直した）
    + 20-1・20-2の実行結果が別のサンプルのものだった、モデルの変更の通知（`fire〜`）を説明していなかった、統合アプリに働かない部品があった（2026-11-14）
    + 2026-11-18に、まとめの「カスタムセルレンダラーの作成」「カスタムツリーセルレンダラーの作成」（本文に例がない）を除いた

## 節ごとの要約

- 本章の学習目標（L3–45）
    + 6項目の目標（JTableと行の選択、TableRowSorter、JTreeとノードの追加・削除、JListのモデルとレンダラー、セルの編集に使う部品（コード例は付録F）、組み合わせたアプリ）
    + JTable・JListの概要とMVC設計の説明。SwingがViewとControllerをまとめた変形のMVCであることと、MVCの由来は第3章で触れたことを脚注で述べる。前提知識に第17〜19章、第9章、第7章、推奨知識に第8章のRecord、MVC、第19章のObserverパターン、第18章のEDT・第19章のSwingWorkerを挙げる
- JTableのしくみと基本（L46–117）
    + JTableは仕事を部品に分けている（値は`TableModel`、描画は`TableCellRenderer`、編集は`TableCellEditor`、並べ替えと絞り込みは`TableRowSorter`）と文章で述べ、役割分担の図（chapter20-jtable-roles）を示す
    + 2次元配列と列名からJTableを作り、JScrollPaneに入れる基本例（20-1）と、表示される内容（列: ID・名前・年齢・メールアドレス、セルはダブルクリックで編集できる）
- テーブルのソートとフィルタリング（L118–194）
    + TableRowSorterによるヘッダークリックのソートと、RowFilterによる名前列のフィルタ（「適用」ボタンで実行）、行の追加・削除（20-2）。PersonTableModel（AbstractTableModelを継承）の役割を前置きで述べ、定義は全文にある
    + コードのあとで、`RowFilter.regexFilter(正規表現, 列の番号)`、`Pattern.quote`で記号を文字として扱うこと、`(?i)`を説明する。並べ替え・絞り込みをした表では画面とモデルの行の順が違うため、「削除」ボタンでは`getSelectedRow()`の結果を`convertRowIndexToModel()`で変換すると説明する（抜粋に削除ボタンのリスナーを含む）
- JTreeの実装（L195–247）
    + DefaultMutableTreeNodeでファイルシステム風のツリーを作る基本例（20-3）。子を持つノードにはハンドルが出て、`setShowsRootHandles(true)`はルートにもハンドルを出す指定だと説明する
    + 表示したあとにノードを追加・削除する: `DefaultTreeModel`の`insertNodeInto`・`removeNodeFromParent`・`nodeChanged`を通すこと（ノードの`add`だけでは表示に反映されないことを脚注で述べる）。右クリックメニューの例は付録Fへ誘導する
- JListの高度な使用法（L248–358）
    + 第17章の17-9（文字列の配列とsetSelectionMode）から、モデルとカスタムレンダラーへ進むと述べる。レンダラーが1つの部品をスタンプのように使い回すことを脚注で説明する
    + `TaskListModel`は`AbstractListModel`を継承し`getSize`・`getElementAt`を実装する。変更をリストに知らせる`fireIntervalAdded`・`fireIntervalRemoved`・`fireContentsChanged`、内部で知らせを送るDefaultListModelとの違い
    + Taskクラス、TaskListModel、JPanelベースのListCellRendererで、タイトル・説明・期限・完了状態を表示するタスクリスト（20-4）。中のパネルを`setOpaque(false)`にして選択色を見せ、文字色も部品ごとに切り替える（完了タスクの灰色は選択していないときだけ）。表のセルやツリーのノードも同じ考え方のレンダラー（`TableCellRenderer`・`TreeCellRenderer`）で表示を変えられると述べる。ダブルクリックで完了を切り替える
- セルの編集に使う部品を変える（L359–366）
    + `DefaultCellEditor`と`JComboBox`を`TableColumn.setCellEditor`で設定すること、`getColumnClass`で`Boolean.class`を返せばチェックボックスになること、標準にない部品は`AbstractCellEditor`を継承し`TableCellEditor`を実装することを数文で述べる。3種類のエディターの例は付録Fへ誘導する
- 実践的なアプリケーション例（L367–442）
    + JTree・JTable・JListをJSplitPaneで組み合わせ、ツリー選択でタスク表とメンバー一覧を切り替えるプロジェクト管理アプリ（20-5）
    + タスクの表は表示専用で、DefaultTableModelに1行ずつ入れ直す。メニューは「ファイル」（終了）と「表示」（ステータスバーの表示切り替え）。ステータスバーに選択中のプロジェクト名を出す
- よくあるエラーと対処法（L443–464）
    + 本文に挙げる問題: 並べ替えた表の行番号の変換（`convertRowIndexToModel()`）、自作のテーブルモデルの変更の通知、JTreeの`insertNodeInto()`、JScrollPaneに入れないと見出しが出ない、編集中の値の確定（`stopCellEditing()`）、レンダラーの選択状態、大量データの7項目。詳細は`appendix/e-common-errors/chapters/chapter20.md`
- まとめ（L465–492）
    + 学習内容5項目（JTable、JTree、JList、MVC、統合アプリ）と次章（ユニットテスト）への導入
- 章末演習（L493–514）
    + 演習リンクは`exercises/chapter20/`。基礎課題（学生名簿の表、ファイルの一覧、書籍管理とMVC、データのグラフ）、発展課題（成績一覧表・本棚の分類ツリー）、チャレンジ課題（ToDoリスト）の3段構成

## 他章との関係

- 第3章: MVCの由来に触れた章として脚注から参照する
- 第17章（GUIプログラミングの基礎）: JListの基本（17-9）を扱う。本章の20-4の前置きから参照する
- 第18章（基本的なイベント処理）: MouseListener、ActionListener、EDTの前提
- 第19章（高度なイベント処理パターン）: Observerパターン、SwingWorkerの前提
- 第21章（ユニットテスト）: まとめと章末演習で次章として導入する
- 付録E: エラー集の詳細を置く
- 付録F（Swingの発展的な機能）: ツリーの動的な操作とカスタムセルエディターのコード例を置く

## 校正で見つけた個別の問題

- 2026-11-12に解消したもの: 「推奨される知識：」のコロン、まとめのリストの字下げ、「次のステップ：」の形、付録側のエラー集の誤り（EDT違反のエラーメッセージ、ダブルバッファリング、大量データのJTable、GridBagLayout）
- 2026-11-14の校閲で解消したもの: JListのレンダラーで中のパネルが不透明なため選択色が見えなかった不具合、統合アプリの抜粋で`setupLayout()`の宣言行が抜けていたこと、20-2で`(`などを入力すると`PatternSyntaxException`になることと削除後に追加するとIDが重複すること、使っていないimportと変数、`setShowsRootHandles`の不正確な説明、統合アプリの`HashMap`による並び順
- 2026-11-16: 付録の再編に合わせ、付録の文字とエラー集のURLを`appendix/e-common-errors/`に直した
- 2026-11-16: 動的なツリー操作（旧20-4）とカスタムセルエディター（旧20-6）のコード例を付録Fへ移し、節「セルの編集に使う部品を変える」にした。JListのレンダラーは20-4、統合アプリは20-5になった
- 2026-11-18の校閲で直したもの
    + JTableの役割分担を本文の文章で説明した（それまでは図だけ）
    + 20-2の正規表現の説明を脚注から本文へ移し、`convertRowIndexToModel()`の説明と削除ボタンの抜粋を加えた
    + 20-2・20-5の抜粋の折り返しを直した（20-5は`DateTimeFormatter`をimportして短くした）
    + まとめから本文にない「カスタムセルレンダラー」「カスタムツリーセルレンダラー」の作成を除いた
