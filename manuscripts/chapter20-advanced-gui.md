# <b>20章</b> <span>JTableとJListの基礎</span> <small>表形式データの表示</small>

## 本章の学習目標

### 前提知識

**必須**：
- 第18章のGUIプログラミングの基礎（Swing、AWT、基本コンポーネント）
- 第19章のGUIイベント処理（ActionListener、MouseListener等）
- 第10章のコレクションフレームワーク（List、Set、Map）
- 第7章の抽象クラスとインターフェイス

**推奨**：
- 第12章のRecord（データ表現）
- MVCアーキテクチャパターンの基本概念
- オブザーバーパターンの理解
- 第16章のマルチスレッドプログラミング（SwingUtilities.invokeLater）

### 学習目標

#### 知識理解目標
- JTableとJListの基本的な使い方
- 表形式データの表示と操作
- リストコンポーネントの活用

#### 技能習得目標
- 基本的なJTableの作成とデータ表示
- JListでのアイテム選択と表示
- 基本的なテーブル操作（選択、クリック）

#### 実践的な活用目標
- 簡単なデータ表示アプリケーションの作成
- ユーザーが選択できるリストの実装

#### 到達レベルの指標
- テーブルとリストを使った基本的なGUIアプリケーションが作成できる

## JTableの詳細な実装

### 基本的なJTableの作成

JTableは、表形式のデータを表示・編集するための強力なコンポーネントです。まず、基本的な使い方から見ていきましょう。

```java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BasicTableExample extends JFrame {
    public BasicTableExample() {
        setTitle("基本的なJTableの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 列名の定義
        String[] columnNames = {"ID", "名前", "年齢", "メールアドレス"};
        
        // データの定義
        Object[][] data = {
            {1, "田中太郎", 25, "tanaka@example.com"},
            {2, "鈴木花子", 30, "suzuki@example.com"},
            {3, "佐藤次郎", 28, "sato@example.com"},
            {4, "山田美咲", 22, "yamada@example.com"}
        };
        
        // JTableの作成
        JTable table = new JTable(data, columnNames);
        
        // スクロールペインに追加
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        setSize(600, 300);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicTableExample().setVisible(true);
        });
    }
}
```

## テーブルのソートとフィルタリング

JTableは組み込みのソート機能を提供しています。

```java
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class SortableTableExample extends JFrame {
    private PersonTableModel model;
    private JTable table;
    private TableRowSorter<PersonTableModel> sorter;
    
    public SortableTableExample() {
        setTitle("ソート可能なテーブル");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // モデルとテーブルの作成
        model = new PersonTableModel();
        table = new JTable(model);
        
        // ソーターの設定
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        // フィルタリング機能の追加
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel filterLabel = new JLabel("フィルター:");
        JTextField filterField = new JTextField(20);
        JButton filterButton = new JButton("適用");
        
        filterButton.addActionListener(e -> {
            String text = filterField.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                // 名前列でフィルタリング
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
            }
        });
        
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);
        filterPanel.add(filterButton);
        
        // レイアウト
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // 操作パネル
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("追加");
        JButton deleteButton = new JButton("削除");
        
        addButton.addActionListener(e -> {
            int newId = model.getRowCount() + 1;
            model.addPerson(new Person(newId, "新規ユーザー", 20, "new@example.com"));
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                model.removePerson(modelRow);
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(700, 400);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SortableTableExample().setVisible(true);
        });
    }
}
```

## JTreeの実装

### 基本的なJTreeの作成

JTreeは階層構造のデータを表示するコンポーネントです。

