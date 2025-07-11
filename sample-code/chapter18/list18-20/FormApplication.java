/**
 * リスト18-20
 * FormApplicationクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (1042行目)
 */

import java.awt.*;
import javax.swing.*;

public class FormApplication {
    public static void main(String[] args) {
        JFrame frame = new JFrame("フォーム入力アプリケーション");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 入力パネル
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("個人情報入力"));

        // 名前入力
        inputPanel.add(new JLabel("名前:"));
        JTextField nameField = new JTextField();
        inputPanel.add(nameField);

        // 年齢入力
        inputPanel.add(new JLabel("年齢:"));
        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(20, 0, 120, 1));
        inputPanel.add(ageSpinner);

        // 性別選択
        inputPanel.add(new JLabel("性別:"));
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup genderGroup = new ButtonGroup();
        JRadioButton maleRadio = new JRadioButton("男性", true);
        JRadioButton femaleRadio = new JRadioButton("女性");
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        inputPanel.add(genderPanel);

        // 趣味選択
        inputPanel.add(new JLabel("趣味:"));
        String[] hobbies = {"読書", "映画鑑賞", "スポーツ", "音楽", "料理"};
        JComboBox<String> hobbyCombo = new JComboBox<>(hobbies);
        inputPanel.add(hobbyCombo);

        // メルマガ購読
        JCheckBox newsletterCheck = new JCheckBox("メールマガジンを購読する");
        inputPanel.add(new JLabel()); // 空のラベル
        inputPanel.add(newsletterCheck);

        frame.add(inputPanel, BorderLayout.CENTER);

        // 結果表示エリア
        JTextArea resultArea = new JTextArea(8, 30);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createTitledBorder("入力結果"));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        frame.add(scrollPane, BorderLayout.SOUTH);

        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton submitButton = new JButton("送信");
        JButton clearButton = new JButton("クリア");

        submitButton.addActionListener(e -> {
            StringBuilder result = new StringBuilder();
            result.append("=== 入力内容 ===\n");
            result.append("名前: ").append(nameField.getText()).append("\n");
            result.append("年齢: ").append(ageSpinner.getValue()).append("歳\n");
            result.append("性別: ").append(maleRadio.isSelected() ? "男性" : "女性").append("\n");
            result.append("趣味: ").append(hobbyCombo.getSelectedItem()).append("\n");
            result.append("メルマガ: ").append(newsletterCheck.isSelected() ? "購読する" : "購読しない").append("\n");
            result.append("==================\n");
            
            resultArea.append(result.toString());
        });

        clearButton.addActionListener(e -> {
            nameField.setText("");
            ageSpinner.setValue(20);
            maleRadio.setSelected(true);
            hobbyCombo.setSelectedIndex(0);
            newsletterCheck.setSelected(false);
            resultArea.setText("");
        });

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);

        frame.add(buttonPanel, BorderLayout.NORTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}