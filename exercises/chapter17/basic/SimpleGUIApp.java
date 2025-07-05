/**
 * 第17章 基本課題1: 簡単なGUIアプリケーション
 * 
 * Swingの基本コンポーネントを使用して、シンプルなGUIアプリケーションを作成してください。
 * 
 * 要件:
 * 1. JFrameを使用してウィンドウを作成する
 * 2. JLabel、JTextField、JButtonを配置する
 * 3. ボタンクリック時にテキストフィールドの内容をラベルに表示する
 * 4. 適切なレイアウトマネージャーを使用する
 * 
 * 学習ポイント:
 * - Swingコンポーネントの基本的な使用方法
 * - レイアウトマネージャーの理解
 * - イベントハンドリングの基礎
 * - GUI アプリケーションの基本構造
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleGUIApp extends JFrame {
    
    // TODO: 必要なコンポーネントのフィールドを宣言してください
    // ヒント: JLabel、JTextField、JButton が必要です
    
    public SimpleGUIApp() {
        // TODO: フレームの基本設定を行ってください
        // - タイトルの設定
        // - サイズの設定
        // - 閉じる操作の設定
        // - 中央配置
        
        // TODO: コンポーネントの初期化を行ってください
        
        // TODO: レイアウトの設定を行ってください
        // ヒント: BorderLayoutまたはGridLayoutを使用することを推奨
        
        // TODO: イベントリスナーの設定を行ってください
        // ヒント: ActionListenerを実装またはラムダ式を使用
    }
    
    // TODO: 必要に応じてヘルパーメソッドを作成してください
    
    public static void main(String[] args) {
        // TODO: イベントディスパッチスレッドでGUIを作成してください
        // ヒント: SwingUtilities.invokeLater() を使用
    }
}

/*
 * 実装のヒント:
 * 
 * 1. コンポーネントの作成
 *    JLabel label = new JLabel("初期テキスト");
 *    JTextField textField = new JTextField(20);
 *    JButton button = new JButton("実行");
 * 
 * 2. レイアウトの例（BorderLayout）
 *    add(label, BorderLayout.NORTH);
 *    add(textField, BorderLayout.CENTER);
 *    add(button, BorderLayout.SOUTH);
 * 
 * 3. イベントリスナーの例
 *    button.addActionListener(e -> {
 *        String text = textField.getText();
 *        label.setText(text);
 *    });
 * 
 * 4. フレーム設定の例
 *    setTitle("簡単なGUIアプリケーション");
 *    setSize(300, 150);
 *    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 *    setLocationRelativeTo(null);
 * 
 * 発展課題:
 * - 入力検証の追加
 * - 複数のボタンの追加
 * - メニューバーの追加
 * - アイコンの設定
 */