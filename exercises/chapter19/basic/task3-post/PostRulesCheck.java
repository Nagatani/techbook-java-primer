/**
 * PostRulesの動作確認
 */
public class PostRulesCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== PostRulesの動作確認 ===");
        check("空なら残り140文字", 140, PostRules.remaining(""));
        check("日本語も1文字ずつ数える", 135, PostRules.remaining("こんにちは"));
        check("超えると負の数", -1, PostRules.remaining("あ".repeat(141)));
        check("空は投稿できない", false, PostRules.canPost(""));
        check("空白だけは投稿できない", false, PostRules.canPost("  \n "));
        check("140文字ちょうどは投稿できる", true, PostRules.canPost("あ".repeat(140)));
        check("141文字は投稿できない", false, PostRules.canPost("あ".repeat(141)));
        check("ハッシュタグを数える", 2, PostRules.countHashtags("Java楽しい #java #勉強中"));
        check("#だけの語は数えない", 0, PostRules.countHashtags("# のみ"));
        check("語の途中の#は数えない", 0, PostRules.countHashtags("C#を学ぶ"));
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, Object expected, Object actual) {
        total++;
        if (expected.equals(actual)) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
