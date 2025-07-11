/**
 * リスト19-4
 * コードスニペット
 * 
 * 元ファイル: chapter19-gui-event-handling.md (227行目)
 */

// 色による状態表示
Color VALID = new Color(144, 238, 144);   // 淡い緑
Color INVALID = new Color(255, 182, 193); // 淡い赤
Color NORMAL = UIManager.getColor("TextField.background");

// アイコンによる状態表示
Icon CHECK = new ImageIcon("check.png");
Icon CROSS = new ImageIcon("cross.png");
Icon INFO = new ImageIcon("info.png");