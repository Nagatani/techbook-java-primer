package app;

import login.LoginService;
import login.UserAccount;

public class LoginApp {
    public static void main(String[] args) {
        LoginService service = new LoginService();

        IO.println("=== 登録 ===");
        service.register(new UserAccount("admin", "kanri-2026", UserAccount.ROLE_ADMIN));
        service.register(new UserAccount("tanaka", "tanaka-pass"));
        service.register(new UserAccount("tanaka", "another-pass"));

        IO.println();
        IO.println("=== ログイン ===");
        service.login("tanaka", "tanaka-pass");
        service.login("tanaka", "wrong-pass");
        service.login("tanaka", "wrong-pass");
        service.login("tanaka", "wrong-pass");
        service.login("tanaka", "tanaka-pass");
        service.login("suzuki", "suzuki-pass");

        IO.println();
        IO.println("=== ロックの解除 ===");
        service.unlock("tanaka", "tanaka-pass", "tanaka");
        service.unlock("admin", "kanri-2026", "tanaka");
        service.login("tanaka", "tanaka-pass");

        IO.println();
        IO.println("=== パスワードの変更 ===");
        UserAccount user = new UserAccount("sato", "sato-pass1");
        service.register(user);
        IO.println("短すぎる新しいパスワード: " + (user.changePassword("sato-pass1", "abc") ? "変更しました" : "変更できません"));
        IO.println("正しい手順: " + (user.changePassword("sato-pass1", "sato-pass2") ? "変更しました" : "変更できません"));
        service.login("sato", "sato-pass2");
        // user.unlock(); はパッケージが違うのでコンパイルエラーになる

        IO.println();
        IO.println("=== 状況 ===");
        service.showReport();
    }
}
