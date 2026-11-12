// TodoApp.java
import javax.swing.*;
import java.awt.*;

public class TodoApp {
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    public TodoApp() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Todo Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // リストコンポーネント
        JList<String> todoList = new JList<>(listModel);
        frame.add(new JScrollPane(todoList), BorderLayout.CENTER);

        // 入力パネル（TodoInputPanel.javaで定義したクラス）
        frame.add(new TodoInputPanel(listModel), BorderLayout.SOUTH);

        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TodoApp::new);
    }
}
