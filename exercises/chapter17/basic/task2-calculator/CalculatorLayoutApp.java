import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * 電卓の画面（基礎課題2の解答例）
 *
 * 表示欄・ボタンの格子・クリアボタンを、BorderLayoutとGridLayoutで組み立てます。
 * 計算の処理は、第18章で学ぶイベント処理で付けます。
 */
public class CalculatorLayoutApp extends JFrame {
    // ボタンの文字を、画面に並ぶ順に配列へ入れておく
    private static final String[] KEYS = {
        "7", "8", "9", "÷",
        "4", "5", "6", "×",
        "1", "2", "3", "−",
        "0", ".", "=", "+"
    };

    public CalculatorLayoutApp() {
        super("電卓");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 表示欄: 入力はボタンで行うので、キーボードからは編集させない
        JTextField display = new JTextField("0");
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font(Font.MONOSPACED, Font.BOLD, 28));

        // 4行4列の格子。どのボタンも同じ大きさになる。引数の4, 4はボタンの間隔
        JPanel keyPanel = new JPanel(new GridLayout(4, 4, 4, 4));
        Font keyFont = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
        for (String key : KEYS) {
            JButton button = new JButton(key);
            button.setFont(keyFont);
            keyPanel.add(button);
        }

        // SOUTHに置いた部品は左右いっぱいに広がる
        JButton clearButton = new JButton("クリア");
        clearButton.setFont(keyFont);

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(display, BorderLayout.NORTH);
        content.add(keyPanel, BorderLayout.CENTER);
        content.add(clearButton, BorderLayout.SOUTH);
        setContentPane(content);

        setSize(300, 380);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorLayoutApp().setVisible(true));
    }
}
