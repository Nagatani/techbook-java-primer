import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * 長さの単位換算。入力や単位を変えるたびに結果を計算し直す。
 */
public class UnitConverterApp {
    private final JTextField inputField = new JTextField("1", 10);
    private final JComboBox<LengthUnit> fromBox = new JComboBox<>(LengthUnit.values());
    private final JComboBox<LengthUnit> toBox = new JComboBox<>(LengthUnit.values());
    private final JCheckBox roundCheck = new JCheckBox("小数点以下2桁に丸める");
    private final JLabel resultLabel = new JLabel(" ", JLabel.CENTER);
    private final JLabel messageLabel = new JLabel(" ", JLabel.CENTER);

    public UnitConverterApp() {
        fromBox.setSelectedItem(LengthUnit.INCH);
        toBox.setSelectedItem(LengthUnit.CENTIMETER);

        // Enterキー、単位の選択、チェックボックスのどれでも計算し直す
        inputField.addActionListener(e -> convert());
        fromBox.addActionListener(e -> convert());
        toBox.addActionListener(e -> convert());
        // チェックを入れたときも外したときも呼ばれる
        roundCheck.addItemListener(e -> convert());

        JButton convertButton = new JButton("換算");
        convertButton.addActionListener(e -> convert());

        // 単位を入れ替える。setSelectedItemでもActionListenerが呼ばれる
        JButton swapButton = new JButton("単位を入れ替え");
        swapButton.addActionListener(e -> {
            LengthUnit from = (LengthUnit) fromBox.getSelectedItem();
            fromBox.setSelectedItem(toBox.getSelectedItem());
            toBox.setSelectedItem(from);
        });

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("値"));
        form.add(inputField);
        form.add(new JLabel("換算前の単位"));
        form.add(fromBox);
        form.add(new JLabel("換算後の単位"));
        form.add(toBox);
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel options = new JPanel(new FlowLayout());
        options.add(convertButton);
        options.add(swapButton);
        options.add(roundCheck);

        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        messageLabel.setForeground(Color.RED);

        JPanel bottom = new JPanel(new GridLayout(3, 1));
        bottom.add(options);
        bottom.add(resultLabel);
        bottom.add(messageLabel);

        JFrame frame = new JFrame("長さの単位換算");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(form, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);

        convert();
        frame.setVisible(true);
    }

    /** 入力を読んで換算し、結果かエラーを表示する */
    private void convert() {
        LengthUnit from = (LengthUnit) fromBox.getSelectedItem();
        LengthUnit to = (LengthUnit) toBox.getSelectedItem();
        try {
            double value = LengthConverter.parse(inputField.getText());
            double result = LengthConverter.convert(value, from, to);
            int digits = roundCheck.isSelected() ? 2 : 6;
            resultLabel.setText("%s %s = %s %s".formatted(
                    LengthConverter.format(value, 6), from.getSymbol(),
                    LengthConverter.format(result, digits), to.getSymbol()));
            messageLabel.setText(" ");
            inputField.setBackground(Color.WHITE);
        } catch (IllegalArgumentException ex) {
            resultLabel.setText(" ");
            messageLabel.setText(ex.getMessage());
            inputField.setBackground(new Color(255, 220, 220));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UnitConverterApp());
    }
}