```java
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class BasicTreeExample extends JFrame {
    public BasicTreeExample() {
        setTitle("基本的なJTreeの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ルートノードの作成
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("プロジェクト");
        
        // 子ノードの追加
        DefaultMutableTreeNode src = new DefaultMutableTreeNode("src");
        DefaultMutableTreeNode lib = new DefaultMutableTreeNode("lib");
        DefaultMutableTreeNode doc = new DefaultMutableTreeNode("doc");
        
        root.add(src);
        root.add(lib);
        root.add(doc);
        
        // srcフォルダの中身
        DefaultMutableTreeNode main = new DefaultMutableTreeNode("main");
        DefaultMutableTreeNode test = new DefaultMutableTreeNode("test");
        src.add(main);
        src.add(test);
        
        // mainフォルダの中身
        main.add(new DefaultMutableTreeNode("App.java"));
        main.add(new DefaultMutableTreeNode("Utils.java"));
        main.add(new DefaultMutableTreeNode("Config.java"));
        
        // JTreeの作成
        JTree tree = new JTree(root);
        tree.setShowsRootHandles(true);
        
        // スクロールペインに追加
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);
        
        setSize(400, 500);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicTreeExample().setVisible(true);
        });
    }
}
```

### 動的なツリー操作

ユーザーの操作に応じてツリーノードを動的に追加・削除する実装例です。

```java
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

public class DynamicTreeExample extends JFrame {
    private DefaultTreeModel treeModel;
    private JTree tree;
    
    public DynamicTreeExample() {
        setTitle("動的なツリー操作");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ルートノードとモデルの作成
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ファイルシステム");
        treeModel = new DefaultTreeModel(root);
        
        // 初期フォルダの追加
        DefaultMutableTreeNode documents = new DefaultMutableTreeNode("Documents");
        DefaultMutableTreeNode downloads = new DefaultMutableTreeNode("Downloads");
        DefaultMutableTreeNode desktop = new DefaultMutableTreeNode("Desktop");
        
        root.add(documents);
        root.add(downloads);
        root.add(desktop);
        
        // JTreeの作成
        tree = new JTree(treeModel);
        tree.setEditable(true); // ノード名の編集を許可
        tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // ポップアップメニューの作成
        JPopupMenu popup = new JPopupMenu();
        JMenuItem addFolder = new JMenuItem("フォルダを追加");
        JMenuItem addFile = new JMenuItem("ファイルを追加");
        JMenuItem delete = new JMenuItem("削除");
        JMenuItem rename = new JMenuItem("名前変更");
        
        popup.add(addFolder);
        popup.add(addFile);
        popup.addSeparator();
        popup.add(rename);
        popup.add(delete);
        
        // ポップアップメニューのアクション
        addFolder.addActionListener(e -> addNode("新しいフォルダ", true));
        addFile.addActionListener(e -> addNode("新しいファイル.txt", false));
        delete.addActionListener(e -> deleteNode());
        rename.addActionListener(e -> renameNode());
        
        // マウスリスナーでポップアップを表示
        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }
            
            private void showPopup(java.awt.event.MouseEvent e) {
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    tree.setSelectionPath(path);
                    popup.show(tree, e.getX(), e.getY());
                }
            }
        });
        
        // カスタムレンダラーの設定
        tree.setCellRenderer(new FileSystemTreeCellRenderer());
        
        // レイアウト
        add(new JScrollPane(tree), BorderLayout.CENTER);
        
        // 情報パネル
        JPanel infoPanel = new JPanel();
        JLabel infoLabel = new JLabel("右クリックでメニューを表示");
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
        
        setSize(500, 600);
        setLocationRelativeTo(null);
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
    
    private void renameNode() {
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            tree.startEditingAtPath(path);
        }
    }
    
    // カスタムツリーセルレンダラー
    class FileSystemTreeCellRenderer extends DefaultTreeCellRenderer {
        private Icon folderIcon = UIManager.getIcon("Tree.closedIcon");
        private Icon fileIcon = UIManager.getIcon("Tree.leafIcon");
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            String nodeName = node.getUserObject().toString();
            
            if (node.getAllowsChildren()) {
                setIcon(folderIcon);
            } else {
                setIcon(fileIcon);
            }
            
            return this;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DynamicTreeExample().setVisible(true);
        });
    }
}
```

## JListの高度な使用法

### カスタムレンダラーを使ったJList

JListにカスタムレンダラーを適用して、リッチな表示を実現します。

