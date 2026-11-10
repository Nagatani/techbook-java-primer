public class TryCatchSample {
    public static void main(String[] args) {
        try {
            String str = null;
            IO.println(str.length()); // ここでNullPointerExceptionが発生
            IO.println("この行は実行されません。");
        } catch (NullPointerException e) {
            System.err.println("NullPointerExceptionをキャッチしました！");
            e.printStackTrace(); // 開発中はスタックトレースを出力するとデバッグに役立つ
        } catch (Exception e) {
            System.err.println("その他の予期せぬ例外をキャッチしました。");
        }
        IO.println("プログラムは処理を継続しています。");
    }
}
