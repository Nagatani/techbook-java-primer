/**
 * 第18章 GUI イベント処理応用
 * 演習課題: FormValidator.java
 * 
 * 【課題概要】
 * リアルタイムバリデーション機能付きのフォームアプリケーションを作成してください。
 * 
 * 【要求仕様】
 * 1. 複数の入力フィールド（名前、メール、電話番号、パスワード、確認パスワード）
 * 2. リアルタイムバリデーション（文字入力時に即座にチェック）
 * 3. バリデーション結果の視覚的フィードバック（色変更、アイコン表示）
 * 4. エラーメッセージの表示とクリア
 * 5. 全項目の総合的な入力チェック
 * 6. 送信ボタンの有効/無効制御
 * 7. 入力支援機能（フォーマット自動調整、候補表示）
 * 8. フォーカス移動とタブ順序の制御
 * 9. キーボードショートカット（Enter で次フィールド移動等）
 * 10. 入力履歴の保存と復元機能
 * 
 * 【学習ポイント】
 * - DocumentListener によるリアルタイム入力監視
 * - FocusListener による入力フィールドの状態管理
 * - 正規表現を使った入力値の検証
 * - JTextField の外観カスタマイズ
 * - KeyListener によるキーボード入力の制御
 * - Timer クラスによる遅延処理
 * - MVC パターンによる入力データの管理
 * 
 * 【実装のヒント】
 * 1. DocumentListener で文字入力を監視し、即座にバリデーション実行
 * 2. 正規表現パターンで各フィールドの形式をチェック
 * 3. setBorder() や setBackground() で視覚的フィードバックを実装
 * 4. javax.swing.Timer で入力完了後の遅延バリデーション
 * 5. ActionMap と InputMap でキーバインディング設定
 * 
 * 【よくある間違いと対策】
 * - 入力中の過度なバリデーション実行
 *   → Timer を使って適切な遅延を設ける
 * - バリデーション結果の表示タイミング
 *   → FocusListener で適切なタイミングでメッセージ表示
 * - 正規表現パターンの設計ミス
 *   → 段階的なバリデーション（空文字→形式→内容）
 * - UI の状態同期不良
 *   → Observer パターンでの統一的な状態管理
 * 
 * 【段階的な実装指針】
 * 1. 基本的なフォームレイアウトの作成
 * 2. 各フィールドの基本バリデーション機能実装
 * 3. リアルタイムバリデーションの追加
 * 4. 視覚的フィードバック機能の実装
 * 5. 送信ボタンの制御機能追加
 * 6. キーボードナビゲーション機能の実装
 * 7. 入力支援機能の追加
 * 8. 履歴機能と全体的な改善
 */

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.prefs.Preferences;