```java
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// データモデルクラス
class Task {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private boolean completed;
    
    public Task(String title, String description, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = false;
    }
    
    // getter/setter
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getDueDate() { return dueDate; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

// カスタムリストモデル
class TaskListModel extends AbstractListModel<Task> {
    private java.util.List<Task> tasks = new java.util.ArrayList<>();
    
    public TaskListModel() {
        // サンプルデータ
        tasks.add(new Task("レポート作成", "月次レポートを作成する", 
            LocalDateTime.now().plusDays(2)));
        tasks.add(new Task("会議準備", "プレゼン資料を準備する", 
            LocalDateTime.now().plusDays(1)));
        tasks.add(new Task("コードレビュー", "新機能のコードをレビューする", 
            LocalDateTime.now().plusHours(4)));
    }
    
    @Override
    public int getSize() {
        return tasks.size();
    }
    
    @Override
    public Task getElementAt(int index) {
        return tasks.get(index);
    }
    
    public void addTask(Task task) {
        tasks.add(task);
        fireIntervalAdded(this, tasks.size() - 1, tasks.size() - 1);
    }
    
    public void removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            fireIntervalRemoved(this, index, index);
        }
    }
    
    public void updateTask(int index) {
        fireContentsChanged(this, index, index);
    }
}

// カスタムレンダラー
class TaskListCellRenderer extends JPanel implements ListCellRenderer<Task> {
    private JLabel titleLabel;
    private JLabel descriptionLabel;
    private JLabel dueDateLabel;
    private JCheckBox completedCheckBox;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    
    public TaskListCellRenderer() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // タイトルと説明のパネル
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        titleLabel = new JLabel();
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        descriptionLabel = new JLabel();
        descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(12f));
        textPanel.add(titleLabel);
        textPanel.add(descriptionLabel);
        
        // 期限とチェックボックスのパネル
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        dueDateLabel = new JLabel();
        completedCheckBox = new JCheckBox("完了");
        rightPanel.add(dueDateLabel);
        rightPanel.add(completedCheckBox);
        
        add(textPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    @Override
    public Component getListCellRendererComponent(JList<? extends Task> list,
            Task task, int index, boolean isSelected, boolean cellHasFocus) {
        
        titleLabel.setText(task.getTitle());
        descriptionLabel.setText(task.getDescription());
        dueDateLabel.setText("期限: " + task.getDueDate().format(formatter));
        completedCheckBox.setSelected(task.isCompleted());
        
        // 選択状態の表示
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        // 完了タスクはグレーアウト
        if (task.isCompleted()) {
            titleLabel.setForeground(Color.GRAY);
            descriptionLabel.setForeground(Color.GRAY);
        } else {
            titleLabel.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            descriptionLabel.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        }
        
        return this;
    }
}

// メインアプリケーション
public class AdvancedListExample extends JFrame {
    private TaskListModel model;
    private JList<Task> taskList;
    
    public AdvancedListExample() {
        setTitle("タスク管理リスト");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // モデルとリストの作成
        model = new TaskListModel();
        taskList = new JList<>(model);
        taskList.setCellRenderer(new TaskListCellRenderer());
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // リストのクリックイベント
        taskList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = taskList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        Task task = model.getElementAt(index);
                        task.setCompleted(!task.isCompleted());
                        model.updateTask(index);
                    }
                }
            }
        });
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("タスク追加");
        JButton removeButton = new JButton("タスク削除");
        
        addButton.addActionListener(e -> {
            String title = JOptionPane.showInputDialog(this, "タスクのタイトル:");
            if (title != null && !title.trim().isEmpty()) {
                model.addTask(new Task(title, "詳細な説明", 
                    LocalDateTime.now().plusDays(3)));
            }
        });
        
        removeButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                model.removeTask(selectedIndex);
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        
        // レイアウト
        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 説明ラベル
        JLabel infoLabel = new JLabel("ダブルクリックでタスクの完了/未完了を切り替え");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(infoLabel, BorderLayout.NORTH);
        
        setSize(600, 400);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdvancedListExample().setVisible(true);
        });
    }
}
```

## カスタムセルエディターの実装

### JTableのカスタムエディター

テーブルセルに特殊な入力コンポーネントを使用する例です。

