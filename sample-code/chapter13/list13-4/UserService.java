/**
 * リスト13-4
 * UserServiceクラス
 * 
 * 元ファイル: chapter13-lambda-and-functional-interfaces.md (149行目)
 */

public class UserService {
    // モナドを使わない場合
    public String getUserEmailTraditional(String userId) {
        User user = userRepository.findById(userId);
        if (user != null) {
            Profile profile = user.getProfile();
            if (profile != null) {
                Email email = profile.getEmail();
                if (email != null && email.isVerified()) {
                    return email.getAddress();
                }
            }
        }
        return "noemail@example.com";
    }
    
    // Optionalモナドを使った場合
    public String getUserEmail(String userId) {
        return userRepository.findById(userId)
            .map(User::getProfile)
            .map(Profile::getEmail)
            .filter(Email::isVerified)
            .map(Email::getAddress)
            .orElse("noemail@example.com");
    }
    
    // さらに高度なモナドの合成
    public CompletableFuture<String> sendNotification(String userId) {
        return userRepository.findByIdAsync(userId)
            .thenCompose(userOpt -> userOpt
                .map(user -> notificationService.send(user))
                .orElse(CompletableFuture.
                    completedFuture("User not found"))
            );
    }
}