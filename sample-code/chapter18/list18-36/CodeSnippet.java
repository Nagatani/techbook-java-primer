/**
 * リスト18-36
 * コードスニペット
 * 
 * 元ファイル: chapter18-gui-basics.md (2306行目)
 */

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    // 必要な場合のみ再描画
    if (needsRepaint) {
        // 重い描画処理
        needsRepaint = false;
    }
}

// 効率的な境界計算
@Override
public boolean contains(int x, int y) {
    // カスタムの境界判定
    return customBounds.contains(x, y);
}