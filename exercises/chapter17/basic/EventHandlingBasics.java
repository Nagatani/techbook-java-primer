/**
 * 第17章 基本課題3: イベントハンドリングの基礎
 * 
 * 様々な種類のイベントを処理するGUIアプリケーションを作成してください。
 * 
 * 要件:
 * 1. ボタンクリック、マウスイベント、キーボードイベントを処理
 * 2. 各イベントの詳細情報をテキストエリアに表示
 * 3. イベントの種類に応じて異なる処理を実行
 * 4. イベントログの表示とクリア機能
 * 
 * 学習ポイント:
 * - 各種イベントリスナーの実装方法
 * - イベントオブジェクトから情報を取得する方法
 * - イベント駆動プログラミングの理解
 * - ユーザーインターフェースの応答性向上
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EventHandlingBasics extends JFrame {
    
    // TODO: 必要なコンポーネントを宣言してください
    // ヒント: JTextArea（ログ表示用）、JButton、JTextField、JLabel等
    
    public EventHandlingBasics() {
        setTitle("イベントハンドリングの基礎");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        
        // TODO: コンポーネントの初期化
        
        // TODO: レイアウトの設定
        
        // TODO: イベントリスナーの設定
    }
    
    /**
     * UIコンポーネントを初期化します
     */
    private void initializeComponents() {
        // TODO: 以下のコンポーネントを初期化してください
        // - ログ表示用のJTextArea（スクロール可能）
        // - イベント生成用のボタン群
        // - テキスト入力用のJTextField
        // - ログクリア用のボタン
    }
    
    /**
     * レイアウトを設定します
     */
    private void setupLayout() {
        // TODO: 適切なレイアウトマネージャーを使用してコンポーネントを配置してください
        // ヒント: BorderLayoutをメインとして、ButtonPanel用にFlowLayoutなどを組み合わせ
    }
    
    /**
     * イベントリスナーを設定します
     */
    private void setupEventListeners() {
        // TODO: 以下のイベントリスナーを実装してください
        
        // 1. ActionListener（ボタンクリック）
        
        // 2. MouseListener（マウスイベント）
        
        // 3. KeyListener（キーボードイベント）
        
        // 4. FocusListener（フォーカスイベント）
    }
    
    /**
     * ActionListenerを実装します
     */
    private void setupActionListeners() {
        // TODO: ボタンクリックイベントを処理してください
        // 例：「ボタンがクリックされました」をログに出力
    }
    
    /**
     * MouseListenerを実装します
     */
    private void setupMouseListeners() {
        // TODO: マウスイベントを処理してください
        // マウスクリック、プレス、リリース、エンター、イグジットを検出
    }
    
    /**
     * KeyListenerを実装します
     */
    private void setupKeyListeners() {
        // TODO: キーボードイベントを処理してください
        // キープレス、リリース、タイプを検出
    }
    
    /**
     * FocusListenerを実装します
     */
    private void setupFocusListeners() {
        // TODO: フォーカスイベントを処理してください
        // フォーカス取得、失去を検出
    }
    
    /**
     * イベントログに情報を追加します
     * @param eventType イベントの種類
     * @param details イベントの詳細情報
     */
    private void logEvent(String eventType, String details) {
        // TODO: 現在時刻とともにイベント情報をログに追加してください
        // フォーマット例: "[10:30:45] ActionEvent: ボタンがクリックされました"
    }
    
    /**
     * 現在時刻を文字列として取得します
     * @return フォーマットされた時刻文字列
     */
    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    /**
     * ログをクリアします
     */
    private void clearLog() {
        // TODO: ログエリアの内容をクリアしてください
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new EventHandlingBasics().setVisible(true);
        });
    }
}

/*
 * 実装のヒント:
 * 
 * 1. ActionListenerの実装例
 *    button.addActionListener(e -> {
 *        logEvent("ActionEvent", "ボタン「" + e.getActionCommand() + "」がクリックされました");
 *    });
 * 
 * 2. MouseListenerの実装例
 *    component.addMouseListener(new MouseAdapter() {
 *        @Override
 *        public void mouseClicked(MouseEvent e) {
 *            logEvent("MouseEvent", "マウスクリック (" + e.getX() + ", " + e.getY() + ")");
 *        }
 *    });
 * 
 * 3. KeyListenerの実装例
 *    textField.addKeyListener(new KeyAdapter() {
 *        @Override
 *        public void keyPressed(KeyEvent e) {
 *            logEvent("KeyEvent", "キープレス: " + KeyEvent.getKeyText(e.getKeyCode()));
 *        }
 *    });
 * 
 * 4. ログ表示の例
 *    JTextArea logArea = new JTextArea(20, 50);
 *    logArea.setEditable(false);
 *    JScrollPane scrollPane = new JScrollPane(logArea);
 * 
 * テスト項目:
 * - 各種ボタンのクリック
 * - マウスの移動とクリック
 * - キーボード入力
 * - フォーカスの移動
 * - ログのクリア機能
 * 
 * 発展課題:
 * - WindowListenerの追加
 * - カスタムイベントの作成
 * - イベントフィルタリング機能
 * - イベント統計の表示
 */