// UserDao.java - ユーザーの保存と検索の窓口（SQLを知らない側から使う）
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user) throws SQLException;          // 追加して、IDと登録日時の付いたUserを返す
    Optional<User> findById(int id) throws SQLException;
    List<User> findAll() throws SQLException;
    boolean update(User user) throws SQLException;       // 更新したらtrue
    boolean delete(int id) throws SQLException;          // 削除したらtrue
}
