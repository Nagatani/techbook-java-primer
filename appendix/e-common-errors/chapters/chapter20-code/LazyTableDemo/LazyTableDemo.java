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
            // 2. テーブルの設定
            JTable table = new JTable(new LazyTableModel(dataSource));
            table.setFillsViewportHeight(true);
            table.setRowHeight(20);  // 行の高さを固定にする
            // setAutoCreateRowSorter(true)で並べ替えを有効にすると、
            // 並べ替えのために全行の値を読むので、遅延読み込みの効果がなくなる

            JFrame frame = new JFrame("遅延読み込みのテーブル");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JScrollPane(table));
            frame.setSize(500, 400);
            frame.setVisible(true);
        });
    }
}
