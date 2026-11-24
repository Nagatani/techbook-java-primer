// CounterFrame.java - ラムダ式の中で変える値は、フィールドに持つ
import javax.swing.*;

public class CounterFrame {
    private final JFrame frame = new JFrame("カウンター");
    private int count = 0; // フィールドなら、ラムダ式の中で変更できる
    private final JButton button = new JButton("+1");

    public CounterFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        button.addActionListener(e -> {
            count++;
            button.setText("回数: " + count);
        });
        frame.add(button);
        frame.pack();
    }

    private void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CounterFrame().show());
    }
}
