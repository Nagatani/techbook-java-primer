package chapter23.solutions;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 国際化対応メッセージ表示アプリケーション
 * 設定ファイルとリソースバンドルを使用して多言語対応を実現
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 */
public class MessageApp extends JFrame {
    private Properties config;
    private ResourceBundle messages;
    private JLabel messageLabel;
    private JComboBox<String> languageCombo;
    
    /**
     * アプリケーションのエントリーポイント
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // デフォルトのLook and Feelを使用
            }
            
            MessageApp app = new MessageApp();
            app.setVisible(true);
        });
    }
    
    /**
     * コンストラクタ
     */
    public MessageApp() {
        super();
        loadConfiguration();
        loadMessages();
        initializeUI();
    }
    
    /**
     * 設定ファイルを読み込む
     */
    private void loadConfiguration() {
        config = new Properties();
        
        try (InputStream is = getClass().getResourceAsStream("/resources/config.properties")) {
            if (is == null) {
                // リソースが見つからない場合のデフォルト設定
                System.err.println("警告: config.propertiesが見つかりません。デフォルト設定を使用します。");
                config.setProperty("app.title", "Message Viewer");
                config.setProperty("app.version", "1.0.0");
                config.setProperty("app.language", "ja");
            } else {
                config.load(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // エラーが発生した場合のデフォルト設定
            config.setProperty("app.title", "Message Viewer");
            config.setProperty("app.version", "1.0.0");
            config.setProperty("app.language", "ja");
        }
    }
    
    /**
     * 選択された言語のメッセージを読み込む
     */
    private void loadMessages() {
        String language = config.getProperty("app.language", "ja");
        loadMessagesForLanguage(language);
    }
    
    /**
     * 指定された言語のメッセージを読み込む
     * 
     * @param language 言語コード（ja, en等）
     */
    private void loadMessagesForLanguage(String language) {
        try {
            Locale locale = new Locale(language);
            messages = ResourceBundle.getBundle("resources.messages", locale);
        } catch (MissingResourceException e) {
            System.err.println("警告: " + language + "のメッセージリソースが見つかりません。");
            // フォールバック: ハードコードされたメッセージを使用
            messages = new ResourceBundle() {
                private final Map<String, String> map = new HashMap<>();
                {
                    map.put("welcome.message", "ようこそ！");
                    map.put("select.language", "言語を選択:");
                    map.put("current.time", "現在時刻:");
                    map.put("about.message", "Message Viewer v1.0\n国際化対応サンプルアプリケーション");
                }
                
                @Override
                protected Object handleGetObject(String key) {
                    return map.get(key);
                }
                
                @Override
                public Enumeration<String> getKeys() {
                    return Collections.enumeration(map.keySet());
                }
            };
        }
    }
    
    /**
     * UIを初期化する
     */
    private void initializeUI() {
        // ウィンドウの基本設定
        setTitle(config.getProperty("app.title", "Message Viewer") + 
                " v" + config.getProperty("app.version", "1.0.0"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // アイコンの設定
        loadAndSetIcon();
        
        // コンポーネントの作成
        createComponents();
        
        // ウィンドウサイズと位置の設定
        setSize(500, 300);
        setLocationRelativeTo(null); // 画面中央に配置
    }
    
    /**
     * アイコンを読み込んで設定する
     */
    private void loadAndSetIcon() {
        try {
            URL iconURL = getClass().getResource("/resources/icon.png");
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                setIconImage(icon.getImage());
            } else {
                System.err.println("警告: icon.pngが見つかりません。");
                // デフォルトアイコンを作成
                createDefaultIcon();
            }
        } catch (Exception e) {
            e.printStackTrace();
            createDefaultIcon();
        }
    }
    
    /**
     * デフォルトアイコンを作成する
     */
    private void createDefaultIcon() {
        // プログラムで簡単なアイコンを生成
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 背景
        g2d.setColor(new Color(100, 150, 200));
        g2d.fillOval(2, 2, 28, 28);
        
        // テキスト
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("M", 10, 22);
        
        g2d.dispose();
        setIconImage(icon);
    }
    
    /**
     * UIコンポーネントを作成する
     */
    private void createComponents() {
        // 上部パネル（言語選択）
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel langLabel = new JLabel(messages.getString("select.language"));
        topPanel.add(langLabel);
        
        String[] languages = {"日本語", "English"};
        languageCombo = new JComboBox<>(languages);
        String currentLang = config.getProperty("app.language", "ja");
        languageCombo.setSelectedIndex(currentLang.equals("ja") ? 0 : 1);
        languageCombo.addActionListener(e -> changeLanguage());
        topPanel.add(languageCombo);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 中央パネル（メッセージ表示）
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        messageLabel = new JLabel(messages.getString("welcome.message"));
        messageLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        centerPanel.add(messageLabel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // 下部パネル（現在時刻と情報）
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel timeLabel = new JLabel(messages.getString("current.time") + " " + 
                                    new Date().toString());
        bottomPanel.add(timeLabel, BorderLayout.WEST);
        
        JButton aboutButton = new JButton("About");
        aboutButton.addActionListener(e -> showAbout());
        bottomPanel.add(aboutButton, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // タイマーで時刻を更新
        Timer timer = new Timer(1000, e -> {
            timeLabel.setText(messages.getString("current.time") + " " + 
                            new Date().toString());
        });
        timer.start();
    }
    
    /**
     * 言語を変更する
     */
    private void changeLanguage() {
        String selectedLang = languageCombo.getSelectedIndex() == 0 ? "ja" : "en";
        config.setProperty("app.language", selectedLang);
        
        // メッセージを再読み込み
        loadMessagesForLanguage(selectedLang);
        
        // UIを更新
        updateUI();
    }
    
    /**
     * UIのテキストを更新する
     */
    private void updateUI() {
        // 新しいウィンドウを作成して置き換える方が簡単
        Point location = getLocation();
        Dimension size = getSize();
        
        dispose();
        
        MessageApp newApp = new MessageApp();
        newApp.setLocation(location);
        newApp.setSize(size);
        newApp.setVisible(true);
    }
    
    /**
     * Aboutダイアログを表示する
     */
    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            messages.getString("about.message"),
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
}