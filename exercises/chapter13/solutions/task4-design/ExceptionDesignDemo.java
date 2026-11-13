import java.util.Optional;

/**
 * 失敗の知らせ方の使い分けを試すプログラム（課題4の解答例）
 */
public class ExceptionDesignDemo {
    public static void main(String[] args) {
        UserService service = new UserService();
        IO.println("--- 検証結果をまとめて返す ---");
        ValidationResult ok = service.validate("田中太郎", "tanaka@example.com", 25);
        IO.println("田中太郎: " + (ok.isValid() ? "成功" : "失敗"));
        ValidationResult ng = service.validate("", "invalid-email", -5);
        IO.println("空の名前: " + (ng.isValid() ? "成功" : "失敗"));
        for (String error : ng.getErrors()) {
            IO.println("- " + error);
        }
        IO.println("\n--- 不正な値は例外で知らせる ---");
        User tanaka = service.register("田中太郎", "tanaka@example.com", 25);
        IO.println("登録: " + tanaka);
        try {
            service.register("佐藤花子", "sato.example.com", 30);
        } catch (IllegalArgumentException e) {
            IO.println("登録できません: " + e.getMessage());
        }
        IO.println("\n--- 見つからないことはOptionalで返す ---");
        Optional<User> found = service.findById(1001);
        IO.println("ID=1001: " + found.map(User::name).orElse("ゲスト"));
        Optional<User> missing = service.findById(9999);
        IO.println("ID=9999: " + missing.map(User::name).orElse("ゲスト"));
    }
}
