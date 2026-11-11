public class JsonDemo {
    public static void main(String[] args) {
        // 使用例
        User[] users = { new User("Alice"), new User("Bob") };
        IO.println("Serialized users: " + JsonSerializable.serializeAll(users));

        User special = new User("Charlie \"The Boss\" Smith\nNew line");
        IO.println("Special user JSON: " + special.toJson());
    }
}
