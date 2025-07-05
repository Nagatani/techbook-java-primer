/**
 * 第18章 基本課題1: キーボードショートカット - 解答例
 * 
 * キーボードショートカット機能を持つシンプルなテキストエディタの完全実装。
 * プロフェッショナルなテキストエディタの基本機能を提供します。
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

public class KeyboardShortcuts extends JFrame {
    
    private JTextArea textArea;
    private JLabel statusLabel;
    private UndoManager undoManager;
    private boolean isModified = false;
    private File currentFile = null;
    private List<File> recentFiles = new ArrayList<>();
    private static final int MAX_RECENT_FILES = 5;
    
    public KeyboardShortcuts() {
        setTitle("テキストエディタ - 新規文書");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        initializeComponents();
        setupLayout();
        createMenuBar();
        setupKeyboardShortcuts();
        setupEventListeners();
        setupWindowListener();
        
        updateStatus();
    }
    
    /**
     * UIコンポーネントを初期化します
     */
    private void initializeComponents() {
        // テキストエリアの初期化
        textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setTabSize(4);
        
        // ステータスラベルの初期化
        statusLabel = new JLabel("文字数: 0 | 行数: 1 | 列: 1");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // アンドゥマネージャーの初期化
        undoManager = new UndoManager();
        Document doc = textArea.getDocument();
        doc.addUndoableEditListener(undoManager);
    }
    
    /**
     * レイアウトを設定します
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // メインエリア（テキストエリアをスクロールペインに追加）
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        
        // ステータスバー
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    /**
     * メニューバーを作成します
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // ファイルメニュー
        JMenu fileMenu = new JMenu("ファイル(F)");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem newItem = new JMenuItem("新規(N)");
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(e -> newDocument());
        fileMenu.add(newItem);
        
        JMenuItem openItem = new JMenuItem("開く(O)");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> openDocument());
        fileMenu.add(openItem);
        
        // 最近使用したファイル
        JMenu recentMenu = new JMenu("最近使用したファイル");
        updateRecentFilesMenu(recentMenu);
        fileMenu.add(recentMenu);
        
        fileMenu.addSeparator();
        
        JMenuItem saveItem = new JMenuItem("保存(S)");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveDocument());
        fileMenu.add(saveItem);
        
        JMenuItem saveAsItem = new JMenuItem("名前を付けて保存");
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
            InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAsItem.addActionListener(e -> saveAsDocument());
        fileMenu.add(saveAsItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("終了(X)");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        exitItem.addActionListener(e -> exitApplication());
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        
        // 編集メニュー
        JMenu editMenu = new JMenu("編集(E)");
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        JMenuItem undoItem = new JMenuItem("元に戻す(U)");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> undoAction());
        editMenu.add(undoItem);
        
        JMenuItem redoItem = new JMenuItem("やり直し(R)");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redoItem.addActionListener(e -> redoAction());
        editMenu.add(redoItem);
        
        editMenu.addSeparator();
        
        JMenuItem cutItem = new JMenuItem("切り取り(T)");
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        cutItem.addActionListener(e -> textArea.cut());
        editMenu.add(cutItem);
        
        JMenuItem copyItem = new JMenuItem("コピー(C)");
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        copyItem.addActionListener(e -> textArea.copy());
        editMenu.add(copyItem);
        
        JMenuItem pasteItem = new JMenuItem("貼り付け(P)");
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        pasteItem.addActionListener(e -> textArea.paste());
        editMenu.add(pasteItem);
        
        editMenu.addSeparator();
        
        JMenuItem selectAllItem = new JMenuItem("すべて選択(A)");
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        selectAllItem.addActionListener(e -> textArea.selectAll());
        editMenu.add(selectAllItem);
        
        JMenuItem findItem = new JMenuItem("検索(F)");
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        findItem.addActionListener(e -> showFindDialog());
        editMenu.add(findItem);
        
        menuBar.add(editMenu);
        
        // 表示メニュー
        JMenu viewMenu = new JMenu("表示(V)");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JCheckBoxMenuItem wordWrapItem = new JCheckBoxMenuItem("行の折り返し", true);
        wordWrapItem.addActionListener(e -> {
            boolean wrap = wordWrapItem.isSelected();
            textArea.setLineWrap(wrap);
            textArea.setWrapStyleWord(wrap);
        });
        viewMenu.add(wordWrapItem);
        
        JMenuItem fontItem = new JMenuItem("フォント");
        fontItem.addActionListener(e -> showFontDialog());
        viewMenu.add(fontItem);
        
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * キーボードショートカットを設定します
     */
    private void setupKeyboardShortcuts() {
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = textArea.getActionMap();
        
        // Ctrl+N: 新規作成
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "new");
        actionMap.put("new", new CustomAction("新規", this::newDocument));
        
        // Ctrl+O: 開く
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "open");
        actionMap.put("open", new CustomAction("開く", this::openDocument));
        
        // Ctrl+S: 保存
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        actionMap.put("save", new CustomAction("保存", this::saveDocument));
        
        // Ctrl+Z: 元に戻す
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
        actionMap.put("undo", new CustomAction("元に戻す", this::undoAction));
        
        // Ctrl+Y: やり直し
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
        actionMap.put("redo", new CustomAction("やり直し", this::redoAction));
        
        // Ctrl+F: 検索
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "find");
        actionMap.put("find", new CustomAction("検索", this::showFindDialog));
        
        // F3: 次を検索
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "findNext");
        actionMap.put("findNext", new CustomAction("次を検索", this::findNext));
    }
    
    /**
     * イベントリスナーを設定します
     */
    private void setupEventListeners() {
        // ドキュメントの変更を監視
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setModified(true);
                updateStatus();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                setModified(true);
                updateStatus();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateStatus();
            }
        });
        
        // キャレット位置の変更を監視
        textArea.addCaretListener(e -> updateStatus());
    }
    
    /**
     * ウィンドウリスナーを設定します
     */
    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    /**
     * 新規作成アクション
     */
    private void newDocument() {
        if (confirmSaveBeforeAction()) {
            textArea.setText("");
            currentFile = null;
            setModified(false);
            updateTitle();
            updateStatus();
        }
    }
    
    /**
     * ファイル開くアクション
     */
    private void openDocument() {
        if (!confirmSaveBeforeAction()) {
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt") || 
                       f.getName().toLowerCase().endsWith(".java");
            }
            
            @Override
            public String getDescription() {
                return "テキストファイル (*.txt, *.java)";
            }
        });
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String content = readFileContent(file);
                textArea.setText(content);
                currentFile = file;
                setModified(false);
                updateTitle();
                addToRecentFiles(file);
                textArea.setCaretPosition(0);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "ファイルの読み込みに失敗しました: " + e.getMessage(),
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * ファイル保存アクション
     */
    private void saveDocument() {
        if (currentFile == null) {
            saveAsDocument();
        } else {
            try {
                writeFileContent(currentFile, textArea.getText());
                setModified(false);
                JOptionPane.showMessageDialog(this, 
                    "ファイルを保存しました: " + currentFile.getName(),
                    "保存完了", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "ファイルの保存に失敗しました: " + e.getMessage(),
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 名前を付けて保存アクション
     */
    private void saveAsDocument() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }
            
            @Override
            public String getDescription() {
                return "テキストファイル (*.txt)";
            }
        });
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            
            try {
                writeFileContent(file, textArea.getText());
                currentFile = file;
                setModified(false);
                updateTitle();
                addToRecentFiles(file);
                JOptionPane.showMessageDialog(this, 
                    "ファイルを保存しました: " + file.getName(),
                    "保存完了", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "ファイルの保存に失敗しました: " + e.getMessage(),
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 元に戻すアクション
     */
    private void undoAction() {
        try {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (Exception e) {
            // Undo失敗時は無視
        }
    }
    
    /**
     * やり直しアクション
     */
    private void redoAction() {
        try {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (Exception e) {
            // Redo失敗時は無視
        }
    }
    
    /**
     * 検索ダイアログを表示します
     */
    private void showFindDialog() {
        String searchText = JOptionPane.showInputDialog(this, "検索する文字列:", "検索", JOptionPane.PLAIN_MESSAGE);
        if (searchText != null && !searchText.trim().isEmpty()) {
            findText(searchText.trim());
        }
    }
    
    /**
     * 次を検索します
     */
    private void findNext() {
        // 簡易実装: 検索機能の詳細はここでは省略
        JOptionPane.showMessageDialog(this, "次を検索機能は実装中です", "情報", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 文字列を検索します
     */
    private void findText(String searchText) {
        String content = textArea.getText();
        int startIndex = textArea.getCaretPosition();
        int foundIndex = content.indexOf(searchText, startIndex);
        
        if (foundIndex == -1) {
            foundIndex = content.indexOf(searchText, 0);
        }
        
        if (foundIndex != -1) {
            textArea.setCaretPosition(foundIndex);
            textArea.select(foundIndex, foundIndex + searchText.length());
        } else {
            JOptionPane.showMessageDialog(this, 
                "「" + searchText + "」が見つかりませんでした", 
                "検索結果", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * フォントダイアログを表示します
     */
    private void showFontDialog() {
        Font currentFont = textArea.getFont();
        Font newFont = showFontChooser(this, currentFont);
        if (newFont != null) {
            textArea.setFont(newFont);
        }
    }
    
    /**
     * フォント選択ダイアログ（簡易版）
     */
    private Font showFontChooser(Component parent, Font currentFont) {
        String[] fonts = {"Monospaced", "SansSerif", "Serif"};
        Integer[] sizes = {10, 12, 14, 16, 18, 20, 24};
        
        JComboBox<String> fontCombo = new JComboBox<>(fonts);
        JComboBox<Integer> sizeCombo = new JComboBox<>(sizes);
        
        fontCombo.setSelectedItem(currentFont.getName());
        sizeCombo.setSelectedItem(currentFont.getSize());
        
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("フォント:"));
        panel.add(fontCombo);
        panel.add(new JLabel("サイズ:"));
        panel.add(sizeCombo);
        
        int result = JOptionPane.showConfirmDialog(parent, panel, "フォント選択", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String fontName = (String) fontCombo.getSelectedItem();
            int fontSize = (Integer) sizeCombo.getSelectedItem();
            return new Font(fontName, Font.PLAIN, fontSize);
        }
        
        return null;
    }
    
    /**
     * ステータスバーを更新します
     */
    private void updateStatus() {
        String text = textArea.getText();
        int charCount = text.length();
        int lineCount = text.split("\n").length;
        
        // キャレット位置から行と列を計算
        int caretPos = textArea.getCaretPosition();
        int line = 1;
        int column = 1;
        
        for (int i = 0; i < caretPos && i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
        
        statusLabel.setText(String.format("文字数: %d | 行数: %d | 行: %d, 列: %d", 
            charCount, lineCount, line, column));
    }
    
    /**
     * 変更状態を設定します
     */
    private void setModified(boolean modified) {
        this.isModified = modified;
        updateTitle();
    }
    
    /**
     * タイトルを更新します
     */
    private void updateTitle() {
        String title = "テキストエディタ - ";
        if (currentFile != null) {
            title += currentFile.getName();
        } else {
            title += "新規文書";
        }
        
        if (isModified) {
            title += " *";
        }
        
        setTitle(title);
    }
    
    /**
     * アクション実行前に保存確認を行います
     */
    private boolean confirmSaveBeforeAction() {
        if (isModified) {
            int result = JOptionPane.showConfirmDialog(this,
                "変更内容を保存しますか？",
                "確認", JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                saveDocument();
                return !isModified; // 保存が成功した場合のみtrue
            } else if (result == JOptionPane.NO_OPTION) {
                return true;
            } else {
                return false; // キャンセル
            }
        }
        return true;
    }
    
    /**
     * アプリケーションを終了します
     */
    private void exitApplication() {
        if (confirmSaveBeforeAction()) {
            System.exit(0);
        }
    }
    
    /**
     * 最近使用したファイルリストに追加します
     */
    private void addToRecentFiles(File file) {
        recentFiles.remove(file); // 重複削除
        recentFiles.add(0, file); // 先頭に追加
        
        if (recentFiles.size() > MAX_RECENT_FILES) {
            recentFiles.remove(recentFiles.size() - 1);
        }
    }
    
    /**
     * 最近使用したファイルメニューを更新します
     */
    private void updateRecentFilesMenu(JMenu recentMenu) {
        recentMenu.removeAll();
        
        if (recentFiles.isEmpty()) {
            JMenuItem emptyItem = new JMenuItem("(なし)");
            emptyItem.setEnabled(false);
            recentMenu.add(emptyItem);
        } else {
            for (int i = 0; i < recentFiles.size(); i++) {
                File file = recentFiles.get(i);
                JMenuItem item = new JMenuItem((i + 1) + ". " + file.getName());
                item.addActionListener(e -> openRecentFile(file));
                recentMenu.add(item);
            }
        }
    }
    
    /**
     * 最近使用したファイルを開きます
     */
    private void openRecentFile(File file) {
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, 
                "ファイルが見つかりません: " + file.getAbsolutePath(),
                "エラー", JOptionPane.ERROR_MESSAGE);
            recentFiles.remove(file);
            return;
        }
        
        if (confirmSaveBeforeAction()) {
            try {
                String content = readFileContent(file);
                textArea.setText(content);
                currentFile = file;
                setModified(false);
                updateTitle();
                textArea.setCaretPosition(0);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "ファイルの読み込みに失敗しました: " + e.getMessage(),
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * ファイル内容を読み込みます
     */
    private String readFileContent(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    /**
     * ファイルに内容を書き込みます
     */
    private void writeFileContent(File file, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }
    
    /**
     * カスタムアクションクラス
     */
    private class CustomAction extends AbstractAction {
        private final Runnable action;
        
        public CustomAction(String name, Runnable action) {
            super(name);
            this.action = action;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            action.run();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new KeyboardShortcuts().setVisible(true);
        });
    }
}