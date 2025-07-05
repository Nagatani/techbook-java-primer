/**
 * 第18章 基本課題3: イベントハンドリングの基礎 - 解答例
 * 
 * 様々な種類のイベントを処理するGUIアプリケーションの完全実装。
 * 各種イベントの詳細情報をログに表示し、イベント駆動プログラミングを学習します。
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class EventHandlingBasics extends JFrame {
    
    private JTextArea logArea;
    private JLabel statusLabel;
    private JTextField textField;
    private JButton actionButton;
    private JButton clearButton;
    private JLabel mouseLabel;
    private JPanel colorPanel;
    private Timer autoSaveTimer;
    private int eventCount = 0;
    
    public EventHandlingBasics() {
        setTitle("イベントハンドリングの基礎");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupAutoSave();
        
        logEvent("システム", "アプリケーションが開始されました");
    }
    
    /**
     * UIコンポーネントを初期化します
     */
    private void initializeComponents() {
        // ログ表示用のテキストエリア
        logArea = new JTextArea(20, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setBackground(new Color(248, 248, 248));
        
        // ステータスラベル
        statusLabel = new JLabel("イベント数: 0 | 準備完了");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // テキスト入力フィールド
        textField = new JTextField("ここに入力してください...", 20);
        textField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        // ボタン群
        actionButton = new JButton("アクションボタン");
        actionButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        actionButton.setBackground(new Color(70, 130, 180));
        actionButton.setForeground(Color.WHITE);
        
        clearButton = new JButton("ログクリア");
        clearButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        clearButton.setBackground(new Color(220, 20, 60));
        clearButton.setForeground(Color.WHITE);
        
        // マウス位置表示ラベル
        mouseLabel = new JLabel("マウス位置: (0, 0)");
        mouseLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // 色変更パネル
        colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(100, 100));
        colorPanel.setBackground(Color.LIGHT_GRAY);
        colorPanel.setBorder(BorderFactory.createTitledBorder("カラーパネル"));
    }
    
    /**
     * レイアウトを設定します
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // メインログエリア
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
        // 上部のコントロールパネル
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JLabel("テキスト入力:"));
        buttonPanel.add(textField);
        buttonPanel.add(actionButton);
        buttonPanel.add(clearButton);
        
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        topPanel.add(mouseLabel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 右側のイベントテストパネル
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(150, 0));
        rightPanel.setBorder(BorderFactory.createTitledBorder("イベントテスト"));
        
        // 各種テストボタン
        JPanel testButtonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        
        JButton mouseTestButton = new JButton("マウステスト");
        JButton keyTestButton = new JButton("キーテスト");
        JButton focusTestButton = new JButton("フォーカステスト");
        JButton doubleClickButton = new JButton("ダブルクリック");
        JButton rightClickButton = new JButton("右クリック");
        
        testButtonPanel.add(mouseTestButton);
        testButtonPanel.add(keyTestButton);
        testButtonPanel.add(focusTestButton);
        testButtonPanel.add(doubleClickButton);
        testButtonPanel.add(rightClickButton);
        
        rightPanel.add(testButtonPanel, BorderLayout.NORTH);
        rightPanel.add(colorPanel, BorderLayout.CENTER);
        
        add(rightPanel, BorderLayout.EAST);
        
        // ステータスバー
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    /**
     * イベントリスナーを設定します
     */
    private void setupEventListeners() {
        setupActionListeners();
        setupMouseListeners();
        setupKeyListeners();
        setupFocusListeners();
        setupWindowListeners();
        setupComponentListeners();
    }
    
    /**
     * ActionListenerを実装します
     */
    private void setupActionListeners() {
        // メインアクションボタン
        actionButton.addActionListener(e -> {
            logEvent("ActionEvent", "メインボタンがクリックされました");
            changeColorRandomly();
        });
        
        // クリアボタン
        clearButton.addActionListener(e -> {
            clearLog();
            logEvent("ActionEvent", "ログがクリアされました");
        });
        
        // テキストフィールドのアクション（Enterキー）
        textField.addActionListener(e -> {
            String text = textField.getText();
            logEvent("ActionEvent", "テキストフィールドでEnterが押されました: \"" + text + "\"");
            textField.selectAll();
        });
    }
    
    /**
     * MouseListenerを実装します
     */
    private void setupMouseListeners() {
        // フレーム全体でのマウス移動追跡
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseLabel.setText("マウス位置: (" + e.getX() + ", " + e.getY() + ")");
            }
        });
        
        // カラーパネルのマウスイベント
        colorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String button = getMouseButtonName(e.getButton());
                String clickType = e.getClickCount() == 2 ? "ダブルクリック" : "クリック";
                logEvent("MouseEvent", String.format("カラーパネル %s (%s) - 位置: (%d, %d)", 
                    clickType, button, e.getX(), e.getY()));
                
                if (e.getButton() == MouseEvent.BUTTON1) {
                    changeColorRandomly();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                logEvent("MouseEvent", "カラーパネル マウスプレス: " + getMouseButtonName(e.getButton()));
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                logEvent("MouseEvent", "カラーパネル マウスリリース: " + getMouseButtonName(e.getButton()));
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                logEvent("MouseEvent", "カラーパネルにマウスエンター");
                colorPanel.setBorder(BorderFactory.createTitledBorder("カラーパネル [HOVER]"));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                logEvent("MouseEvent", "カラーパネルからマウスエグジット");
                colorPanel.setBorder(BorderFactory.createTitledBorder("カラーパネル"));
            }
        });
        
        // ボタンのマウスホバー効果
        actionButton.addMouseListener(new MouseAdapter() {
            private Color originalColor = actionButton.getBackground();
            
            @Override
            public void mouseEntered(MouseEvent e) {
                actionButton.setBackground(originalColor.brighter());
                logEvent("MouseEvent", "アクションボタンにマウスエンター");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                actionButton.setBackground(originalColor);
                logEvent("MouseEvent", "アクションボタンからマウスエグジット");
            }
        });
    }
    
    /**
     * KeyListenerを実装します
     */
    private void setupKeyListeners() {
        // テキストフィールドのキーイベント
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String keyText = KeyEvent.getKeyText(e.getKeyCode());
                String modifiers = getKeyModifiers(e);
                logEvent("KeyEvent", String.format("キープレス: %s%s (コード: %d)", 
                    modifiers, keyText, e.getKeyCode()));
                
                // 特殊キーの処理
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    textField.setText("");
                    logEvent("KeyEvent", "ESCキーでテキストフィールドをクリア");
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                String keyText = KeyEvent.getKeyText(e.getKeyCode());
                logEvent("KeyEvent", "キーリリース: " + keyText);
            }
            
            @Override
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();
                if (keyChar >= 32 && keyChar <= 126) { // 印刷可能文字のみ
                    logEvent("KeyEvent", "キータイプ: '" + keyChar + "'");
                }
            }
        });
        
        // フレーム全体でのキーイベント（フォーカスが必要）
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F1) {
                    showHelpDialog();
                }
            }
        });
    }
    
    /**
     * FocusListenerを実装します
     */
    private void setupFocusListeners() {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                logEvent("FocusEvent", "テキストフィールドがフォーカス取得");
                textField.setBackground(new Color(255, 255, 200));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                logEvent("FocusEvent", "テキストフィールドがフォーカス失去");
                textField.setBackground(Color.WHITE);
            }
        });
        
        actionButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                logEvent("FocusEvent", "アクションボタンがフォーカス取得");
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                logEvent("FocusEvent", "アクションボタンがフォーカス失去");
            }
        });
    }
    
    /**
     * WindowListenerを実装します
     */
    private void setupWindowListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                logEvent("WindowEvent", "ウィンドウがアクティブになりました");
            }
            
            @Override
            public void windowDeactivated(WindowEvent e) {
                logEvent("WindowEvent", "ウィンドウが非アクティブになりました");
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                logEvent("WindowEvent", "ウィンドウが閉じられています");
            }
        });
    }
    
    /**
     * ComponentListenerを実装します
     */
    private void setupComponentListeners() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = getSize();
                logEvent("ComponentEvent", String.format("ウィンドウサイズ変更: %dx%d", 
                    size.width, size.height));
            }
            
            @Override
            public void componentMoved(ComponentEvent e) {
                Point location = getLocation();
                logEvent("ComponentEvent", String.format("ウィンドウ移動: (%d, %d)", 
                    location.x, location.y));
            }
        });
    }
    
    /**
     * 自動保存機能を設定します
     */
    private void setupAutoSave() {
        autoSaveTimer = new Timer();
        autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    logEvent("TimerEvent", "自動保存実行（30秒間隔）");
                });
            }
        }, 30000, 30000); // 30秒間隔
    }
    
    /**
     * イベントログに情報を追加します
     */
    private void logEvent(String eventType, String details) {
        String timestamp = getCurrentTime();
        String logEntry = String.format("[%s] %s: %s\n", timestamp, eventType, details);
        
        SwingUtilities.invokeLater(() -> {
            logArea.append(logEntry);
            logArea.setCaretPosition(logArea.getDocument().getLength());
            
            eventCount++;
            statusLabel.setText(String.format("イベント数: %d | 最新: %s", eventCount, eventType));
        });
    }
    
    /**
     * 現在時刻を文字列として取得します
     */
    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    /**
     * ログをクリアします
     */
    private void clearLog() {
        logArea.setText("");
        eventCount = 0;
        statusLabel.setText("イベント数: 0 | ログクリア完了");
    }
    
    /**
     * マウスボタン名を取得します
     */
    private String getMouseButtonName(int button) {
        switch (button) {
            case MouseEvent.BUTTON1: return "左ボタン";
            case MouseEvent.BUTTON2: return "中ボタン";
            case MouseEvent.BUTTON3: return "右ボタン";
            default: return "不明ボタン";
        }
    }
    
    /**
     * キー修飾子を取得します
     */
    private String getKeyModifiers(KeyEvent e) {
        StringBuilder modifiers = new StringBuilder();
        
        if (e.isControlDown()) modifiers.append("Ctrl+");
        if (e.isAltDown()) modifiers.append("Alt+");
        if (e.isShiftDown()) modifiers.append("Shift+");
        if (e.isMetaDown()) modifiers.append("Meta+");
        
        return modifiers.toString();
    }
    
    /**
     * カラーパネルの色をランダムに変更します
     */
    private void changeColorRandomly() {
        Color randomColor = new Color(
            (int) (Math.random() * 256),
            (int) (Math.random() * 256),
            (int) (Math.random() * 256)
        );
        colorPanel.setBackground(randomColor);
        logEvent("ColorChange", String.format("カラーパネル色変更: RGB(%d,%d,%d)", 
            randomColor.getRed(), randomColor.getGreen(), randomColor.getBlue()));
    }
    
    /**
     * ヘルプダイアログを表示します
     */
    private void showHelpDialog() {
        String helpText = 
            "イベントハンドリング基礎 - 操作方法\n\n" +
            "• テキストフィールド: 文字入力とEnterキーを試してください\n" +
            "• アクションボタン: クリックで色変更\n" +
            "• カラーパネル: マウスクリックやホバーを試してください\n" +
            "• ログクリア: ログをクリアします\n" +
            "• ESCキー: テキストフィールドをクリア\n" +
            "• F1キー: このヘルプを表示\n" +
            "• ウィンドウリサイズ・移動も検出されます\n\n" +
            "すべてのイベントがログに記録されます。";
        
        JOptionPane.showMessageDialog(this, helpText, "ヘルプ", JOptionPane.INFORMATION_MESSAGE);
        logEvent("HelpEvent", "ヘルプダイアログを表示");
    }
    
    /**
     * アプリケーション終了時の処理
     */
    private void cleanup() {
        if (autoSaveTimer != null) {
            autoSaveTimer.cancel();
        }
        logEvent("システム", "アプリケーションを終了します");
    }
    
    @Override
    public void dispose() {
        cleanup();
        super.dispose();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new EventHandlingBasics().setVisible(true);
        });
    }
}