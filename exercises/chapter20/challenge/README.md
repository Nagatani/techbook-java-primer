# 第20章 チャレンジ課題

## 学習目標
- JList・JTableを`JSplitPane`で組み合わせ、選んだ項目に応じて表の中身を切り替える
- `AbstractListModel`と`ListCellRenderer`で、リストの中身と見た目を自分で決める
- `AbstractTableModel`の`getColumnClass`を使い、チェックボックスの列や日付の列を作る
- `DefaultCellEditor`と`TableCellRenderer`で、セルの編集方法と表示を変える
- データを持つModel、画面のView、操作をつなぐControllerに役割を分ける（MVC）

## 課題一覧

### 課題1: ToDoリスト
**ファイル**（このフォルダーのファイルは解答例です）:
```
challenge/
├── Priority.java              優先度（列挙型）
├── Task.java                  タスク1件
├── TaskManagerListener.java   Modelの変更を受け取るリスナー
├── TaskManager.java           Model: タスクと分類の管理（GUIなし）
├── CategoryListModel.java     分類一覧のリストモデル
├── CategoryCellRenderer.java  分類に件数を添えて表示するレンダラー
├── TaskTableModel.java        タスクの表のテーブルモデル
├── DueDateCellRenderer.java   期限切れを赤く表示するレンダラー
├── TodoFrame.java             View: 部品の組み立てと配置
├── TodoController.java        Controller: イベントの処理
└── TodoApp.java               mainメソッド（見本のデータを作り、MVCをつなぐ）
```

分類ごとにタスクを管理するToDoリストを作成してください。
左に分類の一覧、右に選んだ分類のタスクの表を置き、下の入力欄からタスクを追加します。

**要求仕様**:
- Model
    - `Task`は題名・分類・優先度（高・中・低）・期限（`LocalDate`）・完了したかどうかを持つ。題名か分類が空なら例外を投げる。未完了で期限が今日より前なら期限切れとする
    - `TaskManager`はタスクと分類の一覧を持つ。タスクの追加（分類がまだなければ分類も追加する）・削除・完了の切り替え・優先度の変更をし、そのたびに登録されたリスナー（`TaskManagerListener`）へ知らせる
    - 分類を指定してタスクの一覧・全体の件数・未完了の件数を返す。分類に`null`を渡したらすべてのタスクを対象にする
    - Swingのクラスを使わない
- 画面用のモデルとレンダラー
    - `CategoryListModel`（`AbstractListModel`を継承）は、先頭に「すべて」を置き、その後に分類を並べる
    - `CategoryCellRenderer`は「学校（未完了 3 / 4件）」のように、件数を添えて表示する
    - `TaskTableModel`（`AbstractTableModel`を継承）は、選んだ分類のタスクを「完了」「題名」「分類」「優先度」「期限」の列で表示する。完了の列は`Boolean`、優先度の列は`Priority`、期限の列は`LocalDate`を列の型にする。完了と優先度だけ編集でき、変更は`TaskManager`に渡す
    - `DueDateCellRenderer`は期限を「11月12日(木)」の形で表示し、期限切れなら赤い文字に、今日が期限なら太字にする
- View（`TodoFrame`）
    - 左右を`JSplitPane`で分け、左に分類の`JList`、右に「完了したタスクを隠す」チェックボックス・削除ボタン・タスクの`JTable`を置く
    - 表は期限の早い順に並べて表示し、列見出しのクリックでも並べ替えられる。優先度のセルはコンボボックスで選ぶ（`DefaultCellEditor`）
    - 下に、題名・分類（入力もできるコンボボックス）・優先度・期限の入力欄と追加ボタン、状態の表示、エラーメッセージの表示を置く
    - イベントの処理は書かない
- Controller（`TodoController`）
    - 分類を選ぶと、表をその分類のタスクに切り替え、追加欄の分類もその分類にする
    - 「完了したタスクを隠す」にチェックを入れると、未完了のタスクだけを表示する（`RowFilter`）
    - 追加ボタンか題名欄でのEnterキーでタスクを追加する。期限が「2026-12-01」の形でないとき、題名が空のときは赤い文字でメッセージを表示する
    - 削除ボタンで、表で選んだタスクを削除する。行を選んでいないときは押せない
    - `TaskManager`から変更が知らされたら、分類の一覧・表・追加欄の分類の選択肢・状態の表示を更新する

**実行例**:

`java TodoApp`を実行すると「ToDoリスト」というウィンドウが開きます。
見本のタスクの期限は起動した日を基準に決まります。
次は2026年11月12日（木）に起動した場合です。

- 左の一覧は次のとおりで、「すべて」が選ばれている

```
すべて（未完了 7 / 8件）
学校（未完了 3 / 4件）
アルバイト（未完了 2 / 2件）
生活（未完了 2 / 2件）
```

