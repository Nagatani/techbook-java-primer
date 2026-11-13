import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 利用者の登録と検索（課題4の解答例）
 *
 * 失敗の知らせ方を、場面に合わせて使い分ける。
 * - 入力の検査: 誤りをすべて集めて返す（ValidationResult）
 * - 不正な値での登録: プログラムの誤りなので、例外（IllegalArgumentException）で止める
 * - 見つからない: よくあることなので、Optional（第12章）で「ないかもしれない」ことを返す
 */
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1001;

    public ValidationResult validate(String name, String email, int age) {
        ValidationResult result = new ValidationResult();
        if (name == null || name.isBlank()) {
            result.addError("名前が空です");
        }
        if (email == null || !email.contains("@")) {
            result.addError("メールアドレスの形式が正しくありません");
        }
        if (age < 0) {
            result.addError("年齢は0以上にしてください");
        }
        return result;
    }

    public User register(String name, String email, int age) {
        ValidationResult result = validate(name, email, age);
        if (!result.isValid()) {
            throw new IllegalArgumentException(String.join(" / ", result.getErrors()));
        }
        User user = new User(nextId, name, email, age);
        nextId++;
        users.put(user.id(), user);
        return user;
    }

    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }
}
