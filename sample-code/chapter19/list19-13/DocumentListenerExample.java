/**
 * リスト19-13
 * DocumentListenerExampleクラス
 * 
 * 元ファイル: chapter19-gui-event-handling.md (1038行目)
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.*;

public class DocumentListenerExample extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextArea commentArea;
    private JLabel emailStatus;
    private JLabel passwordStatus;
    private JLabel confirmStatus;
    private JLabel charCountLabel;
    private JButton submitButton;
    
    public DocumentListenerExample() {
        setTitle("リアルタイムバリデーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Email入力
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);
        
        gbc.gridx = 2;
        emailStatus = new JLabel("必須");
        emailStatus.setForeground(Color.GRAY);
        add(emailStatus, gbc);
        
        // パスワード入力
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("パスワード:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);
        
        gbc.gridx = 2;
        passwordStatus = new JLabel("8文字以上");
        passwordStatus.setForeground(Color.GRAY);
        add(passwordStatus, gbc);
        
        // パスワード確認
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("パスワード確認:"), gbc);
        
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        add(confirmPasswordField, gbc);
        
        gbc.gridx = 2;
        confirmStatus = new JLabel("一致確認");
        confirmStatus.setForeground(Color.GRAY);
        add(confirmStatus, gbc);
        
        // コメント入力
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("コメント:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        commentArea = new JTextArea(5, 30);
        commentArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        add(scrollPane, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 1;
        charCountLabel = new JLabel("0/200文字");
        add(charCountLabel, gbc);
        
        // 送信ボタン
        gbc.gridx = 1; gbc.gridy = 5;
        submitButton = new JButton("送信");
        submitButton.setEnabled(false);
        add(submitButton, gbc);
        
        // DocumentListenerの追加
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateEmail(); }
            public void removeUpdate(DocumentEvent e) { validateEmail(); }
            public void changedUpdate(DocumentEvent e) { validateEmail(); }
        });
        
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validatePassword(); }
            public void removeUpdate(DocumentEvent e) { validatePassword(); }
            public void changedUpdate(DocumentEvent e) { validatePassword(); }
        });
        
        confirmPasswordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validatePasswordConfirm(); }
            public void removeUpdate(DocumentEvent e) { validatePasswordConfirm(); }
            public void changedUpdate(DocumentEvent e) { validatePasswordConfirm(); }
        });
        
        // 文字数制限付きDocumentListenter
        commentArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCharCount(); }
            public void removeUpdate(DocumentEvent e) { updateCharCount(); }
            public void changedUpdate(DocumentEvent e) { updateCharCount(); }
            
            private void updateCharCount() {
                int length = commentArea.getText().length();
                charCountLabel.setText(length + "/200文字");
                if (length > 200) {
                    charCountLabel.setForeground(Color.RED);
                    // 文字数制限を超えた場合の処理
                    SwingUtilities.invokeLater(() -> {
                        try {
                            commentArea.setText(commentArea.getText(0, 200));
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    charCountLabel.setForeground(Color.BLACK);
                }
                checkFormValidity();
            }
        });
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void validateEmail() {
        String email = emailField.getText();
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Matcher matcher = pattern.matcher(email);
        
        if (email.isEmpty()) {
            emailStatus.setText("必須");
            emailStatus.setForeground(Color.GRAY);
            emailField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        } else if (matcher.matches()) {
            emailStatus.setText("✓ OK");
            emailStatus.setForeground(Color.GREEN);
            emailField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        } else {
            emailStatus.setText("✗ 無効");
            emailStatus.setForeground(Color.RED);
            emailField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        checkFormValidity();
    }
    
    private void validatePassword() {
        String password = new String(passwordField.getPassword());
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        
        if (password.length() >= 8 && hasUpperCase && hasLowerCase && hasDigit) {
            passwordStatus.setText("✓ 強い");
            passwordStatus.setForeground(Color.GREEN);
            passwordField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        } else if (password.length() >= 8) {
            passwordStatus.setText("△ 普通");
            passwordStatus.setForeground(Color.ORANGE);
            passwordField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        } else {
            passwordStatus.setText("✗ 弱い");
            passwordStatus.setForeground(Color.RED);
            passwordField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        validatePasswordConfirm();
        checkFormValidity();
    }
    
    private void validatePasswordConfirm() {
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        
        if (confirm.isEmpty()) {
            confirmStatus.setText("一致確認");
            confirmStatus.setForeground(Color.GRAY);
            confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        } else if (password.equals(confirm)) {
            confirmStatus.setText("✓ 一致");
            confirmStatus.setForeground(Color.GREEN);
            confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        } else {
            confirmStatus.setText("✗ 不一致");
            confirmStatus.setForeground(Color.RED);
            confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        checkFormValidity();
    }
    
    private void checkFormValidity() {
        boolean emailValid = emailStatus.getText().contains("✓");
        boolean passwordValid = passwordStatus.getText().contains("✓") || 
                              passwordStatus.getText().contains("△");
        boolean confirmValid = confirmStatus.getText().contains("✓");
        
        submitButton.setEnabled(emailValid && passwordValid && confirmValid);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DocumentListenerExample().setVisible(true);
        });
    }
}