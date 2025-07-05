/**
 * 第17章 基本課題2: レイアウトマネージャーの比較
 * 
 * 異なるレイアウトマネージャーの特徴を理解するため、
 * 複数のレイアウトを実装して比較してください。
 * 
 * 要件:
 * 1. BorderLayout、FlowLayout、GridLayoutを使用した3つのパネルを作成
 * 2. JTabbedPaneを使用してタブ形式で表示
 * 3. 各レイアウトに同じコンポーネント（ボタンなど）を配置
 * 4. レイアウトの違いを実際に確認できるようにする
 * 
 * 学習ポイント:
 * - 各レイアウトマネージャーの特徴と適用場面
 * - JTabbedPaneの使用方法
 * - 複合的なレイアウト設計
 * - コンポーネントのサイズと配置の制御
 */

import javax.swing.*;
import java.awt.*;

public class LayoutManagerDemo extends JFrame {
    
    public LayoutManagerDemo() {
        setTitle("レイアウトマネージャーの比較");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        // TODO: JTabbedPaneを作成してください
        
        // TODO: 各レイアウトのパネルを作成してタブに追加してください
        
        // TODO: フレームにタブペインを追加してください
    }
    
    /**
     * BorderLayoutを使用したパネルを作成します
     * @return BorderLayoutパネル
     */
    private JPanel createBorderLayoutPanel() {
        // TODO: BorderLayoutを使用したパネルを実装してください
        // ヒント: 5つの領域（NORTH, SOUTH, EAST, WEST, CENTER）にコンポーネントを配置
        return null;
    }
    
    /**
     * FlowLayoutを使用したパネルを作成します
     * @return FlowLayoutパネル
     */
    private JPanel createFlowLayoutPanel() {
        // TODO: FlowLayoutを使用したパネルを実装してください
        // ヒント: コンポーネントが横に並んで配置される
        return null;
    }
    
    /**
     * GridLayoutを使用したパネルを作成します
     * @return GridLayoutパネル
     */
    private JPanel createGridLayoutPanel() {
        // TODO: GridLayoutを使用したパネルを実装してください
        // ヒント: 格子状にコンポーネントが配置される（例：3x3のグリッド）
        return null;
    }
    
    /**
     * 共通のボタンセットを作成します
     * @return ボタンの配列
     */
    private JButton[] createButtons() {
        // TODO: 複数のボタンを作成して配列で返してください
        // ヒント: 「ボタン1」「ボタン2」...のような名前のボタンを5-6個作成
        return null;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LayoutManagerDemo().setVisible(true);
        });
    }
}

/*
 * 実装のヒント:
 * 
 * 1. JTabbedPaneの使用
 *    JTabbedPane tabbedPane = new JTabbedPane();
 *    tabbedPane.addTab("BorderLayout", createBorderLayoutPanel());
 * 
 * 2. BorderLayoutの例
 *    JPanel panel = new JPanel(new BorderLayout());
 *    panel.add(new JButton("North"), BorderLayout.NORTH);
 *    panel.add(new JButton("Center"), BorderLayout.CENTER);
 * 
 * 3. FlowLayoutの例
 *    JPanel panel = new JPanel(new FlowLayout());
 *    for (JButton button : buttons) {
 *        panel.add(button);
 *    }
 * 
 * 4. GridLayoutの例
 *    JPanel panel = new JPanel(new GridLayout(3, 2)); // 3行2列
 *    for (JButton button : buttons) {
 *        panel.add(button);
 *    }
 * 
 * 観察ポイント:
 * - ウィンドウサイズを変更した時の各レイアウトの動作
 * - コンポーネントの配置順序と表示位置の関係
 * - 各レイアウトの適用場面の違い
 * 
 * 発展課題:
 * - GridBagLayoutの追加
 * - CardLayoutの実装
 * - カスタムレイアウトマネージャーの作成
 */