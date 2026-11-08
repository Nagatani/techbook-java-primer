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
