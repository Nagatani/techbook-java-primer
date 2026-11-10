import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.*;

public class DocumentListenerExample extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JTextArea statusArea;
    private JButton submitButton;
    
    public DocumentListenerExample() {
        setTitle("DocumentListenerによるバリデーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Email フィールド
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);
        
        emailField = new JTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);
        
        // パスワードフィールド
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("パスワード:"), gbc);
        
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);
        
        // 確認フィールド
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("確認:"), gbc);
        
        confirmField = new JPasswordField(20);
        gbc.gridx = 1;
        add(confirmField, gbc);
        
        // ステータスエリア
        statusArea = new JTextArea(5, 30);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(statusArea);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(scrollPane, gbc);
        
        // 送信ボタン
        submitButton = new JButton("送信");
        submitButton.setEnabled(false);
        gbc.gridy = 4;
        add(submitButton, gbc);
        
        // DocumentListenerの追加
        setupDocumentListeners();
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupDocumentListeners() {
        // Emailフィールドのリスナー
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateForm(); }
        });
        
        // パスワードフィールドのリスナー
        DocumentListener passwordListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateForm(); }
        };
        
        passwordField.getDocument().addDocumentListener(passwordListener);
        confirmField.getDocument().addDocumentListener(passwordListener);
    }
    
    private void validateForm() {
        StringBuilder status = new StringBuilder();
        boolean isValid = true;
        
        // Emailの検証
        String email = emailField.getText();
        if (email.isEmpty()) {
            status.append("× Emailを入力してください\n");
            emailField.setBackground(Color.PINK);
            isValid = false;
        } else if (!isValidEmail(email)) {
            status.append("× 有効なEmailアドレスを入力してください\n");
            emailField.setBackground(Color.PINK);
            isValid = false;
        } else {
            status.append("✓ Email: OK\n");
            emailField.setBackground(Color.WHITE);
        }
        
        // パスワードの検証
        char[] password = passwordField.getPassword();
        if (password.length == 0) {
            status.append("× パスワードを入力してください\n");
            passwordField.setBackground(Color.PINK);
            isValid = false;
        } else if (password.length < 8) {
            status.append("× パスワードは8文字以上必要です\n");
            passwordField.setBackground(Color.PINK);
            isValid = false;
        } else {
            status.append("✓ パスワード: OK (");
            status.append(getPasswordStrength(password));
            status.append(")\n");
            passwordField.setBackground(Color.WHITE);
        }
        
        // 確認パスワードの検証
        char[] confirm = confirmField.getPassword();
        if (confirm.length == 0) {
            status.append("× 確認パスワードを入力してください\n");
            confirmField.setBackground(Color.PINK);
            isValid = false;
        } else if (!java.util.Arrays.equals(password, confirm)) {
            status.append("× パスワードが一致しません\n");
            confirmField.setBackground(Color.PINK);
            isValid = false;
        } else {
            status.append("✓ 確認パスワード: 一致\n");
            confirmField.setBackground(Color.WHITE);
        }
        
        // クリーンアップ
        java.util.Arrays.fill(password, ' ');
        java.util.Arrays.fill(confirm, ' ');
        
        statusArea.setText(status.toString());
        submitButton.setEnabled(isValid);
    }
    
    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(email).matches();
    }
    
    private String getPasswordStrength(char[] password) {
        int score = 0;
        if (password.length >= 12) score++;
        if (String.valueOf(password).matches(".*[A-Z].*")) score++;
        if (String.valueOf(password).matches(".*[a-z].*")) score++;
        if (String.valueOf(password).matches(".*[0-9].*")) score++;
        if (String.valueOf(password).matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;
        
        return switch (score) {
            case 0, 1 -> "弱い";
            case 2, 3 -> "普通";
            case 4, 5 -> "強い";
            default -> "不明";
        };
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DocumentListenerExample().setVisible(true);
        });
    }
}
