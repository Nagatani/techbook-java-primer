# 付録F: Swingの発展的な機能<small>イベントの通知・ドラッグ&ドロップ・ツリーとセルの編集</small>

書籍の第19章「高度なイベント処理パターン」と第20章「JTable・JList・JTree」から移した、Swingの発展的な機能の解説です。
本文では各機能の要点だけを説明し、まとまったコード例をこの付録に置いています。
第19章・第20章の発展課題とチャレンジ課題で、ここで紹介する機能を使います。

各節のコードは抜粋です。コンパイルして実行できる全文は`code/`の下にあります（Java 25。各ディレクトリで`javac *.java`のあと、`java クラス名`で実行します）。

## 目次

- PropertyChangeSupportによるイベントの通知（第19章）
- ドラッグ&ドロップ（第19章）
- イベント処理の最適化（デバウンスとスロットル、第19章）
- JTreeの動的な操作（第20章）
- JTableのカスタムセルエディター（第20章）
- 統合型データ管理アプリケーション（第20章）

## PropertyChangeSupportによるイベントの通知

第19章のサンプルコード19-4では、イベントクラス、リスナーインターフェイス、リスナーの一覧をすべて自分で書きました。
JavaBeans（JavaBeansは、Javaで再利用できる部品を作るための仕様で、JDK 1.1から使えます。名前は、Javaという言葉がコーヒーを連想させることにちなんだ「コーヒー豆」のしゃれです）の`PropertyChangeSupport`を使うと、リスナーの一覧の管理と通知をこのクラスに任せられます。
以下のコードは、株価の変化を複数の表示パネルに通知する株価モニターの例です。
`PropertyChangeSupport`では、プロパティの名前を指定して、そのプロパティの変化だけを受け取るリスナーを登録できます。

コードの抜粋（コンパイルできる全文：[code/JavaBeansEventExample/](code/JavaBeansEventExample/)のJavaBeansEventExample.java）

```java
// モデルクラス（PropertyChangeSupport を使用）
class StockPrice {
    private String symbol;
    private double price;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    // ...
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    // ...
    public void setPrice(double newPrice) {
        double oldPrice = this.price;
        this.price = newPrice;
        pcs.firePropertyChange("price", oldPrice, newPrice);
    }
}

// 株価表示コンポーネント
class StockPricePanel extends JPanel implements PropertyChangeListener {
    // ...
    public StockPricePanel(StockPrice stock) {
        // ...
        previousPrice = stock.getPrice();
        stock.addPropertyChangeListener("price", this);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("price".equals(evt.getPropertyName())) {
            double newPrice = (Double) evt.getNewValue();
            double oldPrice = (Double) evt.getOldValue();
            
            priceLabel.setText(String.format("$%.2f", newPrice));
            
            double change = newPrice - oldPrice;
            double changePercent = (change / oldPrice) * 100;
            
            changeLabel.setText(String.format("%+.2f (%+.2f%%)", change, changePercent));
            // ...
        }
    }
}

// メインアプリケーション
public class JavaBeansEventExample {
    private final JFrame frame =
            new JFrame("JavaBeansイベントモデルの例（株価モニター）");
    // ...
    private final Random random = new Random();
    // ...
    private void startPriceUpdates() {
        updateTimer = new Timer(1000, e -> {
            for (StockPrice stock : stocks.values()) {
                // ランダムな価格変動（-2%～+2%）
                double change = (random.nextDouble() - 0.5) * 0.04;
                double newPrice = stock.getPrice() * (1 + change);
                stock.setPrice(newPrice);
            }
        });
        updateTimer.start();
    }
    // ...
}
```

実行すると、4つの銘柄の株価を並べたウィンドウが開きます。
「更新開始」ボタンを押すと、1秒ごとに株価がランダムに変わり、各パネルが価格と変化量を表示します。
値上がりした銘柄は緑、値下がりした銘柄は赤で表示し、「更新停止」ボタンで更新を止めます。

## ドラッグ&ドロップ

以下のコードは、2つのリストの間でアイテムをドラッグして移動する処理と、外部からファイルをドロップする処理を実装した例です。
リスト間の移動は`TransferHandler`で、ファイルのドロップは`DropTarget`で受け付け、ドロップされたファイルの情報を表示します。

