import java.util.ArrayList;
import java.util.List;

/**
 * 入力の検証結果（課題4の解答例）
 *
 * 誤りを見つけるたびに例外を投げると、最初の1つしか伝えられない。
 * 誤りをすべて集めて返すと、利用者は一度に全部を直せる。
 */
public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    public void addError(String message) {
        errors.add(message);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return List.copyOf(errors);
    }
}