```java
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

// カラーセルエディター
class ColorCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JButton button;
    private Color currentColor;
    private JColorChooser colorChooser;
    private JDialog dialog;
    
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

// カラーセルレンダラー
class ColorCellRenderer extends JLabel implements TableCellRenderer {
    public ColorCellRenderer() {
        setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Color color = (Color) value;
        setBackground(color);
        setText(String.format("RGB(%d,%d,%d)", 
            color.getRed(), color.getGreen(), color.getBlue()));
        setForeground(getContrastColor(color));
        
        if (isSelected) {
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        } else {
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
        
        return this;
    }
    
    private Color getContrastColor(Color color) {
        // 背景色に対してコントラストの高い文字色を返す
        int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
        return gray > 128 ? Color.BLACK : Color.WHITE;
    }
}

// コンボボックスエディター
class PriorityCellEditor extends DefaultCellEditor {
    public PriorityCellEditor() {
        super(new JComboBox<>(new String[]{"低", "中", "高", "緊急"}));
    }
}

// メインアプリケーション
public class CustomEditorExample extends JFrame {
    public CustomEditorExample() {
        setTitle("カスタムエディターの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // テーブルモデルの作成
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{
                {"タスク1", "高", Color.RED, false},
                {"タスク2", "中", Color.YELLOW, true},
                {"タスク3", "低", Color.GREEN, false},
                {"タスク4", "緊急", Color.ORANGE, true}
            },
            new String[]{"タスク名", "優先度", "色", "完了"}
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;
                    case 1: return String.class;
                    case 2: return Color.class;
                    case 3: return Boolean.class;
                    default: return Object.class;
                }
            }
        };
        
        // テーブルの作成
        JTable table = new JTable(model);
        table.setRowHeight(30);
        
        // カスタムエディターとレンダラーの設定
        table.getColumnModel().getColumn(1).setCellEditor(new PriorityCellEditor());
        table.getColumnModel().getColumn(2).setCellRenderer(new ColorCellRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ColorCellEditor());
        
        // スクロールペインに追加
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // 説明パネル
        JPanel infoPanel = new JPanel();
        infoPanel.add(new JLabel("優先度はドロップダウン、色はクリックで変更できます"));
        add(infoPanel, BorderLayout.SOUTH);
        
        setSize(600, 300);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomEditorExample().setVisible(true);
        });
    }
}
```

## 実践的なアプリケーション例

### 統合型データ管理アプリケーション

JTable、JTree、JListを組み合わせた総合的なアプリケーションの例です。

