/**
 * リスト18-29
 * RoundButtonクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (1679行目)
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundButton extends JComponent {
    private String text;
    private boolean pressed = false;
    private Color backgroundColor = Color.LIGHT_GRAY;
    private Color textColor = Color.BLACK;
    private Color pressedColor = Color.GRAY;
    
    public RoundButton(String text) {
        this.text = text;
        setOpaque(false); // 透明にして独自描画を有効にする
        setFocusable(true); // フォーカス可能にする
        
        // マウスイベントリスナーを追加
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                repaint(); // 再描画を要求
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
                
                // ボタンの境界内でリリースされた場合のみアクションを実行
                if (contains(e.getPoint())) {
                    fireActionPerformed();
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // アンチエイリアシングを有効にして滑らかな描画
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        // ボタンの背景色を決定
        Color currentBgColor = pressed ? pressedColor : backgroundColor;
        
        // 円形のボタンを描画
        int diameter = Math.min(getWidth(), getHeight()) - 4;
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;
        
        g2d.setColor(currentBgColor);
        g2d.fillOval(x, y, diameter, diameter);
        
        // 境界線を描画
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawOval(x, y, diameter, diameter);
        
        // テキストを描画
        g2d.setColor(textColor);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() - textHeight) / 2 + fm.getAscent();
        
        g2d.drawString(text, textX, textY);
        g2d.dispose();
    }
    
    @Override
    public Dimension getPreferredSize() {
        // コンポーネントの推奨サイズを返す
        FontMetrics fm = getFontMetrics(getFont());
        int textWidth = fm.stringWidth(text);
        int size = Math.max(textWidth + 20, 60); // 最小サイズ60px
        return new Dimension(size, size);
    }
    
    // ActionListenerサポートを追加
    private java.util.List<java.awt.event.ActionListener> actionListeners = 
        new java.util.ArrayList<>();
    
    public void addActionListener(java.awt.event.ActionListener listener) {
        actionListeners.add(listener);
    }
    
    public void removeActionListener(java.awt.event.ActionListener listener) {
        actionListeners.remove(listener);
    }
    
    private void fireActionPerformed() {
        java.awt.event.ActionEvent event = new java.awt.event.ActionEvent(
            this, java.awt.event.ActionEvent.ACTION_PERFORMED, text);
        
        for (java.awt.event.ActionListener listener : actionListeners) {
            listener.actionPerformed(event);
        }
    }
    
    // プロパティのアクセサメソッド
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
        repaint();
    }
    
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }
}