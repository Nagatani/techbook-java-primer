public class UserService {
    private final UserRepository userRepository;

    // コンストラクターで依存性を受け取ります
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
