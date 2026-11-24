// 組み立てたメールを表すクラス（本文では省略している補助クラス）
public class Email {
    private final String to;
    private final String subject;
    private final String body;
    private final boolean html;

    public Email(String to, String subject, String body, boolean html) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.html = html;
    }

    @Override
    public String toString() {
        return "宛先: " + to + "\n件名: " + subject
                + "\n形式: " + (html ? "HTML" : "テキスト")
                + "\n本文: " + body;
    }
}