- 右の表には8件が期限の早い順に並ぶ。先頭の「履修登録」（11月5日(木)）は完了の列にチェックが付いている。「洗剤を買う」（11月10日(火)）と「線形代数のレポート」（11月11日(水)）は期限が赤い文字、「Javaの演習課題」（11月12日(木)）は太字になっている
- いちばん下の行は`表示 8件　全体: 未完了 7 / 8件（期限切れ 2件）`である
- 左の「学校」を選ぶと、表が学校の4件だけになり、追加欄の分類が「学校」になる
- 「完了したタスクを隠す」にチェックを入れると、「履修登録」が消えて3件になる
- 「線形代数のレポート」の完了の列をクリックしてチェックを付けると、絞り込みによりその行が表から消える。左の一覧は「学校（未完了 2 / 4件）」、下の行は`期限切れ 1件`に変わる
- 「図書館に本を返す」の優先度のセルをクリックすると、高・中・低から選ぶコンボボックスが開く。「高」を選んで「優先度」の見出しをクリックすると、高い順に並ぶ
- 題名に`サークルの会費`、分類に`サークル`と入力し、期限を`2026-11-20`にして追加を押すと、左の一覧の末尾に「サークル（未完了 1 / 1件）」が加わる
- 期限を`11/20`にして追加を押すと、赤い文字で「期限は2026-12-01の形で入力してください」と表示される

## コンパイルと実行

```bash
javac *.java
java TodoApp
```

## 実装のヒント

```java
// Model: 変更したら、登録されたリスナーに知らせる
public void setDone(Task task, boolean done) {
    if (task.isDone() != done) {
        task.setDone(done);
        fireChanged();
    }
}

// TaskTableModel: 表で編集された値は、自分で持たずにModelへ渡す
@Override
public void setValueAt(Object value, int row, int column) {
    Task t = rows.get(row);
    if (column == DONE_COLUMN && value instanceof Boolean done) {
        manager.setDone(t, done);
    }
}

// Controller: Modelが変わったら、画面用のモデルをまとめて更新する
manager.addListener(this::modelChanged);
```

- 列の型を`Boolean.class`にすると、JTableが自動でチェックボックスで表示・編集する。`Priority`や`LocalDate`のように`Comparable`を実装した型は、`TableRowSorter`が`compareTo`で並べ替える
- 表の編集は「`TaskTableModel.setValueAt` → `TaskManager.setDone` → リスナー → `TaskTableModel.refresh`」の順に流れる。画面用のモデルがデータを直接書き換えないので、分類の件数など別の表示も同じ仕組みで更新される
- `refresh`で毎回`fireTableDataChanged`を呼ぶと、表の選択が外れる。行の顔ぶれが変わっていなければ`fireTableRowsUpdated`で値だけを描き直す
- レンダラーに渡される`row`は表示上の行番号である。タスクを取り出すときは`table.convertRowIndexToModel(row)`で直す
- `JList`の選択の変化（`ListSelectionListener`）は、マウスを押している間にも届く。`getValueIsAdjusting()`が`false`のときだけ処理する
- 期限の判定に使う「今日」は、`LocalDate.now()`をあちこちで呼ばず、起動時に1回だけ求めて渡す。見本のデータも今日を基準に作ると、いつ起動しても同じ見え方になる

## 応用のポイント

1. Modelは画面を知らない
    - `TaskManager`はSwingのクラスを使わず、変わったことをリスナーに知らせるだけである。画面を作り直しても、Modelはそのまま使える
2. 1つのModelに複数のView
    - 分類の一覧と表は、同じ`TaskManager`のデータを別の形で見せている。どちらかで変更しても、もう一方も自動で更新される
3. Controllerに操作をまとめる
    - `TodoFrame`は部品を並べるだけで、「追加ボタンを押したら何をするか」は`TodoController`にある。画面の配置を変えても、処理の部分は変わらない

## 発展（任意）

- タスクをテキストファイルに保存し、次に起動したときに読み込む（ファイルの読み書きは第14章で学んだ）
- 表の行をダブルクリックしたら、題名と期限を編集するダイアログを開く
- 分類の名前を変える・分類を削除する機能を加える。分類に属するタスクをどう扱うかを決める
- `TaskManager`のメソッドを、第21章で学ぶJUnitでテストする

## 完了チェックリスト

- [ ] 分類を選ぶと、表がその分類のタスクに切り替わる
- [ ] 完了の列のチェックと優先度のコンボボックスで、タスクを変更できる
- [ ] 完了したタスクを隠すと、チェックを付けたタスクがすぐに消える
- [ ] 期限切れのタスクが赤い文字で表示される
- [ ] タスクを追加・削除すると、分類の件数と状態の表示も更新される
- [ ] `TaskManager`がSwingのクラスを使っておらず、表のモデルがデータを直接書き換えていない

**次のステップ**: チャレンジ課題が完了したら、第21章「ユニットテストと品質保証」に進みましょう。
