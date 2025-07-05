/**
 * 第17章 GUI プログラミング基礎
 * 演習課題: ColorPicker.java
 * 
 * 【課題概要】
 * 色を選択・調整できるカラーピッカーアプリケーションを作成してください。
 * 
 * 【要求仕様】
 * 1. RGB値を個別に調整できるスライダー
 * 2. HSB値を個別に調整できるスライダー
 * 3. 色のプレビューエリア
 * 4. 16進数カラーコード表示
 * 5. 色履歴の保存・呼び出し機能
 * 6. プリセットカラー選択
 * 7. 色の名前表示（基本的な色名）
 * 8. 色の明度・彩度調整
 * 9. 色の補色・類似色表示
 * 10. JColorChooser との連携
 * 
 * 【学習ポイント】
 * - JSlider の使用方法とイベント処理
 * - Color クラスの RGB/HSB 変換
 * - 数値と文字列の相互変換
 * - リスト・配列を使った色履歴管理
 * - JColorChooser の使用方法
 * - カスタム描画による色表示
 * 
 * 【実装のヒント】
 * 1. Color.RGBtoHSB() と Color.HSBtoRGB() を使用
 * 2. JSlider の値変更リスナーでリアルタイム更新
 * 3. Graphics2D を使った色プレビュー描画
 * 4. ArrayList で色履歴を管理
 * 5. 色名の判定は基本的な色の範囲チェック
 * 
 * 【よくある間違いと対策】
 * - RGB と HSB の値の範囲の違い
 *   → RGB: 0-255, HSB: 0.0-1.0 (Hue: 0-360)
 * - スライダーの値変更時の無限ループ
 *   → フラグを使って相互更新を制御
 * - 色変更時のちらつき
 *   → SwingUtilities.invokeLater() を使用
 * - 浮動小数点の精度問題
 *   → Math.round() で適切に丸める
 * 
 * 【段階的な実装指針】
 * 1. 基本的なウィンドウとRGBスライダーの作成
 * 2. 色プレビューエリアの実装
 * 3. HSBスライダーの追加
 * 4. 16進数カラーコード表示の実装
 * 5. 色履歴機能の追加
 * 6. プリセットカラーの実装
 * 7. JColorChooser との連携
 * 8. 補色・類似色表示の実装
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ColorPicker extends JFrame {
    private JSlider redSlider, greenSlider, blueSlider;
    private JSlider hueSlider, saturationSlider, brightnessSlider;
    private JLabel redLabel, greenLabel, blueLabel;
    private JLabel hueLabel, saturationLabel, brightnessLabel;
    private JPanel colorPreview;
    private JLabel hexLabel;
    private JTextField hexField;
    private JLabel colorNameLabel;
    private JButton colorChooserButton;
    private JList<Color> colorHistoryList;
    private DefaultListModel<Color> historyModel;
    private List<Color> presetColors;
    private JPanel presetPanel;
    private JPanel complementaryPanel;
    private JPanel analogousPanel;
    private boolean isUpdating = false;
    
    public ColorPicker() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupWindow();
        updateColor();
    }
    
    private void initializeComponents() {
        // RGB スライダー
        redSlider = new JSlider(0, 255, 128);
        greenSlider = new JSlider(0, 255, 128);
        blueSlider = new JSlider(0, 255, 128);
        
        // HSB スライダー
        hueSlider = new JSlider(0, 360, 180);
        saturationSlider = new JSlider(0, 100, 50);
        brightnessSlider = new JSlider(0, 100, 50);
        
        // ラベル
        redLabel = new JLabel("Red: 128");
        greenLabel = new JLabel("Green: 128");
        blueLabel = new JLabel("Blue: 128");
        hueLabel = new JLabel("Hue: 180");
        saturationLabel = new JLabel("Saturation: 50");
        brightnessLabel = new JLabel("Brightness: 50");
        
        // 色プレビュー
        colorPreview = new JPanel();
        colorPreview.setPreferredSize(new Dimension(200, 100));
        colorPreview.setBorder(BorderFactory.createTitledBorder("Color Preview"));
        
        // 16進数表示
        hexLabel = new JLabel("Hex Color:");
        hexField = new JTextField("#808080", 10);
        
        // 色名表示
        colorNameLabel = new JLabel("Color Name: Gray");
        
        // ボタン
        colorChooserButton = new JButton("Advanced Color Chooser");
        
        // 色履歴
        historyModel = new DefaultListModel<>();
        colorHistoryList = new JList<>(historyModel);
        colorHistoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        colorHistoryList.setCellRenderer(new ColorListCellRenderer());
        colorHistoryList.setVisibleRowCount(5);
        
        // プリセットカラー
        presetColors = createPresetColors();
        presetPanel = new JPanel(new GridLayout(2, 8, 2, 2));
        presetPanel.setBorder(BorderFactory.createTitledBorder("Preset Colors"));
        
        // 補色・類似色表示
        complementaryPanel = new JPanel();
        complementaryPanel.setPreferredSize(new Dimension(50, 50));
        complementaryPanel.setBorder(BorderFactory.createTitledBorder("Complementary"));
        
        analogousPanel = new JPanel(new GridLayout(1, 3, 2, 2));
        analogousPanel.setBorder(BorderFactory.createTitledBorder("Analogous Colors"));
        
        setupPresetColors();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // メインパネル
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 左パネル (スライダー)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // RGB スライダー
        JPanel rgbPanel = new JPanel(new GridBagLayout());
        rgbPanel.setBorder(BorderFactory.createTitledBorder("RGB"));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        rgbPanel.add(redLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        rgbPanel.add(redSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        rgbPanel.add(greenLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        rgbPanel.add(greenSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        rgbPanel.add(blueLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        rgbPanel.add(blueSlider, gbc);
        
        // HSB スライダー
        JPanel hsbPanel = new JPanel(new GridBagLayout());
        hsbPanel.setBorder(BorderFactory.createTitledBorder("HSB"));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        hsbPanel.add(hueLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hsbPanel.add(hueSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        hsbPanel.add(saturationLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hsbPanel.add(saturationSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        hsbPanel.add(brightnessLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hsbPanel.add(brightnessSlider, gbc);
        
        // 左パネルにRGBとHSBを配置
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0;
        leftPanel.add(rgbPanel, gbc);
        gbc.gridy = 1;
        leftPanel.add(hsbPanel, gbc);
        
        // 右パネル (プレビューと情報)
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        // プレビューエリア
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.add(colorPreview, BorderLayout.CENTER);
        
        // 情報パネル
        JPanel infoPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        infoPanel.add(hexLabel, gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        infoPanel.add(hexField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(colorNameLabel, gbc);
        
        gbc.gridy = 2;
        infoPanel.add(colorChooserButton, gbc);
        
        rightPanel.add(previewPanel, BorderLayout.CENTER);
        rightPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // 下パネル
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // 補色・類似色パネル
        JPanel colorRelationPanel = new JPanel(new BorderLayout());
        colorRelationPanel.add(complementaryPanel, BorderLayout.WEST);
        colorRelationPanel.add(analogousPanel, BorderLayout.CENTER);
        
        bottomPanel.add(presetPanel, BorderLayout.NORTH);
        bottomPanel.add(colorRelationPanel, BorderLayout.CENTER);
        bottomPanel.add(new JScrollPane(colorHistoryList), BorderLayout.SOUTH);
        
        // メインパネルに配置
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        // RGB スライダーのリスナー
        ChangeListener rgbListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating) {
                    updateColorFromRGB();
                }
            }
        };
        redSlider.addChangeListener(rgbListener);
        greenSlider.addChangeListener(rgbListener);
        blueSlider.addChangeListener(rgbListener);
        
        // HSB スライダーのリスナー
        ChangeListener hsbListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isUpdating) {
                    updateColorFromHSB();
                }
            }
        };
        hueSlider.addChangeListener(hsbListener);
        saturationSlider.addChangeListener(hsbListener);
        brightnessSlider.addChangeListener(hsbListener);
        
        // 16進数フィールドのリスナー
        hexField.addActionListener(e -> updateColorFromHex());
        
        // カラーチューザーボタン
        colorChooserButton.addActionListener(e -> showColorChooser());
        
        // 色履歴リスト
        colorHistoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Color selectedColor = colorHistoryList.getSelectedValue();
                if (selectedColor != null) {
                    setCurrentColor(selectedColor);
                }
            }
        });
    }
    
    private void setupPresetColors() {
        presetPanel.removeAll();
        for (Color color : presetColors) {
            JButton colorButton = new JButton();
            colorButton.setPreferredSize(new Dimension(30, 30));
            colorButton.setBackground(color);
            colorButton.setBorder(BorderFactory.createRaisedBevelBorder());
            colorButton.setOpaque(true);
            colorButton.addActionListener(e -> setCurrentColor(color));
            presetPanel.add(colorButton);
        }
    }
    
    private List<Color> createPresetColors() {
        List<Color> colors = new ArrayList<>();
        colors.add(Color.BLACK);
        colors.add(Color.DARK_GRAY);
        colors.add(Color.GRAY);
        colors.add(Color.LIGHT_GRAY);
        colors.add(Color.WHITE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.ORANGE);
        colors.add(Color.PINK);
        colors.add(Color.CYAN);
        colors.add(Color.MAGENTA);
        colors.add(new Color(128, 0, 0));    // Maroon
        colors.add(new Color(0, 128, 0));    // Dark Green
        colors.add(new Color(0, 0, 128));    // Navy
        return colors;
    }
    
    private void updateColorFromRGB() {
        int r = redSlider.getValue();
        int g = greenSlider.getValue();
        int b = blueSlider.getValue();
        
        Color color = new Color(r, g, b);
        setCurrentColor(color);
        addToHistory(color);
    }
    
    private void updateColorFromHSB() {
        float h = hueSlider.getValue() / 360.0f;
        float s = saturationSlider.getValue() / 100.0f;
        float br = brightnessSlider.getValue() / 100.0f;
        
        Color color = Color.getHSBColor(h, s, br);
        setCurrentColor(color);
        addToHistory(color);
    }
    
    private void updateColorFromHex() {
        try {
            String hex = hexField.getText().trim();
            if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }
            
            if (hex.length() == 6) {
                int rgb = Integer.parseInt(hex, 16);
                Color color = new Color(rgb);
                setCurrentColor(color);
                addToHistory(color);
            }
        } catch (NumberFormatException e) {
            // Invalid hex color, ignore
        }
    }
    
    private void showColorChooser() {
        Color currentColor = getCurrentColor();
        Color newColor = JColorChooser.showDialog(this, "Choose Color", currentColor);
        if (newColor != null) {
            setCurrentColor(newColor);
            addToHistory(newColor);
        }
    }
    
    private void setCurrentColor(Color color) {
        isUpdating = true;
        
        // RGB スライダーの更新
        redSlider.setValue(color.getRed());
        greenSlider.setValue(color.getGreen());
        blueSlider.setValue(color.getBlue());
        
        // HSB スライダーの更新
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        hueSlider.setValue(Math.round(hsb[0] * 360));
        saturationSlider.setValue(Math.round(hsb[1] * 100));
        brightnessSlider.setValue(Math.round(hsb[2] * 100));
        
        isUpdating = false;
        
        updateColor();
    }
    
    private Color getCurrentColor() {
        return new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
    }
    
    private void updateColor() {
        Color currentColor = getCurrentColor();
        
        // ラベルの更新
        redLabel.setText("Red: " + redSlider.getValue());
        greenLabel.setText("Green: " + greenSlider.getValue());
        blueLabel.setText("Blue: " + blueSlider.getValue());
        hueLabel.setText("Hue: " + hueSlider.getValue());
        saturationLabel.setText("Saturation: " + saturationSlider.getValue());
        brightnessLabel.setText("Brightness: " + brightnessSlider.getValue());
        
        // プレビューの更新
        colorPreview.setBackground(currentColor);
        
        // 16進数の更新
        String hex = String.format("#%02X%02X%02X", 
            currentColor.getRed(), 
            currentColor.getGreen(), 
            currentColor.getBlue());
        hexField.setText(hex);
        
        // 色名の更新
        colorNameLabel.setText("Color Name: " + getColorName(currentColor));
        
        // 補色・類似色の更新
        updateColorRelations(currentColor);
        
        // 再描画
        colorPreview.repaint();
    }
    
    private String getColorName(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        if (r == 0 && g == 0 && b == 0) return "Black";
        if (r == 255 && g == 255 && b == 255) return "White";
        if (r == 255 && g == 0 && b == 0) return "Red";
        if (r == 0 && g == 255 && b == 0) return "Green";
        if (r == 0 && g == 0 && b == 255) return "Blue";
        if (r == 255 && g == 255 && b == 0) return "Yellow";
        if (r == 255 && g == 0 && b == 255) return "Magenta";
        if (r == 0 && g == 255 && b == 255) return "Cyan";
        if (r == 255 && g == 165 && b == 0) return "Orange";
        if (r == 255 && g == 192 && b == 203) return "Pink";
        if (r > 200 && g > 200 && b > 200) return "Light Gray";
        if (r < 50 && g < 50 && b < 50) return "Dark Gray";
        if (Math.abs(r - g) < 30 && Math.abs(g - b) < 30) return "Gray";
        return "Custom";
    }
    
    private void updateColorRelations(Color color) {
        // 補色
        Color complementary = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        complementaryPanel.setBackground(complementary);
        
        // 類似色
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        analogousPanel.removeAll();
        
        for (int i = -1; i <= 1; i++) {
            float newHue = (hsb[0] + i * 30.0f / 360.0f) % 1.0f;
            if (newHue < 0) newHue += 1.0f;
            
            Color analogous = Color.getHSBColor(newHue, hsb[1], hsb[2]);
            JPanel analogousColor = new JPanel();
            analogousColor.setBackground(analogous);
            analogousColor.setPreferredSize(new Dimension(40, 40));
            analogousPanel.add(analogousColor);
        }
        
        analogousPanel.revalidate();
        analogousPanel.repaint();
    }
    
    private void addToHistory(Color color) {
        if (!historyModel.contains(color)) {
            historyModel.add(0, color);
            if (historyModel.size() > 10) {
                historyModel.remove(10);
            }
        }
    }
    
    private void setupWindow() {
        setTitle("Color Picker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    // カスタムセルレンダラー
    private class ColorListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Color) {
                Color color = (Color) value;
                setBackground(color);
                setText(String.format("RGB(%d, %d, %d)", color.getRed(), color.getGreen(), color.getBlue()));
                setForeground(getContrastColor(color));
            }
            
            return c;
        }
        
        private Color getContrastColor(Color color) {
            int luminance = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
            return luminance > 128 ? Color.BLACK : Color.WHITE;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new ColorPicker().setVisible(true);
        });
    }
}