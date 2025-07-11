/**
 * リスト19-5
 * コードスニペット
 * 
 * 元ファイル: chapter19-gui-event-handling.md (321行目)
 */

// 従来の方法：重複コード
JMenuItem saveMenuItem = new JMenuItem("保存");
saveMenuItem.addActionListener(e -> saveFile());
JButton saveButton = new JButton("保存");
saveButton.addActionListener(e -> saveFile());

// Actionパターン：一元管理
Action saveAction = new AbstractAction("保存") {
    public void actionPerformed(ActionEvent e) {
        saveFile();
    }
};
saveAction.putValue(Action.ACCELERATOR_KEY, 
    KeyStroke.getKeyStroke("ctrl S"));
saveAction.putValue(Action.SMALL_ICON, saveIcon);

// 複数のUI要素で共有
new JMenuItem(saveAction);
new JButton(saveAction);