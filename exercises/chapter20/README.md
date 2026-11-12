# 第20章 JTableとJListの基礎 - 演習課題

書籍『Java入門』第20章「JTableとJListの基礎」の演習課題です。

## 基礎課題（[basic/](basic/)）

- 学生名簿の表（AbstractTableModel、TableRowSorter、RowFilter、CSVの保存と読込）
- ファイルの一覧（JTreeとJTableをJSplitPaneで組み合わせる）
- 書籍管理とMVC（Model・View・Controllerの分担、オブザーバーによる通知）
- データのグラフ（paintComponentによる折れ線・棒・円グラフの描画）
- このフォルダーのJavaファイルは、空欄を埋めて完成させる雛形です

## 発展課題（[advanced/](advanced/)）

- 成績一覧表（task1-grade-table）- テーブルモデル、列の型による並べ替え、RowFilterの組み合わせ、セルエディター
- 本棚の分類ツリー（task2-bookshelf-tree）- DefaultTreeModelによるノードの追加・削除、TreeCellRenderer
- このフォルダーのJavaファイルは解答例です

## チャレンジ課題（[challenge/](challenge/)）

- ToDoリスト - JListとJTableをJSplitPaneで組み合わせ、Model・View・Controllerに役割を分ける
- このフォルダーのJavaファイルは解答例です

## 解答例（[solutions/](solutions/)）

基礎課題の解答例です。雛形と同じ名前・同じ構成で、雛形の「ここに実装してください」「TODO」を埋めた形で書いています。

- `StudentManagementGUI.java` - 学生名簿の表
- `FileExplorer.java` - ファイルの一覧（プレビュー、右クリックのメニューでのコピー・移動・削除つき）
- `MVCExample.java` - 書籍管理とMVC
- `DataVisualization.java` - データのグラフ（アニメーション、画像への書き出しつき）
- 第12章のStream APIで短く書ける箇所には、その書き方をコメントで添えている
