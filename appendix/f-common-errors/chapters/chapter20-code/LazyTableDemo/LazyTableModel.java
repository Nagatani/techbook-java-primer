import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

// 1. 遅延読み込み（Lazy Loading）の実装
public class LazyTableModel extends AbstractTableModel {
    private final List<Object[]> dataCache = new ArrayList<>();
    private final DataSource dataSource;
    private final int pageSize = 1000;
    
    public LazyTableModel(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public int getRowCount() {
        return dataSource.getTotalRowCount();
    }
    
    @Override
    public int getColumnCount() {
        return 3;
    }
    
    @Override
    public Object getValueAt(int row, int column) {
        // 必要に応じてデータを読み込み
        ensureDataLoaded(row);
        return dataCache.get(row)[column];
    }
    
    private void ensureDataLoaded(int row) {
        if (row >= dataCache.size() || dataCache.get(row) == null) {
            // ページ単位でデータを読み込み
            int startIndex = (row / pageSize) * pageSize;
            List<Object[]> pageData = dataSource.getData(startIndex, pageSize);
            
            // キャッシュを拡張
            while (dataCache.size() < startIndex + pageData.size()) {
                dataCache.add(null);
            }
            
            // データを設定
            for (int i = 0; i < pageData.size(); i++) {
                dataCache.set(startIndex + i, pageData.get(i));
            }
        }
    }
}
