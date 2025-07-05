/**
 * 第18章 GUI イベント処理応用
 * 演習課題: MenuSystem.java
 * 
 * 【課題概要】
 * 多層メニューシステムと動的メニュー生成機能を持つアプリケーションを作成してください。
 * 
 * 【要求仕様】
 * 1. 階層的なメニュー構造（メニュー、サブメニュー、サブサブメニュー）
 * 2. 動的メニュー項目の追加・削除・変更機能
 * 3. カスタムメニューアイテム（アイコン、カスタム描画）
 * 4. コンテキストメニュー（右クリックメニュー）の実装
 * 5. ツールバーとメニューバーの連携
 * 6. メニュー項目の有効/無効制御
 * 7. 最近使用したファイル（MRU: Most Recently Used）メニュー
 * 8. カスタマイズ可能なメニュー配置
 * 9. メニューアクションの履歴管理
 * 10. キーボードショートカットの動的割り当て
 * 
 * 【学習ポイント】
 * - JMenuBar、JMenu、JMenuItem の階層構造
 * - Action クラスによる統一的なアクション管理
 * - PopupMenu の作成と表示制御
 * - MenuListener によるメニューイベント処理
 * - カスタムメニューアイテムの描画
 * - KeyStroke による動的キーバインディング
 * - MVC パターンによるメニューデータ管理
 * 
 * 【実装のヒント】
 * 1. Action クラスでアクションの統一管理
 * 2. MenuListener でメニューの表示/非表示イベントを処理
 * 3. SwingUtilities.isRightMouseButton() で右クリック判定
 * 4. DefaultListModel でMRUリストを管理
 * 5. Properties ファイルでメニュー設定を永続化
 * 
 * 【よくある間違いと対策】
 * - メニュー項目の重複管理
 *   → Action クラスで一元管理し、複数箇所で使い回し
 * - メモリリークによるメニュー項目の蓄積
 *   → 動的メニューの適切な削除と参照管理
 * - キーボードショートカットの重複
 *   → 一元的なキーマネージャーで競合チェック
 * - メニューの状態同期不良
 *   → Observer パターンでの統一的な状態管理
 * 
 * 【段階的な実装指針】
 * 1. 基本的なメニューバー構造の作成
 * 2. Action クラスによるアクション管理の実装
 * 3. 動的メニュー操作機能の追加
 * 4. コンテキストメニューの実装
 * 5. MRUメニューの実装
 * 6. カスタムメニューアイテムの追加
 * 7. メニューカスタマイズ機能の実装
 * 8. 設定の永続化と復元機能の追加
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

public class MenuSystem extends JFrame {
    // メニュー管理
    private JMenuBar menuBar;
    private Map<String, Action> actions;
    private List<String> recentFiles;
    private JMenu recentMenu;
    private Properties menuConfig;
    private Preferences preferences;
    
    // UI コンポーネント
    private JToolBar toolBar;
    private JTextArea contentArea;
    private JLabel statusLabel;
    private JPanel dynamicMenuPanel;
    private DefaultListModel<String> menuHistoryModel;
    private JList<String> menuHistoryList;
    
    // メニューカスタマイズ
    private MenuCustomizer menuCustomizer;
    private boolean menuBarVisible = true;
    private boolean toolBarVisible = true;
    
    public MenuSystem() {
        actions = new HashMap<>();
        recentFiles = new ArrayList<>();
        menuConfig = new Properties();
        preferences = Preferences.userNodeForPackage(MenuSystem.class);
        menuHistoryModel = new DefaultListModel<>();
        
        initializeActions();
        initializeComponents();
        setupMenuBar();
        setupToolBar();
        setupLayout();
        setupContextMenus();
        setupEventHandlers();
        setupWindow();
        
        loadConfiguration();
        loadRecentFiles();
    }
    
    private void initializeActions() {
        // ファイルアクション
        actions.put("new", new MenuAction("新規作成", "新しいドキュメントを作成", 
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),
            loadIcon("new.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFile();
                addToHistory("新規作成");
            }
        });
        
        actions.put("open", new MenuAction("開く", "ファイルを開く", 
            KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK),
            loadIcon("open.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
                addToHistory("ファイルを開く");
            }
        });
        
        actions.put("save", new MenuAction("保存", "ファイルを保存", 
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK),
            loadIcon("save.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
                addToHistory("ファイルを保存");
            }
        });
        
        actions.put("exit", new MenuAction("終了", "アプリケーションを終了", 
            KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK),
            null) {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitApplication();
            }
        });
        
        // 編集アクション
        actions.put("cut", new MenuAction("切り取り", "選択したテキストを切り取り", 
            KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK),
            loadIcon("cut.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (contentArea.getSelectedText() != null) {
                    contentArea.cut();
                    addToHistory("切り取り");
                }
            }
        });
        
        actions.put("copy", new MenuAction("コピー", "選択したテキストをコピー", 
            KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK),
            loadIcon("copy.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (contentArea.getSelectedText() != null) {
                    contentArea.copy();
                    addToHistory("コピー");
                }
            }
        });
        
        actions.put("paste", new MenuAction("貼り付け", "クリップボードから貼り付け", 
            KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK),
            loadIcon("paste.png")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentArea.paste();
                addToHistory("貼り付け");
            }
        });
        
        // 表示アクション
        actions.put("toggleMenuBar", new MenuAction("メニューバー表示切替", "メニューバーの表示/非表示", 
            KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK),
            null) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMenuBar();
                addToHistory("メニューバー表示切替");
            }
        });
        
        actions.put("toggleToolBar", new MenuAction("ツールバー表示切替", "ツールバーの表示/非表示", 
            KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK),
            null) {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleToolBar();
                addToHistory("ツールバー表示切替");
            }
        });
        
        // カスタムアクション
        actions.put("customize", new MenuAction("メニューカスタマイズ", "メニューをカスタマイズ", 
            null, null) {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMenuCustomizer();
                addToHistory("メニューカスタマイズ");
            }
        });
        
        actions.put("addDynamicMenu", new MenuAction("動的メニュー追加", "新しいメニュー項目を追加", 
            null, null) {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDynamicMenuItem();
                addToHistory("動的メニュー追加");
            }
        });
    }
    
    private void initializeComponents() {
        contentArea = new JTextArea(20, 40);
        contentArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        
        statusLabel = new JLabel("準備完了");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        dynamicMenuPanel = new JPanel(new FlowLayout());
        dynamicMenuPanel.setBorder(BorderFactory.createTitledBorder("動的メニュー"));
        
        menuHistoryList = new JList<>(menuHistoryModel);
        menuHistoryList.setVisibleRowCount(10);
        menuHistoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        menuCustomizer = new MenuCustomizer();
    }
    
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        
        // ファイルメニュー
        JMenu fileMenu = new JMenu("ファイル(F)");
        fileMenu.setMnemonic('F');
        
        fileMenu.add(actions.get("new"));
        fileMenu.add(actions.get("open"));
        fileMenu.addSeparator();
        fileMenu.add(actions.get("save"));
        
        // 最近使用したファイルメニュー
        recentMenu = new JMenu("最近使用したファイル");
        fileMenu.add(recentMenu);
        
        fileMenu.addSeparator();
        fileMenu.add(actions.get("exit"));
        
        // 編集メニュー
        JMenu editMenu = new JMenu("編集(E)");
        editMenu.setMnemonic('E');
        
        editMenu.add(actions.get("cut"));
        editMenu.add(actions.get("copy"));
        editMenu.add(actions.get("paste"));
        
        // 表示メニュー
        JMenu viewMenu = new JMenu("表示(V)");
        viewMenu.setMnemonic('V');
        
        viewMenu.add(actions.get("toggleMenuBar"));
        viewMenu.add(actions.get("toggleToolBar"));
        
        // サブメニューの例
        JMenu appearanceMenu = new JMenu("外観");
        appearanceMenu.add(new JMenuItem("フォント設定"));
        appearanceMenu.add(new JMenuItem("色設定"));
        
        JMenu themesMenu = new JMenu("テーマ");
        themesMenu.add(new JRadioButtonMenuItem("デフォルト", true));
        themesMenu.add(new JRadioButtonMenuItem("ダーク"));
        themesMenu.add(new JRadioButtonMenuItem("ライト"));
        
        appearanceMenu.add(themesMenu);
        viewMenu.add(appearanceMenu);
        
        // ツールメニュー
        JMenu toolsMenu = new JMenu("ツール(T)");
        toolsMenu.setMnemonic('T');
        
        toolsMenu.add(actions.get("addDynamicMenu"));
        toolsMenu.add(actions.get("customize"));
        
        // 動的メニューセクション
        JMenu dynamicMenu = new JMenu("動的メニュー");
        toolsMenu.add(dynamicMenu);
        
        // ヘルプメニュー
        JMenu helpMenu = new JMenu("ヘルプ(H)");
        helpMenu.setMnemonic('H');
        
        helpMenu.add(new JMenuItem("使用方法"));
        helpMenu.add(new JMenuItem("キーボードショートカット"));
        helpMenu.addSeparator();
        helpMenu.add(new JMenuItem("バージョン情報"));
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
        
        // メニューイベントリスナー
        setupMenuListeners();
    }
    
    private void setupMenuListeners() {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            menu.addMenuListener(new MenuListener() {
                @Override
                public void menuSelected(MenuEvent e) {
                    updateMenuStates();
                    statusLabel.setText("メニュー: " + ((JMenu)e.getSource()).getText());
                }
                
                @Override
                public void menuDeselected(MenuEvent e) {
                    statusLabel.setText("準備完了");
                }
                
                @Override
                public void menuCanceled(MenuEvent e) {
                    statusLabel.setText("準備完了");
                }
            });
        }
    }
    
    private void setupToolBar() {
        toolBar = new JToolBar("メインツールバー");
        toolBar.setFloatable(true);
        
        toolBar.add(actions.get("new"));
        toolBar.add(actions.get("open"));
        toolBar.add(actions.get("save"));
        toolBar.addSeparator();
        toolBar.add(actions.get("cut"));
        toolBar.add(actions.get("copy"));
        toolBar.add(actions.get("paste"));
        toolBar.addSeparator();
        
        // カスタムコンポーネント
        JComboBox<String> fontSizeCombo = new JComboBox<>(new String[]{"10", "12", "14", "16", "18", "20"});
        fontSizeCombo.setSelectedItem("14");
        fontSizeCombo.addActionListener(e -> {
            int size = Integer.parseInt((String)fontSizeCombo.getSelectedItem());
            contentArea.setFont(contentArea.getFont().deriveFont((float)size));
            addToHistory("フォントサイズ変更: " + size);
        });
        
        toolBar.add(new JLabel("サイズ:"));
        toolBar.add(fontSizeCombo);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // メインコンテンツエリア
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // サイドパネル（メニュー履歴）
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBorder(BorderFactory.createTitledBorder("メニュー履歴"));
        sidePanel.add(new JScrollPane(menuHistoryList), BorderLayout.CENTER);
        
        JButton clearHistoryButton = new JButton("履歴クリア");
        clearHistoryButton.addActionListener(e -> {
            menuHistoryModel.clear();
            addToHistory("履歴クリア");
        });
        sidePanel.add(clearHistoryButton, BorderLayout.SOUTH);
        sidePanel.setPreferredSize(new Dimension(200, 0));
        
        // レイアウト配置
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void setupContextMenus() {
        // テキストエリア用コンテキストメニュー
        JPopupMenu textContextMenu = new JPopupMenu();
        textContextMenu.add(actions.get("cut"));
        textContextMenu.add(actions.get("copy"));
        textContextMenu.add(actions.get("paste"));
        textContextMenu.addSeparator();
        textContextMenu.add(new JMenuItem("すべて選択"));
        textContextMenu.add(new JMenuItem("検索"));
        
        contentArea.setComponentPopupMenu(textContextMenu);
        
        // メニューバー用コンテキストメニュー
        JPopupMenu menuBarContextMenu = new JPopupMenu();
        menuBarContextMenu.add(actions.get("customize"));
        menuBarContextMenu.add(actions.get("addDynamicMenu"));
        menuBarContextMenu.addSeparator();
        menuBarContextMenu.add(new JMenuItem("メニューをリセット"));
        
        menuBar.setComponentPopupMenu(menuBarContextMenu);
    }
    
    private void setupEventHandlers() {
        // ウィンドウクローズイベント
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveConfiguration();
                saveRecentFiles();
                System.exit(0);
            }
        });
        
        // テキストエリアの選択状態でメニューを更新
        contentArea.addCaretListener(e -> updateMenuStates());
    }
    
    private void setupWindow() {
        setTitle("Advanced Menu System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void updateMenuStates() {
        boolean hasSelection = contentArea.getSelectedText() != null;
        actions.get("cut").setEnabled(hasSelection);
        actions.get("copy").setEnabled(hasSelection);
        
        // クリップボードの状態をチェック（簡略化）
        actions.get("paste").setEnabled(true);
    }
    
    private void updateRecentFilesMenu() {
        recentMenu.removeAll();
        
        for (int i = 0; i < Math.min(recentFiles.size(), 10); i++) {
            String filePath = recentFiles.get(i);
            String fileName = new File(filePath).getName();
            
            JMenuItem item = new JMenuItem((i + 1) + ". " + fileName);
            item.setToolTipText(filePath);
            item.addActionListener(e -> {
                openRecentFile(filePath);
                addToHistory("最近のファイルを開く: " + fileName);
            });
            
            recentMenu.add(item);
        }
        
        if (!recentFiles.isEmpty()) {
            recentMenu.addSeparator();
            JMenuItem clearItem = new JMenuItem("履歴をクリア");
            clearItem.addActionListener(e -> {
                recentFiles.clear();
                updateRecentFilesMenu();
                addToHistory("最近のファイル履歴をクリア");
            });
            recentMenu.add(clearItem);
        }
    }
    
    private void addToHistory(String action) {
        String timestamp = new Date().toString();
        String entry = timestamp + " - " + action;
        
        menuHistoryModel.insertElementAt(entry, 0);
        
        // 履歴の最大数を制限
        if (menuHistoryModel.size() > 100) {
            menuHistoryModel.remove(menuHistoryModel.size() - 1);
        }
    }
    
    private void addToRecentFiles(String filePath) {
        recentFiles.remove(filePath); // 重複を削除
        recentFiles.add(0, filePath);
        
        // 最大10ファイルまで
        if (recentFiles.size() > 10) {
            recentFiles.remove(recentFiles.size() - 1);
        }
        
        updateRecentFilesMenu();
    }
    
    // アクション実装メソッド
    private void newFile() {
        contentArea.setText("");
        statusLabel.setText("新しいドキュメントを作成しました");
    }
    
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                contentArea.read(reader, null);
                addToRecentFiles(file.getAbsolutePath());
                statusLabel.setText("ファイルを開きました: " + file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "ファイルの読み込みに失敗しました: " + ex.getMessage(),
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                contentArea.write(writer);
                addToRecentFiles(file.getAbsolutePath());
                statusLabel.setText("ファイルを保存しました: " + file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "ファイルの保存に失敗しました: " + ex.getMessage(),
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void openRecentFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            contentArea.read(reader, null);
            statusLabel.setText("最近のファイルを開きました: " + new File(filePath).getName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "ファイルの読み込みに失敗しました: " + ex.getMessage(),
                "エラー", JOptionPane.ERROR_MESSAGE);
            recentFiles.remove(filePath);
            updateRecentFilesMenu();
        }
    }
    
    private void toggleMenuBar() {
        menuBarVisible = !menuBarVisible;
        setJMenuBar(menuBarVisible ? menuBar : null);
        revalidate();
        statusLabel.setText("メニューバー: " + (menuBarVisible ? "表示" : "非表示"));
    }
    
    private void toggleToolBar() {
        toolBarVisible = !toolBarVisible;
        toolBar.setVisible(toolBarVisible);
        statusLabel.setText("ツールバー: " + (toolBarVisible ? "表示" : "非表示"));
    }
    
    private void addDynamicMenuItem() {
        String itemName = JOptionPane.showInputDialog(this, "新しいメニュー項目の名前:", "動的メニュー追加", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (itemName != null && !itemName.trim().isEmpty()) {
            // ツールメニューに動的に追加
            JMenu toolsMenu = menuBar.getMenu(3); // ツールメニュー
            JMenu dynamicMenu = (JMenu) toolsMenu.getItem(toolsMenu.getItemCount() - 2); // 動的メニュー
            
            JMenuItem newItem = new JMenuItem(itemName);
            newItem.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "動的メニュー '" + itemName + "' が実行されました");
                addToHistory("動的メニュー実行: " + itemName);
            });
            
            dynamicMenu.add(newItem);
            statusLabel.setText("動的メニュー項目を追加しました: " + itemName);
        }
    }
    
    private void showMenuCustomizer() {
        menuCustomizer.showDialog(this);
    }
    
    private void exitApplication() {
        saveConfiguration();
        saveRecentFiles();
        System.exit(0);
    }
    
    private void loadConfiguration() {
        // 設定の読み込み（簡略化）
        try {
            String configPath = System.getProperty("user.home") + "/.menuconfig.properties";
            File configFile = new File(configPath);
            if (configFile.exists()) {
                menuConfig.load(new FileInputStream(configFile));
            }
        } catch (IOException e) {
            // 設定ファイルが見つからない場合は無視
        }
    }
    
    private void saveConfiguration() {
        try {
            String configPath = System.getProperty("user.home") + "/.menuconfig.properties";
            menuConfig.setProperty("menuBarVisible", String.valueOf(menuBarVisible));
            menuConfig.setProperty("toolBarVisible", String.valueOf(toolBarVisible));
            menuConfig.store(new FileOutputStream(configPath), "Menu System Configuration");
        } catch (IOException e) {
            // 保存エラーは無視
        }
    }
    
    private void loadRecentFiles() {
        String recentFilesStr = preferences.get("recentFiles", "");
        if (!recentFilesStr.isEmpty()) {
            String[] files = recentFilesStr.split(";");
            for (String file : files) {
                if (!file.trim().isEmpty()) {
                    recentFiles.add(file);
                }
            }
            updateRecentFilesMenu();
        }
    }
    
    private void saveRecentFiles() {
        String recentFilesStr = String.join(";", recentFiles);
        preferences.put("recentFiles", recentFilesStr);
    }
    
    private Icon loadIcon(String iconName) {
        // アイコンの読み込み（リソースファイルから）
        try {
            // 実際の実装では、リソースファイルからアイコンを読み込む
            return UIManager.getIcon("FileView.fileIcon"); // プレースホルダー
        } catch (Exception e) {
            return null;
        }
    }
    
    // カスタムアクションクラス
    private abstract class MenuAction extends AbstractAction {
        public MenuAction(String name, String description, KeyStroke accelerator, Icon icon) {
            super(name, icon);
            putValue(SHORT_DESCRIPTION, description);
            if (accelerator != null) {
                putValue(ACCELERATOR_KEY, accelerator);
            }
        }
    }
    
    // メニューカスタマイザークラス
    private class MenuCustomizer {
        private JDialog dialog;
        private JList<String> availableActions;
        private JList<String> menuItems;
        
        public MenuCustomizer() {
            initializeDialog();
        }
        
        private void initializeDialog() {
            dialog = new JDialog(MenuSystem.this, "メニューカスタマイズ", true);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(MenuSystem.this);
            
            // カスタマイズUI（簡略化）
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel("メニューカスタマイズ機能（実装例）"), BorderLayout.CENTER);
            
            JButton closeButton = new JButton("閉じる");
            closeButton.addActionListener(e -> dialog.setVisible(false));
            panel.add(closeButton, BorderLayout.SOUTH);
            
            dialog.add(panel);
        }
        
        public void showDialog(Component parent) {
            dialog.setVisible(true);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new MenuSystem().setVisible(true);
        });
    }
}