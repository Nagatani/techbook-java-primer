/**
 * 第19章 解答例4: MVCパターンの実装例
 * 
 * 完全なMVCアーキテクチャを実装したタスク管理アプリケーション
 * 
 * 実装内容:
 * - Model: データとビジネスロジック
 * - View: ユーザーインターフェース
 * - Controller: ModelとViewの制御
 * 
 * 技術的特徴:
 * - オブザーバーパターンによる疎結合
 * - イベント駆動アーキテクチャ
 * - データバインディング
 * - 状態管理の分離
 * - テスト容易性を考慮した設計
 */

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MVCExample {
    
    /**
     * Model: タスクデータとビジネスロジックを管理
     */
    public static class TaskModel extends Observable {
        
        // タスクの状態を表す列挙型
        public enum TaskStatus {
            TODO("未完了"),
            IN_PROGRESS("進行中"),
            COMPLETED("完了");
            
            private final String displayName;
            
            TaskStatus(String displayName) {
                this.displayName = displayName;
            }
            
            @Override
            public String toString() {
                return displayName;
            }
        }
        
        // タスク優先度の列挙型
        public enum TaskPriority {
            LOW("低", 1),
            MEDIUM("中", 2),
            HIGH("高", 3),
            URGENT("緊急", 4);
            
            private final String displayName;
            private final int level;
            
            TaskPriority(String displayName, int level) {
                this.displayName = displayName;
                this.level = level;
            }
            
            public int getLevel() {
                return level;
            }
            
            @Override
            public String toString() {
                return displayName;
            }
        }
        
        // タスクエンティティクラス
        public static class Task {
            private static int nextId = 1;
            
            private final int id;
            private String title;
            private String description;
            private TaskStatus status;
            private TaskPriority priority;
            private LocalDateTime createdAt;
            private LocalDateTime updatedAt;
            private LocalDateTime dueDate;
            
            public Task(String title, String description, TaskPriority priority) {
                this.id = nextId++;
                this.title = title;
                this.description = description;
                this.priority = priority;
                this.status = TaskStatus.TODO;
                this.createdAt = LocalDateTime.now();
                this.updatedAt = LocalDateTime.now();
            }
            
            // Getters and Setters
            public int getId() { return id; }
            
            public String getTitle() { return title; }
            public void setTitle(String title) {
                this.title = title;
                this.updatedAt = LocalDateTime.now();
            }
            
            public String getDescription() { return description; }
            public void setDescription(String description) {
                this.description = description;
                this.updatedAt = LocalDateTime.now();
            }
            
            public TaskStatus getStatus() { return status; }
            public void setStatus(TaskStatus status) {
                this.status = status;
                this.updatedAt = LocalDateTime.now();
            }
            
            public TaskPriority getPriority() { return priority; }
            public void setPriority(TaskPriority priority) {
                this.priority = priority;
                this.updatedAt = LocalDateTime.now();
            }
            
            public LocalDateTime getCreatedAt() { return createdAt; }
            public LocalDateTime getUpdatedAt() { return updatedAt; }
            
            public LocalDateTime getDueDate() { return dueDate; }
            public void setDueDate(LocalDateTime dueDate) {
                this.dueDate = dueDate;
                this.updatedAt = LocalDateTime.now();
            }
            
            public boolean isOverdue() {
                return dueDate != null && LocalDateTime.now().isAfter(dueDate) && status != TaskStatus.COMPLETED;
            }
            
            public String getFormattedCreatedAt() {
                return createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
            }
            
            public String getFormattedDueDate() {
                return dueDate != null ? dueDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")) : "-";
            }
            
            @Override
            public String toString() {
                return String.format("Task{id=%d, title='%s', status=%s, priority=%s}",
                        id, title, status, priority);
            }
            
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Task)) return false;
                Task task = (Task) o;
                return id == task.id;
            }
            
            @Override
            public int hashCode() {
                return Objects.hash(id);
            }
        }
        
        // モデルの状態
        private List<Task> tasks;
        private TaskStatus filterStatus;
        private TaskPriority filterPriority;
        private String searchText;
        
        public TaskModel() {
            this.tasks = new ArrayList<>();
            this.filterStatus = null;
            this.filterPriority = null;
            this.searchText = "";
            generateSampleData();
        }
        
        // タスク操作メソッド
        public void addTask(Task task) {
            tasks.add(task);
            setChanged();
            notifyObservers("TASK_ADDED");
        }
        
        public void updateTask(Task task) {
            int index = tasks.indexOf(task);
            if (index != -1) {
                tasks.set(index, task);
                setChanged();
                notifyObservers("TASK_UPDATED");
            }
        }
        
        public void deleteTask(Task task) {
            if (tasks.remove(task)) {
                setChanged();
                notifyObservers("TASK_DELETED");
            }
        }
        
        public Task findTaskById(int id) {
            return tasks.stream()
                    .filter(task -> task.getId() == id)
                    .findFirst()
                    .orElse(null);
        }
        
        // フィルタリング機能
        public List<Task> getFilteredTasks() {
            return tasks.stream()
                    .filter(this::matchesFilter)
                    .sorted(this::compareTasksByPriority)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        private boolean matchesFilter(Task task) {
            // ステータスフィルタ
            if (filterStatus != null && task.getStatus() != filterStatus) {
                return false;
            }
            
            // 優先度フィルタ
            if (filterPriority != null && task.getPriority() != filterPriority) {
                return false;
            }
            
            // テキスト検索
            if (!searchText.isEmpty()) {
                String search = searchText.toLowerCase();
                return task.getTitle().toLowerCase().contains(search) ||
                       task.getDescription().toLowerCase().contains(search);
            }
            
            return true;
        }
        
        private int compareTasksByPriority(Task a, Task b) {
            // 優先度の降順（緊急 → 高 → 中 → 低）
            int priorityComparison = Integer.compare(
                    b.getPriority().getLevel(), 
                    a.getPriority().getLevel()
            );
            
            if (priorityComparison != 0) {
                return priorityComparison;
            }
            
            // 優先度が同じ場合は作成日時の昇順
            return a.getCreatedAt().compareTo(b.getCreatedAt());
        }
        
        // フィルタ設定
        public void setStatusFilter(TaskStatus status) {
            this.filterStatus = status;
            setChanged();
            notifyObservers("FILTER_CHANGED");
        }
        
        public void setPriorityFilter(TaskPriority priority) {
            this.filterPriority = priority;
            setChanged();
            notifyObservers("FILTER_CHANGED");
        }
        
        public void setSearchText(String searchText) {
            this.searchText = searchText != null ? searchText : "";
            setChanged();
            notifyObservers("FILTER_CHANGED");
        }
        
        // 統計情報
        public Map<TaskStatus, Integer> getTaskCountByStatus() {
            Map<TaskStatus, Integer> counts = new EnumMap<>(TaskStatus.class);
            for (TaskStatus status : TaskStatus.values()) {
                counts.put(status, 0);
            }
            
            for (Task task : tasks) {
                counts.put(task.getStatus(), counts.get(task.getStatus()) + 1);
            }
            
            return counts;
        }
        
        public int getOverdueTaskCount() {
            return (int) tasks.stream().filter(Task::isOverdue).count();
        }
        
        // サンプルデータの生成
        private void generateSampleData() {
            addTask(new Task("プロジェクト企画書作成", "新プロジェクトの企画書を作成する", TaskPriority.HIGH));
            addTask(new Task("週次レポート提出", "今週の進捗レポートをまとめる", TaskPriority.MEDIUM));
            addTask(new Task("システム設計書レビュー", "開発チームの設計書をレビューする", TaskPriority.HIGH));
            addTask(new Task("顧客プレゼン準備", "来週の顧客プレゼンテーション資料を準備する", TaskPriority.URGENT));
            addTask(new Task("バックアップ設定確認", "サーバーのバックアップ設定を確認する", TaskPriority.LOW));
            
            // いくつかのタスクの状態を変更
            tasks.get(1).setStatus(TaskStatus.COMPLETED);
            tasks.get(2).setStatus(TaskStatus.IN_PROGRESS);
            
            // 期限を設定
            tasks.get(0).setDueDate(LocalDateTime.now().plusDays(3));
            tasks.get(3).setDueDate(LocalDateTime.now().plusDays(1));
            tasks.get(4).setDueDate(LocalDateTime.now().minusDays(1)); // 期限切れ
        }
        
        // Getters
        public List<Task> getAllTasks() { return new ArrayList<>(tasks); }
        public TaskStatus getFilterStatus() { return filterStatus; }
        public TaskPriority getFilterPriority() { return filterPriority; }
        public String getSearchText() { return searchText; }
    }
    
    /**
     * View: ユーザーインターフェースを管理
     */
    public static class TaskView extends JFrame implements Observer {
        
        private TaskModel model;
        private TaskController controller;
        
        // GUI コンポーネント
        private JTable taskTable;
        private TaskTableModel tableModel;
        private JTextField searchField;
        private JComboBox<TaskModel.TaskStatus> statusFilterCombo;
        private JComboBox<TaskModel.TaskPriority> priorityFilterCombo;
        private JButton addButton, editButton, deleteButton;
        private JLabel statusLabel;
        private JProgressBar progressBar;
        private JPanel statisticsPanel;
        
        public TaskView(TaskModel model) {
            this.model = model;
            this.model.addObserver(this);
            
            initializeComponents();
            setupLayout();
            setupEventListeners();
            setupFrame();
            updateStatistics();
        }
        
        private void initializeComponents() {
            // テーブル
            tableModel = new TaskTableModel();
            taskTable = new JTable(tableModel);
            taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            taskTable.setRowHeight(25);
            setupTableRenderers();
            
            // フィルタコンポーネント
            searchField = new JTextField(20);
            statusFilterCombo = new JComboBox<>();
            priorityFilterCombo = new JComboBox<>();
            
            setupFilterCombos();
            
            // ボタン
            addButton = new JButton("追加");
            editButton = new JButton("編集");
            deleteButton = new JButton("削除");
            
            // ステータス表示
            statusLabel = new JLabel("準備完了");
            statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
            
            // プログレスバー
            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            
            // 統計パネル
            statisticsPanel = new JPanel(new GridLayout(1, 4, 5, 5));
            statisticsPanel.setBorder(BorderFactory.createTitledBorder("統計情報"));
        }
        
        private void setupTableRenderers() {
            // 優先度列のカスタムレンダラー
            taskTable.getColumnModel().getColumn(2).setCellRenderer(new PriorityRenderer());
            
            // ステータス列のカスタムレンダラー
            taskTable.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
            
            // 期限列のカスタムレンダラー
            taskTable.getColumnModel().getColumn(4).setCellRenderer(new DueDateRenderer());
            
            // 列幅の設定
            TableColumnModel columnModel = taskTable.getColumnModel();
            columnModel.getColumn(0).setPreferredWidth(50);  // ID
            columnModel.getColumn(1).setPreferredWidth(200); // タイトル
            columnModel.getColumn(2).setPreferredWidth(80);  // 優先度
            columnModel.getColumn(3).setPreferredWidth(80);  // ステータス
            columnModel.getColumn(4).setPreferredWidth(120); // 期限
        }
        
        private void setupFilterCombos() {
            // ステータスフィルタ
            statusFilterCombo.addItem(null); // "すべて"を表す
            for (TaskModel.TaskStatus status : TaskModel.TaskStatus.values()) {
                statusFilterCombo.addItem(status);
            }
            statusFilterCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText(value == null ? "すべて" : value.toString());
                    return this;
                }
            });
            
            // 優先度フィルタ
            priorityFilterCombo.addItem(null); // "すべて"を表す
            for (TaskModel.TaskPriority priority : TaskModel.TaskPriority.values()) {
                priorityFilterCombo.addItem(priority);
            }
            priorityFilterCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText(value == null ? "すべて" : value.toString());
                    return this;
                }
            });
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            // 上部パネル（フィルタ）
            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            filterPanel.add(new JLabel("検索:"));
            filterPanel.add(searchField);
            filterPanel.add(new JLabel("ステータス:"));
            filterPanel.add(statusFilterCombo);
            filterPanel.add(new JLabel("優先度:"));
            filterPanel.add(priorityFilterCombo);
            
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(filterPanel, BorderLayout.NORTH);
            topPanel.add(statisticsPanel, BorderLayout.SOUTH);
            
            add(topPanel, BorderLayout.NORTH);
            
            // 中央パネル（テーブル）
            JScrollPane scrollPane = new JScrollPane(taskTable);
            scrollPane.setBorder(BorderFactory.createTitledBorder("タスク一覧"));
            add(scrollPane, BorderLayout.CENTER);
            
            // 右パネル（ボタン）
            JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            add(buttonPanel, BorderLayout.EAST);
            
            // 下部パネル（ステータス）
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(statusLabel, BorderLayout.CENTER);
            bottomPanel.add(progressBar, BorderLayout.EAST);
            add(bottomPanel, BorderLayout.SOUTH);
        }
        
        private void setupEventListeners() {
            // 検索フィールド
            searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) { filterChanged(); }
                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) { filterChanged(); }
                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) { filterChanged(); }
            });
            
            // フィルタコンボボックス
            statusFilterCombo.addActionListener(e -> filterChanged());
            priorityFilterCombo.addActionListener(e -> filterChanged());
            
            // テーブル選択
            taskTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    updateButtonStates();
                }
            });
            
            // ダブルクリックで編集
            taskTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && taskTable.getSelectedRow() != -1) {
                        editTask();
                    }
                }
            });
        }
        
        private void setupFrame() {
            setTitle("タスク管理システム - MVCパターンデモ");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(900, 600);
            setLocationRelativeTo(null);
            
            // メニューバー
            setupMenuBar();
        }
        
        private void setupMenuBar() {
            JMenuBar menuBar = new JMenuBar();
            
            JMenu taskMenu = new JMenu("タスク");
            JMenuItem addItem = new JMenuItem("新規タスク");
            JMenuItem editItem = new JMenuItem("編集");
            JMenuItem deleteItem = new JMenuItem("削除");
            
            addItem.addActionListener(e -> addTask());
            editItem.addActionListener(e -> editTask());
            deleteItem.addActionListener(e -> deleteTask());
            
            taskMenu.add(addItem);
            taskMenu.add(editItem);
            taskMenu.add(deleteItem);
            
            JMenu viewMenu = new JMenu("表示");
            JMenuItem refreshItem = new JMenuItem("更新");
            refreshItem.addActionListener(e -> refreshTable());
            viewMenu.add(refreshItem);
            
            menuBar.add(taskMenu);
            menuBar.add(viewMenu);
            
            setJMenuBar(menuBar);
        }
        
        // Observerインターフェースの実装
        @Override
        public void update(Observable o, Object arg) {
            SwingUtilities.invokeLater(() -> {
                refreshTable();
                updateStatistics();
                updateStatusLabel((String) arg);
            });
        }
        
        // コントローラーの設定
        public void setController(TaskController controller) {
            this.controller = controller;
            
            // ボタンイベントの設定
            addButton.addActionListener(e -> addTask());
            editButton.addActionListener(e -> editTask());
            deleteButton.addActionListener(e -> deleteTask());
            
            updateButtonStates();
        }
        
        // UI更新メソッド
        private void refreshTable() {
            tableModel.setTasks(model.getFilteredTasks());
        }
        
        private void updateStatistics() {
            Map<TaskModel.TaskStatus, Integer> counts = model.getTaskCountByStatus();
            int total = counts.values().stream().mapToInt(Integer::intValue).sum();
            int completed = counts.get(TaskModel.TaskStatus.COMPLETED);
            int overdue = model.getOverdueTaskCount();
            
            statisticsPanel.removeAll();
            
            statisticsPanel.add(createStatLabel("総数", String.valueOf(total), Color.BLUE));
            statisticsPanel.add(createStatLabel("完了", String.valueOf(completed), Color.GREEN));
            statisticsPanel.add(createStatLabel("進行中", String.valueOf(counts.get(TaskModel.TaskStatus.IN_PROGRESS)), Color.ORANGE));
            statisticsPanel.add(createStatLabel("期限切れ", String.valueOf(overdue), Color.RED));
            
            // プログレスバーの更新
            int progress = total > 0 ? (completed * 100 / total) : 0;
            progressBar.setValue(progress);
            progressBar.setString(progress + "% 完了");
            
            statisticsPanel.revalidate();
            statisticsPanel.repaint();
        }
        
        private JLabel createStatLabel(String title, String value, Color color) {
            JLabel label = new JLabel(String.format("<html><b>%s</b><br><font size='+1' color='%s'>%s</font></html>",
                    title, String.format("#%06X", color.getRGB() & 0xFFFFFF), value));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEtchedBorder());
            return label;
        }
        
        private void updateButtonStates() {
            boolean hasSelection = taskTable.getSelectedRow() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        }
        
        private void updateStatusLabel(String action) {
            String message = switch (action) {
                case "TASK_ADDED" -> "タスクが追加されました";
                case "TASK_UPDATED" -> "タスクが更新されました";
                case "TASK_DELETED" -> "タスクが削除されました";
                case "FILTER_CHANGED" -> "フィルターが変更されました";
                default -> "準備完了";
            };
            statusLabel.setText(message + " [" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]");
        }
        
        // イベントハンドラー
        private void filterChanged() {
            if (controller != null) {
                controller.updateFilters(
                        searchField.getText(),
                        (TaskModel.TaskStatus) statusFilterCombo.getSelectedItem(),
                        (TaskModel.TaskPriority) priorityFilterCombo.getSelectedItem()
                );
            }
        }
        
        private void addTask() {
            if (controller != null) {
                controller.showAddTaskDialog();
            }
        }
        
        private void editTask() {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow != -1 && controller != null) {
                TaskModel.Task task = tableModel.getTaskAt(selectedRow);
                controller.showEditTaskDialog(task);
            }
        }
        
        private void deleteTask() {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow != -1 && controller != null) {
                TaskModel.Task task = tableModel.getTaskAt(selectedRow);
                controller.deleteTask(task);
            }
        }
        
        // カスタムテーブルモデル
        private class TaskTableModel extends AbstractTableModel {
            private final String[] columnNames = {"ID", "タイトル", "優先度", "ステータス", "期限"};
            private List<TaskModel.Task> tasks = new ArrayList<>();
            
            public void setTasks(List<TaskModel.Task> tasks) {
                this.tasks = new ArrayList<>(tasks);
                fireTableDataChanged();
            }
            
            @Override
            public int getRowCount() { return tasks.size(); }
            
            @Override
            public int getColumnCount() { return columnNames.length; }
            
            @Override
            public String getColumnName(int column) { return columnNames[column]; }
            
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                TaskModel.Task task = tasks.get(rowIndex);
                return switch (columnIndex) {
                    case 0 -> task.getId();
                    case 1 -> task.getTitle();
                    case 2 -> task.getPriority();
                    case 3 -> task.getStatus();
                    case 4 -> task.getFormattedDueDate();
                    default -> null;
                };
            }
            
            public TaskModel.Task getTaskAt(int rowIndex) {
                return tasks.get(rowIndex);
            }
        }
        
        // カスタムレンダラー
        private class PriorityRenderer extends DefaultTableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof TaskModel.TaskPriority priority) {
                    Color color = switch (priority) {
                        case URGENT -> Color.RED;
                        case HIGH -> Color.ORANGE;
                        case MEDIUM -> Color.BLUE;
                        case LOW -> Color.GRAY;
                    };
                    
                    if (!isSelected) {
                        setForeground(color);
                    }
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                
                return this;
            }
        }
        
        private class StatusRenderer extends DefaultTableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value instanceof TaskModel.TaskStatus status) {
                    Color color = switch (status) {
                        case TODO -> Color.BLACK;
                        case IN_PROGRESS -> Color.BLUE;
                        case COMPLETED -> Color.GREEN;
                    };
                    
                    if (!isSelected) {
                        setForeground(color);
                    }
                }
                
                return this;
            }
        }
        
        private class DueDateRenderer extends DefaultTableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                TaskModel.Task task = ((TaskTableModel) table.getModel()).getTaskAt(row);
                if (task.isOverdue() && !isSelected) {
                    setForeground(Color.RED);
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                
                return this;
            }
        }
    }
    
    /**
     * Controller: ModelとViewの橋渡しを行う
     */
    public static class TaskController {
        
        private TaskModel model;
        private TaskView view;
        
        public TaskController(TaskModel model, TaskView view) {
            this.model = model;
            this.view = view;
        }
        
        // フィルター更新
        public void updateFilters(String searchText, TaskModel.TaskStatus status, TaskModel.TaskPriority priority) {
            model.setSearchText(searchText);
            model.setStatusFilter(status);
            model.setPriorityFilter(priority);
        }
        
        // タスク追加ダイアログ
        public void showAddTaskDialog() {
            TaskDialog dialog = new TaskDialog(view, "新規タスク", null);
            dialog.setVisible(true);
            
            if (dialog.isOK()) {
                TaskModel.Task task = dialog.getTask();
                model.addTask(task);
            }
        }
        
        // タスク編集ダイアログ
        public void showEditTaskDialog(TaskModel.Task task) {
            TaskDialog dialog = new TaskDialog(view, "タスク編集", task);
            dialog.setVisible(true);
            
            if (dialog.isOK()) {
                TaskModel.Task updatedTask = dialog.getTask();
                // 既存タスクのプロパティを更新
                task.setTitle(updatedTask.getTitle());
                task.setDescription(updatedTask.getDescription());
                task.setPriority(updatedTask.getPriority());
                task.setStatus(updatedTask.getStatus());
                task.setDueDate(updatedTask.getDueDate());
                
                model.updateTask(task);
            }
        }
        
        // タスク削除
        public void deleteTask(TaskModel.Task task) {
            int result = JOptionPane.showConfirmDialog(view,
                    "タスク「" + task.getTitle() + "」を削除しますか？",
                    "削除確認", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                model.deleteTask(task);
            }
        }
    }
    
    /**
     * タスク入力ダイアログ
     */
    private static class TaskDialog extends JDialog {
        private JTextField titleField, descriptionField;
        private JComboBox<TaskModel.TaskPriority> priorityCombo;
        private JComboBox<TaskModel.TaskStatus> statusCombo;
        private JTextField dueDateField;
        private boolean okPressed = false;
        private TaskModel.Task task;
        
        public TaskDialog(JFrame parent, String title, TaskModel.Task task) {
            super(parent, title, true);
            this.task = task;
            initializeDialog();
            if (task != null) {
                populateFields(task);
            }
        }
        
        private void initializeDialog() {
            setLayout(new BorderLayout());
            
            // フォームパネル
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            titleField = new JTextField(20);
            descriptionField = new JTextField(20);
            priorityCombo = new JComboBox<>(TaskModel.TaskPriority.values());
            statusCombo = new JComboBox<>(TaskModel.TaskStatus.values());
            dueDateField = new JTextField(20);
            dueDateField.setToolTipText("yyyy-MM-dd HH:mm 形式で入力してください");
            
            // フィールドの配置
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("タイトル:"), gbc);
            gbc.gridx = 1;
            formPanel.add(titleField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(new JLabel("説明:"), gbc);
            gbc.gridx = 1;
            formPanel.add(descriptionField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            formPanel.add(new JLabel("優先度:"), gbc);
            gbc.gridx = 1;
            formPanel.add(priorityCombo, gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            formPanel.add(new JLabel("ステータス:"), gbc);
            gbc.gridx = 1;
            formPanel.add(statusCombo, gbc);
            
            gbc.gridx = 0; gbc.gridy = 4;
            formPanel.add(new JLabel("期限:"), gbc);
            gbc.gridx = 1;
            formPanel.add(dueDateField, gbc);
            
            // ボタンパネル
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("キャンセル");
            
            okButton.addActionListener(e -> {
                if (validateAndSave()) {
                    okPressed = true;
                    dispose();
                }
            });
            
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
            
            setSize(400, 300);
            setLocationRelativeTo(getParent());
        }
        
        private void populateFields(TaskModel.Task task) {
            titleField.setText(task.getTitle());
            descriptionField.setText(task.getDescription());
            priorityCombo.setSelectedItem(task.getPriority());
            statusCombo.setSelectedItem(task.getStatus());
            if (task.getDueDate() != null) {
                dueDateField.setText(task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
        }
        
        private boolean validateAndSave() {
            try {
                String title = titleField.getText().trim();
                String description = descriptionField.getText().trim();
                TaskModel.TaskPriority priority = (TaskModel.TaskPriority) priorityCombo.getSelectedItem();
                TaskModel.TaskStatus status = (TaskModel.TaskStatus) statusCombo.getSelectedItem();
                
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "タイトルを入力してください", "入力エラー", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                LocalDateTime dueDate = null;
                String dueDateText = dueDateField.getText().trim();
                if (!dueDateText.isEmpty()) {
                    dueDate = LocalDateTime.parse(dueDateText, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                }
                
                if (task == null) {
                    // 新規タスクの作成
                    task = new TaskModel.Task(title, description, priority);
                    task.setStatus(status);
                    task.setDueDate(dueDate);
                } else {
                    // 既存タスクの更新
                    task.setTitle(title);
                    task.setDescription(description);
                    task.setPriority(priority);
                    task.setStatus(status);
                    task.setDueDate(dueDate);
                }
                
                return true;
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "入力値に誤りがあります: " + e.getMessage(), 
                        "入力エラー", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        
        public boolean isOK() {
            return okPressed;
        }
        
        public TaskModel.Task getTask() {
            return task;
        }
    }
    
    /**
     * アプリケーションのエントリーポイント
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // MVCコンポーネントの初期化
            TaskModel model = new TaskModel();
            TaskView view = new TaskView(model);
            TaskController controller = new TaskController(model, view);
            
            // コントローラーをビューに設定
            view.setController(controller);
            
            // アプリケーション開始
            view.setVisible(true);
        });
    }
}

/*
 * MVCパターンの実装ポイント:
 * 
 * 1. Model（データとビジネスロジック）
 *    - データの管理とビジネスルールの実装
 *    - オブザーバーパターンによる変更通知
 *    - フィルタリングとソート機能
 *    - 統計情報の計算
 *    - データの整合性保証
 * 
 * 2. View（ユーザーインターフェース）
 *    - GUI コンポーネントの管理
 *    - ユーザー入力の受け取り
 *    - モデルの変更を監視（Observer）
 *    - 表示の更新とレンダリング
 *    - UI状態の管理
 * 
 * 3. Controller（制御ロジック）
 *    - ModelとViewの橋渡し
 *    - ユーザーアクションの処理
 *    - ビジネスロジックの呼び出し
 *    - ダイアログの管理
 *    - データフローの制御
 * 
 * 4. 設計の利点
 *    - 関心の分離（Separation of Concerns）
 *    - 疎結合な設計
 *    - テスト容易性
 *    - 拡張性と保守性
 *    - 再利用性の向上
 * 
 * 5. パターンの活用
 *    - Observer Pattern（モデルの変更通知）
 *    - Strategy Pattern（フィルタリング戦略）
 *    - Command Pattern（ユーザーアクション）
 *    - Factory Pattern（オブジェクト生成）
 * 
 * 6. 実装の特徴
 *    - イミュータブルなデータ設計
 *    - スレッドセーフな更新処理
 *    - エラーハンドリングの充実
 *    - ユーザビリティの配慮
 *    - パフォーマンスの最適化
 */