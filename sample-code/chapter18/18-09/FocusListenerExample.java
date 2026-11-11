import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FocusListenerExample extends JFrame {
    private JTextField field1, field2, field3;
    private JLabel statusLabel;
    
    public FocusListenerExample() {
        setTitle("FocusListener の例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // テキストフィールドの作成
        field1 = new JTextField(15);
        field2 = new JTextField(15);
        field3 = new JTextField(15);
        
        // FocusListenerの作成
        FocusListener focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField source = (JTextField) e.getSource();
                source.setBackground(Color.YELLOW);
                statusLabel.setText("フォーカスを取得: " + getFieldName(source));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                JTextField source = (JTextField) e.getSource();
                source.setBackground(Color.WHITE);
                
                // バリデーションの例
                if (source == field1 && source.getText().isEmpty()) {
                    statusLabel.setText("名前を入力してください");
                    source.setBackground(Color.PINK);
                }
            }
        };
        
        field1.addFocusListener(focusListener);
        field2.addFocusListener(focusListener);
        field3.addFocusListener(focusListener);
        
        // レイアウト
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("名前:"), gbc);
        gbc.gridx = 1;
        add(field1, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("メール:"), gbc);
        gbc.gridx = 1;
        add(field2, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("電話番号:"), gbc);
        gbc.gridx = 1;
        add(field3, gbc);
        
        // ステータスラベル
        statusLabel = new JLabel("フィールドをクリックしてください");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(statusLabel, gbc);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private String getFieldName(JTextField field) {
        if (field == field1) return "名前フィールド";
        if (field == field2) return "メールフィールド";
        if (field == field3) return "電話番号フィールド";
        return "不明なフィールド";
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FocusListenerExample().setVisible(true);
        });
    }
}
