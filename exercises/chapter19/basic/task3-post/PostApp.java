import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 文字数を数えながら書く投稿画面（基礎課題3の解答例）
 *
 * DocumentListenerで、文字を打つたびに残りの文字数を表示し直します。
 */
public class PostApp extends JFrame {
    private final JTextArea inputArea = new JTextArea(5, 36);
    private final JTextArea timelineArea = new JTextArea(8, 36);
    private final JLabel counterLabel = new JLabel();
    private final JButton postButton = new JButton("投稿");
    private int postCount = 0;

    public PostApp() {
        super("投稿");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inputArea.setLineWrap(true);
        timelineArea.setEditable(false);
        timelineArea.setLineWrap(true);

        // 文字の追加・削除・属性の変更の3つが届く。どれでも表示を合わせ直す
        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCounter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCounter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCounter();
            }
        });

        // 投稿すると入力欄を空にする。DocumentListenerの中ではなく、ボタンの処理で書き換える
        postButton.addActionListener(e -> {
            postCount++;
            timelineArea.append(postCount + ": " + inputArea.getText() + "\n");
            inputArea.setText("");
            inputArea.requestFocusInWindow();
        });

        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("いまどうしてる？"));
        JScrollPane timelineScroll = new JScrollPane(timelineArea);
        timelineScroll.setBorder(BorderFactory.createTitledBorder("投稿した内容"));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(counterLabel);
        bottom.add(postButton);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputScroll, BorderLayout.CENTER);
        inputPanel.add(bottom, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        content.add(inputPanel, BorderLayout.NORTH);
        content.add(timelineScroll, BorderLayout.CENTER);
        setContentPane(content);

        updateCounter();
        pack();
        setLocationRelativeTo(null);
    }

    private void updateCounter() {
        String text = inputArea.getText();
        int remaining = PostRules.remaining(text);
        counterLabel.setText("残り " + remaining + "文字   ハッシュタグ " + PostRules.countHashtags(text) + "個");
        counterLabel.setForeground(remaining < 0 ? Color.RED : Color.DARK_GRAY);
        postButton.setEnabled(PostRules.canPost(text));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PostApp().setVisible(true));
    }
}
