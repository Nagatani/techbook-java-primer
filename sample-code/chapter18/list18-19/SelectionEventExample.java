/**
 * リスト18-19
 * SelectionEventExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (981行目)
 */

import java.awt.GridLayout;
import javax.swing.*;

public class SelectionEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("選択イベントの例");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1));

        // チェックボックス
        JCheckBox agreeCheckBox = new JCheckBox("利用規約に同意する");
        JLabel agreeLabel = new JLabel("同意状態: 未同意");
        
        agreeCheckBox.addActionListener(e -> {
            if (agreeCheckBox.isSelected()) {
                agreeLabel.setText("同意状態: 同意済み");
            } else {
                agreeLabel.setText("同意状態: 未同意");
            }
        });

        // ラジオボタン
        JPanel radioPanel = new JPanel(new FlowLayout());
        ButtonGroup group = new ButtonGroup();
        JRadioButton beginnerRadio = new JRadioButton("初心者", true);
        JRadioButton intermediateRadio = new JRadioButton("中級者");
        JRadioButton advancedRadio = new JRadioButton("上級者");
        
        group.add(beginnerRadio);
        group.add(intermediateRadio);
        group.add(advancedRadio);
        
        JLabel levelLabel = new JLabel("レベル: 初心者");
        
        // 各ラジオボタンにリスナーを設定
        beginnerRadio.addActionListener(e -> levelLabel.setText("レベル: 初心者"));
        intermediateRadio.addActionListener(e -> levelLabel.setText("レベル: 中級者"));
        advancedRadio.addActionListener(e -> levelLabel.setText("レベル: 上級者"));
        
        radioPanel.add(beginnerRadio);
        radioPanel.add(intermediateRadio);
        radioPanel.add(advancedRadio);

        frame.add(agreeCheckBox);
        frame.add(agreeLabel);
        frame.add(radioPanel);
        frame.add(levelLabel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}