```java
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.util.List;

// プロジェクトデータクラス
class Project {
    private String name;
    private List<Task> tasks;
    private List<String> members;
    
    public Project(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
        this.members = new ArrayList<>();
    }
    
    // getter/setter
    public String getName() { return name; }
    public List<Task> getTasks() { return tasks; }
    public List<String> getMembers() { return members; }
    
    public void addTask(Task task) { tasks.add(task); }
    public void addMember(String member) { members.add(member); }
}

// メインアプリケーション
public class IntegratedDataManagementApp extends JFrame {
    private JSplitPane mainSplitPane;
    private JSplitPane rightSplitPane;
    private JTree projectTree;
    private JTable taskTable;
    private JList<String> memberList;
    private Map<String, Project> projects;
    private Project currentProject;
    
    public IntegratedDataManagementApp() {
        setTitle("プロジェクト管理システム");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // プロジェクトデータの初期化
        initializeData();
        
        // コンポーネントの作成
        createProjectTree();
        createTaskTable();
        createMemberList();
        
        // レイアウトの設定
        setupLayout();
        
        // メニューバーの作成
        createMenuBar();
        
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
    
    private void initializeData() {
        projects = new HashMap<>();
        
        // サンプルプロジェクト1
        Project webProject = new Project("Webアプリケーション開発");
        webProject.addTask(new Task("要件定義", "クライアントとの要件確認", 
            LocalDateTime.now().plusDays(7)));
        webProject.addTask(new Task("DB設計", "データベーススキーマの設計", 
            LocalDateTime.now().plusDays(14)));
        webProject.addMember("田中太郎");
        webProject.addMember("鈴木花子");
        projects.put(webProject.getName(), webProject);
        
        // サンプルプロジェクト2
        Project mobileProject = new Project("モバイルアプリ開発");
        mobileProject.addTask(new Task("UI設計", "画面デザインの作成", 
            LocalDateTime.now().plusDays(5)));
        mobileProject.addMember("佐藤次郎");
        mobileProject.addMember("山田美咲");
        projects.put(mobileProject.getName(), mobileProject);
    }
    
    private void createProjectTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("プロジェクト一覧");
        
        for (Project project : projects.values()) {
            DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project.getName());
            
            // タスクノード
            DefaultMutableTreeNode tasksNode = new DefaultMutableTreeNode("タスク");
            for (Task task : project.getTasks()) {
                tasksNode.add(new DefaultMutableTreeNode(task.getTitle()));
            }
            projectNode.add(tasksNode);
            
            // メンバーノード
            DefaultMutableTreeNode membersNode = new DefaultMutableTreeNode("メンバー");
            for (String member : project.getMembers()) {
                membersNode.add(new DefaultMutableTreeNode(member));
            }
            projectNode.add(membersNode);
            
            root.add(projectNode);
        }
        
        projectTree = new JTree(root);
        projectTree.addTreeSelectionListener(new TreeSelectionListener() {
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
                }
            }
        });
    }
    
    private void createTaskTable() {
        String[] columnNames = {"タスク名", "説明", "期限", "状態"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Boolean.class;
                return String.class;
            }
        };
        
        taskTable = new JTable(model);
        taskTable.setRowHeight(25);
        
        // 期限列のカスタムレンダラー
        taskTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // 期限が近いタスクを赤で表示
                if (value instanceof String) {
                    setForeground(Color.BLACK);
                    // ここで期限チェックロジックを実装可能
                }
                
                return this;
            }
        });
    }
    
    private void createMemberList() {
        memberList = new JList<>(new DefaultListModel<>());
        memberList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void updateTaskTable() {
        DefaultTableModel model = (DefaultTableModel) taskTable.getModel();
        model.setRowCount(0);
        
        if (currentProject != null) {
            for (Task task : currentProject.getTasks()) {
                model.addRow(new Object[]{
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    task.isCompleted()
                });
            }
        }
    }
    
    private void updateMemberList() {
        DefaultListModel<String> model = (DefaultListModel<String>) memberList.getModel();
        model.clear();
        
        if (currentProject != null) {
            for (String member : currentProject.getMembers()) {
                model.addElement(member);
            }
        }
    }
    
    private void setupLayout() {
        // 左側：プロジェクトツリー
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("プロジェクト"));
        leftPanel.add(new JScrollPane(projectTree), BorderLayout.CENTER);
        
        // 右上：タスクテーブル
        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.setBorder(BorderFactory.createTitledBorder("タスク一覧"));
        topRightPanel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        
        // タスク操作ボタン
        JPanel taskButtonPanel = new JPanel();
        JButton addTaskButton = new JButton("タスク追加");
        JButton deleteTaskButton = new JButton("タスク削除");
        taskButtonPanel.add(addTaskButton);
        taskButtonPanel.add(deleteTaskButton);
        topRightPanel.add(taskButtonPanel, BorderLayout.SOUTH);
        
        // 右下：メンバーリスト
        JPanel bottomRightPanel = new JPanel(new BorderLayout());
        bottomRightPanel.setBorder(BorderFactory.createTitledBorder("メンバー"));
        bottomRightPanel.add(new JScrollPane(memberList), BorderLayout.CENTER);
        
        // メンバー操作ボタン
        JPanel memberButtonPanel = new JPanel();
        JButton addMemberButton = new JButton("メンバー追加");
        JButton deleteMemberButton = new JButton("メンバー削除");
        memberButtonPanel.add(addMemberButton);
        memberButtonPanel.add(deleteMemberButton);
        bottomRightPanel.add(memberButtonPanel, BorderLayout.SOUTH);
        
        // 分割ペインの設定
        rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            topRightPanel, bottomRightPanel);
        rightSplitPane.setDividerLocation(400);
        
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            leftPanel, rightSplitPane);
        mainSplitPane.setDividerLocation(300);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        // ステータスバー
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("プロジェクトを選択してください");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // ファイルメニュー
        JMenu fileMenu = new JMenu("ファイル");
        JMenuItem newProjectItem = new JMenuItem("新規プロジェクト");
        JMenuItem saveItem = new JMenuItem("保存");
        JMenuItem exitItem = new JMenuItem("終了");
        
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newProjectItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // 編集メニュー
        JMenu editMenu = new JMenu("編集");
        JMenuItem cutItem = new JMenuItem("切り取り");
        JMenuItem copyItem = new JMenuItem("コピー");
        JMenuItem pasteItem = new JMenuItem("貼り付け");
        
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        
        // 表示メニュー
        JMenu viewMenu = new JMenu("表示");
        JCheckBoxMenuItem showToolbarItem = new JCheckBoxMenuItem("ツールバー", true);
        JCheckBoxMenuItem showStatusItem = new JCheckBoxMenuItem("ステータスバー", true);
        
        viewMenu.add(showToolbarItem);
        viewMenu.add(showStatusItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // システムのLook & Feelを使用
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new IntegratedDataManagementApp().setVisible(true);
        });
    }
}
```

