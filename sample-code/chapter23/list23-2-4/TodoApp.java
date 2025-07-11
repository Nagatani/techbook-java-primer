/**
 * リスト23-4
 * TodoAppクラス
 * 
 * 元ファイル: chapter23-build-and-deploy.md (256行目)
 */

// TodoApp.java
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TodoApp {
    private List<TodoItem> items = new ArrayList<>();
    private DefaultListModel<TodoItem> listModel = new DefaultListModel<>();
    
    public TodoApp() {
        createAndShowGUI();
    }
    
    private void createAndShowGUI() {
        JFrame frame = new JFrame(AppConfig.getAppName() + " v" + AppConfig.getVersion());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // リストコンポーネント
        JList<TodoItem> todoList = new JList<>(listModel);
        todoList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(todoList);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // 入力パネル
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        JButton addButton = new JButton("追加");
        
        addButton.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                TodoItem item = new TodoItem(text);
                items.add(item);
                listModel.addElement(item);
                inputField.setText("");
            }
        });
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        JButton completeButton = new JButton("完了/未完了");
        JButton deleteButton = new JButton("削除");
        
        completeButton.addActionListener(e -> {
            int index = todoList.getSelectedIndex();
            if (index >= 0) {
                TodoItem item = items.get(index);
                item.setCompleted(!item.isCompleted());
                listModel.setElementAt(item, index);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int index = todoList.getSelectedIndex();
            if (index >= 0) {
                items.remove(index);
                listModel.remove(index);
            }
        });
        
        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);
        frame.add(buttonPanel, BorderLayout.NORTH);
        
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TodoApp::new);
    }
}