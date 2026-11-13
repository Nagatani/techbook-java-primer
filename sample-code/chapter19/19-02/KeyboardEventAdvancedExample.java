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
                // 文字入力の検出
                char ch = e.getKeyChar();
                if (!Character.isISOControl(ch)) {
                    statusLabel.setText("入力文字: '" + ch + "'");
                }
            }
        });
        
        // フォーカスが外れたら、押されているキーの記録を消す
        // （ダイアログが開くと、キーを離したイベントはテキストエリアに届かない）
        textArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                pressedKeys.clear();
            }
        });
        
        // InputMapとActionMapを使ったより良いアプローチ
        setupKeyBindings();
        
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupKeyBindings() {
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();
        
        // Ctrl+Dで行削除
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), 
            "delete-line");
        actionMap.put("delete-line", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteLine();
            }
        });
        
        // F5で更新
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        actionMap.put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
    }
    
    private void updateStatus(KeyEvent e, String action) {
        String modifiers = "";
        if (e.isControlDown()) modifiers += "Ctrl+";
        if (e.isShiftDown()) modifiers += "Shift+";
        if (e.isAltDown()) modifiers += "Alt+";
        
        statusLabel.setText(action + ": " + modifiers + 
            KeyEvent.getKeyText(e.getKeyCode()));
    }
    
    private void saveDocument() {
        JOptionPane.showMessageDialog(this, "ドキュメントを保存しました");
    }
    
    private void showHelp() {
        JOptionPane.showMessageDialog(this, "F1: ヘルプ\nCtrl+S: 保存\nCtrl+A: 全選択");
    }
    
    private void duplicateLine() {
        try {
            int caretPosition = textArea.getCaretPosition();
            int lineNumber = textArea.getLineOfOffset(caretPosition);
            int lineStart = textArea.getLineStartOffset(lineNumber);
            int lineEnd = textArea.getLineEndOffset(lineNumber);
            String line = textArea.getText(lineStart, lineEnd - lineStart);
            textArea.insert(line, lineEnd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void deleteLine() {
        try {
            int caretPosition = textArea.getCaretPosition();
            int lineNumber = textArea.getLineOfOffset(caretPosition);
            int lineStart = textArea.getLineStartOffset(lineNumber);
            int lineEnd = textArea.getLineEndOffset(lineNumber);
            textArea.replaceRange("", lineStart, lineEnd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void refresh() {
        textArea.setText("");
        statusLabel.setText("内容をクリアしました");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new KeyboardEventAdvancedExample().setVisible(true);
        });
    }
}