## まとめ

本章では、Swingの高度なGUIコンポーネントについて詳しく学習しました。

### 学習した内容

1. **JTableの活用**
   - AbstractTableModelによるカスタムモデルの実装
   - TableRowSorterによるソートとフィルタリング
   - カスタムセルレンダラーとエディターの作成

2. **JTreeの実装**
   - TreeModelによる階層データの表現
   - 動的なノードの追加・削除
   - カスタムツリーセルレンダラーの作成

3. **JListの高度な使用法**
   - カスタムListModelの実装
   - 複雑なセルレンダラーによるリッチな表示
   - マウスイベントとの連携

4. **MVCパターンの実践**
   - モデル・ビュー・コントローラーの分離
   - イベントドリブンな更新処理
   - 再利用可能なコンポーネント設計

5. **統合アプリケーションの開発**
   - 複数のコンポーネントの連携
   - 分割ペインによる効果的なレイアウト
   - メニューバーとツールバーの実装

これらの技術を習得することで、エンタープライズレベルのデスクトップアプリケーションを開発できるようになります。次章では、これらのGUIアプリケーションの品質を保証するためのユニットテストについて学習します。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています：
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter20/`

### 課題構成
- **基礎課題**: 本章の基本概念の理解確認
- **発展課題**: 応用的な実装練習
- **チャレンジ課題**: 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

1. **基礎課題**: 高度なテーブル操作システムの実装
2. **発展課題**: ファイルエクスプローラの開発
3. **チャレンジ課題**: 統合開発環境（IDE）風アプリケーションの構築

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

次のステップ: 基礎課題が完了したら、第22章「ユニットテストと品質保証」に進みましょう。

## よくあるエラーと対処法

高度なGUIプログラミングの学習において遭遇しやすい典型的なエラーとその対処法を以下にまとめます。

### カスタムコンポーネントの描画問題

#### 問題：paintComponent()で描画した内容が表示されない

**エラー症状**：
```java
public class CustomPanel extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(0, 0, 100, 100);
        // 赤い矩形が表示されない
    }
}
```

**原因**：
- `super.paintComponent(g)`が呼び出されていない
- コンポーネントのサイズが0になっている
- 透明度の設定に問題がある

**対処法**：
```java
public class CustomPanel extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // 必須：親クラスの描画処理を実行
        
        g.setColor(Color.RED);
        g.fillRect(0, 0, 100, 100);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 200);  // 適切なサイズを指定
    }
}
```

#### 問題：カスタム描画がちらつく

**エラー症状**：
```java
@Override
public void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    // 複雑な描画処理
    for (int i = 0; i < 1000; i++) {
        g.drawLine(i, 0, i, getHeight());
    }
    // 描画時にちらつきが発生
}
```

**原因**：
- ダブルバッファリングが無効になっている
- 描画処理が重すぎる
- 不要な再描画が発生している

**対処法**：
```java
public class SmoothPanel extends JPanel {
    private BufferedImage buffer;
    private boolean needsRedraw = true;
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // バッファが未作成または再描画が必要な場合のみ描画
        if (buffer == null || needsRedraw) {
            createBuffer();
            needsRedraw = false;
        }
        
