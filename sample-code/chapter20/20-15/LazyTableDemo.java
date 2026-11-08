import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class LazyTableDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 10万行のデータを、表示に必要なページだけ作るデータ源
            DataSource dataSource = new DataSource() {
                @Override
                public int getTotalRowCount() {
                    return 100000;
                }

                @Override
                public List<Object[]> getData(int startIndex, int count) {
                    List<Object[]> page = new ArrayList<>();
                    for (int i = startIndex; i < Math.min(startIndex + count, 100000); i++) {
                        page.add(new Object[]{i, "データ" + i, Math.random()});
                    }
                    return page;
                }
            };
            JTable table = new JTable(new LazyTableModel(dataSource));

            // 2. テーブルの最適化設定
            table.setAutoCreateRowSorter(true);
            table.setFillsViewportHeight(true);
            table.getTableHeader().setReorderingAllowed(false);

            // 3. 不要な更新を避ける
            table.setUpdateSelectionOnSort(false);
            table.setRowHeight(20);  // 固定の行高さを設定

            JFrame frame = new JFrame("遅延読み込みのテーブル");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JScrollPane(table));
            frame.setSize(500, 400);
            frame.setVisible(true);
        });
    }
}
