/**
 * 第18章 基本課題2: レスポンシブGUI - 解答例
 * 
 * ユーザーの操作に応じてリアルタイムに変化するGUIアプリケーションの完全実装。
 * 高度なUIレスポンシブ機能を提供します。
 */

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResponsiveGUI extends JFrame {
    
    // カラーコントロール
    private JSlider redSlider;
    private JSlider greenSlider;
    private JSlider blueSlider;
    private JPanel colorPreview;
    private JLabel rgbLabel;
    private JLabel hexLabel;
    
    // 検索機能
    private JTextField searchField;
    private DefaultListModel<String> originalListModel;
    private DefaultListModel<String> filteredListModel;
    private JList<String> dataList;
    private JLabel searchResultLabel;
    
    // プログレス機能
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private Timer progressTimer;
    private boolean progressIncreasing = true;
    
    // レイアウト制御
    private JPanel dynamicPanel;
    private boolean isHorizontalLayout = true;
    private JButton layoutToggleButton;
    
    // 統計情報
    private JLabel interactionCountLabel;
    private int interactionCount = 0;
    
    public ResponsiveGUI() {
        setTitle("レスポンシブGUI - リアルタイム応答システム");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupProgressTimer();
        
        updateColor(128, 128, 128); // 初期色設定
        filterList(""); // 初期リスト表示
    }
    
    /**
     * UIコンポーネントを初期化します
     */
    private void initializeComponents() {
        // カラースライダーの初期化
        redSlider = new JSlider(0, 255, 128);
        greenSlider = new JSlider(0, 255, 128);
        blueSlider = new JSlider(0, 255, 128);
        
        redSlider.setMajorTickSpacing(51);
        redSlider.setMinorTickSpacing(17);
        redSlider.setPaintTicks(true);
        redSlider.setPaintLabels(true);
        
        greenSlider.setMajorTickSpacing(51);
        greenSlider.setMinorTickSpacing(17);
        greenSlider.setPaintTicks(true);
        greenSlider.setPaintLabels(true);
        
        blueSlider.setMajorTickSpacing(51);
        blueSlider.setMinorTickSpacing(17);
        blueSlider.setPaintTicks(true);
        blueSlider.setPaintLabels(true);
        
        // カラープレビューパネル
        colorPreview = new JPanel();
        colorPreview.setPreferredSize(new Dimension(200, 100));
        colorPreview.setBorder(BorderFactory.createTitledBorder("カラープレビュー"));
        
        // カラー情報ラベル
        rgbLabel = new JLabel("RGB(128, 128, 128)");
        rgbLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        hexLabel = new JLabel("#808080");
        hexLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        
        // 検索フィールド
        searchField = new JTextField("検索キーワードを入力...", 20);
        searchField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        // データリスト
        originalListModel = new DefaultListModel<>();
        filteredListModel = new DefaultListModel<>();
        dataList = new JList<>(filteredListModel);
        dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        // サンプルデータの生成
        List<String> sampleData = generateSampleData();
        for (String item : sampleData) {
            originalListModel.addElement(item);
        }
        
        searchResultLabel = new JLabel("検索結果: 0件");
        
        // プログレスバー
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("進行状況: 0%");
        progressLabel = new JLabel("自動プログレス実行中...");
        
        // レイアウト切り替えボタン
        layoutToggleButton = new JButton("レイアウト切り替え (横→縦)");
        layoutToggleButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        // 動的パネル
        dynamicPanel = new JPanel();
        dynamicPanel.setBorder(BorderFactory.createTitledBorder("動的レイアウトパネル"));
        setupDynamicPanelContent();
        
        // 統計ラベル
        interactionCountLabel = new JLabel("操作回数: 0");
        interactionCountLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
    }
    
    /**
     * レイアウトを設定します
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 左パネル（カラーコントロール）
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setBorder(BorderFactory.createTitledBorder("カラーコントロール"));
        
        JPanel sliderPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        // 赤スライダー
        JPanel redPanel = new JPanel(new BorderLayout());
        redPanel.add(new JLabel("Red (0-255)", JLabel.CENTER), BorderLayout.NORTH);
        redPanel.add(redSlider, BorderLayout.CENTER);
        sliderPanel.add(redPanel);
        
        // 緑スライダー
        JPanel greenPanel = new JPanel(new BorderLayout());
        greenPanel.add(new JLabel("Green (0-255)", JLabel.CENTER), BorderLayout.NORTH);
        greenPanel.add(greenSlider, BorderLayout.CENTER);
        sliderPanel.add(greenPanel);
        
        // 青スライダー
        JPanel bluePanel = new JPanel(new BorderLayout());
        bluePanel.add(new JLabel("Blue (0-255)", JLabel.CENTER), BorderLayout.NORTH);
        bluePanel.add(blueSlider, BorderLayout.CENTER);
        sliderPanel.add(bluePanel);
        
        leftPanel.add(sliderPanel, BorderLayout.CENTER);
        
        // カラー情報パネル
        JPanel colorInfoPanel = new JPanel(new BorderLayout());
        colorInfoPanel.add(colorPreview, BorderLayout.CENTER);
        
        JPanel labelPanel = new JPanel(new GridLayout(2, 1));
        labelPanel.add(rgbLabel);
        labelPanel.add(hexLabel);
        colorInfoPanel.add(labelPanel, BorderLayout.SOUTH);
        
        leftPanel.add(colorInfoPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        
        // 中央パネル（検索機能）
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("リアルタイム検索"));
        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel("検索:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchResultLabel, BorderLayout.SOUTH);
        
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(dataList), BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // 右パネル（プログレスとレイアウト制御）
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(250, 0));
        
        // プログレスパネル
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createTitledBorder("自動プログレス"));
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        // レイアウトコントロールパネル
        JPanel layoutPanel = new JPanel(new BorderLayout());
        layoutPanel.setBorder(BorderFactory.createTitledBorder("レイアウト制御"));
        layoutPanel.add(layoutToggleButton, BorderLayout.NORTH);
        layoutPanel.add(dynamicPanel, BorderLayout.CENTER);
        
        rightPanel.add(progressPanel, BorderLayout.NORTH);
        rightPanel.add(layoutPanel, BorderLayout.CENTER);
        
        add(rightPanel, BorderLayout.EAST);
        
        // 下部ステータスパネル
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusPanel.add(interactionCountLabel);
        statusPanel.add(new JLabel(" | "));
        statusPanel.add(new JLabel("リアルタイム応答システム稼働中"));
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    /**
     * イベントリスナーを設定します
     */
    private void setupEventListeners() {
        setupColorSliders();
        setupSearchFunction();
        setupDynamicLayout();
        setupListSelection();
        setupSearchFieldFocus();
    }
    
    /**
     * カラースライダーのリスナーを設定します
     */
    private void setupColorSliders() {
        ChangeListener colorChangeListener = e -> {
            int r = redSlider.getValue();
            int g = greenSlider.getValue();
            int b = blueSlider.getValue();
            updateColor(r, g, b);
            incrementInteractionCount();
        };
        
        redSlider.addChangeListener(colorChangeListener);
        greenSlider.addChangeListener(colorChangeListener);
        blueSlider.addChangeListener(colorChangeListener);
    }
    
    /**
     * 検索機能を設定します
     */
    private void setupSearchFunction() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterList(searchField.getText());
                incrementInteractionCount();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                filterList(searchField.getText());
                incrementInteractionCount();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                filterList(searchField.getText());
            }
        });
    }
    
    /**
     * 検索フィールドのフォーカス機能を設定します
     */
    private void setupSearchFieldFocus() {
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("検索キーワードを入力...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("検索キーワードを入力...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        
        // 初期表示設定
        searchField.setForeground(Color.GRAY);
    }
    
    /**
     * リスト選択機能を設定します
     */
    private void setupListSelection() {
        dataList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = dataList.getSelectedValue();
                if (selected != null) {
                    JOptionPane.showMessageDialog(this, 
                        "選択されたアイテム: " + selected, 
                        "選択確認", JOptionPane.INFORMATION_MESSAGE);
                    incrementInteractionCount();
                }
            }
        });
    }
    
    /**
     * プログレスバーとタイマーを設定します
     */
    private void setupProgressTimer() {
        progressTimer = new Timer(50, e -> {
            int currentValue = progressBar.getValue();
            
            if (progressIncreasing) {
                if (currentValue < 100) {
                    currentValue++;
                } else {
                    progressIncreasing = false;
                    currentValue--;
                }
            } else {
                if (currentValue > 0) {
                    currentValue--;
                } else {
                    progressIncreasing = true;
                    currentValue++;
                }
            }
            
            updateProgress(currentValue);
        });
        
        progressTimer.start();
    }
    
    /**
     * 動的レイアウト変更機能を実装します
     */
    private void setupDynamicLayout() {
        layoutToggleButton.addActionListener(e -> {
            toggleDynamicPanelLayout();
            incrementInteractionCount();
        });
    }
    
    /**
     * 動的パネルのコンテンツを設定します
     */
    private void setupDynamicPanelContent() {
        dynamicPanel.setLayout(new BoxLayout(dynamicPanel, BoxLayout.X_AXIS));
        
        for (int i = 1; i <= 4; i++) {
            JButton button = new JButton("Item " + i);
            button.setPreferredSize(new Dimension(60, 30));
            button.addActionListener(e -> incrementInteractionCount());
            dynamicPanel.add(button);
            if (i < 4) {
                dynamicPanel.add(Box.createHorizontalStrut(5));
            }
        }
    }
    
    /**
     * 動的パネルのレイアウトを切り替えます
     */
    private void toggleDynamicPanelLayout() {
        dynamicPanel.removeAll();
        
        if (isHorizontalLayout) {
            dynamicPanel.setLayout(new BoxLayout(dynamicPanel, BoxLayout.Y_AXIS));
            layoutToggleButton.setText("レイアウト切り替え (縦→横)");
            
            for (int i = 1; i <= 4; i++) {
                JButton button = new JButton("Item " + i);
                button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                button.addActionListener(e -> incrementInteractionCount());
                dynamicPanel.add(button);
                if (i < 4) {
                    dynamicPanel.add(Box.createVerticalStrut(5));
                }
            }
        } else {
            dynamicPanel.setLayout(new BoxLayout(dynamicPanel, BoxLayout.X_AXIS));
            layoutToggleButton.setText("レイアウト切り替え (横→縦)");
            
            for (int i = 1; i <= 4; i++) {
                JButton button = new JButton("Item " + i);
                button.setPreferredSize(new Dimension(60, 30));
                button.addActionListener(e -> incrementInteractionCount());
                dynamicPanel.add(button);
                if (i < 4) {
                    dynamicPanel.add(Box.createHorizontalStrut(5));
                }
            }
        }
        
        isHorizontalLayout = !isHorizontalLayout;
        dynamicPanel.revalidate();
        dynamicPanel.repaint();
    }
    
    /**
     * 色を更新します
     */
    private void updateColor(int r, int g, int b) {
        Color newColor = new Color(r, g, b);
        colorPreview.setBackground(newColor);
        
        // RGB表示更新
        rgbLabel.setText(String.format("RGB(%d, %d, %d)", r, g, b));
        
        // HEX表示更新
        String hex = String.format("#%02X%02X%02X", r, g, b);
        hexLabel.setText(hex);
        
        // 明度に応じて文字色を調整
        int brightness = (int) (0.299 * r + 0.587 * g + 0.114 * b);
        Color textColor = brightness > 128 ? Color.BLACK : Color.WHITE;
        rgbLabel.setForeground(textColor);
        hexLabel.setForeground(textColor);
        
        colorPreview.repaint();
    }
    
    /**
     * リストをフィルタリングします
     */
    private void filterList(String searchText) {
        filteredListModel.clear();
        
        if (searchText.trim().isEmpty() || searchText.equals("検索キーワードを入力...")) {
            // 全件表示
            for (int i = 0; i < originalListModel.getSize(); i++) {
                filteredListModel.addElement(originalListModel.getElementAt(i));
            }
        } else {
            // フィルタリング実行
            String lowerSearchText = searchText.toLowerCase();
            for (int i = 0; i < originalListModel.getSize(); i++) {
                String item = originalListModel.getElementAt(i);
                if (item.toLowerCase().contains(lowerSearchText)) {
                    filteredListModel.addElement(item);
                }
            }
        }
        
        searchResultLabel.setText("検索結果: " + filteredListModel.getSize() + "件");
    }
    
    /**
     * プログレスバーを更新します
     */
    private void updateProgress(int value) {
        progressBar.setValue(value);
        progressBar.setString("進行状況: " + value + "%");
        
        // プログレス状態に応じて色を変更
        if (value <= 33) {
            progressBar.setForeground(Color.RED);
        } else if (value <= 66) {
            progressBar.setForeground(Color.ORANGE);
        } else {
            progressBar.setForeground(Color.GREEN);
        }
    }
    
    /**
     * 操作回数をカウントします
     */
    private void incrementInteractionCount() {
        interactionCount++;
        interactionCountLabel.setText("操作回数: " + interactionCount);
    }
    
    /**
     * サンプルデータを生成します
     */
    private List<String> generateSampleData() {
        List<String> data = new ArrayList<>();
        
        // 都市名
        String[] cities = {"東京", "大阪", "名古屋", "横浜", "神戸", "京都", "福岡", "札幌", "仙台", "広島"};
        for (String city : cities) {
            data.add("都市: " + city);
        }
        
        // 動物名
        String[] animals = {"犬", "猫", "鳥", "魚", "馬", "牛", "豚", "羊", "鶏", "兎"};
        for (String animal : animals) {
            data.add("動物: " + animal);
        }
        
        // 色名
        String[] colors = {"赤", "青", "緑", "黄", "紫", "橙", "白", "黒", "灰", "桃"};
        for (String color : colors) {
            data.add("色: " + color);
        }
        
        // 果物名
        String[] fruits = {"りんご", "みかん", "バナナ", "ぶどう", "いちご", "メロン", "スイカ", "桃", "梨", "柿"};
        for (String fruit : fruits) {
            data.add("果物: " + fruit);
        }
        
        return data;
    }
    
    @Override
    public void dispose() {
        if (progressTimer != null) {
            progressTimer.stop();
        }
        super.dispose();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ResponsiveGUI().setVisible(true);
        });
    }
}