/**
 * リスト21-8
 * UserRepositoryStubクラス
 * 
 * 元ファイル: chapter21-unit-testing.md (260行目)
 */

// UserRepositoryStub.java - テスト用の偽物リポジトリ
public class UserRepositoryStub implements UserRepository {
    @Override
    public String findById(String id) {
        // DBには接続せず、テスト用の固定値を返します
        return "Test User";
    }
}

// UserServiceManualTest.java
public class UserServiceManualTest {
    public static void main(String[] args) {
        // Arrange（準備）
        UserRepository stub = new UserRepositoryStub();
        UserService userService = new UserService(stub);

        // Act（実行）
        String actual = userService.getUserProfile("123");

        // Assert（検証）
        String expected = "【Profile】Test User";
        if (expected.equals(actual)) {
            System.out.println("✅ テスト成功！");
        } else {
            System.err.println("❌ テスト失敗！");
        }
    }
}