        // バッファの内容を描画
        g.drawImage(buffer, 0, 0, this);
    }
    
    private void createBuffer() {
        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = buffer.createGraphics();
        
        // アンチエイリアシングを有効化
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 重い描画処理
        for (int i = 0; i < 1000; i++) {
            g2d.drawLine(i, 0, i, getHeight());
        }
        
        g2d.dispose();
    }
    
    public void invalidateBuffer() {
        needsRedraw = true;
        repaint();
    }
}
```

### スレッド間でのGUI更新

#### 問題：背景スレッドからGUIを更新すると例外が発生

**エラーメッセージ**：
```
Exception in thread "Thread-1" java.lang.IllegalStateException: 
This component does not support a null LayoutManager
```

**原因**：
- EDT（Event Dispatch Thread）以外からGUIコンポーネントを更新している
- スレッドセーフでない操作を実行している

**対処法**：
```java
// 間違った方法
new Thread(() -> {
    // 背景スレッドからGUIを直接更新（危険）
    label.setText("更新");
}).start();

// 正しい方法
new Thread(() -> {
    // 重い処理を背景スレッドで実行
    String result = performHeavyComputation();
    
    // GUI更新はEDTで実行
    SwingUtilities.invokeLater(() -> {
        label.setText(result);
    });
}).start();

