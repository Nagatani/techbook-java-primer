import java.util.logging.*;
import java.time.Instant;
import java.util.*;

public class ExceptionLogging {
    private static final Logger logger = 
        Logger.getLogger(ExceptionLogging.class.getName());
    
    public void processUserRequest(String userId) {
        try {
            User user = findUser(userId);
            performBusinessLogic(user);
        } catch (UserNotFoundException e) {
            // ビジネス例外：警告レベル
            logger.log(Level.WARNING, 
                "ユーザーが見つかりません: " + userId, e);
        } catch (ValidationException e) {
            // 入力検証エラー：情報レベル（想定内のエラー）
            logger.log(Level.INFO, "検証エラー: " + e.getMessage());
        } catch (DatabaseException e) {
            // システム例外：エラーレベル
            logger.log(Level.SEVERE, 
                "データベースエラーが発生しました", e);
            
            // アラート送信などの追加処理
            notifyAdministrator(e);
        } catch (Exception e) {
            // 予期せぬ例外：重大エラー
            // ここで受け止めて終えるので、ログに書くのはこの1回だけ
            String errorId = generateErrorId();
            logger.log(Level.SEVERE,
                    "予期せぬエラーが発生しました。エラーID: " + errorId, e);

            // 利用者にはエラーIDだけを伝える
            IO.println("システムエラーが発生しました。エラーID: " + errorId);
        }
    }
    
    private String generateErrorId() {
        return "ERR-" + System.currentTimeMillis();
    }
    
    private void notifyAdministrator(Exception e) {
        // 管理者への通知処理
    }
    
    // 以下は本文では省略している補助メソッド
    private User findUser(String userId) throws UserNotFoundException, DatabaseException {
        if (userId == null || userId.isEmpty()) {
            throw new UserNotFoundException("ユーザーIDが空です");
        }
        return new User(userId);
    }
    
    private void performBusinessLogic(User user) throws ValidationException {
        IO.println(user.id() + " の処理を実行しました");
    }
}
