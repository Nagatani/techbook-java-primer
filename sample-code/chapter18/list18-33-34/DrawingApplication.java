/**
 * リスト18-34
 * DrawingApplicationクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (2218行目)
 */

import javax.swing.*;
import java.awt.*;

public class DrawingApplication extends JFrame {
    private DrawingPanel drawingPanel;
    
    public DrawingApplication() {
        setTitle("描画アプリケーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 描画パネル
        drawingPanel = new DrawingPanel();
        JScrollPane scrollPane = new JScrollPane(drawingPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        // ツールバー
        JPanel toolbar = new JPanel(new FlowLayout());
        
        // 色選択ボタン
        JButton[] colorButtons = {
            createColorButton("黒", Color.BLACK),
            createColorButton("赤", Color.RED),
            createColorButton("青", Color.BLUE),
            createColorButton("緑", Color.GREEN)
        };
        
        for (JButton button : colorButtons) {
            toolbar.add(button);
        }
        
        // 線の太さスライダー
        toolbar.add(new JLabel("線の太さ:"));
        JSlider strokeSlider = new JSlider(1, 10, 2);
        strokeSlider.addChangeListener(e -> {
            drawingPanel.setStrokeWidth(strokeSlider.getValue());
        });
        toolbar.add(strokeSlider);
        
        // クリアボタン
        JButton clearButton = new JButton("クリア");
        clearButton.addActionListener(e -> drawingPanel.clearDrawing());
        toolbar.add(clearButton);
        
        add(toolbar, BorderLayout.NORTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private JButton createColorButton(String name, Color color) {
        JButton button = new JButton(name);
        button.setBackground(color);
        button.setForeground(color.equals(Color.BLACK) ? Color.WHITE : Color.BLACK);
        button.addActionListener(e -> drawingPanel.setDrawingColor(color));
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DrawingApplication().setVisible(true);
        });
    }
}