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
        IO.println("User: " + user);  // toStringが自動生成
        IO.println("Username: " + user.getUsername());  // getterが自動生成
        
        user.setAge(26);  // setterが自動生成
        IO.println("Updated age: " + user.getAge());
        
        // 3. equalsの動作確認（passwordもequalsの比較に含まれる）
        User otherPassword = User.builder()
            .username("tanaka")
            .email("tanaka@example.com")
            .age(26)
            .password("different")
            .build();
        IO.println("Equals (different password): " + user.equals(otherPassword));
        
        otherPassword.setPassword("secret123");
        IO.println("Equals (same password): " + user.equals(otherPassword));
        
        // 4. @NonNullの動作確認
        try {
            new User(null, "email@example.com", 20, "pass");
        } catch (NullPointerException e) {
            IO.println("NullPointerException: " + e.getMessage());
        }
    }
}
