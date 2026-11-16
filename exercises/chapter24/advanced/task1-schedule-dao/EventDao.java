import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 予定を読み書きするDAO
 */
public interface EventDao {
    /** 予定を登録し、データベースが振ったIDを付けた予定を返す */
    Event create(Event event) throws SQLException;

    Optional<Event> findById(int id) throws SQLException;

    /** その日に始まる予定を、始まりの時刻の順に返す */
    List<Event> findOn(LocalDate date) throws SQLException;

    /** fromからto（toの日を含む）までに始まる予定を、始まりの時刻の順に返す */
    List<Event> findBetween(LocalDate from, LocalDate to) throws SQLException;

    /** 指定した時間帯と重なる予定を返す（境目で接するだけのものは含めない） */
    List<Event> findOverlapping(LocalDateTime start, LocalDateTime end) throws SQLException;

    boolean update(Event event) throws SQLException;

    boolean delete(int id) throws SQLException;
}
