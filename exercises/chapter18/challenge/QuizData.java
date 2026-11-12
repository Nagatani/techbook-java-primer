import java.util.List;

/**
 * 出題する問題。順番は固定。
 */
public class QuizData {
    public static List<Question> questions() {
        return List.of(
            new Question("Javaのプログラムの実行を始めるメソッドは？",
                List.of("start", "main", "run", "init"), 1),
            new Question("ボタンのクリックを受け取るリスナーは？",
                List.of("ActionListener", "KeyListener", "FocusListener", "ItemListener"), 0),
            new Question("Swingのイベントを処理するスレッドは？",
                List.of("mainスレッド", "ガベージコレクター", "イベントディスパッチスレッド", "ワーカースレッド"), 2),
            new Question("一定時間ごとにEDT上で処理を呼ぶクラスは？",
                List.of("java.util.Timer", "Thread", "javax.swing.Timer", "Runnable"), 2),
            new Question("チェックボックスの選択の変化を受け取るリスナーは？",
                List.of("ChangeListener", "ItemListener", "WindowListener", "MouseListener"), 1)
        );
    }
}
