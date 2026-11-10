import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ItemListenerExample extends JFrame {
    private JCheckBox checkBox1, checkBox2, checkBox3;
    private JRadioButton radio1, radio2, radio3;
    private JLabel statusLabel;
    
    public ItemListenerExample() {
        setTitle("ItemListener の例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // チェックボックスパネル
        JPanel checkPanel = new JPanel();
        checkPanel.setBorder(BorderFactory.createTitledBorder("オプション選択"));
        checkBox1 = new JCheckBox("オプション1");
        checkBox2 = new JCheckBox("オプション2");
        checkBox3 = new JCheckBox("オプション3");
        
        ItemListener checkListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                String status = e.getStateChange() == ItemEvent.SELECTED ? "選択" : "解除";
                statusLabel.setText(source.getText() + " が" + status + "されました");
                updateSelection();
            }
        };
        
        checkBox1.addItemListener(checkListener);
        checkBox2.addItemListener(checkListener);
        checkBox3.addItemListener(checkListener);
        
        checkPanel.add(checkBox1);
        checkPanel.add(checkBox2);
        checkPanel.add(checkBox3);
        
        // ラジオボタンパネル
        JPanel radioPanel = new JPanel();
        radioPanel.setBorder(BorderFactory.createTitledBorder("サイズ選択"));
        radio1 = new JRadioButton("小", true);
        radio2 = new JRadioButton("中");
        radio3 = new JRadioButton("大");
        
        ButtonGroup group = new ButtonGroup();
        group.add(radio1);
        group.add(radio2);
        group.add(radio3);
        
        ItemListener radioListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                JRadioButton source = (JRadioButton) e.getSource();
                statusLabel.setText("サイズ: " + source.getText() + " が選択されました");
            }
        };
        
        radio1.addItemListener(radioListener);
        radio2.addItemListener(radioListener);
        radio3.addItemListener(radioListener);
        
        radioPanel.add(radio1);
        radioPanel.add(radio2);
        radioPanel.add(radio3);
        
        // ステータスラベル
        statusLabel = new JLabel("選択してください");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // レイアウト
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        mainPanel.add(checkPanel);
        mainPanel.add(radioPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void updateSelection() {
        StringBuilder selected = new StringBuilder("選択中: ");
        if (checkBox1.isSelected()) selected.append("オプション1 ");
        if (checkBox2.isSelected()) selected.append("オプション2 ");
        if (checkBox3.isSelected()) selected.append("オプション3 ");
        IO.println(selected.toString());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ItemListenerExample().setVisible(true);
        });
    }
}
