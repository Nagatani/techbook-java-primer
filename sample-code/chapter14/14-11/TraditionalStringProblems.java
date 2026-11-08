public class TraditionalStringProblems {
    public static void main(String[] args) {
        // 従来の方法：読みにくく、エラーが起きやすい
        String jsonData = "{\n" +
                "  \"name\": \"田中太郎\",\n" +
                "  \"age\": 30,\n" +
                "  \"address\": {\n" +
                "    \"city\": \"東京\",\n" +
                "    \"zipCode\": \"100-0001\"\n" +
                "  }\n" +
                "}";
        
        String sqlQuery = "SELECT u.name, u.email, p.title\n" +
                "FROM users u\n" +
                "INNER JOIN posts p ON u.id = p.user_id\n" +
                "WHERE u.created_at > '2024-01-01'\n" +
                "ORDER BY u.name";
        
        String htmlTemplate = "<html>\n" +
                "<head>\n" +
                "  <title>ユーザー情報</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <h1>プロフィール</h1>\n" +
                "  <p>名前: %s</p>\n" +
                "  <p>年齢: %d</p>\n" +
                "</body>\n" +
                "</html>";
        
        System.out.println("=== 従来の方法の問題点 ===");
        System.out.println("1. エスケープシーケンス（\\n, \\\"）が多くて読みにくい");
        System.out.println("2. 文字列連結（+）によるエラーが起きやすい");
        System.out.println("3. インデントの管理が困難");
        System.out.println("4. 引用符のエスケープが煩雑");
    }
}
