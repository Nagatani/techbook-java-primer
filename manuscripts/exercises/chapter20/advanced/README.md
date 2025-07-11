# 第20章 高度なGUIコンポーネント - 応用レベル課題

## 概要
応用レベル課題では、複数の高度なコンポーネントを組み合わせた実用的なアプリケーションを開発します。カスタムモデル、高度なレンダリング、データバインディングなど、プロフェッショナルレベルの技術を習得します。

## 課題一覧

### 課題1: データベース連携テーブルエディタ
**DatabaseTableEditor.java**

データベースと連携した高機能テーブルエディタを実装します。

**要求仕様：**
- データベースからのデータ読み込み（JDBC使用）
- ページング機能（大量データ対応）
- フィルタリングと検索機能
- インライン編集とバッチ更新
- トランザクション管理
- エクスポート機能（CSV、Excel）

**実装のポイント：**
```java
public class DatabaseTableEditor extends JFrame {
    private JTable table;
    private DatabaseTableModel tableModel;
    private JTextField searchField;
    private JComboBox<Integer> pageSizeCombo;
    private JLabel statusLabel;
    
    // ページング対応のTableModel
    public class DatabaseTableModel extends AbstractTableModel {
        private Connection connection;
        private List<Row> currentPage;
        private int pageSize = 100;
        private int currentPageNumber = 0;
        private int totalRows;
        private String filterQuery = "";
        
        public void loadPage(int pageNumber) {
            SwingWorker<List<Row>, Void> worker = new SwingWorker<>() {
                @Override
                protected List<Row> doInBackground() throws Exception {
                    String sql = buildQuery(pageNumber);
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        stmt.setInt(1, pageSize);
                        stmt.setInt(2, pageNumber * pageSize);
                        
                        ResultSet rs = stmt.executeQuery();
                        List<Row> rows = new ArrayList<>();
                        
                        while (rs.next()) {
                            rows.add(extractRow(rs));
                        }
                        return rows;
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        currentPage = get();
                        fireTableDataChanged();
                        updateStatus();
                    } catch (Exception e) {
                        showError("データ読み込みエラー", e);
                    }
                }
            };
            worker.execute();
        }
        
        // バッチ更新
        public void saveChanges() {
            try {
                connection.setAutoCommit(false);
                
                for (Row row : currentPage) {
                    if (row.isModified()) {
                        updateRow(row);
                    }
                }
                
                connection.commit();
                JOptionPane.showMessageDialog(
                    DatabaseTableEditor.this,
                    "変更を保存しました"
                );
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    // ログ記録
                }
                showError("保存エラー", e);
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    // ログ記録
                }
            }
        }
    }
    
    // 高度なフィルタリング
    private void setupFiltering() {
        // リアルタイム検索
        DocumentListener searchListener = new DocumentListener() {
            private Timer searchTimer = new Timer(300, e -> performSearch());
            {
                searchTimer.setRepeats(false);
            }
            
            @Override
            public void insertUpdate(DocumentEvent e) { triggerSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { triggerSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { triggerSearch(); }
            
            private void triggerSearch() {
                searchTimer.restart();
            }
        };
        
        searchField.getDocument().addDocumentListener(searchListener);
    }
}
```

### 課題2: 高機能ファイルマネージャー
**AdvancedFileManager.java**

プロ仕様のファイルマネージャーを実装します。

**要求仕様：**
- デュアルペインビュー（分割表示）
- ファイルプレビュー機能
- 高度な検索機能（正規表現、日付、サイズ）
- ブックマークとクイックアクセス
- ファイル操作のキューイング
- プログレス表示

**実装のポイント：**
```java
public class AdvancedFileManager extends JFrame {
    private JSplitPane mainSplitPane;
    private FilePanel leftPanel;
    private FilePanel rightPanel;
    private PreviewPanel previewPanel;
    private OperationQueue operationQueue;
    
    // ファイルパネル（TreeとTableの切り替え可能）
    class FilePanel extends JPanel {
        private JTree fileTree;
        private JTable fileTable;
        private CardLayout cardLayout;
        private ViewMode currentMode = ViewMode.TABLE;
        
        // 高度なファイルテーブルモデル
        class FileTableModel extends AbstractTableModel {
            private List<FileInfo> files;
            private FileComparator comparator;
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Icon.class;
                    case 1: return String.class;
                    case 2: return Long.class;
                    case 3: return Date.class;
                    case 4: return String.class;
                    default: return Object.class;
                }
            }
            
            // カスタムソート
            public void sort(int column, SortOrder order) {
                comparator = new FileComparator(column, order);
                Collections.sort(files, comparator);
                fireTableDataChanged();
            }
        }
        
        // ファイルプレビュー
        private void showPreview(File file) {
            SwingWorker<PreviewData, Void> worker = new SwingWorker<>() {
                @Override
                protected PreviewData doInBackground() throws Exception {
                    return PreviewGenerator.generate(file);
                }
                
                @Override
                protected void done() {
                    try {
                        previewPanel.setPreview(get());
                    } catch (Exception e) {
                        previewPanel.showError(e.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }
    
    // 非同期ファイル操作
    class OperationQueue {
        private final BlockingQueue<FileOperation> queue;
        private final ExecutorService executor;
        
        public void addOperation(FileOperation operation) {
            queue.offer(operation);
            
            // プログレス表示の更新
            SwingUtilities.invokeLater(() -> {
                progressPanel.addOperation(operation);
            });
        }
        
        private void processOperations() {
            executor.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        FileOperation operation = queue.take();
                        operation.execute();
                        
                        SwingUtilities.invokeLater(() -> {
                            progressPanel.updateOperation(operation);
                            if (operation.isCompleted()) {
                                refreshView();
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }
}
```

