/**
 * リスト18-37
 * コードスニペット
 * 
 * 元ファイル: chapter18-gui-basics.md (2329行目)
 */

public MyComponent() {
    // キーボードナビゲーション対応
    setFocusable(true);
    
    // アクセシビリティ情報を設定
    getAccessibleContext().setAccessibleName("カスタムコンポーネント");
    getAccessibleContext().setAccessibleDescription("説明");
}