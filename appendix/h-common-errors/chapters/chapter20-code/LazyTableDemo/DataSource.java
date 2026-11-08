import java.util.List;

// 全体の行数と、指定した範囲の行を返すデータ源（データベースなどを想定）
public interface DataSource {
    int getTotalRowCount();
    List<Object[]> getData(int startIndex, int count);
}
