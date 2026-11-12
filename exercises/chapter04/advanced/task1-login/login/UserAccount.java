package login;

/**
 * 利用者のアカウント。パスワードはそのまま持たず、変換した値（ハッシュ値）だけを持つ
 *
 * パスワードのハッシュ値と失敗の回数はprivateにし、外から読めないようにする。
 * ロックの解除はパッケージプライベートにして、同じパッケージのLoginServiceからだけ呼べるようにする。
 */
public class UserAccount {
    public static final String ROLE_USER = "一般";
    public static final String ROLE_ADMIN = "管理者";
    public static final int MAX_FAILURES = 3;
    public static final int MIN_PASSWORD_LENGTH = 8;

    private final String userId;
    private final String role;
    private int passwordHash;
    private int failureCount;
    private boolean locked;

    public UserAccount(String userId, String password, String role) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("利用者IDを指定してください");
        }
        if (!ROLE_USER.equals(role) && !ROLE_ADMIN.equals(role)) {
            throw new IllegalArgumentException("権限は「一般」か「管理者」にしてください");
        }
        checkPassword(password);
        this.userId = userId;
        this.role = role;
        this.passwordHash = hash(password);
    }

    // 権限を省いたときは一般の利用者にする
    public UserAccount(String userId, String password) {
        this(userId, password, ROLE_USER);
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equals(role);
    }

    public boolean isLocked() {
        return locked;
    }

    public int getFailureCount() {
        return failureCount;
    }

    // パスワードを確かめる。続けてMAX_FAILURES回まちがえるとロックする
    public boolean login(String password) {
        if (locked) {
            return false;
        }
        if (password != null && hash(password) == passwordHash) {
            failureCount = 0;
            return true;
        }
        failureCount++;
        if (failureCount >= MAX_FAILURES) {
            locked = true;
        }
        return false;
    }

    // いまのパスワードが正しいときだけ変更する
    public boolean changePassword(String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        if (!login(oldPassword)) {
            return false;
        }
        passwordHash = hash(newPassword);
        return true;
    }

    // パッケージプライベート: LoginServiceが管理者を確かめてから呼ぶ
    void unlock() {
        locked = false;
        failureCount = 0;
    }

    private static void checkPassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "パスワードは" + MIN_PASSWORD_LENGTH + "文字以上にしてください");
        }
    }

    // 練習用の簡単な変換。実際のシステムでは、パスワード専用のハッシュ関数を使う
    private static int hash(String password) {
        return (password + "#salt").hashCode();
    }
}
