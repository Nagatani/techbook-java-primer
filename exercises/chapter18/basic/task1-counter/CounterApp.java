import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * 教室の入室人数を数えるカウンター（基礎課題1の解答例）
 */
public class CounterApp extends JFrame {
    private final Counter counter = new Counter(10);
    private final JLabel valueLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JButton plusButton = new JButton("+1");
    private final JButton minusButton = new JButton("-1");
    private final JButton resetButton = new JButton("リセット");

    public CounterApp() {
        super("入室カウンター");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 48f));

        // 3つのボタンで1つのリスナーを共有し、アクションコマンドで区別する
        plusButton.setActionCommand("plus");
        minusButton.setActionCommand("minus");
        resetButton.setActionCommand("reset");
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (e.getActionCommand()) {
                    case "plus" -> counter.increment();
                    case "minus" -> counter.decrement();
                    case "reset" -> counter.reset();
                    default -> { }
                }
                updateView();
            }
        };
        plusButton.addActionListener(listener);
        minusButton.addActionListener(listener);
        resetButton.addActionListener(listener);

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(minusButton);
        buttons.add(plusButton);
        buttons.add(resetButton);

        add(valueLabel, BorderLayout.NORTH);
        add(messageLabel, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        updateView();
        setSize(320, 200);
        setLocationRelativeTo(null);
    }

    // ロジックの状態を画面に映す
    private void updateView() {
        valueLabel.setText(counter.getValue() + "人");
        plusButton.setEnabled(counter.canIncrement());
        minusButton.setEnabled(counter.canDecrement());
        if (!counter.canIncrement()) {
            messageLabel.setText("定員（" + counter.getMax() + "人）に達しました");
        } else {
            messageLabel.setText(" ");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CounterApp().setVisible(true));
    }
}
