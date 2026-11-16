// CounterFrame.java - ラムダ式の中で変える値は、フィールドに持つ
import javax.swing.*;

public class CounterFrame extends JFrame {
    private int count = 0; // フィールドなら、ラムダ式の中で変更できる
    private final JButton button = new JButton("+1");

    public CounterFrame() {
        super("カウンター");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        button.addActionListener(e -> {
            count++;
            button.setText("回数: " + count);
        });
        add(button);
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CounterFrame().setVisible(true));
    }
}
