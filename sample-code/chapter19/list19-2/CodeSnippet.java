/**
 * リスト19-2
 * コードスニペット
 * 
 * 元ファイル: chapter19-gui-event-handling.md (123行目)
 */

// ① 非効率的なアプローチ：毎回全体再描画
public void paintComponent(Graphics g) {
    super.paintComponent(g);
    // ①-1: すべての図形を毎回再描画
}

// ② 効率的なアプローチ：BufferedImageへの差分描画
Graphics2D g2d = bufferedImage.createGraphics(); // ②-1
g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // ②-2
                    RenderingHints.VALUE_ANTIALIAS_ON);