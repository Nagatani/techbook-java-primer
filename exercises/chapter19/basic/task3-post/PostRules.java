/**
 * 投稿の文字数とハッシュタグを数えるクラス（画面に依存しないロジック）
 */
public class PostRules {
    public static final int MAX_LENGTH = 140;

    private PostRules() {
    }

    /** あと何文字書けるか。超えているときは負の数になる */
    public static int remaining(String text) {
        return MAX_LENGTH - text.length();
    }

    /** 空白だけでなく、上限を超えていなければ投稿できる */
    public static boolean canPost(String text) {
        return !text.isBlank() && text.length() <= MAX_LENGTH;
    }

    /** 「#」で始まり、後ろに1文字以上続く語の数 */
    public static int countHashtags(String text) {
        int count = 0;
        for (String word : text.split("\\s+")) {
            if (word.startsWith("#") && word.length() > 1) {
                count++;
            }
        }
        return count;
    }
}
