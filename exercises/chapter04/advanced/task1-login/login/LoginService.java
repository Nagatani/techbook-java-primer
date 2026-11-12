package login;

/**
 * アカウントを登録し、ログインとロックの解除を受け付けるクラス
 */
public class LoginService {
    private UserAccount[] accounts;
    private int count;
    private int totalFailures;

    public LoginService(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("登録できる数は1以上にしてください");
        }
        this.accounts = new UserAccount[capacity];
    }

    // 上限を省いたときは10人まで
    public LoginService() {
        this(10);
    }

    // 同じIDがあるか、いっぱいのときは登録しない
    public boolean register(UserAccount account) {
        if (count >= accounts.length || find(account.getUserId()) != null) {
            IO.println("登録できません: " + account.getUserId());
            return false;
        }
        accounts[count] = account;
        count++;
        IO.println("登録しました: " + account.getUserId() + "（" + account.getRole() + "）");
        return true;
    }

    public boolean login(String userId, String password) {
        UserAccount account = find(userId);
        if (account == null) {
            IO.println(userId + ": 登録されていません");
            return false;
        }
        if (account.isLocked()) {
            IO.println(userId + ": ロック中のためログインできません");
            return false;
        }
        if (account.login(password)) {
            IO.println(userId + ": ログインしました（" + account.getRole() + "）");
            return true;
        }
        totalFailures++;
        String message = userId + ": パスワードが違います（"
                + account.getFailureCount() + "/" + UserAccount.MAX_FAILURES + "回）";
        if (account.isLocked()) {
            message += "。ロックしました";
        }
        IO.println(message);
        return false;
    }

    // 管理者のIDとパスワードが正しいときだけ、対象のロックを解除する
    public boolean unlock(String adminId, String adminPassword, String targetId) {
        UserAccount admin = find(adminId);
        UserAccount target = find(targetId);
        if (admin == null || !admin.isAdmin() || !admin.login(adminPassword)) {
            IO.println(targetId + "のロック解除: 管理者として確認できません");
            return false;
        }
        if (target == null || !target.isLocked()) {
            IO.println(targetId + "のロック解除: ロックされていません");
            return false;
        }
        target.unlock();
        IO.println(targetId + "のロック解除: 解除しました");
        return true;
    }

    public void showReport() {
        int lockedCount = 0;
        for (int i = 0; i < count; i++) {
            if (accounts[i].isLocked()) {
                lockedCount++;
            }
        }
        IO.println("登録数: " + count + "人、ロック中: " + lockedCount
                + "人、パスワードの誤り: " + totalFailures + "回");
    }

    private UserAccount find(String userId) {
        for (int i = 0; i < count; i++) {
            if (accounts[i].getUserId().equals(userId)) {
                return accounts[i];
            }
        }
        return null;
    }
}
