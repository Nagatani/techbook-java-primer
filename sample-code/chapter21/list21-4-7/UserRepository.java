/**
 * リスト21-7
 * UserServiceクラス
 * 
 * 元ファイル: chapter21-unit-testing.md (236行目)
 */

// UserRepository.java - インターフェイス
public interface UserRepository {
    String findById(String id);
}

// UserService.java - DIを適用
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUserProfile(String id) {
        String name = userRepository.findById(id);
        return "【Profile】" + name;
    }
}