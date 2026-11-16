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
        // 中のパネルは透明にして、外側のパネルの背景色（選択色）を見せる
        textPanel.setOpaque(false);
        
        // 期限とチェックボックスのパネル
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        dueDateLabel = new JLabel();
        completedCheckBox = new JCheckBox("完了");
        rightPanel.add(dueDateLabel);
        rightPanel.add(completedCheckBox);
        rightPanel.setOpaque(false);
        completedCheckBox.setOpaque(false);
        
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
        
        // 中の部品の文字色も選択状態に合わせる（選択していない完了タスクの題名と説明は灰色）
        Color textColor = isSelected ? list.getSelectionForeground() : list.getForeground();
        Color doneColor = task.isCompleted() && !isSelected ? Color.GRAY : textColor;
        titleLabel.setForeground(doneColor);
        descriptionLabel.setForeground(doneColor);
        dueDateLabel.setForeground(textColor);
        completedCheckBox.setForeground(textColor);
        
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
