public class EmailBuilderDemo {
    public static void main(String[] args) {
        // 使用例：流暢なインターフェイス
        Email email = new EmailBuilder()
            .to("user@example.com")
            .subject("重要なお知らせ")
            .body("<h1>こんにちは</h1>")
            .asHtml()
            .build();
        IO.println(email);
    }
}
