/**
 * リスト18-38
 * コードスニペット
 * 
 * 元ファイル: chapter18-gui-basics.md (2343行目)
 */

// 標準的なイベントリスナーパターンを実装
public void addMyComponentListener(MyComponentListener listener) {
    listeners.add(listener);
}

public void removeMyComponentListener(MyComponentListener listener) {
    listeners.remove(listener);
}

protected void fireMyComponentEvent(MyComponentEvent event) {
    for (MyComponentListener listener : listeners) {
        listener.componentChanged(event);
    }
}