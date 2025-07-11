/**
 * リスト1-18
 * GreetingAppクラス
 * 
 * 元ファイル: chapter01-introduction.md (780行目)
 */

import javax.swing.*;
import java.awt.*;

public class GreetingApp extends JFrame {

    private JLabel messageLabel;
    private JTextField nameTextField;
    private JButton greetButton;

    public GreetingApp() {
        // ウィンドウの基本設定
        setTitle("名前入力");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        // レイアウトマネージャーを GridLayout に設定（4行1列）
        setLayout(new GridLayout(4, 1));

        // ラベル（説明）の作成
        JLabel nameLabel = new JLabel("名前を入力してください:");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER); // テキストを中央揃え
        add(nameLabel);

        // テキストボックスの作成
        nameTextField = new JTextField(15);
        add(nameTextField);

        // メッセージ表示用ラベルの作成
        messageLabel = new JLabel("");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel);

        // ボタンの作成とActionListenerの設定
        greetButton = new JButton("挨拶する");
        greetButton.addActionListener(e -> {
            String name = nameTextField.getText();
            if (!name.isEmpty()) {
                messageLabel.setText("こんにちは！" + name + "さん");
            } else {
                messageLabel.setText("名前を入力してください");
            }
        });
        add(greetButton);

        // ウィンドウを表示
        setVisible(true);
    }

    public static void main(String[] args) {
        // イベントディスパッチスレッドでGUIを作成・実行
        SwingUtilities.invokeLater(() -> new GreetingApp());
    }
}