# 第18章 GUIのイベント処理 - Part B: 代表的なイベントの種類

## 12.2 代表的なイベントの種類

Swingに挟まざまなイベントがあります。目的に応じて適切なリスナを使い分けましょう。

| イベント | 説明 | 主なリスナ |
| :--- | :--- | :--- |
| `ActionEvent` | ボタンクリックなど、明確なアクション | `ActionListener` |
| `MouseEvent` | マウスのクリック、カーソルの出入り | `MouseListener` |
| `KeyEvent` | キーボードのキー操作 | `KeyListener` |
| `WindowEvent` | ウィンドウが開く、閉じるなどの状態変化 | `WindowListener` |
| `ItemEvent` | チェックボックスなどの項目選択状態の変化 | `ItemListener` |
| `FocusEvent` | コンポーネントがフォーカスを得る/失う | `FocusListener` |
| `ChangeEvent` | スライダーなど、コンポーネントの内部状態の連続的な変化 | `ChangeListener` |

### `WindowListener`で終了確認

ウィンドウを閉じる際に確認ダイアログを表示する例です。`WindowListener`インターフェイスには多くのメソッドがありますが、`WindowAdapter`クラスを継承することで、必要なメソッドだけをオーバーライドして実装できます。

```java
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowEventExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("終了確認");
        // デフォルトの閉じる操作を無効化
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // WindowAdapterを匿名クラスで実装して登録
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(frame, "本当に終了しますか？");
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0); // 「はい」ならプログラムを終了
                }
            }
        });

        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
```

