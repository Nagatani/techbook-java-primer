/**
 * リスト1-17
 * HelloGUIAppクラス
 * 
 * 元ファイル: chapter01-introduction.md (734行目)
 */

import javax.swing.*;
import java.awt.*;

public class HelloGUIApp extends JFrame {

    private JLabel label;
    private JButton button;

    public HelloGUIApp() {
        // JFrameの初期設定
        setTitle("Hello GUI App!!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ウィンドウのサイズを設定
        setSize(300, 150);
        setLocationRelativeTo(null); // 画面中央に表示

        // レイアウトマネージャの設定
        setLayout(new GridLayout(2, 1));

        // ラベルの作成と初期テキストの設定
        label = new JLabel("こんにちは");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);

        // ボタンの作成とActionListenerの設定
        button = new JButton("押して");
        button.addActionListener(e -> {
            label.setText("Hello, OOP!!");
        });
        add(button);

        // ウィンドウを表示
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HelloGUIApp());
    }
}