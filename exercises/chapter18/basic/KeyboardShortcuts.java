/**
 * 第18章 基本課題1: キーボードショートカット
 * 
 * キーボードショートカット機能を持つシンプルなテキストエディタを作成してください。
 * 
 * 要件:
 * 1. JTextAreaを使用したテキスト編集機能
 * 2. 以下のキーボードショートカットを実装:
 *    - Ctrl+N: 新規作成（テキストクリア）
 *    - Ctrl+S: 保存（ダイアログ表示）
 *    - Ctrl+O: 開く（ダイアログ表示）
 *    - Ctrl+Z: 元に戻す
 *    - Ctrl+C, Ctrl+V, Ctrl+X: コピー、貼り付け、切り取り
 * 3. ステータスバーで現在の文字数を表示
 * 4. メニューバーも同時に提供
 * 
 * 学習ポイント:
 * - KeyStrokeとActionMapの使用方法
 * - キーバインディングの設定
 * - アクションの抽象化
 * - メニューとショートカットの連携
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;

public class KeyboardShortcuts extends JFrame {
    
    // TODO: 必要なコンポーネントを宣言してください
    // ヒント: JTextArea、JLabel（ステータス用）、UndoManager
    
    public KeyboardShortcuts() {
        setTitle("キーボードショートカット - テキストエディタ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        // TODO: コンポーネントの初期化
        
        // TODO: レイアウトの設定
        
        // TODO: メニューバーの作成
        
        // TODO: キーボードショートカットの設定
        
        // TODO: イベントリスナーの設定
    }
    
    /**
     * UIコンポーネントを初期化します
     */
    private void initializeComponents() {
        // TODO: 以下のコンポーネントを初期化してください
        // - JTextArea（スクロール可能）
        // - ステータスラベル
        // - UndoManager
    }
    
    /**
     * メニューバーを作成します
     */
    private void createMenuBar() {
        // TODO: メニューバーを作成してください
        // ファイルメニュー: 新規、開く、保存、終了
        // 編集メニュー: 元に戻す、切り取り、コピー、貼り付け
        // 各メニュー項目にアクセラレータキー（ショートカット）を設定
    }
    
    /**
     * キーボードショートカットを設定します
     */
    private void setupKeyboardShortcuts() {
        // TODO: 以下のショートカットを設定してください
        
        // Ctrl+N: 新規作成
        // InputMap inputMap = textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        // ActionMap actionMap = textArea.getActionMap();
        
        // Ctrl+S: 保存
        
        // Ctrl+O: 開く
        
        // Ctrl+Z: 元に戻す
        
        // その他の標準的なショートカット
    }
    
    /**
     * 新規作成アクション
     */
    private void newDocument() {
        // TODO: テキストエリアをクリアして新規文書を作成
        // 確認ダイアログを表示することも検討
    }
    
    /**
     * ファイル保存アクション
     */
    private void saveDocument() {
        // TODO: ファイル保存ダイアログを表示
        // 実際の保存は簡易実装で可（ダイアログ表示のみでも可）
    }
    
    /**
     * ファイル開くアクション
     */
    private void openDocument() {
        // TODO: ファイル選択ダイアログを表示
        // 実際の読み込みは簡易実装で可（ダイアログ表示のみでも可）
    }
    
    /**
     * 元に戻すアクション
     */
    private void undoAction() {
        // TODO: UndoManagerを使用して操作を元に戻す
    }
    
    /**
     * ステータスバーを更新します
     */
    private void updateStatus() {
        // TODO: 現在の文字数を計算してステータスラベルに表示
        // 例: "文字数: 123"
    }
    
    /**
     * カスタムアクションクラス
     */
    private class CustomAction extends AbstractAction {
        private final Runnable action;
        
        public CustomAction(String name, Runnable action) {
            super(name);
            this.action = action;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            action.run();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new KeyboardShortcuts().setVisible(true);
        });
    }
}

/*
 * 実装のヒント:
 * 
 * 1. キーストロークの設定
 *    KeyStroke ctrlN = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
 *    inputMap.put(ctrlN, "new");
 *    actionMap.put("new", new CustomAction("新規", this::newDocument));
 * 
 * 2. UndoManagerの使用
 *    UndoManager undoManager = new UndoManager();
 *    Document doc = textArea.getDocument();
 *    doc.addUndoableEditListener(undoManager);
 * 
 * 3. メニューアイテムの作成
 *    JMenuItem newItem = new JMenuItem("新規");
 *    newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
 *    newItem.addActionListener(e -> newDocument());
 * 
 * 4. ステータス更新のリスナー
 *    textArea.getDocument().addDocumentListener(new DocumentListener() {
 *        public void insertUpdate(DocumentEvent e) { updateStatus(); }
 *        public void removeUpdate(DocumentEvent e) { updateStatus(); }
 *        public void changedUpdate(DocumentEvent e) { updateStatus(); }
 *    });
 * 
 * テスト項目:
 * - 各キーボードショートカットの動作確認
 * - メニューからの操作確認
 * - ステータスバーの更新確認
 * - 元に戻す機能の動作確認
 * 
 * 発展課題:
 * - やり直し機能（Ctrl+Y）の追加
 * - 検索・置換機能（Ctrl+F）の追加
 * - 最近使用したファイルリストの表示
 * - 自動保存機能の実装
 */