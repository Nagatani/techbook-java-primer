import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.time.LocalDateTime;

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
