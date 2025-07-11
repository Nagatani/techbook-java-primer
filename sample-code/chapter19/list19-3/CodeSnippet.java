/**
 * リスト19-3
 * コードスニペット
 * 
 * 元ファイル: chapter19-gui-event-handling.md (206行目)
 */

// 1. 即座バリデーション（キー入力毎）
document.addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) { validate(); }
    public void removeUpdate(DocumentEvent e) { validate(); }
    public void changedUpdate(DocumentEvent e) { validate(); }
});

// 2. 遅延バリデーション（入力停止後）
Timer delayTimer = new Timer(500, e -> validate());
delayTimer.setRepeats(false);