このサンプルで使う`JList`と`DefaultListModel`は、第20章「JTable・JList・JTree」で詳しく学びます。
ここでは、`DefaultListModel`に要素を追加・削除すると`JList`の表示が更新される、という点だけを押さえてください。

コードの抜粋（コンパイルできる全文：[code/DragAndDropExample/](code/DragAndDropExample/)のDragAndDropExample.java）

```java
public class DragAndDropExample {
    private final JFrame frame = new JFrame("ドラッグ&ドロップの例");
    // ...
    public DragAndDropExample() {
        // ...
        // ドラッグ機能の設定
        sourceList.setDragEnabled(true);
        sourceList.setTransferHandler(new ListTransferHandler());
        
        // ドロップ機能の設定
        targetList.setTransferHandler(new ListTransferHandler());
        // ...
    }
    // ...
    // リスト用のTransferHandler
    class ListTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }
        
        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            
            JList<String> list = (JList<String>) support.getComponent();
            DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
            
            try {
                String data = (String) support.getTransferable()
                    .getTransferData(DataFlavor.stringFlavor);
                
                JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
                int index = dl.getIndex();
                
                if (index == -1) {
                    model.addElement(data);
                } else {
                    model.add(index, data);
                }
                
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        // ...
    }
    
    // ファイルドロップ用のリスナー
    class FileDropTargetListener extends DropTargetAdapter {
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = dtde.getTransferable();
                
                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    List<File> files = (List<File>) transferable
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    
                    for (File file : files) {
                        dropArea.append("ドロップされたファイル: " + file.getAbsolutePath() + "\n");
                        dropArea.append("  サイズ: " + file.length() + " bytes\n");
                        dropArea.append("  更新日時: " + new Date(file.lastModified()) + "\n\n");
                    }
                }
                
                dtde.dropComplete(true);
                // ...
        }
        
        // ...
    }
    // ...
}
```

実行すると、「ソース」と「ターゲット」の2つのリストと、ファイルをドロップする欄が並んだウィンドウが開きます。
「ソース」のアイテムを「ターゲット」へドラッグすると、アイテムが移動します。
デスクトップなどからファイルを下の欄へドロップすると、ファイルのパス、サイズ、更新日時を表示します。

## イベント処理の最適化（デバウンスとスロットル）

マウスの移動のように短い間隔で大量に発生するイベントでは、イベントごとに重い処理をすると画面の反応が遅くなります。
処理の回数を減らす方法として、デバウンスとスロットルがあります（デバウンス（debounce）は電子回路の用語に由来します。スイッチを押した瞬間に接点が細かく跳ねて（bounce）信号が何度も切り替わる現象を、1回の入力にまとめる処理をこう呼びます）。

- デバウンス
    + イベントが一定時間発生しなくなってから、1回だけ処理する
- スロットル
    + 前回の処理から一定時間がたつまでのイベントは処理せず、一定の間隔でだけ処理する

以下のコードは、`mouseMoved`に対して両方を実装した例です。
デバウンスは、入力が止まってから検索する機能や自動保存にも使えます。

コードの抜粋（コンパイルできる全文：[code/EventOptimizationExample/](code/EventOptimizationExample/)のEventOptimizationExample.java）

```java
public class EventOptimizationExample {
    private final JFrame frame = new JFrame("イベント処理の最適化");
    // ...
    private Timer debounceTimer;
    private long lastThrottleTime = 0;
    private static final int DEBOUNCE_DELAY = 300; // ミリ秒
    private static final int THROTTLE_DELAY = 100; // ミリ秒
    // ...
    public EventOptimizationExample() {
        // ...
        // マウス移動イベント（位置の表示は毎回、描画とログはデバウンスとスロットルで間引く）
        drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            // ...
            @Override
            public void mouseMoved(MouseEvent e) {
                eventCount++;
                statusLabel.setText(String.format("マウス位置: (%d, %d) - イベント数: %d", 
                    e.getX(), e.getY(), eventCount));
                
                // デバウンス処理
                debounceMouseMove(e.getPoint());
                
                // スロットル処理
                throttleMouseMove(e.getPoint());
            }
        });
        // ...
        // デバウンスタイマーの初期化
        debounceTimer = new Timer(DEBOUNCE_DELAY, e -> {
            log("デバウンス: マウス移動が停止しました - " + currentMousePos);
        });
        debounceTimer.setRepeats(false);
        // ...
    }
    // ...
    // デバウンス: 最後のイベントから一定時間後に処理
    private void debounceMouseMove(Point point) {
        currentMousePos = point;
        debounceTimer.restart();
    }
    
    // スロットル: 一定間隔で処理
    private void throttleMouseMove(Point point) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastThrottleTime >= THROTTLE_DELAY) {
            lastThrottleTime = currentTime;
            drawPanel.repaint();
            log("スロットル: 描画更新 - " + point);
        }
    }
    // ...
}
```

