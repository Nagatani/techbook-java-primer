// TodoApp.java
import javax.swing.*;
import java.awt.*;

public class TodoApp {
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    
    public TodoApp() {
        createAndShowGUI();
    }
    
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Todo Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // リストコンポーネント
        JList<String> todoList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(todoList);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // 入力パネル
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        JButton addButton = new JButton("追加");
        
        addButton.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                listModel.addElement(text);
                inputField.setText("");
            }
        });
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);
        
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TodoApp::new);
    }
}
