// JdbcUserDao.java - UserDaoをJDBCで実装する（SQLを書くのはこのクラスだけ）
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserDao implements UserDao {
    private final Connection conn;

    public JdbcUserDao(Connection conn) {
        this.conn = conn;
    }

    @Override
    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        // RETURN_GENERATED_KEYSで、データベースが振ったIDを受け取れるようにする
        try (PreparedStatement ps = conn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.username());
            ps.setString(2, user.email());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("IDを取得できません");
                }
                // 登録日時もデータベースが付けるので、読み直して返す
                return findById(keys.getInt(1)).orElseThrow();
            }
        }
    }

    @Override
    public Optional<User> findById(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(toUser(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM users ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(toUser(rs));
            }
        }
        return users;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.username());
            ps.setString(2, user.email());
            ps.setInt(3, user.id());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ResultSetの今の行をUserにする（列とフィールドの対応をここに集める）
    private User toUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("username"),
                rs.getString("email"),
            rs.getObject("created_at", LocalDateTime.class));
    }
}
