/**
 * リスト19-12
 * KeyboardEventAdvancedExampleクラス
 * 
 * 元ファイル: chapter19-gui-event-handling.md (868行目)
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class KeyboardEventAdvancedExample extends JFrame {
    private JTextArea textArea;
    private JLabel statusLabel;
    private Set<Integer> pressedKeys = new HashSet<>();
    
    public KeyboardEventAdvancedExample() {
        setTitle("高度なキーボードイベント処理");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // テキストエリア
        textArea = new JTextArea(10, 40);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        // ステータスラベル
        statusLabel = new JLabel("キーを押してください");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // キーリスナーの追加
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                updateStatus(e, "押下");
                
                // ショートカットキーの検出
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    saveDocument();
                    e.consume(); // デフォルトの動作をキャンセル
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A) {
                    textArea.selectAll();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_F1) {
                    showHelp();
                }
                
                // 複数キーの同時押し検出
                if (pressedKeys.contains(KeyEvent.VK_CONTROL) && 
                    pressedKeys.contains(KeyEvent.VK_SHIFT) && 
                    pressedKeys.contains(KeyEvent.VK_D)) {
                    duplicateLine();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
                updateStatus(e, "解放");
            }
            
            @Override
            public void keyTyped(KeyEvent e) {
                // 文字が入力された時
                char ch = e.getKeyChar();
                if (!Character.isISOControl(ch)) {
                    statusLabel.setText("入力文字: '" + ch + "' (Unicode: " + 
                        (int)ch + ")");
                }
            }
        });
        
        // InputMapとActionMapを使ったより高度なキーバインディング
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();
        
        // Ctrl+Dで行複製
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 
            InputEvent.CTRL_DOWN_MASK), "duplicate-line");
        actionMap.put("duplicate-line", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateLine();
            }
        });
        
        // F5でタイムスタンプ挿入
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "insert-timestamp");
        actionMap.put("insert-timestamp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.insert(java.time.LocalDateTime.now().toString(), 
                    textArea.getCaretPosition());
            }
        });
        
        // メニューバーの作成（アクセラレータキー付き）
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("ファイル");
        fileMenu.setMnemonic(KeyEvent.VK_F); // Alt+F
        
        JMenuItem saveItem = new JMenuItem("保存");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
            InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveDocument());
        
        JMenuItem exitItem = new JMenuItem("終了");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 
            InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void updateStatus(KeyEvent e, String action) {
        String modifiers = "";
        if (e.isControlDown()) modifiers += "Ctrl+";
        if (e.isAltDown()) modifiers += "Alt+";
        if (e.isShiftDown()) modifiers += "Shift+";
        
        String keyText = KeyEvent.getKeyText(e.getKeyCode());
        statusLabel.setText("キー" + action + ": " + modifiers + keyText + 
            " (コード: " + e.getKeyCode() + ")");
    }
    
    private void saveDocument() {
        JOptionPane.showMessageDialog(this, "ドキュメントを保存しました（仮想）");
    }
    
    private void showHelp() {
        JOptionPane.showMessageDialog(this, 
            "ショートカットキー:\n" +
            "Ctrl+S: 保存\n" +
            "Ctrl+A: すべて選択\n" +
            "Ctrl+D: 行複製\n" +
            "F1: このヘルプ\n" +
            "F5: タイムスタンプ挿入");
    }
    
    private void duplicateLine() {
        try {
            int caretPos = textArea.getCaretPosition();
            int lineNum = textArea.getLineOfOffset(caretPos);
            int lineStart = textArea.getLineStartOffset(lineNum);
            int lineEnd = textArea.getLineEndOffset(lineNum);
            String line = textArea.getText(lineStart, lineEnd - lineStart);
            textArea.insert(line, lineEnd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new KeyboardEventAdvancedExample().setVisible(true);
        });
    }
}