// SwingWorkerを使用する方法（推奨）
SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
    @Override
    protected String doInBackground() throws Exception {
        return performHeavyComputation();
    }
    
    @Override
    protected void done() {
        try {
            label.setText(get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
};
worker.execute();
```

### リソース管理の問題

#### 問題：大量の画像を扱うとメモリ不足になる

**エラーメッセージ**：
```
java.lang.OutOfMemoryError: Java heap space
```

**原因**：
- 画像リソースが適切に解放されていない
- 大きな画像を縮小せずに使用している
- 画像のキャッシュが適切に管理されていない

**対処法**：
```java
public class ImageManager {
    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    private final int MAX_CACHE_SIZE = 100;
    
    public BufferedImage loadImage(String path, int width, int height) {
        String key = path + "_" + width + "_" + height;
        
        // キャッシュから取得
        BufferedImage cached = imageCache.get(key);
        if (cached != null) {
            return cached;
        }
        
        // 画像を読み込み、リサイズ
        try {
            BufferedImage original = ImageIO.read(new File(path));
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g2d = resized.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                               RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(original, 0, 0, width, height, null);
            g2d.dispose();
            
            // 元画像を解放
            original.flush();
            
            // キャッシュサイズを制限
            if (imageCache.size() >= MAX_CACHE_SIZE) {
                clearOldestCache();
            }
            
            imageCache.put(key, resized);
            return resized;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void clearOldestCache() {
        // 簡単な実装：最初の要素を削除
        Iterator<Map.Entry<String, BufferedImage>> iterator = imageCache.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<String, BufferedImage> entry = iterator.next();
            entry.getValue().flush();  // 画像リソースを解放
            iterator.remove();
        }
    }
    
    public void clearCache() {
        for (BufferedImage image : imageCache.values()) {
            image.flush();
        }
        imageCache.clear();
    }
}
```

### パフォーマンスの最適化

#### 問題：大量のデータを表示するJTableの動作が遅い

**エラー症状**：
```java
// 10万行のデータを表示すると動作が非常に遅くなる
DefaultTableModel model = new DefaultTableModel();
for (int i = 0; i < 100000; i++) {
    model.addRow(new Object[]{i, "データ" + i, Math.random()});
}
table.setModel(model);
```

**原因**：
- 全てのデータを一度にメモリに読み込んでいる
- 適切な仮想化が行われていない
- 不要な再描画が発生している

**対処法**：
```java
// 1. 遅延読み込み（Lazy Loading）の実装
public class LazyTableModel extends AbstractTableModel {
    private final List<Object[]> dataCache = new ArrayList<>();
    private final DataSource dataSource;
    private final int pageSize = 1000;
    
    public LazyTableModel(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public int getRowCount() {
        return dataSource.getTotalRowCount();
    }
    
    @Override
    public int getColumnCount() {
        return 3;
    }
    
    @Override
    public Object getValueAt(int row, int column) {
        // 必要に応じてデータを読み込み
        ensureDataLoaded(row);
        return dataCache.get(row)[column];
    }
    
    private void ensureDataLoaded(int row) {
        if (row >= dataCache.size()) {
            // ページ単位でデータを読み込み
            int startIndex = (row / pageSize) * pageSize;
            List<Object[]> pageData = dataSource.getData(startIndex, pageSize);
            
            // キャッシュを拡張
            while (dataCache.size() <= row) {
                dataCache.add(null);
            }
            
            // データを設定
            for (int i = 0; i < pageData.size(); i++) {
                dataCache.set(startIndex + i, pageData.get(i));
            }
        }
    }
}

// 2. テーブルの最適化設定
table.setAutoCreateRowSorter(true);
table.setFillsViewportHeight(true);
table.getTableHeader().setReorderingAllowed(false);

// 3. 不要な更新を避ける
table.setUpdateSelectionOnSort(false);
table.setRowHeight(20);  // 固定の行高さを設定
```

### 複雑なレイアウトの管理

#### 問題：動的にコンポーネントを追加・削除すると配置が崩れる

**エラー症状**：
```java
JPanel panel = new JPanel(new GridBagLayout());
GridBagConstraints gbc = new GridBagConstraints();

// 動的にコンポーネントを追加
for (int i = 0; i < 5; i++) {
    gbc.gridx = i;
    panel.add(new JButton("Button " + i), gbc);
}

// 中間のコンポーネントを削除すると配置が崩れる
panel.remove(2);
panel.revalidate();
```

**原因**：
- GridBagLayoutの制約が適切に管理されていない
- レイアウトの再計算が正しく行われていない
- コンポーネントの削除後に制約が更新されていない

**対処法**：
```java
public class DynamicPanel extends JPanel {
    private final List<JComponent> components = new ArrayList<>();
    private final GridBagConstraints gbc = new GridBagConstraints();
    
    public DynamicPanel() {
        setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
    }
    
    public void addComponent(JComponent component) {
        components.add(component);
        refreshLayout();
    }
    
    public void removeComponent(int index) {
        if (index >= 0 && index < components.size()) {
            JComponent component = components.remove(index);
            remove(component);
            refreshLayout();
        }
    }
    
    private void refreshLayout() {
        // 全てのコンポーネントを一度削除
        removeAll();
        
        // 制約を再計算して追加
        for (int i = 0; i < components.size(); i++) {
            gbc.gridx = i % 3;  // 3列のグリッド
            gbc.gridy = i / 3;
            add(components.get(i), (GridBagConstraints)gbc.clone());
        }
        
        // レイアウトを更新
        revalidate();
        repaint();
    }
    
    public void clear() {
        components.clear();
        removeAll();
        revalidate();
        repaint();
    }
}
```

### カスタムレンダラーの問題

#### 問題：JTableのカスタムレンダラーで選択状態が正しく表示されない

**エラー症状**：
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

**原因**：
- 選択状態やフォーカス状態を適切に処理していない
- コンポーネントの設定が不完全

**対処法**：
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

### デバッグのヒント

#### 1. 描画領域の可視化

```java
@Override
public void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    // デバッグ用：境界を表示
    if (DEBUG_MODE) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2d.dispose();
    }
}
```

#### 2. パフォーマンスの測定

```java
long startTime = System.nanoTime();
// 処理
long endTime = System.nanoTime();
System.out.println("処理時間: " + (endTime - startTime) / 1_000_000 + "ms");
```

#### 3. メモリ使用量の監視

```java
Runtime runtime = Runtime.getRuntime();
long usedMemory = runtime.totalMemory() - runtime.freeMemory();
System.out.println("使用メモリ: " + usedMemory / 1024 / 1024 + "MB");
```

これらの対処法を参考に、高度なGUIアプリケーションの開発を進めてください。複雑な問題に直面した場合は、問題を小さく分割し、段階的に解決することが重要です。
