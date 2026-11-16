// StudentRepository.java - 学生の保存と検索の窓口（インターフェイスにした）
// 検索・集計はStudentServiceでfindAllを使って行い、ここには保存に必要な操作だけを置く
import java.util.List;

public interface StudentRepository<T extends Student> {
    // 同じIDの学生がいればIllegalArgumentExceptionを投げる
    void addStudent(T student);

    // 見つからなければnullを返す（フェーズ3の振る舞いのまま）
    T findById(String studentId);

    // 登録した順に返す
    List<T> findAll();

    // 削除したらtrueを返す
    boolean removeStudent(String studentId);
}
