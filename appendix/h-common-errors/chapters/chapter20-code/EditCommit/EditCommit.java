import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class EditCommit {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditCommit::createAndShow);
    }

    private static void createAndShow() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[][] {{"りんご", "120"}, {"みかん", "80"}},
                new String[] {"品名", "価格"});
        JTable table = new JTable(model);
        // ほかの部品にフォーカスが移ったら、編集中の値を確定させる
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            // 編集中のセルがあれば、値をモデルに書き込んで編集を終える
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            for (int row = 0; row < model.getRowCount(); row++) {
                IO.println(model.getValueAt(row, 0) + ": " + model.getValueAt(row, 1));
            }
        });

        JFrame frame = new JFrame("編集中の値の確定");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(saveButton, BorderLayout.SOUTH);
        frame.setSize(300, 180);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
