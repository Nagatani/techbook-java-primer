import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * 制限時間つきの4択クイズ。
 * ボタンのクリックか、数字キー1〜4で答える。Enterキーで次の問題へ進む。
 */
public class QuizApp {
    private static final int TIME_LIMIT = 10;

    private final Quiz quiz = new Quiz(QuizData.questions(), TIME_LIMIT);
    private final JFrame frame = new JFrame("Javaクイズ");
    private final JPanel root = new JPanel(new BorderLayout(10, 10));
    private final JLabel statusLabel = new JLabel();
    private final JLabel questionLabel = new JLabel();
    private final JButton[] choiceButtons = new JButton[4];
    private final JLabel feedbackLabel = new JLabel(" ", JLabel.CENTER);
    private final JButton nextButton = new JButton("次の問題へ");
    private final Timer timer;

    public QuizApp() {
        // 1秒ごとに残り時間を減らす
        timer = new Timer(1000, e -> {
            Result result = quiz.tick();
            if (result == Result.TIME_UP) {
                showResult(result);
            } else {
                updateStatus();
            }
        });

        JPanel choicePanel = new JPanel(new GridLayout(2, 2, 8, 8));
        for (int i = 0; i < choiceButtons.length; i++) {
            int index = i;  // ラムダ式の中で使うため、変更しない変数に写す
            choiceButtons[i] = new JButton();
            choiceButtons[i].setFocusable(false);  // キー入力をrootパネルに集める
            choiceButtons[i].addActionListener(e -> answer(index));
            choicePanel.add(choiceButtons[i]);
        }

        nextButton.setFocusable(false);
        nextButton.addActionListener(e -> goNext());

        // 数字キーで回答、Enterで次へ
        root.setFocusable(true);
        root.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_4) {
                    answer(code - KeyEvent.VK_1);
                } else if (code == KeyEvent.VK_ENTER && nextButton.isEnabled()) {
                    goNext();
                }
            }
        });

        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        feedbackLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel north = new JPanel(new GridLayout(2, 1, 0, 8));
        north.add(statusLabel);
        north.add(questionLabel);

        JPanel south = new JPanel(new GridLayout(2, 1));
        south.add(feedbackLabel);
        JPanel nextPanel = new JPanel(new FlowLayout());
        nextPanel.add(nextButton);
        south.add(nextPanel);

        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.add(north, BorderLayout.NORTH);
        root.add(choicePanel, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        // 途中で閉じようとしたら確認する
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmClose();
            }
        });

        frame.add(root);
        frame.setSize(520, 320);
        frame.setLocationRelativeTo(null);
        showQuestion();
        frame.setVisible(true);
        root.requestFocusInWindow();
    }

    /** 現在の問題を表示し、タイマーを動かす */
    private void showQuestion() {
        Question q = quiz.currentQuestion();
        questionLabel.setText(q.text());
        List<String> choices = q.choices();
        for (int i = 0; i < choiceButtons.length; i++) {
            choiceButtons[i].setText((i + 1) + ". " + choices.get(i));
            choiceButtons[i].setEnabled(true);
            choiceButtons[i].setBackground(null);
        }
        feedbackLabel.setText(" ");
        nextButton.setEnabled(false);
        nextButton.setText(quiz.isLastQuestion() ? "結果を見る" : "次の問題へ");
        updateStatus();
        timer.restart();
    }

    private void answer(int index) {
        Result result = quiz.answer(index);
        if (result != null) {  // 回答済みのときはnullなので何もしない
            showResult(result);
        }
    }

    /** 回答の結果を表示し、タイマーを止める */
    private void showResult(Result result) {
        timer.stop();
        Question q = quiz.currentQuestion();
        for (JButton b : choiceButtons) {
            b.setEnabled(false);
        }
        choiceButtons[q.answerIndex()].setBackground(new Color(200, 240, 200));
        if (result == Result.CORRECT) {
            feedbackLabel.setForeground(new Color(0, 128, 0));
            feedbackLabel.setText("正解です");
        } else {
            feedbackLabel.setForeground(Color.RED);
            feedbackLabel.setText("%sです。正解は「%s」".formatted(result.getLabel(), q.answerText()));
        }
        nextButton.setEnabled(true);
        updateStatus();
    }

    private void goNext() {
        if (quiz.next()) {
            showQuestion();
        } else if (quiz.isFinished()) {
            showFinalScore();
        }
    }

    /** 全問の結果を表示し、もう一度挑戦するかを尋ねる */
    private void showFinalScore() {
        StringBuilder sb = new StringBuilder();
        sb.append("%d問中%d問正解でした\n\n".formatted(quiz.getQuestionCount(), quiz.getScore()));
        List<Result> results = quiz.getResults();
        for (int i = 0; i < results.size(); i++) {
            sb.append("第%d問: %s\n".formatted(i + 1, results.get(i).getLabel()));
        }
        sb.append("\nもう一度挑戦しますか？");
        int choice = JOptionPane.showConfirmDialog(frame, sb.toString(), "結果",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            quiz.restart();
            showQuestion();
        } else {
            frame.dispose();
            System.exit(0);
        }
    }

    private void updateStatus() {
        statusLabel.setText("第%d問 / 全%d問　残り %d秒　正解 %d問".formatted(
                quiz.getCurrentNumber(), quiz.getQuestionCount(),
                quiz.getRemainingSeconds(), quiz.getScore()));
    }

    private void confirmClose() {
        if (quiz.isInProgress()) {
            timer.stop();
            int choice = JOptionPane.showConfirmDialog(frame,
                    "クイズの途中です。終了しますか？", "確認", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                if (!quiz.isAnswered()) {
                    timer.start();  // 出題中なら時間を数え直さずに再開する
                }
                return;
            }
        }
        frame.dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuizApp());
    }
}