### 課題3: 高度なプロパティエディタ
**PropertyEditor.java**

オブジェクトのプロパティを編集する汎用エディタを実装します。

**要求仕様：**
- リフレクションによる自動プロパティ検出
- 型に応じた適切なエディタ選択
- ネストされたオブジェクトの編集
- カスタムエディタの登録機能
- 変更履歴とUndo/Redo

**実装のポイント：**
```java
public class PropertyEditor extends JPanel {
    private JTable propertyTable;
    private PropertyTableModel model;
    private Map<Class<?>, PropertyCellEditor> editors;
    private UndoManager undoManager;
    
    // プロパティテーブルモデル
    class PropertyTableModel extends AbstractTableModel {
        private Object target;
        private List<PropertyDescriptor> properties;
        private Map<String, Object> originalValues;
        
        public void setTarget(Object target) {
            this.target = target;
            this.properties = introspect(target);
            this.originalValues = captureValues();
            fireTableDataChanged();
        }
        
        private List<PropertyDescriptor> introspect(Object obj) {
            try {
                BeanInfo info = Introspector.getBeanInfo(obj.getClass());
                return Arrays.stream(info.getPropertyDescriptors())
                    .filter(pd -> pd.getReadMethod() != null)
                    .filter(pd -> !"class".equals(pd.getName()))
                    .sorted(Comparator.comparing(PropertyDescriptor::getName))
                    .collect(Collectors.toList());
            } catch (IntrospectionException e) {
                return Collections.emptyList();
            }
        }
        
        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col != 1) return;
            
            PropertyDescriptor pd = properties.get(row);
            Object oldValue = getValueAt(row, col);
            
            try {
                // Undo対応
                PropertyEdit edit = new PropertyEdit(pd, oldValue, value);
                pd.getWriteMethod().invoke(target, value);
                undoManager.addEdit(edit);
                fireTableCellUpdated(row, col);
            } catch (Exception e) {
                showError("値の設定エラー", e);
            }
        }
    }
    
    // カスタムプロパティエディタ
    class ColorPropertyEditor extends AbstractCellEditor 
                            implements TableCellEditor {
        private JButton button;
        private Color currentColor;
        
        public ColorPropertyEditor() {
            button = new JButton();
            button.addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(
                    PropertyEditor.this, "色の選択", currentColor);
                if (newColor != null) {
                    currentColor = newColor;
                    fireEditingStopped();
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected,
                int row, int column) {
            currentColor = (Color) value;
            button.setBackground(currentColor);
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            return currentColor;
        }
    }
}
```

## 学習のポイント

1. **高度なモデル実装**
   - 遅延読み込み
   - ページング
   - キャッシング
   - 非同期データ取得

2. **カスタムレンダラー/エディタ**
   - 複雑なセルレンダリング
   - インラインエディタ
   - 型別エディタの実装

3. **パフォーマンス最適化**
   - 仮想化テクニック
   - バッチ処理
   - プログレッシブレンダリング

4. **統合機能**
   - データベース連携
   - ファイルシステム操作
   - プレビュー生成

## 提出物

1. 完全に動作するアプリケーション
2. クラス設計図（UML）
3. パフォーマンステストレポート
4. ユーザーマニュアル

## 評価基準

- **機能の完成度（35%）**: 高度な要求の実現
- **設計品質（30%）**: MVCパターン、拡張性
- **パフォーマンス（20%）**: 大量データ処理能力
- **ユーザビリティ（15%）**: 操作性、視認性

## 発展課題

- プラグインアーキテクチャの実装
- マルチウィンドウ対応
- クラウドストレージ連携
- 高度な可視化機能

## 参考リソース

- [SwingX Project](https://github.com/arotenberg/swingx)
- [JIDE Components](https://www.jidesoft.com/)
- [Advanced Swing Components](https://docs.oracle.com/javase/tutorial/uiswing/components/)