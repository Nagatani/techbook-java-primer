/**
 * リスト22-8
 * Userクラス
 * 
 * 元ファイル: chapter22-documentation-and-libraries.md (767行目)
 */

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.NonNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

// @Data = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @NonNull
    private String username;
    
    private String email;
    
    private int age;
    
    @ToString.Exclude  // toStringから除外
    private String password;
}

// ログ機能付きクラス
@Slf4j
public class LombokExample {
    public static void main(String[] args) {
        // 1. Builderパターンでインスタンス作成
        User user = User.builder()
            .username("tanaka")
            .email("tanaka@example.com")
            .age(25)
            .password("secret123")
            .build();
        
        // 2. 自動生成されたメソッドの使用
        System.out.println("User: " + user);  // toStringが自動生成
        System.out.println("Username: " + user.getUsername());  // getterが自動生成
        
        user.setAge(26);  // setterが自動生成
        
        // 3. equalsの動作確認
        User sameUser = User.builder()
            .username("tanaka")
            .email("tanaka@example.com")
            .age(26)
            .password("different")
            .build();
        
        System.out.println("Equals: " + user.equals(sameUser));
        
        // 4. ログ出力（SLF4J）
        log.info("User created: {}", user.getUsername());
        log.debug("Debug information: {}", user);
        
        // 5. @NonNullの動作確認
        try {
            User invalidUser = new User(null, "email@example.com", 20, "pass");
        } catch (NullPointerException e) {
            log.error("Null username not allowed", e);
        }
    }
}

// 個別のアノテーション使用例
class Product {
    @Getter @Setter
    private String name;
    
    @Getter
    private final double price;  // finalフィールドはsetterなし
    
    @ToString.Include(name = "id")  // toStringでの名前を指定
    private long productId;
    
    public Product(double price) {
        this.price = price;
    }
}