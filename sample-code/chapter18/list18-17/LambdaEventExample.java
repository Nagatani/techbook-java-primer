/**
 * リスト18-17
 * LambdaEventExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (853行目)
 */

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class LambdaEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ラムダ式イベント処理");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 1));

        JTextField inputField = new JTextField();
        JLabel resultLabel = new JLabel("結果が表示されます", JLabel.CENTER);
        JPanel buttonPanel = new JPanel();

        // 各ボタンにラムダ式でイベント処理を設定
        JButton upperButton = new JButton("大文字に変換");
        upperButton.addActionListener(e -> {
            String text = inputField.getText();
            resultLabel.setText(text.toUpperCase());
        });

        JButton lowerButton = new JButton("小文字に変換");
        lowerButton.addActionListener(e -> {
            String text = inputField.getText();
            resultLabel.setText(text.toLowerCase());
        });

        JButton clearButton = new JButton("クリア");
        clearButton.addActionListener(e -> {
            inputField.setText("");
            resultLabel.setText("結果が表示されます");
        });

        buttonPanel.add(upperButton);
        buttonPanel.add(lowerButton);
        buttonPanel.add(clearButton);

        frame.add(inputField);
        frame.add(resultLabel);
        frame.add(buttonPanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}