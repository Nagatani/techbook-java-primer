/**
 * 第17章 課題1: 基本的な電卓アプリ
 * 
 * SwingとAWTを使った基本的な電卓アプリケーションを作成してください。
 * 
 * 要求仕様:
 * - 基本的な四則演算（+、-、*、/）
 * - クリア機能（C）
 * - 数字ボタン（0-9）
 * - 計算結果の表示
 * - エラーハンドリング（ゼロ除算など）
 * 
 * 学習ポイント:
 * - JFrameとJPanelの基本的な使い方
 * - GridLayoutによるボタン配置
 * - ActionListenerによるイベント処理
 * - 電卓の計算ロジック実装
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator extends JFrame implements ActionListener {
    
    // ここに実装してください
    
    // 必要なフィールドの例:
    // private JTextField display;
    // private JButton[] numberButtons;
    // private JButton[] operationButtons;
    // private double firstNumber, secondNumber, result;
    // private char operator;
    
    public Calculator() {
        // ここにコンストラクタを実装してください
        // フレームの設定、コンポーネントの配置、イベントリスナーの設定
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // ここにボタンクリックイベントの処理を実装してください
    }
    
    // 計算ロジックのメソッド
    private void calculate() {
        // ここに計算処理を実装してください
    }
    
    // 表示をクリアするメソッド
    private void clear() {
        // ここにクリア処理を実装してください
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Calculator().setVisible(true);
        });
    }
}

/*
 * 実装のヒント:
 * 
 * 1. レイアウト設計
 *    - BorderLayoutを使用してメイン構造を作成
 *    - 上部に表示用のJTextField
 *    - 中央にボタンパネル（GridLayout使用）
 * 
 * 2. ボタンの配置
 *    C  /  *  -
 *    7  8  9  +
 *    4  5  6  +
 *    1  2  3  =
 *    0     .  =
 * 
 * 3. イベント処理
 *    - 数字ボタン: 表示に数字を追加
 *    - 演算子ボタン: 計算の準備
 *    - =ボタン: 計算実行
 *    - Cボタン: リセット
 * 
 * 4. エラーハンドリング
 *    - ゼロ除算のチェック
 *    - 無効な入力の処理
 *    - 数値形式エラーの対応
 * 
 * よくある間違い:
 * - イベントリスナーの設定忘れ
 * - レイアウトマネージャーの不適切な使用
 * - 計算順序の間違い
 * - 状態管理の不備
 */