### マウスイベントの完全な処理

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MouseEventCompleteExample extends JFrame {
    private JTextArea logArea;
    private JPanel drawPanel;
    private Point startPoint;
    private Point currentPoint;
    
    public MouseEventCompleteExample() {
        setTitle("マウスイベント完全例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 描画パネル
        drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (startPoint != null && currentPoint != null) {
                    g.setColor(Color.BLUE);
                    g.drawLine(startPoint.x, startPoint.y, currentPoint.x, currentPoint.y);
                }
            }
        };
        drawPanel.setBackground(Color.WHITE);
        drawPanel.setPreferredSize(new Dimension(400, 300));
        drawPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // ログエリア
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        // マウスリスナーの追加
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String button = getButtonName(e.getButton());
                int clickCount = e.getClickCount();
                log("クリック: " + button + " (回数: " + clickCount + ") at " + 
                    e.getPoint());
                
                // ダブルクリックの検出
                if (clickCount == 2) {
                    log("ダブルクリック検出！");
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                log("マウス押下: " + getButtonName(e.getButton()) + " at " + startPoint);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                log("マウス解放: " + getButtonName(e.getButton()) + " at " + e.getPoint());
                startPoint = null;
                currentPoint = null;
                drawPanel.repaint();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                log("マウスがパネルに入りました");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                drawPanel.setCursor(Cursor.getDefaultCursor());
                log("マウスがパネルから出ました");
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                currentPoint = e.getPoint();
                drawPanel.repaint();
                log("ドラッグ中: " + currentPoint);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                // 頻繁に発生するので通常はログしない
                // log("マウス移動: " + e.getPoint());
            }
            
            private String getButtonName(int button) {
                switch (button) {
                    case MouseEvent.BUTTON1: return "左ボタン";
                    case MouseEvent.BUTTON2: return "中央ボタン";
                    case MouseEvent.BUTTON3: return "右ボタン";
                    default: return "不明なボタン";
                }
            }
        };
        
        drawPanel.addMouseListener(mouseHandler);
        drawPanel.addMouseMotionListener(mouseHandler);
        
        // コンテキストメニュー（右クリックメニュー）
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new JMenuItem("切り取り"));
        popupMenu.add(new JMenuItem("コピー"));
        popupMenu.add(new JMenuItem("貼り付け"));
        
        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        add(drawPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MouseEventCompleteExample().setVisible(true);
        });
    }
}
```

### キーボードイベントの高度な処理

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class KeyboardEventAdvancedExample extends JFrame {
    private JTextArea textArea;
    private JLabel statusLabel;
    private Set<Integer> pressedKeys = new HashSet<>();
    
    public KeyboardEventAdvancedExample() {
        setTitle("高度なキーボードイベント処理");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // テキストエリア
        textArea = new JTextArea(10, 40);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        // ステータスラベル
        statusLabel = new JLabel("キーを押してください");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // キーリスナーの追加
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                updateStatus(e, "押下");
                
                // ショートカットキーの検出
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    saveDocument();
                    e.consume(); // デフォルトの動作をキャンセル
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A) {
                    textArea.selectAll();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_F1) {
                    showHelp();
                }
                
                // 複数キーの同時押し検出
                if (pressedKeys.contains(KeyEvent.VK_CONTROL) && 
                    pressedKeys.contains(KeyEvent.VK_SHIFT) && 
                    pressedKeys.contains(KeyEvent.VK_D)) {
                    duplicateLine();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
                updateStatus(e, "解放");
            }
            
            @Override
            public void keyTyped(KeyEvent e) {
                // 文字が入力された時
                char ch = e.getKeyChar();
                if (!Character.isISOControl(ch)) {
                    statusLabel.setText("入力文字: '" + ch + "' (Unicode: " + 
                        (int)ch + ")");
                }
            }
        });
        
        // InputMapとActionMapを使ったより高度なキーバインディング
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();
        
        // Ctrl+Dで行複製
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 
            InputEvent.CTRL_DOWN_MASK), "duplicate-line");
        actionMap.put("duplicate-line", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateLine();
            }
        });
        
        // F5でタイムスタンプ挿入
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "insert-timestamp");
        actionMap.put("insert-timestamp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.insert(new java.util.Date().toString(), 
                    textArea.getCaretPosition());
            }
        });
        
        // メニューバーの作成（アクセラレータキー付き）
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("ファイル");
        fileMenu.setMnemonic(KeyEvent.VK_F); // Alt+F
        
        JMenuItem saveItem = new JMenuItem("保存");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
            InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveDocument());
        
        JMenuItem exitItem = new JMenuItem("終了");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 
            InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void updateStatus(KeyEvent e, String action) {
        String modifiers = "";
        if (e.isControlDown()) modifiers += "Ctrl+";
        if (e.isAltDown()) modifiers += "Alt+";
        if (e.isShiftDown()) modifiers += "Shift+";
        
        String keyText = KeyEvent.getKeyText(e.getKeyCode());
        statusLabel.setText("キー" + action + ": " + modifiers + keyText + 
            " (コード: " + e.getKeyCode() + ")");
    }
    
    private void saveDocument() {
        JOptionPane.showMessageDialog(this, "ドキュメントを保存しました（仮想）");
    }
    
    private void showHelp() {
        JOptionPane.showMessageDialog(this, 
            "ショートカットキー:\n" +
            "Ctrl+S: 保存\n" +
            "Ctrl+A: すべて選択\n" +
            "Ctrl+D: 行複製\n" +
            "F1: このヘルプ\n" +
            "F5: タイムスタンプ挿入");
    }
    
    private void duplicateLine() {
        try {
            int caretPos = textArea.getCaretPosition();
            int lineNum = textArea.getLineOfOffset(caretPos);
            int lineStart = textArea.getLineStartOffset(lineNum);
            int lineEnd = textArea.getLineEndOffset(lineNum);
            String line = textArea.getText(lineStart, lineEnd - lineStart);
            textArea.insert(line, lineEnd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new KeyboardEventAdvancedExample().setVisible(true);
        });
    }
}
```

