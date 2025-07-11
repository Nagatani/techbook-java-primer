# 第20章 高度なGUIコンポーネント - 基礎レベル課題

## 概要
本ディレクトリでは、JTable、JTree、JListなどの高度なSwingコンポーネントの基礎を学習します。MVCパターンを理解し、データと表示を分離した効率的なGUIアプリケーションの開発技術を身につけます。

## 課題一覧

### 課題1: 基本的なテーブル操作
**BasicTableDemo.java**

JTableを使用した基本的なデータ表示と操作を実装します。

**要求仕様：**
- カスタムTableModelの実装
- 編集可能なセルの設定
- 行の追加・削除機能
- ソート機能の実装
- 列幅の自動調整

**実装のポイント：**
```java
// カスタムTableModelの実装
public class StudentTableModel extends AbstractTableModel {
    private List<Student> students = new ArrayList<>();
    private final String[] columnNames = {"ID", "名前", "年齢", "成績"};
    
    @Override
    public int getRowCount() {
        return students.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student student = students.get(rowIndex);
        switch (columnIndex) {
            case 0: return student.getId();
            case 1: return student.getName();
            case 2: return student.getAge();
            case 3: return student.getGrade();
            default: return null;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0; // ID以外は編集可能
    }
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Student student = students.get(rowIndex);
        switch (columnIndex) {
            case 1: student.setName((String) value); break;
            case 2: student.setAge((Integer) value); break;
            case 3: student.setGrade((Double) value); break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
```

### 課題2: ツリー構造の実装
**FileTreeExplorer.java**

JTreeを使用したファイルシステムエクスプローラーを作成します。

**要求仕様：**
- ファイルシステムのツリー表示
- 遅延読み込み（ディレクトリ展開時）
- アイコンの表示
- 右クリックメニュー
- ファイル情報の表示

**実装のポイント：**
```java
// カスタムTreeModelの実装
public class FileSystemTreeModel implements TreeModel {
    private File root;
    private List<TreeModelListener> listeners = new ArrayList<>();
    
    public FileSystemTreeModel(File root) {
        this.root = root;
    }
    
    @Override
    public Object getRoot() {
        return root;
    }
    
    @Override
    public Object getChild(Object parent, int index) {
        File directory = (File) parent;
        File[] children = directory.listFiles();
        if (children != null && index < children.length) {
            return children[index];
        }
        return null;
    }
    
    @Override
    public int getChildCount(Object parent) {
        File file = (File) parent;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            return children != null ? children.length : 0;
        }
        return 0;
    }
}

// カスタムレンダラー
class FileTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        
        super.getTreeCellRendererComponent(
            tree, value, sel, expanded, leaf, row, hasFocus);
        
        File file = (File) value;
        setText(file.getName());
        
        if (file.isDirectory()) {
            setIcon(folderIcon);
        } else {
            setIcon(fileIcon);
        }
        
        return this;
    }
}
```

### 課題3: リストとコンボボックスの連携
**LinkedListComboApp.java**

JListとJComboBoxを連携させたデータ選択インターフェイスを実装します。

**要求仕様：**
- カテゴリ選択用JComboBox
- アイテム表示用JList
- カテゴリ変更時のリスト更新
- 複数選択と単一選択の切り替え
- カスタムリストレンダラー

**実装のポイント：**
```java
// リストモデルの動的更新
public class CategoryItemApp extends JFrame {
    private JComboBox<String> categoryCombo;
    private JList<Item> itemList;
    private DefaultListModel<Item> listModel;
    private Map<String, List<Item>> categoryItems;
    
    private void setupComponents() {
        // カテゴリ選択時の処理
        categoryCombo.addActionListener(e -> {
            String category = (String) categoryCombo.getSelectedItem();
            updateItemList(category);
        });
        
        // カスタムリストレンダラー
        itemList.setCellRenderer(new ListCellRenderer<Item>() {
            private JPanel panel = new JPanel(new BorderLayout());
            private JLabel iconLabel = new JLabel();
            private JLabel textLabel = new JLabel();
            private JLabel priceLabel = new JLabel();
            
            {
                panel.add(iconLabel, BorderLayout.WEST);
                panel.add(textLabel, BorderLayout.CENTER);
                panel.add(priceLabel, BorderLayout.EAST);
            }
            
            @Override
            public Component getListCellRendererComponent(
                    JList<? extends Item> list, Item value,
                    int index, boolean isSelected,
                    boolean cellHasFocus) {
                
                iconLabel.setIcon(value.getIcon());
                textLabel.setText(value.getName());
                priceLabel.setText(String.format("¥%,d", value.getPrice()));
                
                if (isSelected) {
                    panel.setBackground(list.getSelectionBackground());
                } else {
                    panel.setBackground(list.getBackground());
                }
                
                return panel;
            }
        });
    }
}
```

### 課題4: スピナーとスライダーの活用
**NumericInputPanel.java**

JSpinnerとJSliderを使った数値入力インターフェイスを作成します。

**要求仕様：**
- 連動するスピナーとスライダー
- 値の範囲制限
- カスタムSpinnerModel
- 値変更時のリアルタイムプレビュー
- 単位表示とフォーマット

**実装のポイント：**
```java
// スピナーとスライダーの連携
SpinnerNumberModel spinnerModel = new SpinnerNumberModel(50, 0, 100, 1);
JSpinner spinner = new JSpinner(spinnerModel);
JSlider slider = new JSlider(0, 100, 50);

// 双方向バインディング
spinner.addChangeListener(e -> {
    int value = (Integer) spinner.getValue();
    slider.setValue(value);
    updatePreview(value);
});

slider.addChangeListener(e -> {
    int value = slider.getValue();
    spinner.setValue(value);
    updatePreview(value);
});
```

## 学習のポイント

1. **MVCパターンの理解**
   - Model: データの管理
   - View: 表示の制御
   - Controller: ユーザー入力の処理

2. **モデルクラスの実装**
   - TableModel、TreeModel、ListModel
   - イベント通知メカニズム
   - データ変更の反映

3. **レンダラーとエディタ**
   - カスタムレンダラーの作成
   - セルエディタの実装
   - パフォーマンスの考慮

4. **選択モデル**
   - ListSelectionModel
   - TreeSelectionModel
   - 選択イベントの処理

## 提出物

1. 各課題のソースコード（*.java）
2. 実行画面のスクリーンショット
3. MVCパターンの適用説明書
4. パフォーマンス測定結果（大量データ時）

## 評価基準

- **機能の実装（40%）**: 要求仕様を満たしているか
- **MVCパターンの適用（25%）**: 適切な責務分離
- **ユーザビリティ（20%）**: 使いやすさ、レスポンス
- **コード品質（15%）**: 可読性、拡張性

## 参考リソース

- [How to Use Tables](https://docs.oracle.com/javase/tutorial/uiswing/components/table.html)
- [How to Use Trees](https://docs.oracle.com/javase/tutorial/uiswing/components/tree.html)
- [Swing Models](https://docs.oracle.com/javase/tutorial/uiswing/components/model.html)