実行すると、マウスの位置とイベントの数を表示するラベルと、白い描画パネルを並べたウィンドウが開きます。
パネルでマウスを動かすと、イベントの数は動かしているあいだ増え続けます。
一方で、スロットルによる描画の更新は100ミリ秒に1回までになり、右のログに記録します。
マウスを止めて300ミリ秒たつと、デバウンスによる「マウス移動が停止しました」をログに1回だけ記録します。

## JTreeの動的な操作

ユーザーの操作に応じてツリーノードを動的に追加・削除する実装例です。

以下のコードは、右クリックメニューからノードを追加・削除できるツリーの例です。
ポップアップメニュー、名前を編集できるノード、カスタムレンダラーを組み合わせ、ファイルエクスプローラーのような操作を実現しています。
ノードの追加・削除は`DefaultTreeModel`を通して行い、ツリー構造の変更をビューに通知します（表示したあとに`DefaultMutableTreeNode`の`add`で直接ノードを足すと、モデルが変更を知らないため、画面のツリーに反映されません。その場合は、`DefaultTreeModel`の`reload`などで変更を伝えます）。

コードの抜粋（コンパイルできる全文：[code/DynamicTreeExample/](code/DynamicTreeExample/)のDynamicTreeExample.java）

```java
public class DynamicTreeExample {
    private final JFrame frame = new JFrame("動的なツリー操作");
    private DefaultTreeModel treeModel;
    // ...
    public DynamicTreeExample() {
        // ...
        // ルートノードとモデルの作成
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ファイルシステム");
        treeModel = new DefaultTreeModel(root);
        // ...
        // JTreeの作成
        tree = new JTree(treeModel);
        tree.setEditable(true); // ノード名の編集を許可
        tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // ポップアップメニューの作成
        JPopupMenu popup = new JPopupMenu();
        // ...
    }
    
    private void addNode(String name, boolean isFolder) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();
        
        if (parentPath == null) {
            parentNode = (DefaultMutableTreeNode) treeModel.getRoot();
        } else {
            parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
        }
        
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
        newNode.setAllowsChildren(isFolder);
        treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
        
        // 新しいノードを表示
        tree.scrollPathToVisible(new TreePath(newNode.getPath()));
    }
    
    private void deleteNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = 
                (DefaultMutableTreeNode) currentSelection.getLastPathComponent();
            MutableTreeNode parent = (MutableTreeNode) currentNode.getParent();
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
            }
        }
    }
    // ...
}
```

## JTableのカスタムセルエディター

テーブルセルに特殊な入力コンポーネントを使用する例です。

以下のコードは、JTableにカスタムセルエディターとレンダラーを実装する例です。
色の列はカラーピッカー、優先度の列はコンボボックス、完了の列はチェックボックスで編集でき、列のデータ型に合った入力部品を使います。

コードの抜粋（コンパイルできる全文：[code/CustomEditorExample/](code/CustomEditorExample/)のCustomEditorExample.java）

