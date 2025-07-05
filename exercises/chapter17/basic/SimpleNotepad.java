/**
 * 第17章 GUI プログラミング基礎
 * 演習課題: SimpleNotepad.java
 * 
 * 【課題概要】
 * 簡単なメモ帳アプリケーションを作成してください。
 * 
 * 【要求仕様】
 * 1. テキストエリアを持つウィンドウの作成
 * 2. メニューバーの実装（ファイル、編集、ヘルプ）
 * 3. ファイル操作機能（新規作成、開く、保存、名前を付けて保存）
 * 4. 編集機能（切り取り、コピー、貼り付け、全選択）
 * 5. ウィンドウサイズの調整可能
 * 6. 適切なキーボードショートカット
 * 7. 変更検知機能（タイトルバーに * マーク表示）
 * 8. 終了時の保存確認ダイアログ
 * 
 * 【学習ポイント】
 * - JFrame、JTextArea、JMenuBar、JMenu、JMenuItem の使用
 * - ファイル操作（FileReader、FileWriter）
 * - イベント処理（ActionListener、DocumentListener）
 * - ダイアログ表示（JOptionPane、JFileChooser）
 * - キーボードショートカット（KeyStroke）
 * - ウィンドウクローズ処理（WindowAdapter）
 * 
 * 【実装のヒント】
 * 1. JTextArea を JScrollPane で囲む
 * 2. DocumentListener で文書の変更を監視
 * 3. JFileChooser でファイル選択ダイアログを表示
 * 4. 保存状態を boolean フラグで管理
 * 5. WindowAdapter を使って終了時の処理を実装
 * 
 * 【よくある間違いと対策】
 * - 文字エンコーディングの考慮不足
 *   → InputStreamReader、OutputStreamWriter を使用
 * - メモリ不足による大きなファイルの読み込み失敗
 *   → BufferedReader、BufferedWriter を使用
 * - 変更検知の実装漏れ
 *   → DocumentListener の全てのメソッドを実装
 * - 例外処理の不備
 *   → try-catch-finally または try-with-resources を使用
 * 
 * 【段階的な実装指針】
 * 1. 基本的なウィンドウとテキストエリアの作成
 * 2. メニューバーとメニュー項目の追加
 * 3. ファイル操作メニューの実装
 * 4. 編集メニューの実装
 * 5. 変更検知機能の追加
 * 6. 終了時の保存確認機能の実装
 * 7. キーボードショートカットの追加
 * 8. UI の改善とエラーハンドリングの強化
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class SimpleNotepad extends JFrame {
    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu fileMenu, editMenu, helpMenu;
    private JMenuItem newItem, openItem, saveItem, saveAsItem, exitItem;
    private JMenuItem cutItem, copyItem, pasteItem, selectAllItem;
    private JMenuItem aboutItem;
    private JFileChooser fileChooser;
    private File currentFile;
    private boolean isModified;
    private String originalText;
    
    public SimpleNotepad() {
        initializeComponents();
        setupMenus();
        setupEventHandlers();
        setupLayout();
        setupWindow();
    }
    
    private void initializeComponents() {
        // テキストエリアの初期化
        textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        textArea.setTabSize(4);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        // ファイルチューザーの初期化
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("テキストファイル (*.txt)", "txt"));
        
        // 状態の初期化
        currentFile = null;
        isModified = false;
        originalText = "";
    }
    
    private void setupMenus() {
        // メニューバーの作成
        menuBar = new JMenuBar();
        
        // ファイルメニュー
        fileMenu = new JMenu("ファイル(F)");
        fileMenu.setMnemonic('F');
        
        newItem = new JMenuItem("新規作成(N)");
        newItem.setMnemonic('N');
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        
        openItem = new JMenuItem("開く(O)");
        openItem.setMnemonic('O');
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        
        saveItem = new JMenuItem("保存(S)");
        saveItem.setMnemonic('S');
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        
        saveAsItem = new JMenuItem("名前を付けて保存(A)");
        saveAsItem.setMnemonic('A');
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
            InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        
        exitItem = new JMenuItem("終了(X)");
        exitItem.setMnemonic('X');
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // 編集メニュー
        editMenu = new JMenu("編集(E)");
        editMenu.setMnemonic('E');
        
        cutItem = new JMenuItem("切り取り(T)");
        cutItem.setMnemonic('T');
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        
        copyItem = new JMenuItem("コピー(C)");
        copyItem.setMnemonic('C');
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        
        pasteItem = new JMenuItem("貼り付け(P)");
        pasteItem.setMnemonic('P');
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        
        selectAllItem = new JMenuItem("すべて選択(A)");
        selectAllItem.setMnemonic('A');
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);
        
        // ヘルプメニュー
        helpMenu = new JMenu("ヘルプ(H)");
        helpMenu.setMnemonic('H');
        
        aboutItem = new JMenuItem("バージョン情報(A)");
        aboutItem.setMnemonic('A');
        
        helpMenu.add(aboutItem);
        
        // メニューバーに追加
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setupEventHandlers() {
        // ファイルメニューのイベントハンドラー
        newItem.addActionListener(e -> newFile());
        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        saveAsItem.addActionListener(e -> saveAsFile());
        exitItem.addActionListener(e -> exitApplication());
        
        // 編集メニューのイベントハンドラー
        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());
        selectAllItem.addActionListener(e -> textArea.selectAll());
        
        // ヘルプメニューのイベントハンドラー
        aboutItem.addActionListener(e -> showAbout());
        
        // テキストエリアの変更監視
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setModified(true);
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                setModified(true);
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                setModified(true);
            }
        });
        
        // ウィンドウクローズ処理
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
    }
    
    private void setupLayout() {
        // レイアウトの設定
        setLayout(new BorderLayout());
        
        // テキストエリアをスクロールペインに配置
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void setupWindow() {
        setTitle("SimpleNotepad");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void newFile() {
        if (checkUnsavedChanges()) {
            textArea.setText("");
            currentFile = null;
            setModified(false);
            updateTitle();
        }
    }
    
    private void openFile() {
        if (checkUnsavedChanges()) {
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                    
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    
                    textArea.setText(content.toString());
                    currentFile = file;
                    setModified(false);
                    updateTitle();
                    
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "ファイルの読み込みに失敗しました: " + ex.getMessage(),
                        "エラー", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void saveFile() {
        if (currentFile == null) {
            saveAsFile();
        } else {
            saveToFile(currentFile);
        }
    }
    
    private void saveAsFile() {
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            
            if (file.exists()) {
                int choice = JOptionPane.showConfirmDialog(this,
                    "ファイルが既に存在します。上書きしますか？",
                    "確認", JOptionPane.YES_NO_OPTION);
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            saveToFile(file);
            currentFile = file;
            updateTitle();
        }
    }
    
    private void saveToFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            
            writer.write(textArea.getText());
            setModified(false);
            JOptionPane.showMessageDialog(this, 
                "ファイルを保存しました: " + file.getName(),
                "保存完了", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "ファイルの保存に失敗しました: " + ex.getMessage(),
                "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exitApplication() {
        if (checkUnsavedChanges()) {
            System.exit(0);
        }
    }
    
    private boolean checkUnsavedChanges() {
        if (isModified) {
            int choice = JOptionPane.showConfirmDialog(this,
                "変更が保存されていません。保存しますか？",
                "確認", JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                saveFile();
                return !isModified; // 保存が成功した場合のみtrueを返す
            } else if (choice == JOptionPane.NO_OPTION) {
                return true;
            } else {
                return false; // キャンセルされた場合
            }
        }
        return true;
    }
    
    private void setModified(boolean modified) {
        this.isModified = modified;
        updateTitle();
    }
    
    private void updateTitle() {
        String title = "SimpleNotepad";
        if (currentFile != null) {
            title += " - " + currentFile.getName();
        }
        if (isModified) {
            title += " *";
        }
        setTitle(title);
    }
    
    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            "SimpleNotepad v1.0\n" +
            "Java Swing を使用したシンプルなテキストエディタ\n" +
            "Java入門 第17章 演習課題",
            "バージョン情報", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new SimpleNotepad().setVisible(true);
        });
    }
}