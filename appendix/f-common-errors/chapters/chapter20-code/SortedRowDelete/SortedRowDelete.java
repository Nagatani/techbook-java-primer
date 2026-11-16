import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class SortedRowDelete {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SortedRowDelete::createAndShow);
    }

    private static void createAndShow() {
        String[] columns = {"名前", "点数"};
        Object[][] rows = {{"佐藤", 72}, {"鈴木", 95}, {"高橋", 58}, {"田中", 81}};
        DefaultTableModel model = new DefaultTableModel(rows, columns);
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);  // 見出しをクリックすると並べ替わる

        JButton deleteButton = new JButton("選択した行を削除");
        deleteButton.addActionListener(e -> {
            int viewRow = table.getSelectedRow();  // 画面上の行番号
            if (viewRow < 0) {
                return;  // 何も選択されていない
            }
            // 並べ替えた後は画面の行番号とモデルの行番号が違うので、変換してから使う
            int modelRow = table.convertRowIndexToModel(viewRow);
            model.removeRow(modelRow);
        });

        JFrame frame = new JFrame("並べ替えた表の行の削除");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(deleteButton, BorderLayout.SOUTH);
        frame.setSize(320, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
