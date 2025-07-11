/**
 * リスト21-6
 * UserServiceクラス
 * 
 * 元ファイル: chapter21-unit-testing.md (206行目)
 */

public class UserService {
    private UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}