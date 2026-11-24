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
