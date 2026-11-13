import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class TaskTableDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskTableModel model = new TaskTableModel();
            model.addTask(new Task("レポートを書く", false));
            JTable table = new JTable(model);

            JButton addButton = new JButton("追加");
            addButton.addActionListener(e ->
                    model.addTask(new Task("タスク" + (model.getRowCount() + 1), false)));

            JFrame frame = new JFrame("変更を通知するテーブルモデル");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JScrollPane(table), BorderLayout.CENTER);
            frame.add(addButton, BorderLayout.SOUTH);
            frame.setSize(320, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
