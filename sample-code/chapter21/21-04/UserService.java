// UserService.java - ビジネスロジックを担当するクラス
public class UserService {
    // 内部で直接UserRepositoryを生成しています（密結合）
    private final UserRepository userRepository = new UserRepository();

    public String getUserProfile(String id) {
        String name = userRepository.findById(id);
        return "【Profile】" + name;
    }
}
