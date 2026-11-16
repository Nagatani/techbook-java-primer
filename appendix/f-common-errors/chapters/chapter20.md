# 第20章 JTable・JList・JTree：よくあるエラーと対処法

書籍『Java入門』第20章「JTable・JList・JTree」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

## 並べ替えた表で、選択したのと違う行が削除・変更される

### エラー例

```java
table.setAutoCreateRowSorter(true);
// ...
deleteButton.addActionListener(e -> {
    int row = table.getSelectedRow();
    model.removeRow(row);  // 並べ替えた後は、別の行が消える
});
```

### 原因

- `getSelectedRow()`が返すのは、画面に表示している順での行番号（ビューの行番号）
- 並べ替えや絞り込みをすると、画面の順とモデルの中の順が食い違う。ビューの行番号をそのままモデルに渡すと、別の行を操作してしまう
- 並べ替えていないうちは番号が一致するため、気づきにくい

### 対処法

コンパイルできる全文：[chapter20-code/SortedRowDelete/](chapter20-code/SortedRowDelete/)

```java
deleteButton.addActionListener(e -> {
    int viewRow = table.getSelectedRow();  // 画面上の行番号
    if (viewRow < 0) {
        return;  // 何も選択されていない
    }
    // 並べ替えた後は画面の行番号とモデルの行番号が違うので、変換してから使う
    int modelRow = table.convertRowIndexToModel(viewRow);
    model.removeRow(modelRow);
});
```

逆に、モデルの行番号から画面の行を選ぶときは`convertRowIndexToView`を使います。
列も、利用者がドラッグで並べ替えられるので、`convertColumnIndexToModel`で変換します。

## 自作のテーブルモデルでデータを変えても、表示が変わらない

### エラー例

```java
public class TaskTableModel extends AbstractTableModel {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);  // リストには入るが、テーブルの表示は増えない
    }
    // ...
}
```

### 原因

- JTableは、モデルから変更の知らせ（`TableModelEvent`）を受け取ったときだけ表示を描き直す
- `AbstractTableModel`を継承したモデルでは、データを変えたあとに知らせを送るのはモデルを書いた人の仕事。`DefaultTableModel`の`addRow`などは、中で知らせを送っている
- 画面をクリックしたりウィンドウの大きさを変えたりすると表示が直ることがあるため、原因がわかりにくい

### 対処法

コンパイルできる全文：[chapter20-code/TaskTableModel/](chapter20-code/TaskTableModel/)

```java
public void addTask(Task task) {
    tasks.add(task);
    int row = tasks.size() - 1;
    // 追加した行をテーブルに知らせる。これがないと画面が更新されない
    fireTableRowsInserted(row, row);
}

public void removeTask(int row) {
    tasks.remove(row);
    fireTableRowsDeleted(row, row);
}

public void setDone(int row, boolean done) {
    tasks.set(row, new Task(tasks.get(row).title(), done));
    fireTableCellUpdated(row, 1);
}
```

どの行が変わったかわからないほど大きく変えたときは、`fireTableDataChanged()`ですべての行を描き直させます。
`AbstractListModel`を継承したJListのモデルでも同じで、`fireIntervalAdded`・`fireIntervalRemoved`・`fireContentsChanged`で知らせます。

## JTreeにノードを追加しても、ツリーに表示されない

### エラー例

```java
DefaultMutableTreeNode child = new DefaultMutableTreeNode("新しい分類");
root.add(child);  // ノードはつながるが、表示されているツリーは変わらない
```

### 原因

- `DefaultMutableTreeNode`の`add`は、ノードどうしをつなぐだけで、ツリーに変更を知らせない
- JTreeが変更を知るのは、`DefaultTreeModel`から知らせを受け取ったとき

### 対処法

コンパイルできる全文：[chapter20-code/TreeNodeAdd/](chapter20-code/TreeNodeAdd/)

```java
DefaultTreeModel model = new DefaultTreeModel(root);
JTree tree = new JTree(model);
// ...
DefaultMutableTreeNode child =
        new DefaultMutableTreeNode("分類" + (root.getChildCount() + 1));
// root.add(child)だけではツリーに知らせが届かず、画面が変わらない
model.insertNodeInto(child, root, root.getChildCount());
tree.scrollPathToVisible(new TreePath(child.getPath()));
```

削除は`removeNodeFromParent`を使います。
ノードの`add`でまとめて組み替えたあとは、`model.reload(node)`でそのノードより下を描き直させます。

## JTableの列の見出しが表示されない

### エラー例

```java
JTable table = new JTable(data, columnNames);
frame.add(table);  // 見出しの行が出ない
```

### 原因

- JTableの見出し（`JTableHeader`）は、JTableを`JScrollPane`に入れたときに、スクロールペインの上端に置かれる
- JTableを直接パネルに置くと、見出しを置く場所がないので表示されない。行が多いときにスクロールもできない

### 対処法

```java
JTable table = new JTable(data, columnNames);
frame.add(new JScrollPane(table));  // 見出しが付き、スクロールもできる
```

どうしてもスクロールペインを使わないときは、`table.getTableHeader()`で見出しを取り出し、`BorderLayout.NORTH`などに自分で置きます。

## セルの編集中にボタンを押すと、入力した値が保存されない

