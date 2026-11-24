import javax.swing.*;
import java.awt.*;

public class BasicTableExample {
    private final JFrame frame = new JFrame("基本的なJTableの例");

    public BasicTableExample() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] columnNames = {"ID", "名前", "年齢", "メールアドレス"};
        Object[][] data = {
            {1, "田中太郎", 25, "tanaka@example.com"},
            {2, "鈴木花子", 30, "suzuki@example.com"},
            {3, "佐藤次郎", 28, "sato@example.com"},
            {4, "山田美咲", 22, "yamada@example.com"}
        };

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);
    }
    
    private void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicTableExample().show();
        });
    }
}
