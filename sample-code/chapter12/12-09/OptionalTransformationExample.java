import java.util.Optional;

public class OptionalTransformationExample {
    static class User {
        private final String name;
        private final String email;  // メールアドレスがないときはnull

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() { return name; }
        // フィールドはOptionalにせず、戻り値で「値がないかもしれない」ことを示す
        public Optional<String> getEmail() { return Optional.ofNullable(email); }
    }
    
    public static void main(String[] args) {
        Optional<User> user = Optional.of(new User("Alice", "alice@example.com"));
        Optional<User> userWithoutEmail = Optional.of(new User("Bob", null));
        
        // 1. map() - 値が存在する場合に変換を適用
        Optional<String> userName = user.map(User::getName);
        IO.println("map: " + userName);  // Optional[Alice]
        
        // ネストしたmapの例
        Optional<Integer> nameLength = user
            .map(User::getName)
            .map(String::length);
        IO.println("Nested map: " + nameLength);  // Optional[5]
        
        // 2. flatMap() - Optional<Optional<T>>を平坦化
        // getEmailはOptional<String>を返すので、mapではOptional<Optional<String>>になる
        Optional<String> email = user.flatMap(u -> u.getEmail());
        IO.println("flatMap: " + email);  // Optional[alice@example.com]
        
        Optional<String> noEmail = userWithoutEmail.flatMap(u -> u.getEmail());
        IO.println("flatMap (no email): " + noEmail);  // Optional.empty
        
        // 3. filter() - 条件を満たす場合のみ値を保持
        Optional<User> longNameUser = user
            .filter(u -> u.getName().length() > 3);
        IO.println("filter (passed): " + longNameUser.isPresent());  // true
        
        Optional<User> shortNameUser = user
            .filter(u -> u.getName().length() > 10);
        IO.println("filter (failed): " + shortNameUser.isPresent());  // false
    }
}