```java
// カラーセルエディター
class ColorCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JButton button;
    private Color currentColor;
    
    public ColorCellEditor() {
        button = new JButton();
        button.setActionCommand("edit");
        button.addActionListener(e -> {
            if ("edit".equals(e.getActionCommand())) {
                // カラーチューザーを表示
                Color newColor = JColorChooser.showDialog(
                    button, "色を選択", currentColor);
                if (newColor != null) {
                    currentColor = newColor;
                }
                fireEditingStopped();
            }
        });
        button.setBorderPainted(false);
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        currentColor = (Color) value;
        button.setBackground(currentColor);
        return button;
    }
    
    @Override
    public Object getCellEditorValue() {
        return currentColor;
    }
}
// ...
// コンボボックスエディター
class PriorityCellEditor extends DefaultCellEditor {
    public PriorityCellEditor() {
        super(new JComboBox<>(new String[]{"低", "中", "高", "緊急"}));
    }
}
// ...
public class CustomEditorExample {
    private final JFrame frame = new JFrame("カスタムエディターの例");
    public CustomEditorExample() {
        // ...
        // テーブルの作成
        JTable table = new JTable(model);
        table.setRowHeight(30);
        
        // カスタムエディターとレンダラーの設定
        table.getColumnModel().getColumn(1).setCellEditor(new PriorityCellEditor());
        table.getColumnModel().getColumn(2).setCellRenderer(new ColorCellRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ColorCellEditor());
        // ...
    }
    // ...
}
```

## 統合型データ管理アプリケーション

第20章で学んだJTable・JTree・JListを1つの画面に組み合わせた、プロジェクト管理アプリケーションの例です。
第20章の基礎課題「ファイルの一覧」（JTreeとJTableを`JSplitPane`で左右に並べ、ツリーで選んだフォルダーの中身を表に出す）も、この例と同じ組み合わせで作ります。

画面は、第17章「GUIプログラミングの基礎」で学んだ`JSplitPane`で分割します。
左側のツリーでプロジェクトを選択すると、右上のテーブルにそのプロジェクトのタスクが、右下のリストにメンバーが表示されます。
タスクの表は表示専用で、プロジェクトのタスクを`DefaultTableModel`に1行ずつ入れ直して表示します。
メニューバーには、アプリケーションを終了する「ファイル」メニューと、画面下のステータスバーの表示・非表示を切り替える「表示」メニューがあります。
ステータスバーには、選択中のプロジェクト名が表示されます。

データは、プロジェクトを表す`Project`クラスと、タスクを表す`Task`クラスで持ちます。
`Project`は名前とタスクのリスト、メンバーの名前のリストを持ちます。
`Task`は、第20章のカスタムレンダラーを使ったJListの例で使ったものと同じクラスで、題名・説明・期限・完了したかどうかを持ちます。
プロジェクトは名前をキーにした`LinkedHashMap`に入れ、登録した順にツリーへ並べます。

コードの抜粋（コンパイルできる全文：[code/IntegratedDataManagementApp/](code/IntegratedDataManagementApp/)のIntegratedDataManagementApp.java。`Task`クラスは同じディレクトリのTask.java）

```java
public class IntegratedDataManagementApp {
    private final JFrame frame = new JFrame("プロジェクト管理システム");
    // ...
    private void createProjectTree() {
        // ...
        projectTree = new JTree(root);
        projectTree.addTreeSelectionListener(
                new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    projectTree.getLastSelectedPathComponent();
                if (node == null) return;
                
                // プロジェクトノードが選択された場合
                if (node.getLevel() == 1) {
                    String projectName = node.getUserObject().toString();
                    currentProject = projects.get(projectName);
                    updateTaskTable();
                    updateMemberList();
                    statusLabel.setText("選択中: " + projectName);
                }
            }
        });
    }
    // ...
    private void updateTaskTable() {
        DefaultTableModel model =
                (DefaultTableModel) taskTable.getModel();
        model.setRowCount(0);
        
        if (currentProject != null) {
            for (Task task : currentProject.getTasks()) {
                model.addRow(new Object[]{
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate().format(DateTimeFormatter
                            .ofPattern("yyyy/MM/dd")),
                    task.isCompleted()
                });
            }
        }
    }
    // ...
    private void setupLayout() {
        // ...
        // 分割ペインの設定
        rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            topRightPanel, bottomRightPanel);
        rightSplitPane.setDividerLocation(400);
        
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            leftPanel, rightSplitPane);
        mainSplitPane.setDividerLocation(300);
        
        frame.add(mainSplitPane, BorderLayout.CENTER);
        // ...
    }
    // ...
}
```

