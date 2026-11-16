// StudentRepository.java - 学生の保存と検索の窓口。保存の方法はここには書かない
import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    // 学生を保存する（同じIDの学生がいれば置き換える）
    void save(Student student);

    Optional<Student> findById(String id);

    // 登録した順に返す
    List<Student> findAll();

    // 削除したらtrueを返す
    boolean deleteById(String id);
}
