/**
 * リスト19-6
 * コードスニペット
 * 
 * 元ファイル: chapter19-gui-event-handling.md (351行目)
 */

// マウスイベントでの表示制御
component.addMouseListener(new MouseAdapter() {
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) showPopup(e);
    }
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) showPopup(e);
    }
    private void showPopup(MouseEvent e) {
        // コンテキストに応じたメニュー構築
        JPopupMenu popup = createContextMenu(e.getComponent());
        popup.show(e.getComponent(), e.getX(), e.getY());
    }
});