public class FormValidator extends JFrame {
    // バリデーションルール
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\d{3}-\\d{4}-\\d{4}|\\d{10,11})$"
    );
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}a-zA-Z\\s]{1,50}$"
    );
    
    // UI コンポーネント
    private ValidatedTextField nameField;
    private ValidatedTextField emailField;
    private ValidatedTextField phoneField;
    private ValidatedPasswordField passwordField;
    private ValidatedPasswordField confirmPasswordField;
    private JButton submitButton;
    private JButton resetButton;
    private JPanel statusPanel;
    private JLabel overallStatusLabel;
    
    // バリデーション管理
    private Map<String, Boolean> validationStatus;
    private Timer validationTimer;
    private Preferences preferences;
    
    public FormValidator() {
        validationStatus = new HashMap<>();
        preferences = Preferences.userNodeForPackage(FormValidator.class);
        
        initializeComponents();
        setupLayout();
        setupValidation();
        setupEventHandlers();
        setupKeyBindings();
        setupWindow();
        
        loadFormData();
    }
    
    private void initializeComponents() {
        // フィールドの初期化
        nameField = new ValidatedTextField("名前", 20);
        emailField = new ValidatedTextField("メールアドレス", 30);
        phoneField = new ValidatedTextField("電話番号", 15);
        passwordField = new ValidatedPasswordField("パスワード", 20);
        confirmPasswordField = new ValidatedPasswordField("パスワード確認", 20);
        
        // ボタンの初期化
        submitButton = new JButton("送信");
        submitButton.setEnabled(false);
        submitButton.setFont(submitButton.getFont().deriveFont(Font.BOLD));
        
        resetButton = new JButton("リセット");
        
        // ステータスパネル
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("バリデーション状況"));
        
        overallStatusLabel = new JLabel("入力を開始してください");
        overallStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // バリデーションタイマー（入力後の遅延処理用）
        validationTimer = new Timer(300, e -> validateAllFields());
        validationTimer.setRepeats(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // メインフォームパネル
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 名前フィールド
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("名前:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(nameField.getStatusLabel(), gbc);
        
        // メールフィールド
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("メール:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(emailField.getStatusLabel(), gbc);
        
        // 電話番号フィールド
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("電話番号:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(phoneField, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(phoneField.getStatusLabel(), gbc);
        
        // パスワードフィールド
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("パスワード:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(passwordField.getStatusLabel(), gbc);
        
        // パスワード確認フィールド
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("パスワード確認:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(confirmPasswordField, gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(confirmPasswordField.getStatusLabel(), gbc);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(resetButton);
        
        // ステータスパネルの設定
        statusPanel.add(overallStatusLabel, BorderLayout.CENTER);
        
        // 全体レイアウト
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.NORTH);
    }
    
    private void setupValidation() {
        // 各フィールドのバリデーション設定
        nameField.setValidator(text -> {
            if (text.trim().isEmpty()) {
                return new ValidationResult(false, "名前を入力してください");
            }
            if (!NAME_PATTERN.matcher(text).matches()) {
                return new ValidationResult(false, "有効な名前を入力してください");
            }
            return new ValidationResult(true, "");
        });
        
        emailField.setValidator(text -> {
            if (text.trim().isEmpty()) {
                return new ValidationResult(false, "メールアドレスを入力してください");
            }
            if (!EMAIL_PATTERN.matcher(text).matches()) {
                return new ValidationResult(false, "有効なメールアドレスを入力してください");
            }
            return new ValidationResult(true, "");
        });
        
        phoneField.setValidator(text -> {
            if (text.trim().isEmpty()) {
                return new ValidationResult(false, "電話番号を入力してください");
            }
            String formatted = text.replaceAll("[^0-9]", "");
            if (!PHONE_PATTERN.matcher(text).matches() && !PHONE_PATTERN.matcher(formatted).matches()) {
                return new ValidationResult(false, "有効な電話番号を入力してください（例: 090-1234-5678）");
            }
            return new ValidationResult(true, "");
        });
        
        passwordField.setValidator(text -> {
            if (text.isEmpty()) {
                return new ValidationResult(false, "パスワードを入力してください");
            }
            if (text.length() < 8) {
                return new ValidationResult(false, "パスワードは8文字以上で入力してください");
            }
            if (!text.matches(".*[A-Z].*")) {
                return new ValidationResult(false, "大文字を含めてください");
            }
            if (!text.matches(".*[a-z].*")) {
                return new ValidationResult(false, "小文字を含めてください");
            }
            if (!text.matches(".*[0-9].*")) {
                return new ValidationResult(false, "数字を含めてください");
            }
            return new ValidationResult(true, "");
        });
        
        confirmPasswordField.setValidator(text -> {
            if (text.isEmpty()) {
                return new ValidationResult(false, "パスワード確認を入力してください");
            }
            if (!text.equals(passwordField.getText())) {
                return new ValidationResult(false, "パスワードが一致しません");
            }
            return new ValidationResult(true, "");
        });
    }
    
    private void setupEventHandlers() {
        // リアルタイムバリデーション
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scheduleValidation();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                scheduleValidation();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                scheduleValidation();
            }
        };
        
        nameField.getDocument().addDocumentListener(documentListener);
        emailField.getDocument().addDocumentListener(documentListener);
        phoneField.getDocument().addDocumentListener(documentListener);
        passwordField.getDocument().addDocumentListener(documentListener);
        confirmPasswordField.getDocument().addDocumentListener(documentListener);
        
        // パスワードフィールドの特別な処理
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // パスワードが変更されたら確認フィールドも再検証
                SwingUtilities.invokeLater(() -> confirmPasswordField.validateField());
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> confirmPasswordField.validateField());
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> confirmPasswordField.validateField());
            }
        });
        
        // 送信ボタン
        submitButton.addActionListener(e -> submitForm());
        
        // リセットボタン
        resetButton.addActionListener(e -> resetForm());
        
        // フォーカスイベント（入力支援）
        phoneField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // 電話番号の自動フォーマット
                String text = phoneField.getText().replaceAll("[^0-9]", "");
                if (text.length() == 11) {
                    phoneField.setText(text.substring(0, 3) + "-" + 
                                     text.substring(3, 7) + "-" + 
                                     text.substring(7));
                }
            }
        });
    }
    
    private void setupKeyBindings() {
        // Enter キーで次のフィールドに移動
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        
        nameField.getInputMap().put(enterKey, "nextField");
        nameField.getActionMap().put("nextField", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emailField.requestFocus();
            }
        });
        
        emailField.getInputMap().put(enterKey, "nextField");
        emailField.getActionMap().put("nextField", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                phoneField.requestFocus();
            }
        });
        
        phoneField.getInputMap().put(enterKey, "nextField");
        phoneField.getActionMap().put("nextField", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordField.requestFocus();
            }
        });
        
        passwordField.getInputMap().put(enterKey, "nextField");
        passwordField.getActionMap().put("nextField", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmPasswordField.requestFocus();
            }
        });
        
        confirmPasswordField.getInputMap().put(enterKey, "submit");
        confirmPasswordField.getActionMap().put("submit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (submitButton.isEnabled()) {
                    submitForm();
                }
            }
        });
        
        // Ctrl+R でリセット
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "reset");
        getRootPane().getActionMap().put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });
    }
    
    private void setupWindow() {
        setTitle("Form Validator - リアルタイムバリデーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void scheduleValidation() {
        validationTimer.restart();
    }
    
    private void validateAllFields() {
        boolean allValid = true;
        
        allValid &= nameField.validateField();
        allValid &= emailField.validateField();
        allValid &= phoneField.validateField();
        allValid &= passwordField.validateField();
        allValid &= confirmPasswordField.validateField();
        
        submitButton.setEnabled(allValid);
        
        if (allValid) {
            overallStatusLabel.setText("✓ すべての入力が正しく完了しています");
            overallStatusLabel.setForeground(new Color(0, 128, 0));
        } else {
            overallStatusLabel.setText("入力内容を確認してください");
            overallStatusLabel.setForeground(Color.RED);
        }
    }
    
    private void submitForm() {
        if (validateAllFields()) {
            saveFormData();
            
            String message = String.format(
                "フォームが送信されました:\n\n" +
                "名前: %s\n" +
                "メール: %s\n" +
                "電話番号: %s\n" +
                "パスワード: %s",
                nameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                "*".repeat(passwordField.getPassword().length)
            );
            
            JOptionPane.showMessageDialog(this, message, "送信完了", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void resetForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        
        overallStatusLabel.setText("入力を開始してください");
        overallStatusLabel.setForeground(Color.BLACK);
        
        nameField.requestFocus();
    }
    
    private void saveFormData() {
        preferences.put("name", nameField.getText());
        preferences.put("email", emailField.getText());
        preferences.put("phone", phoneField.getText());
    }
    
    private void loadFormData() {
        nameField.setText(preferences.get("name", ""));
        emailField.setText(preferences.get("email", ""));
        phoneField.setText(preferences.get("phone", ""));
    }
    
    // バリデーション結果クラス
    private static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
    
    // バリデーション機能付きテキストフィールド
    private static class ValidatedTextField extends JTextField {
        private JLabel statusLabel;
        private Validator validator;
        private Border originalBorder;
        private Border errorBorder;
        private Border successBorder;
        
        public ValidatedTextField(String placeholder, int columns) {
            super(columns);
            initializeValidation();
            setToolTipText(placeholder);
        }
        
        private void initializeValidation() {
            statusLabel = new JLabel();
            statusLabel.setPreferredSize(new Dimension(20, 20));
            
            originalBorder = getBorder();
            errorBorder = BorderFactory.createLineBorder(Color.RED, 2);
            successBorder = BorderFactory.createLineBorder(new Color(0, 128, 0), 2);
        }
        
        public void setValidator(Validator validator) {
            this.validator = validator;
        }
        
        public boolean validateField() {
            if (validator == null) return true;
            
            ValidationResult result = validator.validate(getText());
            updateVisualFeedback(result);
            return result.isValid();
        }
        
        private void updateVisualFeedback(ValidationResult result) {
            if (getText().trim().isEmpty()) {
                setBorder(originalBorder);
                statusLabel.setIcon(null);
                statusLabel.setToolTipText(null);
            } else if (result.isValid()) {
                setBorder(successBorder);
                statusLabel.setIcon(createIcon("✓", new Color(0, 128, 0)));
                statusLabel.setToolTipText("入力内容は正しいです");
            } else {
                setBorder(errorBorder);
                statusLabel.setIcon(createIcon("✗", Color.RED));
                statusLabel.setToolTipText(result.getMessage());
            }
        }
        
        private Icon createIcon(String text, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    g.setColor(color);
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    FontMetrics fm = g.getFontMetrics();
                    int textX = x + (getIconWidth() - fm.stringWidth(text)) / 2;
                    int textY = y + (getIconHeight() + fm.getAscent()) / 2;
                    g.drawString(text, textX, textY);
                }
                
                @Override
                public int getIconWidth() { return 16; }
                
                @Override
                public int getIconHeight() { return 16; }
            };
        }
        
        public JLabel getStatusLabel() {
            return statusLabel;
        }
    }
    
    // バリデーション機能付きパスワードフィールド
    private static class ValidatedPasswordField extends JPasswordField {
        private JLabel statusLabel;
        private Validator validator;
        private Border originalBorder;
        private Border errorBorder;
        private Border successBorder;
        
        public ValidatedPasswordField(String placeholder, int columns) {
            super(columns);
            initializeValidation();
            setToolTipText(placeholder);
        }
        
        private void initializeValidation() {
            statusLabel = new JLabel();
            statusLabel.setPreferredSize(new Dimension(20, 20));
            
            originalBorder = getBorder();
            errorBorder = BorderFactory.createLineBorder(Color.RED, 2);
            successBorder = BorderFactory.createLineBorder(new Color(0, 128, 0), 2);
        }
        
        public void setValidator(Validator validator) {
            this.validator = validator;
        }
        
        public boolean validateField() {
            if (validator == null) return true;
            
            ValidationResult result = validator.validate(new String(getPassword()));
            updateVisualFeedback(result);
            return result.isValid();
        }
        
        private void updateVisualFeedback(ValidationResult result) {
            if (getPassword().length == 0) {
                setBorder(originalBorder);
                statusLabel.setIcon(null);
                statusLabel.setToolTipText(null);
            } else if (result.isValid()) {
                setBorder(successBorder);
                statusLabel.setIcon(createIcon("✓", new Color(0, 128, 0)));
                statusLabel.setToolTipText("パスワードは有効です");
            } else {
                setBorder(errorBorder);
                statusLabel.setIcon(createIcon("✗", Color.RED));
                statusLabel.setToolTipText(result.getMessage());
            }
        }
        
        private Icon createIcon(String text, Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    g.setColor(color);
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    FontMetrics fm = g.getFontMetrics();
                    int textX = x + (getIconWidth() - fm.stringWidth(text)) / 2;
                    int textY = y + (getIconHeight() + fm.getAscent()) / 2;
                    g.drawString(text, textX, textY);
                }
                
                @Override
                public int getIconWidth() { return 16; }
                
                @Override
                public int getIconHeight() { return 16; }
            };
        }
        
        public JLabel getStatusLabel() {
            return statusLabel;
        }
        
        public String getText() {
            return new String(getPassword());
        }
    }
    
    // バリデーターインターフェース
    @FunctionalInterface
    private interface Validator {
        ValidationResult validate(String text);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new FormValidator().setVisible(true);
        });
    }
}