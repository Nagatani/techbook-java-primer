# 第19章 GUIのイベント処理 - 応用レベル課題

## 概要
応用レベル課題では、複数のイベントを組み合わせた高度な対話的アプリケーションを開発します。カスタムイベント、ドラッグ&ドロップ、アニメーションなど、プロフェッショナルなGUI開発技術を習得します。

## 課題一覧

### 課題1: ドラッグ&ドロップファイルマネージャー
**DragDropFileManager.java**

ファイルやフォルダをドラッグ&ドロップで操作できるファイルマネージャーを実装します。

**要求仕様：**
- JTreeでディレクトリ構造を表示
- ファイルのドラッグ&ドロップ移動
- 複数ファイルの選択と一括操作
- 右クリックコンテキストメニュー
- ファイルのプレビュー機能

**実装のポイント：**
```java
public class DragDropFileManager extends JFrame {
    private JTree fileTree;
    private TransferHandler transferHandler;
    
    private void setupDragDrop() {
        // TransferHandlerの実装
        transferHandler = new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }
            
            @Override
            protected Transferable createTransferable(JComponent c) {
                TreePath[] paths = fileTree.getSelectionPaths();
                List<File> files = pathsToFiles(paths);
                return new FileTransferable(files);
            }
            
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(
                    DataFlavor.javaFileListFlavor);
            }
            
            @Override
            public boolean importData(TransferSupport support) {
                // ドロップ処理の実装
                try {
                    List<File> files = (List<File>) support
                        .getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    // ファイル移動処理
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        };
        
        fileTree.setTransferHandler(transferHandler);
        fileTree.setDragEnabled(true);
    }
}
```

**追加機能：**
- ファイルの切り取り/コピー/貼り付け
- プログレスバー付きの大容量ファイル操作
- ファイル検索機能
- お気に入りフォルダ機能

### 課題2: インタラクティブグラフビジュアライザー
**InteractiveGraphVisualizer.java**

マウス操作でノードを配置・接続できるグラフエディタを作成します。

**要求仕様：**
- ノードのドラッグによる移動
- クリックによるノードの追加/削除
- ドラッグによるエッジの接続
- ズーム/パン機能
- グラフレイアウトの自動調整

**実装のポイント：**
```java
public class GraphPanel extends JPanel {
    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private Node selectedNode = null;
    private Node draggedNode = null;
    private Point2D panOffset = new Point2D.Double();
    private double zoomLevel = 1.0;
    
    public GraphPanel() {
        // マウスホイールでズーム
        addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation();
            double scaleFactor = rotation > 0 ? 0.9 : 1.1;
            zoomLevel *= scaleFactor;
            repaint();
        });
        
        // 複合的なマウス操作
        MouseAdapter mouseHandler = new MouseAdapter() {
            private Point lastPoint;
            
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
                Node clickedNode = getNodeAt(e.getPoint());
                
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (clickedNode != null) {
                        if (e.isControlDown()) {
                            // Ctrl+クリックで接続開始
                            startConnection(clickedNode);
                        } else {
                            // 通常クリックでドラッグ開始
                            draggedNode = clickedNode;
                        }
                    } else if (e.isShiftDown()) {
                        // Shift+クリックで新規ノード
                        addNode(e.getPoint());
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    // 右クリックメニュー
                    showContextMenu(e.getPoint());
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    // ノードの移動
                    draggedNode.setLocation(e.getPoint());
                    repaint();
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    // 中ボタンでパン
                    int dx = e.getX() - lastPoint.x;
                    int dy = e.getY() - lastPoint.y;
                    panOffset.setLocation(
                        panOffset.getX() + dx,
                        panOffset.getY() + dy);
                    lastPoint = e.getPoint();
                    repaint();
                }
            }
        };
        
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }
}
```

### 課題3: カスタムイベントシステム
**CustomEventSystem.java**

独自のイベントとリスナーを定義したアプリケーションを実装します。

**要求仕様：**
- カスタムイベントクラスの定義
- イベントリスナーインターフェース
- イベントの発火と伝播
- イベントバブリング/キャプチャリング
- イベントの優先度管理

**実装のポイント：**
```java
// カスタムイベント定義
public class DataChangeEvent extends EventObject {
    private final String propertyName;
    private final Object oldValue;
    private final Object newValue;
    
    public DataChangeEvent(Object source, String propertyName,
                          Object oldValue, Object newValue) {
        super(source);
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}

// リスナーインターフェース
public interface DataChangeListener extends EventListener {
    void dataChanged(DataChangeEvent e);
}

// イベント発行可能なモデル
public class ObservableModel {
    private final EventListenerList listenerList = new EventListenerList();
    
    public void addDataChangeListener(DataChangeListener l) {
        listenerList.add(DataChangeListener.class, l);
    }
    
    protected void fireDataChange(String property, 
                                Object oldValue, 
                                Object newValue) {
        DataChangeEvent event = new DataChangeEvent(
            this, property, oldValue, newValue);
        
        for (DataChangeListener listener : 
             listenerList.getListeners(DataChangeListener.class)) {
            listener.dataChanged(event);
        }
    }
}
```

## 学習のポイント

1. **高度なイベント処理パターン**
   - イベントの合成と変換
   - イベントフィルタリング
   - イベントの優先度制御
   - 非同期イベント処理

2. **ドラッグ&ドロップの実装**
   - TransferHandlerの活用
   - データフレーバーの理解
   - ドラッグジェスチャーの認識

3. **カスタムイベントの設計**
   - EventObjectの継承
   - リスナーパターンの実装
   - イベントの効率的な配信

4. **複合的な操作の実装**
   - 修飾キーとの組み合わせ
   - マルチタッチジェスチャー
   - コンテキスト依存の操作

## 提出物

1. 各課題の完全なソースコード
2. 操作説明書（動画またはGIF）
3. イベントフロー図
4. パフォーマンス測定結果

## 評価基準

- **機能の完成度（35%）**: 高度な要求仕様の実現
- **操作性（25%）**: 直感的で快適な操作
- **設計品質（25%）**: イベント処理の効率性
- **創造性（15%）**: 独自の機能追加

## 発展課題

- ジェスチャー認識の実装
- アニメーション効果の追加
- マルチモーダル入力対応
- アクセシビリティ機能

## 参考リソース

- [Drag and Drop Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/dnd/)
- [Creating Custom Events](https://docs.oracle.com/javase/tutorial/uiswing/events/generalrules.html)
- [Advanced Event Handling](https://www.oracle.com/technical-resources/articles/java/swingworker.html)