/**
 * 会員登録の入力を検査する（画面に依存しないロジック）
 * 正しければnull、誤りがあればその理由を返す
 */
public class MemberValidator {
    public static String checkName(String name) {
        if (name.isBlank()) {
            return "名前を入力してください";
        }
        return null;
    }

    public static String checkAge(String text) {
        try {
            int age = Integer.parseInt(text.trim());
            if (age < 0 || age > 120) {
                return "年齢は0〜120で入力してください";
            }
            return null;
        } catch (NumberFormatException e) {
            return "年齢は数字で入力してください";
        }
    }

    public static String checkEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@') || !email.substring(at + 1).contains(".")) {
            return "メールアドレスの形式が正しくありません";
        }
        return null;
    }
}