### ItemListenerによる選択状態の監視

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ItemListenerExample extends JFrame {
    private JLabel selectedLabel;
    private JPanel previewPanel;
    
    public ItemListenerExample() {
        setTitle("ItemListener の例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 選択パネル
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("オプション選択"));
        
        // チェックボックス
        JCheckBox boldCheck = new JCheckBox("太字");
        JCheckBox italicCheck = new JCheckBox("斜体");
        JCheckBox underlineCheck = new JCheckBox("下線");
        
        // ラジオボタン
        ButtonGroup sizeGroup = new ButtonGroup();
        JRadioButton smallRadio = new JRadioButton("小");
        JRadioButton mediumRadio = new JRadioButton("中", true);
        JRadioButton largeRadio = new JRadioButton("大");
        sizeGroup.add(smallRadio);
        sizeGroup.add(mediumRadio);
        sizeGroup.add(largeRadio);
        
        // コンボボックス
        String[] colors = {"黒", "赤", "青", "緑"};
        JComboBox<String> colorCombo = new JComboBox<>(colors);
        
        // プレビューパネル
        previewPanel = new JPanel();
        previewPanel.setPreferredSize(new Dimension(300, 100));
        previewPanel.setBorder(BorderFactory.createTitledBorder("プレビュー"));
        
        selectedLabel = new JLabel("サンプルテキスト");
        selectedLabel.setHorizontalAlignment(JLabel.CENTER);
        previewPanel.add(selectedLabel);
        
        // ItemListenerの実装
        ItemListener updatePreview = e -> {
            int style = Font.PLAIN;
            if (boldCheck.isSelected()) style |= Font.BOLD;
            if (italicCheck.isSelected()) style |= Font.ITALIC;
            
            int size = 12;
            if (smallRadio.isSelected()) size = 10;
            else if (mediumRadio.isSelected()) size = 14;
            else if (largeRadio.isSelected()) size = 18;
            
            selectedLabel.setFont(new Font("Dialog", style, size));
            
            // 下線の処理
            if (underlineCheck.isSelected()) {
                selectedLabel.setText("<html><u>" + selectedLabel.getText() + "</u></html>");
            } else {
                selectedLabel.setText("サンプルテキスト");
            }
            
            // 色の処理
            String selectedColor = (String) colorCombo.getSelectedItem();
            switch (selectedColor) {
                case "赤": selectedLabel.setForeground(Color.RED); break;
                case "青": selectedLabel.setForeground(Color.BLUE); break;
                case "緑": selectedLabel.setForeground(Color.GREEN); break;
                default: selectedLabel.setForeground(Color.BLACK);
            }
        };
        
        // リスナーの登録
        boldCheck.addItemListener(updatePreview);
        italicCheck.addItemListener(updatePreview);
        underlineCheck.addItemListener(updatePreview);
        smallRadio.addItemListener(updatePreview);
        mediumRadio.addItemListener(updatePreview);
        largeRadio.addItemListener(updatePreview);
        colorCombo.addItemListener(updatePreview);
        
        // コンポーネントの配置
        selectionPanel.add(new JLabel("スタイル:"));
        selectionPanel.add(boldCheck);
        selectionPanel.add(italicCheck);
        selectionPanel.add(underlineCheck);
        selectionPanel.add(Box.createVerticalStrut(10));
        selectionPanel.add(new JLabel("サイズ:"));
        selectionPanel.add(smallRadio);
        selectionPanel.add(mediumRadio);
        selectionPanel.add(largeRadio);
        selectionPanel.add(Box.createVerticalStrut(10));
        selectionPanel.add(new JLabel("色:"));
        selectionPanel.add(colorCombo);
        
        add(selectionPanel, BorderLayout.WEST);
        add(previewPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ItemListenerExample().setVisible(true);
        });
    }
}
```

### ChangeListenerによる連続的な値の変更

```java
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class ChangeListenerExample extends JFrame {
    private JPanel colorPanel;
    private JLabel rgbLabel;
    private JSlider redSlider, greenSlider, blueSlider;
    
    public ChangeListenerExample() {
        setTitle("ChangeListener の例 - カラーミキサー");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // カラー表示パネル
        colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(300, 200));
        colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        // RGBラベル
        rgbLabel = new JLabel("RGB(0, 0, 0)");
        rgbLabel.setHorizontalAlignment(JLabel.CENTER);
        rgbLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        
        // スライダーパネル
        JPanel sliderPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 赤スライダー
        redSlider = createColorSlider("赤");
        sliderPanel.add(createSliderPanel(redSlider, "赤", Color.RED));
        
        // 緑スライダー
        greenSlider = createColorSlider("緑");
        sliderPanel.add(createSliderPanel(greenSlider, "緑", Color.GREEN));
        
        // 青スライダー
        blueSlider = createColorSlider("青");
        sliderPanel.add(createSliderPanel(blueSlider, "青", Color.BLUE));
        
        // ChangeListenerの実装
        ChangeListener colorChangeListener = e -> updateColor();
        
        redSlider.addChangeListener(colorChangeListener);
        greenSlider.addChangeListener(colorChangeListener);
        blueSlider.addChangeListener(colorChangeListener);
        
        // 初期色の設定
        updateColor();
        
        // レイアウト
        add(colorPanel, BorderLayout.CENTER);
        add(rgbLabel, BorderLayout.NORTH);
        add(sliderPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private JSlider createColorSlider(String name) {
        JSlider slider = new JSlider(0, 255, 0);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }
    
    private JPanel createSliderPanel(JSlider slider, String label, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel(label + ":");
        nameLabel.setForeground(color);
        nameLabel.setPreferredSize(new Dimension(30, 20));
        
        JLabel valueLabel = new JLabel("0");
        valueLabel.setPreferredSize(new Dimension(40, 20));
        valueLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        slider.addChangeListener(e -> {
            valueLabel.setText(String.valueOf(slider.getValue()));
        });
        
        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(slider, BorderLayout.CENTER);
        panel.add(valueLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void updateColor() {
        int red = redSlider.getValue();
        int green = greenSlider.getValue();
        int blue = blueSlider.getValue();
        
        Color newColor = new Color(red, green, blue);
        colorPanel.setBackground(newColor);
        
        rgbLabel.setText(String.format("RGB(%d, %d, %d)", red, green, blue));
        
        // 背景色に応じてテキスト色を調整
        int brightness = (red * 299 + green * 587 + blue * 114) / 1000;
        rgbLabel.setForeground(brightness > 128 ? Color.BLACK : Color.WHITE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChangeListenerExample().setVisible(true);
        });
    }
}
```

### FocusListenerによるフォーカス管理

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FocusListenerExample extends JFrame {
    public FocusListenerExample() {
        setTitle("FocusListener の例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));
        
        // テキストフィールドの作成
        JTextField nameField = createFocusTextField("名前を入力");
        JTextField emailField = createFocusTextField("メールアドレスを入力");
        JTextField phoneField = createFocusTextField("電話番号を入力");
        
        // フォーカストラバーサルポリシーのカスタマイズ
        setFocusTraversalPolicy(new FocusTraversalPolicy() {
            private Component[] order = {nameField, emailField, phoneField};
            
            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                for (int i = 0; i < order.length - 1; i++) {
                    if (order[i] == aComponent) {
                        return order[i + 1];
                    }
                }
                return order[0];
            }
            
            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                for (int i = 1; i < order.length; i++) {
                    if (order[i] == aComponent) {
                        return order[i - 1];
                    }
                }
                return order[order.length - 1];
            }
            
            @Override
            public Component getFirstComponent(Container aContainer) {
                return order[0];
            }
            
            @Override
            public Component getLastComponent(Container aContainer) {
                return order[order.length - 1];
            }
            
            @Override
            public Component getDefaultComponent(Container aContainer) {
                return order[0];
            }
        });
        
        // コンポーネントの追加
        add(new JLabel("名前:"));
        add(nameField);
        add(new JLabel("メール:"));
        add(emailField);
        add(new JLabel("電話番号:"));
        add(phoneField);
        add(new JLabel("")); // スペーサー
        
        JButton submitButton = new JButton("送信");
        submitButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "名前: " + nameField.getText() + "\n" +
                "メール: " + emailField.getText() + "\n" +
                "電話: " + phoneField.getText());
        });
        add(submitButton);
        
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pack();
        setLocationRelativeTo(null);
    }
    
    private JTextField createFocusTextField(String placeholder) {
        JTextField field = new JTextField(15);
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
                field.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
                field.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            }
        });
        
        return field;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FocusListenerExample().setVisible(true);
        });
    }
}
```

## まとめ

このパートでは、Swingの代表的なイベントタイプとリスナについて学習しました：

- **WindowListener**：ウィンドウイベントの処理
- **MouseListener/MouseMotionListener**：マウス操作の詳細な制御
- **KeyListener**：キーボード入力の処理とショートカットキー
- **ItemListener**：選択状態の変更の監視
- **ChangeListener**：連続的な値の変更の処理
- **FocusListener**：フォーカスの取得/喪失の処理

これらのイベントリスナを適切に組み合わせることで、ユーザーの多様な操作に対応できる、応答性の高いGUIアプリケーションを作成できます。



次のパート：[Part C - 高度なイベント処理](chapter18c-advanced-events.md)