### ツリーで選んだプロジェクトを受け取る

`createProjectTree`では、ルートの「プロジェクト一覧」の下にプロジェクトのノードを置き、その下に「タスク」と「メンバー」のノードを置きます。
ツリーは表示する前に組み立て終えるので、`DefaultMutableTreeNode`の`add`で直接ノードを足しています（表示したあとにノードを足すときは、この付録の「JTreeの動的な操作」のように`DefaultTreeModel`を通します）。

ツリーで選んだノードが変わると、`addTreeSelectionListener`で登録した`TreeSelectionListener`の`valueChanged`が呼ばれます。
選ばれたノードは`getLastSelectedPathComponent()`で受け取ります。
選択が外れたときは`null`が返るので、先に確かめています。
`getLevel()`はルートからの深さで、ルートが0、プロジェクトのノードが1です。
深さが1のノード、つまりプロジェクトが選ばれたときだけ、ノードの名前（`getUserObject()`）でプロジェクトを探し、表とリストとステータスバーを更新します。
「タスク」「メンバー」やその下のノードを選んだときは、何もしません。

### 表とリストの中身を入れ直す

`updateTaskTable`は、`setRowCount(0)`で表の行をすべて消してから、選んだプロジェクトのタスクを`addRow`で1行ずつ足します。
`DefaultTableModel`の`setRowCount`や`addRow`は、変更を表に知らせる処理を内部で行うので、`fireTableRowsInserted()`などを自分で呼ぶ必要はありません。
期限の列には、`LocalDateTime`を`DateTimeFormatter`で「2026/11/24」の形の文字列にして入れています。

全文の`createTaskTable`では、`DefaultTableModel`を匿名クラスで継承し、2つのメソッドをオーバーライドしています。
`getColumnClass`で状態の列を`Boolean.class`と知らせると、その列は文字列の「true」ではなくチェックボックスで表示されます。
`isCellEditable`で常に`false`を返し、表を表示専用にしています。
表のセルを編集できるようにしても、`DefaultTableModel`のなかの値が変わるだけで、元の`Task`オブジェクトには反映されないためです。
表の編集を元のデータに反映させたいときは、第20章で学んだ`AbstractTableModel`を継承したモデルに`Task`のリストを持たせ、`setValueAt`で`Task`を書き換えます。

メンバーの一覧の`updateMemberList`も同じ形で、`DefaultListModel`の`clear()`で空にしてから`addElement()`でメンバーを足します。
`DefaultListModel`も、変更をリストに知らせる処理を内部で行います。

### 画面を分割する

`setupLayout`では、ツリー・表・リストをそれぞれ`JScrollPane`に入れ、題名付きの枠（`createTitledBorder`）を付けたパネルに置きます。
`JSplitPane`は、2つの部品を境目で分けて並べる部品です。
`VERTICAL_SPLIT`を指定すると上下に、`HORIZONTAL_SPLIT`を指定すると左右に並べます。
この例では、右側の表とリストを上下に分けた`rightSplitPane`を作り、それを左側のツリーと左右に分けた`mainSplitPane`に入れて、3つの領域に分けています。
`setDividerLocation`は境目の位置（ピクセル）で、利用者は境目をドラッグして大きさを変えられます。

フレームの中央（`BorderLayout.CENTER`）に分割ペインを、下端（`BorderLayout.SOUTH`）にステータスバーのパネルを置きます。
「表示」メニューの`JCheckBoxMenuItem`はチェックの付いたメニュー項目で、チェックを外すとステータスバーのパネルを`setVisible(false)`で隠します。

実行すると、左にプロジェクトのツリー、右上にタスクの表、右下にメンバーのリストを並べたウィンドウが開きます。
ツリーで「Webアプリケーション開発」を選ぶと、表に「要件定義」と「DB設計」の2つのタスクが、リストに「田中太郎」と「鈴木花子」が表示され、ステータスバーが「選択中: Webアプリケーション開発」に変わります。
「モバイルアプリ開発」を選ぶと、表とリストの中身がそのプロジェクトのものに入れ替わります。
タスクの期限は、実行した日からの日数で決めているので、実行した日によって変わります。
