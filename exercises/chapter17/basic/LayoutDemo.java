/**
 * 第17章 GUI プログラミング基礎
 * 演習課題: LayoutDemo.java
 * 
 * 【課題概要】
 * 異なるレイアウトマネージャーの動作を比較できるデモアプリケーションを作成してください。
 * 
 * 【要求仕様】
 * 1. 複数のレイアウトマネージャーを試せるタブ画面
 * 2. 各タブで異なるレイアウトマネージャーを使用
 * 3. BorderLayout のデモ（North、South、East、West、Center）
 * 4. FlowLayout のデモ（LEFT、CENTER、RIGHT）
 * 5. GridLayout のデモ（2x3、3x2、等）
 * 6. BoxLayout のデモ（X_AXIS、Y_AXIS）
 * 7. CardLayout のデモ（カード切り替え）
 * 8. GridBagLayout のデモ（複雑な配置）
 * 9. レイアウトの動的切り替え機能
 * 10. ウィンドウサイズ変更時の動作確認
 * 
 * 【学習ポイント】
 * - JTabbedPane の使用方法
 * - 各レイアウトマネージャーの特徴と使い分け
 * - レイアウトの動的変更方法
 * - コンポーネントの配置と制約
 * - GridBagConstraints の使用方法
 * - レイアウトの入れ子構造
 * 
 * 【実装のヒント】
 * 1. 各レイアウトは別々のパネルで実装
 * 2. JTabbedPane で複数のレイアウトを切り替え
 * 3. ボタンやラベルで視覚的に配置を確認
 * 4. レイアウトの動的変更はsetLayout()を使用
 * 5. GridBagConstraints は丁寧に設定
 * 
 * 【よくある間違いと対策】
 * - レイアウトマネージャーの混在
 *   → 各パネルで統一したレイアウトを使用
 * - GridBagConstraints の設定ミス
 *   → anchor、fill、weightx、weighty を適切に設定
 * - レイアウト変更後の再描画忘れ
 *   → revalidate()、repaint() を呼び出し
 * - コンポーネントサイズの考慮不足
 *   → preferredSize の設定を検討
 * 
 * 【段階的な実装指針】
 * 1. 基本的なタブ画面の作成
 * 2. BorderLayout のデモ実装
 * 3. FlowLayout のデモ実装
 * 4. GridLayout のデモ実装
 * 5. BoxLayout のデモ実装
 * 6. CardLayout のデモ実装
 * 7. GridBagLayout のデモ実装
 * 8. レイアウトの動的切り替え機能追加
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LayoutDemo extends JFrame {
    private JTabbedPane tabbedPane;
    
    public LayoutDemo() {
        initializeComponents();
        setupTabs();
        setupWindow();
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
    }
    
    private void setupTabs() {
        tabbedPane.addTab("BorderLayout", createBorderLayoutPanel());
        tabbedPane.addTab("FlowLayout", createFlowLayoutPanel());
        tabbedPane.addTab("GridLayout", createGridLayoutPanel());
        tabbedPane.addTab("BoxLayout", createBoxLayoutPanel());
        tabbedPane.addTab("CardLayout", createCardLayoutPanel());
        tabbedPane.addTab("GridBagLayout", createGridBagLayoutPanel());
        tabbedPane.addTab("Dynamic Layout", createDynamicLayoutPanel());
    }
    
    private JPanel createBorderLayoutPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("BorderLayout Demo"));
        
        JButton northButton = new JButton("North");
        northButton.setPreferredSize(new Dimension(0, 40));
        northButton.setBackground(Color.RED);
        northButton.setOpaque(true);
        
        JButton southButton = new JButton("South");
        southButton.setPreferredSize(new Dimension(0, 40));
        southButton.setBackground(Color.BLUE);
        southButton.setOpaque(true);
        
        JButton eastButton = new JButton("East");
        eastButton.setPreferredSize(new Dimension(60, 0));
        eastButton.setBackground(Color.GREEN);
        eastButton.setOpaque(true);
        
        JButton westButton = new JButton("West");
        westButton.setPreferredSize(new Dimension(60, 0));
        westButton.setBackground(Color.YELLOW);
        westButton.setOpaque(true);
        
        JButton centerButton = new JButton("Center");
        centerButton.setBackground(Color.PINK);
        centerButton.setOpaque(true);
        
        panel.add(northButton, BorderLayout.NORTH);
        panel.add(southButton, BorderLayout.SOUTH);
        panel.add(eastButton, BorderLayout.EAST);
        panel.add(westButton, BorderLayout.WEST);
        panel.add(centerButton, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFlowLayoutPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        mainPanel.setBorder(BorderFactory.createTitledBorder("FlowLayout Demo"));
        
        // LEFT alignment
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBorder(BorderFactory.createTitledBorder("LEFT"));
        for (int i = 1; i <= 5; i++) {
            leftPanel.add(new JButton("Button " + i));
        }
        
        // CENTER alignment
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBorder(BorderFactory.createTitledBorder("CENTER"));
        for (int i = 1; i <= 5; i++) {
            centerPanel.add(new JButton("Button " + i));
        }
        
        // RIGHT alignment
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBorder(BorderFactory.createTitledBorder("RIGHT"));
        for (int i = 1; i <= 5; i++) {
            rightPanel.add(new JButton("Button " + i));
        }
        
        mainPanel.add(leftPanel);
        mainPanel.add(centerPanel);
        mainPanel.add(rightPanel);
        
        return mainPanel;
    }
    
    private JPanel createGridLayoutPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("GridLayout Demo"));
        
        // コントロールパネル
        JPanel controlPanel = new JPanel(new FlowLayout());
        JLabel rowLabel = new JLabel("Rows:");
        JSpinner rowSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        JLabel colLabel = new JLabel("Columns:");
        JSpinner colSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        JButton updateButton = new JButton("Update Grid");
        
        controlPanel.add(rowLabel);
        controlPanel.add(rowSpinner);
        controlPanel.add(colLabel);
        controlPanel.add(colSpinner);
        controlPanel.add(updateButton);
        
        // グリッドパネル
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        updateGridPanel(gridPanel, 2, 3);
        
        // イベントハンドラー
        updateButton.addActionListener(e -> {
            int rows = (Integer) rowSpinner.getValue();
            int cols = (Integer) colSpinner.getValue();
            updateGridPanel(gridPanel, rows, cols);
        });
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void updateGridPanel(JPanel gridPanel, int rows, int cols) {
        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(rows, cols, 5, 5));
        
        for (int i = 1; i <= rows * cols; i++) {
            JButton button = new JButton("Button " + i);
            button.setBackground(getRandomColor());
            button.setOpaque(true);
            gridPanel.add(button);
        }
        
        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    private JPanel createBoxLayoutPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("BoxLayout Demo"));
        
        // X_AXIS (horizontal)
        JPanel xAxisPanel = new JPanel();
        xAxisPanel.setLayout(new BoxLayout(xAxisPanel, BoxLayout.X_AXIS));
        xAxisPanel.setBorder(BorderFactory.createTitledBorder("X_AXIS (Horizontal)"));
        
        xAxisPanel.add(new JButton("Button 1"));
        xAxisPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        xAxisPanel.add(new JButton("Button 2"));
        xAxisPanel.add(Box.createHorizontalGlue());
        xAxisPanel.add(new JButton("Button 3"));
        
        // Y_AXIS (vertical)
        JPanel yAxisPanel = new JPanel();
        yAxisPanel.setLayout(new BoxLayout(yAxisPanel, BoxLayout.Y_AXIS));
        yAxisPanel.setBorder(BorderFactory.createTitledBorder("Y_AXIS (Vertical)"));
        
        yAxisPanel.add(new JButton("Button 1"));
        yAxisPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        yAxisPanel.add(new JButton("Button 2"));
        yAxisPanel.add(Box.createVerticalGlue());
        yAxisPanel.add(new JButton("Button 3"));
        
        mainPanel.add(xAxisPanel, BorderLayout.NORTH);
        mainPanel.add(yAxisPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createCardLayoutPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("CardLayout Demo"));
        
        // カードパネル
        JPanel cardPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        
        // カード1
        JPanel card1 = new JPanel();
        card1.setBackground(Color.RED);
        card1.add(new JLabel("Card 1"));
        
        // カード2
        JPanel card2 = new JPanel();
        card2.setBackground(Color.GREEN);
        card2.add(new JLabel("Card 2"));
        
        // カード3
        JPanel card3 = new JPanel();
        card3.setBackground(Color.BLUE);
        card3.add(new JLabel("Card 3"));
        
        cardPanel.add(card1, "Card1");
        cardPanel.add(card2, "Card2");
        cardPanel.add(card3, "Card3");
        
        // コントロールパネル
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton previousButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        JButton card1Button = new JButton("Card 1");
        JButton card2Button = new JButton("Card 2");
        JButton card3Button = new JButton("Card 3");
        
        controlPanel.add(previousButton);
        controlPanel.add(nextButton);
        controlPanel.add(new JSeparator());
        controlPanel.add(card1Button);
        controlPanel.add(card2Button);
        controlPanel.add(card3Button);
        
        // イベントハンドラー
        previousButton.addActionListener(e -> cardLayout.previous(cardPanel));
        nextButton.addActionListener(e -> cardLayout.next(cardPanel));
        card1Button.addActionListener(e -> cardLayout.show(cardPanel, "Card1"));
        card2Button.addActionListener(e -> cardLayout.show(cardPanel, "Card2"));
        card3Button.addActionListener(e -> cardLayout.show(cardPanel, "Card3"));
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createGridBagLayoutPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("GridBagLayout Demo"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // ラベル1
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Name:"), gbc);
        
        // テキストフィールド1
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(new JTextField(20), gbc);
        
        // ラベル2
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JLabel("Address:"), gbc);
        
        // テキストエリア
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JTextArea textArea = new JTextArea(5, 20);
        panel.add(new JScrollPane(textArea), gbc);
        
        // ボタンパネル
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JButton("OK"));
        buttonPanel.add(new JButton("Cancel"));
        buttonPanel.add(new JButton("Apply"));
        
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createDynamicLayoutPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Dynamic Layout Demo"));
        
        // コントロールパネル
        JPanel controlPanel = new JPanel(new FlowLayout());
        JLabel layoutLabel = new JLabel("Layout:");
        JComboBox<String> layoutCombo = new JComboBox<>(new String[]{
            "FlowLayout", "GridLayout", "BorderLayout"
        });
        JButton applyButton = new JButton("Apply Layout");
        
        controlPanel.add(layoutLabel);
        controlPanel.add(layoutCombo);
        controlPanel.add(applyButton);
        
        // 動的パネル
        JPanel dynamicPanel = new JPanel(new FlowLayout());
        addComponentsToDynamicPanel(dynamicPanel, new FlowLayout());
        
        // イベントハンドラー
        applyButton.addActionListener(e -> {
            String selectedLayout = (String) layoutCombo.getSelectedItem();
            LayoutManager layout = null;
            
            switch (selectedLayout) {
                case "FlowLayout":
                    layout = new FlowLayout();
                    break;
                case "GridLayout":
                    layout = new GridLayout(2, 3, 5, 5);
                    break;
                case "BorderLayout":
                    layout = new BorderLayout(5, 5);
                    break;
            }
            
            if (layout != null) {
                dynamicPanel.removeAll();
                dynamicPanel.setLayout(layout);
                addComponentsToDynamicPanel(dynamicPanel, layout);
                dynamicPanel.revalidate();
                dynamicPanel.repaint();
            }
        });
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(dynamicPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private void addComponentsToDynamicPanel(JPanel panel, LayoutManager layout) {
        if (layout instanceof BorderLayout) {
            panel.add(new JButton("North"), BorderLayout.NORTH);
            panel.add(new JButton("South"), BorderLayout.SOUTH);
            panel.add(new JButton("East"), BorderLayout.EAST);
            panel.add(new JButton("West"), BorderLayout.WEST);
            panel.add(new JButton("Center"), BorderLayout.CENTER);
        } else {
            for (int i = 1; i <= 6; i++) {
                JButton button = new JButton("Button " + i);
                button.setBackground(getRandomColor());
                button.setOpaque(true);
                panel.add(button);
            }
        }
    }
    
    private Color getRandomColor() {
        Color[] colors = {
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
            Color.ORANGE, Color.PINK, Color.CYAN, Color.MAGENTA
        };
        return colors[(int) (Math.random() * colors.length)];
    }
    
    private void setupWindow() {
        setTitle("Layout Manager Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        setSize(700, 500);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new LayoutDemo().setVisible(true);
        });
    }
}