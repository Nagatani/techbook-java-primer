import java.awt.BorderLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 入力が止まってから検索する画面（発展課題1の解答例）
 *
 * 文字を打つたびに検索せず、最後の入力から300ミリ秒たってから1回だけ検索します（デバウンス）。
 */
public class SearchApp extends JFrame {
    private static final int DELAY_MILLIS = 300;

    private final WordSearch wordSearch = new WordSearch(WordSearch.sampleWords());
    private final JTextField queryField = new JTextField(24);
    private final JTextArea resultArea = new JTextArea(14, 24);
    private final JLabel statusLabel = new JLabel();
    private final Timer debounceTimer;
    private int inputCount = 0;
    private int searchCount = 0;
    private int hitCount = 0;

    public SearchApp() {
        super("クラス名の検索");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1回だけ動くタイマー。restartするたびに待ち時間が最初からになる
        debounceTimer = new Timer(DELAY_MILLIS, e -> runSearch());
        debounceTimer.setRepeats(false);

        queryField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // 文字の属性の変化。テキストフィールドでは届かないので何もしない
            }
        });

        resultArea.setEditable(false);
        JLabel promptLabel = new JLabel("クラス名の一部（" + wordSearch.size() + "語から探す）");
        JPanel top = new JPanel(new BorderLayout(0, 4));
        top.add(promptLabel, BorderLayout.NORTH);
        top.add(queryField, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        content.add(top, BorderLayout.NORTH);
        content.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        content.add(statusLabel, BorderLayout.SOUTH);
        setContentPane(content);

        updateStatus();
        pack();
        setLocationRelativeTo(null);
    }

    // 入力のたびに呼ばれる。ここでは検索せず、タイマーをかけ直すだけにする
    private void onInput() {
        inputCount++;
        debounceTimer.restart();
        updateStatus();
    }

    // タイマーが鳴ったとき（入力が300ミリ秒止まったとき）に呼ばれる。TimerなのでEDTで動く
    private void runSearch() {
        searchCount++;
        List<String> hits = wordSearch.search(queryField.getText());
        hitCount = hits.size();
        resultArea.setText(String.join("\n", hits));
        updateStatus();
    }

    private void updateStatus() {
        statusLabel.setText("入力 " + inputCount + "回   検索 " + searchCount + "回   ヒット " + hitCount + "件");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchApp().setVisible(true));
    }
}