### エラー例

```java
saveButton.addActionListener(e -> {
    // 編集中のセルに入力した値が、まだモデルに入っていない
    for (int row = 0; row < model.getRowCount(); row++) {
        IO.println(model.getValueAt(row, 1));
    }
});
```

### 原因

- セルに入力した値は、Enterキーを押すか別のセルを選んで編集を終えたときに、エディターからモデルへ書き込まれる
- 編集中のままボタンを押すと、編集は続いたままで、モデルには前の値が残っている

### 対処法

コンパイルできる全文：[chapter20-code/EditCommit/](chapter20-code/EditCommit/)

```java
// ほかの部品にフォーカスが移ったら、編集中の値を確定させる
table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

saveButton.addActionListener(e -> {
    // 編集中のセルがあれば、値をモデルに書き込んで編集を終える
    if (table.isEditing()) {
        table.getCellEditor().stopCellEditing();
    }
    // ...
});
```

`stopCellEditing()`は、入力した値をエディターが受け付けないとき（数値の列に文字を入れたときなど）に`false`を返し、編集を続けます。

## JTableのカスタムレンダラーで選択状態が正しく表示されない

### エラー例

```java
table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        JLabel label = new JLabel(value.toString());
        label.setBackground(Color.YELLOW);  // 常に黄色になってしまう
        return label;
    }
});
```

### 原因

- 選択状態やフォーカス状態を適切に処理していない
- コンポーネントの設定が不完全

### 対処法

```java
table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        // 親クラスの処理を利用
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
            setForeground(table.getForeground());
        }
        
        if (hasFocus) {
            setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        } else {
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }
        
        // 値に応じたカスタム表示
        if (value instanceof Number) {
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }
        
        return this;
    }
});
```

## 大量のデータを表示するJTableの動作が遅い

### エラー例

```java
// 10万行のデータを表示すると動作が非常に遅くなる
DefaultTableModel model = new DefaultTableModel();
for (int i = 0; i < 100000; i++) {
    model.addRow(new Object[]{i, "データ" + i, Math.random()});
}
table.setModel(model);
```

### 原因

- 1行追加するたびにモデルが変更を通知し、テーブルが表示を計算し直している
- すべてのデータを一度に作り、メモリに読み込んでいる

JTableが描くのは画面に見えている行だけなので、行数そのものより、データの作り方と通知の回数が問題になります。
データを先にそろえてからモデルを1回で設定するか、次のように、表示に必要な行だけをデータ源から読み込むモデルを作ります。

### 対処法

コンパイルできる全文：[chapter20-code/LazyTableDemo/](chapter20-code/LazyTableDemo/)

```java
// 1. 遅延読み込み（Lazy Loading）の実装
public class LazyTableModel extends AbstractTableModel {
    private final List<Object[]> dataCache = new ArrayList<>();
    private final DataSource dataSource;
    private final int pageSize = 1000;
    // ...
    @Override
    public Object getValueAt(int row, int column) {
        // 必要に応じてデータを読み込み
        ensureDataLoaded(row);
        return dataCache.get(row)[column];
    }
    
    private void ensureDataLoaded(int row) {
        if (row >= dataCache.size() || dataCache.get(row) == null) {
            // ページ単位でデータを読み込み
            int startIndex = (row / pageSize) * pageSize;
            List<Object[]> pageData = dataSource.getData(startIndex, pageSize);
            
            // キャッシュを拡張
            while (dataCache.size() < startIndex + pageData.size()) {
                dataCache.add(null);
            }
            
            // データを設定
            for (int i = 0; i < pageData.size(); i++) {
                dataCache.set(startIndex + i, pageData.get(i));
            }
        }
    }
}

// 2. テーブルの設定
JTable table = new JTable(new LazyTableModel(dataSource));
table.setFillsViewportHeight(true);
table.setRowHeight(20);  // 行の高さを固定にする
// setAutoCreateRowSorter(true)で並べ替えを有効にすると、
// 並べ替えのために全行の値を読むので、遅延読み込みの効果がなくなる
```

## デバッグのヒント

### 1. 行番号の対応を確かめる

並べ替えや絞り込みにかかわる不具合では、ビューとモデルの行番号を並べて出力すると、食い違いがすぐにわかります。

```java
table.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
        int viewRow = table.getSelectedRow();
        IO.println("ビュー: " + viewRow
                + " → モデル: " + table.convertRowIndexToModel(viewRow));
    }
});
```

### 2. モデルの知らせが届いているか確かめる

表示が更新されないときは、モデルにリスナーを登録して、変更の知らせが送られているかを確かめます。

```java
model.addTableModelListener(e ->
        IO.println("変更: 行" + e.getFirstRow() + "〜" + e.getLastRow()
                + " 種類 " + e.getType()));
```

何も表示されなければ、モデルの`fireTable...`の呼び忘れです。

### 3. 処理時間を測る

```java
long startTime = System.nanoTime();
// 処理
long endTime = System.nanoTime();
IO.println("処理時間: " + (endTime - startTime) / 1_000_000 + "ms");
```

これらの対処法を参考に、GUIアプリケーションの開発を進めてください。
複雑な問題に直面したときは、問題を小さく分割し、1つずつ確かめながら解決してください。
