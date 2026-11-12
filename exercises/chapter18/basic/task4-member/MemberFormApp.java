import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * 入力欄を離れたときに検査する会員登録フォーム（基礎課題4の解答例）
 */
public class MemberFormApp extends JFrame {
    private static final Color ERROR_BG = new Color(255, 225, 225);
    private static final Color FOCUS_BG = new Color(255, 255, 210);

    private final JTextField nameField = new JTextField(16);
    private final JTextField ageField = new JTextField(16);
    private final JTextField emailField = new JTextField(16);
    private final JLabel messageLabel = new JLabel(" ");
    private final JLabel helpLabel = new JLabel("ボタンにマウスを乗せると説明が出ます");
    private boolean edited = false;

    public MemberFormApp() {
        super("会員登録");
        // 閉じるボタンで、すぐには閉じない（確認してから閉じる）
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmClose();
            }
        });

        JPanel form = new JPanel(new GridLayout(3, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addField(form, "名前", nameField, MemberValidator::checkName);
        addField(form, "年齢", ageField, MemberValidator::checkAge);
        addField(form, "メール", emailField, MemberValidator::checkEmail);

        // テキストフィールドでEnterを押すと、次の欄へ移る
        nameField.addActionListener(e -> ageField.requestFocusInWindow());
        ageField.addActionListener(e -> emailField.requestFocusInWindow());

        JButton registerButton = new JButton("登録");
        registerButton.addActionListener(e -> register());
        // マウスが乗ったとき・離れたときに説明を切り替える
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                helpLabel.setText("3つの欄がすべて正しいと登録できます");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                helpLabel.setText("ボタンにマウスを乗せると説明が出ます");
            }
        });

        JPanel bottom = new JPanel(new GridLayout(0, 1));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        messageLabel.setForeground(Color.RED);
        bottom.add(messageLabel);
        bottom.add(registerButton);
        bottom.add(helpLabel);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    // 入力欄と、フォーカスを失ったときの検査を登録する
    private void addField(JPanel form, String label, JTextField field, Function<String, String> check) {
        form.add(new JLabel(label));
        form.add(field);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBackground(FOCUS_BG);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!field.getText().isEmpty()) {
                    edited = true;
                }
                String error = check.apply(field.getText());
                field.setBackground(error == null ? Color.WHITE : ERROR_BG);
                messageLabel.setText(error == null ? " " : label + ": " + error);
            }
        });
    }

    private void register() {
        String error = firstError();
        if (error != null) {
            messageLabel.setText(error);
            return;
        }
        JOptionPane.showMessageDialog(this, nameField.getText() + "さんを登録しました");
        nameField.setText("");
        ageField.setText("");
        emailField.setText("");
        messageLabel.setText(" ");
        edited = false;
    }

    private String firstError() {
        String[] labels = {"名前", "年齢", "メール"};
        String[] errors = {
            MemberValidator.checkName(nameField.getText()),
            MemberValidator.checkAge(ageField.getText()),
            MemberValidator.checkEmail(emailField.getText()),
        };
        for (int i = 0; i < errors.length; i++) {
            if (errors[i] != null) {
                return labels[i] + ": " + errors[i];
            }
        }
        return null;
    }

    private void confirmClose() {
        if (!edited) {
            dispose();
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "入力した内容は保存されていません。閉じますか？", "確認", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemberFormApp().setVisible(true));
    }
}
