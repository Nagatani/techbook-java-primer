# <b>19章</b> <span>高度なイベント処理パターン</span> <small>プロフェッショナルなGUIアプリケーションの実装</small>

## 本章の学習目標

### この章で学ぶこと

本章では、プロフェッショナルレベルのGUIアプリケーションを実装するための高度なイベント処理技術を学習します。
具体的には以下の内容を扱います。

1. 複雑なイベント処理の実装
    - マウスイベントの完全な処理とドラッグ&ドロップ
    - キーボードイベントの高度な処理とショートカット
    - DocumentListenerによるテキスト変更の監視
2. カスタムイベントとパターン
    - JavaBeansイベントモデルの実装
    - Observerパターンによるイベント通知
    - カスタムイベントの作成と活用
3. パフォーマンスの最適化
    - イベント処理の最適化技術
    - 非同期処理とSwingWorker
    - メモリ効率を考慮した実装

### この章を始める前に

前章の基本的なイベント処理の知識に加え、デザインパターン（とくにObserverパターン）の概念的理解があると、より深くカスタムイベントの設計を理解できます。

## 詳細なイベント処理の実装

### マウスイベントの完全な処理

前章では基本的なマウスクリックを扱いましたが、実際のアプリケーションでは、より複雑なマウス操作が必要になります。

<span class="listing-number">**サンプルコード19-9**</span>

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
            }
            
            private String getButtonName(int button) {
                return switch (button) {
                    case MouseEvent.BUTTON1 -> "左ボタン";
                    case MouseEvent.BUTTON2 -> "中央ボタン";
                    case MouseEvent.BUTTON3 -> "右ボタン";
                    default -> "不明なボタン";
                };
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

複数キーの同時押しやショートカットキーの実装例です。

<span class="listing-number">**サンプルコード19-10**</span>

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
                // 文字入力の検出
                char ch = e.getKeyChar();
                if (!Character.isISOControl(ch)) {
                    statusLabel.setText("入力文字: '" + ch + "'");
                }
            }
        });
        
        // InputMapとActionMapを使ったより良いアプローチ
        setupKeyBindings();
        
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupKeyBindings() {
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();
        
        // Ctrl+Dで行削除
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), 
            "delete-line");
        actionMap.put("delete-line", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteLine();
            }
        });
        
        // F5で更新
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        actionMap.put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
    }
    
    private void updateStatus(KeyEvent e, String action) {
        String modifiers = "";
        if (e.isControlDown()) modifiers += "Ctrl+";
        if (e.isShiftDown()) modifiers += "Shift+";
        if (e.isAltDown()) modifiers += "Alt+";
        
        statusLabel.setText(action + ": " + modifiers + 
            KeyEvent.getKeyText(e.getKeyCode()));
    }
    
    private void saveDocument() {
        JOptionPane.showMessageDialog(this, "ドキュメントを保存しました");
    }
    
    private void showHelp() {
        JOptionPane.showMessageDialog(this, "F1: ヘルプ\nCtrl+S: 保存\nCtrl+A: 全選択");
    }
    
    private void duplicateLine() {
        try {
            int caretPosition = textArea.getCaretPosition();
            int lineNumber = textArea.getLineOfOffset(caretPosition);
            int lineStart = textArea.getLineStartOffset(lineNumber);
            int lineEnd = textArea.getLineEndOffset(lineNumber);
            String line = textArea.getText(lineStart, lineEnd - lineStart);
            textArea.insert(line, lineEnd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void deleteLine() {
        try {
            int caretPosition = textArea.getCaretPosition();
            int lineNumber = textArea.getLineOfOffset(caretPosition);
            int lineStart = textArea.getLineStartOffset(lineNumber);
            int lineEnd = textArea.getLineEndOffset(lineNumber);
            textArea.replaceRange("", lineStart, lineEnd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void refresh() {
        textArea.setText("");
        statusLabel.setText("内容をクリアしました");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new KeyboardEventAdvancedExample().setVisible(true);
        });
    }
}
```

### DocumentListenerによるテキスト変更の監視

テキストフィールドやテキストエリアの内容変更をリアルタイムで監視する例です。

<span class="listing-number">**サンプルコード19-11**</span>

```java
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.*;

public class DocumentListenerExample extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JTextArea statusArea;
    private JButton submitButton;
    
    public DocumentListenerExample() {
        setTitle("DocumentListenerによるバリデーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Email フィールド
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);
        
        emailField = new JTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);
        
        // パスワードフィールド
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("パスワード:"), gbc);
        
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);
        
        // 確認フィールド
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("確認:"), gbc);
        
        confirmField = new JPasswordField(20);
        gbc.gridx = 1;
        add(confirmField, gbc);
        
        // ステータスエリア
        statusArea = new JTextArea(5, 30);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(statusArea);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(scrollPane, gbc);
        
        // 送信ボタン
        submitButton = new JButton("送信");
        submitButton.setEnabled(false);
        gbc.gridy = 4;
        add(submitButton, gbc);
        
        // DocumentListenerの追加
        setupDocumentListeners();
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupDocumentListeners() {
        // Emailフィールドのリスナー
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateForm(); }
        });
        
        // パスワードフィールドのリスナー
        DocumentListener passwordListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateForm(); }
        };
        
        passwordField.getDocument().addDocumentListener(passwordListener);
        confirmField.getDocument().addDocumentListener(passwordListener);
    }
    
    private void validateForm() {
        StringBuilder status = new StringBuilder();
        boolean isValid = true;
        
        // Emailの検証
        String email = emailField.getText();
        if (email.isEmpty()) {
            status.append("❌ Emailを入力してください\n");
            emailField.setBackground(Color.PINK);
            isValid = false;
        } else if (!isValidEmail(email)) {
            status.append("❌ 有効なEmailアドレスを入力してください\n");
            emailField.setBackground(Color.PINK);
            isValid = false;
        } else {
            status.append("✓ Email: OK\n");
            emailField.setBackground(Color.WHITE);
        }
        
        // パスワードの検証
        char[] password = passwordField.getPassword();
        if (password.length == 0) {
            status.append("❌ パスワードを入力してください\n");
            passwordField.setBackground(Color.PINK);
            isValid = false;
        } else if (password.length < 8) {
            status.append("❌ パスワードは8文字以上必要です\n");
            passwordField.setBackground(Color.PINK);
            isValid = false;
        } else {
            status.append("✓ パスワード: OK (");
            status.append(getPasswordStrength(password));
            status.append(")\n");
            passwordField.setBackground(Color.WHITE);
        }
        
        // 確認パスワードの検証
        char[] confirm = confirmField.getPassword();
        if (confirm.length == 0) {
            status.append("❌ 確認パスワードを入力してください\n");
            confirmField.setBackground(Color.PINK);
            isValid = false;
        } else if (!java.util.Arrays.equals(password, confirm)) {
            status.append("❌ パスワードが一致しません\n");
            confirmField.setBackground(Color.PINK);
            isValid = false;
        } else {
            status.append("✓ 確認パスワード: 一致\n");
            confirmField.setBackground(Color.WHITE);
        }
        
        // クリーンアップ
        java.util.Arrays.fill(password, ' ');
        java.util.Arrays.fill(confirm, ' ');
        
        statusArea.setText(status.toString());
        submitButton.setEnabled(isValid);
    }
    
    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(email).matches();
    }
    
    private String getPasswordStrength(char[] password) {
        int score = 0;
        if (password.length >= 12) score++;
        if (String.valueOf(password).matches(".*[A-Z].*")) score++;
        if (String.valueOf(password).matches(".*[a-z].*")) score++;
        if (String.valueOf(password).matches(".*[0-9].*")) score++;
        if (String.valueOf(password).matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;
        
        return switch (score) {
            case 0, 1 -> "弱い";
            case 2, 3 -> "普通";
            case 4, 5 -> "強い";
            default -> "不明";
        };
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DocumentListenerExample().setVisible(true);
        });
    }
}
```

## カスタムイベントとObserverパターン

### カスタムイベントの作成

独自のイベントを作成して、コンポーネント間の疎結合な通信を実現します。

<span class="listing-number">**サンプルコード19-12**</span>

```java
import java.util.*;
import javax.swing.*;
import java.awt.*;

// カスタムイベントクラス
class TemperatureChangeEvent extends EventObject {
    private final double oldTemperature;
    private final double newTemperature;
    
    public TemperatureChangeEvent(Object source, double oldTemp, double newTemp) {
        super(source);
        this.oldTemperature = oldTemp;
        this.newTemperature = newTemp;
    }
    
    public double getOldTemperature() { return oldTemperature; }
    public double getNewTemperature() { return newTemperature; }
}

// カスタムリスナーインターフェース
interface TemperatureChangeListener extends EventListener {
    void temperatureChanged(TemperatureChangeEvent e);
}

// 温度センサーモデル
class TemperatureSensor {
    private double temperature = 20.0;
    private List<TemperatureChangeListener> listeners = new ArrayList<>();
    
    public void addTemperatureChangeListener(TemperatureChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeTemperatureChangeListener(TemperatureChangeListener listener) {
        listeners.remove(listener);
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double newTemperature) {
        double oldTemperature = this.temperature;
        this.temperature = newTemperature;
        fireTemperatureChanged(oldTemperature, newTemperature);
    }
    
    protected void fireTemperatureChanged(double oldTemp, double newTemp) {
        TemperatureChangeEvent event = new TemperatureChangeEvent(this, oldTemp, newTemp);
        for (TemperatureChangeListener listener : new ArrayList<>(listeners)) {
            listener.temperatureChanged(event);
        }
    }
}

// メインアプリケーション
public class CustomEventExample extends JFrame {
    private TemperatureSensor sensor;
    private JLabel temperatureLabel;
    private JSlider temperatureSlider;
    private JTextArea logArea;
    
    public CustomEventExample() {
        setTitle("カスタムイベントの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        sensor = new TemperatureSensor();
        
        // 温度表示パネル
        JPanel displayPanel = new JPanel();
        temperatureLabel = new JLabel(String.format("%.1f°C", sensor.getTemperature()));
        temperatureLabel.setFont(new Font("Arial", Font.BOLD, 48));
        displayPanel.add(temperatureLabel);
        
        // 温度調整スライダー
        temperatureSlider = new JSlider(-20, 50, (int)sensor.getTemperature());
        temperatureSlider.setMajorTickSpacing(10);
        temperatureSlider.setMinorTickSpacing(5);
        temperatureSlider.setPaintTicks(true);
        temperatureSlider.setPaintLabels(true);
        temperatureSlider.addChangeListener(e -> {
            if (!temperatureSlider.getValueIsAdjusting()) {
                sensor.setTemperature(temperatureSlider.getValue());
            }
        });
        
        // ログエリア
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        // 温度変化リスナーを登録
        sensor.addTemperatureChangeListener(new TemperatureChangeListener() {
            @Override
            public void temperatureChanged(TemperatureChangeEvent e) {
                double newTemp = e.getNewTemperature();
                double oldTemp = e.getOldTemperature();
                
                // 表示を更新
                temperatureLabel.setText(String.format("%.1f°C", newTemp));
                
                // 温度に応じて背景色を変更
                if (newTemp < 0) {
                    displayPanel.setBackground(Color.CYAN);
                } else if (newTemp < 20) {
                    displayPanel.setBackground(Color.GREEN);
                } else if (newTemp < 30) {
                    displayPanel.setBackground(Color.YELLOW);
                } else {
                    displayPanel.setBackground(Color.RED);
                }
                
                // ログに記録
                String change = newTemp > oldTemp ? "上昇" : "下降";
                logArea.append(String.format("温度%s: %.1f°C → %.1f°C\n", 
                    change, oldTemp, newTemp));
                
                // 警告表示
                if (newTemp > 40) {
                    JOptionPane.showMessageDialog(CustomEventExample.this,
                        "警告: 高温です！", "温度警告", JOptionPane.WARNING_MESSAGE);
                } else if (newTemp < -10) {
                    JOptionPane.showMessageDialog(CustomEventExample.this,
                        "警告: 低温です！", "温度警告", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        // 別のリスナー（統計情報の記録）
        sensor.addTemperatureChangeListener(new TemperatureChangeListener() {
            private double maxTemp = sensor.getTemperature();
            private double minTemp = sensor.getTemperature();
            
            @Override
            public void temperatureChanged(TemperatureChangeEvent e) {
                double temp = e.getNewTemperature();
                if (temp > maxTemp) {
                    maxTemp = temp;
                    logArea.append(String.format("新しい最高温度: %.1f°C\n", maxTemp));
                }
                if (temp < minTemp) {
                    minTemp = temp;
                    logArea.append(String.format("新しい最低温度: %.1f°C\n", minTemp));
                }
            }
        });
        
        add(displayPanel, BorderLayout.NORTH);
        add(temperatureSlider, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomEventExample().setVisible(true);
        });
    }
}
```

### JavaBeansイベントモデルの実装

より高度なイベント管理のためのJavaBeansパターンの実装例です。

<span class="listing-number">**サンプルコード19-13**</span>

```java
import java.beans.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

// モデルクラス（PropertyChangeSupport を使用）
class StockPrice {
    private String symbol;
    private double price;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public StockPrice(String symbol, double initialPrice) {
        this.symbol = symbol;
        this.price = initialPrice;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }
    
    public String getSymbol() { return symbol; }
    
    public double getPrice() { return price; }
    
    public void setPrice(double newPrice) {
        double oldPrice = this.price;
        this.price = newPrice;
        pcs.firePropertyChange("price", oldPrice, newPrice);
    }
}

// 株価表示コンポーネント
class StockPricePanel extends JPanel implements PropertyChangeListener {
    private JLabel symbolLabel;
    private JLabel priceLabel;
    private JLabel changeLabel;
    private double previousPrice;
    
    public StockPricePanel(StockPrice stock) {
        setLayout(new GridLayout(3, 1));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        symbolLabel = new JLabel(stock.getSymbol(), JLabel.CENTER);
        symbolLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        priceLabel = new JLabel(String.format("$%.2f", stock.getPrice()), JLabel.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        
        changeLabel = new JLabel("0.00 (0.00%)", JLabel.CENTER);
        
        add(symbolLabel);
        add(priceLabel);
        add(changeLabel);
        
        previousPrice = stock.getPrice();
        stock.addPropertyChangeListener("price", this);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("price".equals(evt.getPropertyName())) {
            double newPrice = (Double) evt.getNewValue();
            double oldPrice = (Double) evt.getOldValue();
            
            priceLabel.setText(String.format("$%.2f", newPrice));
            
            double change = newPrice - oldPrice;
            double changePercent = (change / oldPrice) * 100;
            
            changeLabel.setText(String.format("%+.2f (%+.2f%%)", change, changePercent));
            
            // 色の変更
            if (change > 0) {
                priceLabel.setForeground(Color.GREEN);
                changeLabel.setForeground(Color.GREEN);
                setBackground(new Color(200, 255, 200));
            } else if (change < 0) {
                priceLabel.setForeground(Color.RED);
                changeLabel.setForeground(Color.RED);
                setBackground(new Color(255, 200, 200));
            } else {
                priceLabel.setForeground(Color.BLACK);
                changeLabel.setForeground(Color.BLACK);
                setBackground(Color.WHITE);
            }
        }
    }
}

// メインアプリケーション
public class JavaBeansEventExample extends JFrame {
    private Map<String, StockPrice> stocks = new HashMap<>();
    private JPanel stocksPanel;
    private Timer updateTimer;
    
    public JavaBeansEventExample() {
        setTitle("JavaBeansイベントモデルの例（株価モニター）");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 株価データの初期化
        stocks.put("AAPL", new StockPrice("AAPL", 150.00));
        stocks.put("GOOGL", new StockPrice("GOOGL", 2800.00));
        stocks.put("MSFT", new StockPrice("MSFT", 300.00));
        stocks.put("AMZN", new StockPrice("AMZN", 3300.00));
        
        // 株価表示パネル
        stocksPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        stocksPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (StockPrice stock : stocks.values()) {
            stocksPanel.add(new StockPricePanel(stock));
        }
        
        // コントロールパネル
        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("更新開始");
        JButton stopButton = new JButton("更新停止");
        stopButton.setEnabled(false);
        
        startButton.addActionListener(e -> {
            startPriceUpdates();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        });
        
        stopButton.addActionListener(e -> {
            stopPriceUpdates();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        });
        
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        
        add(stocksPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void startPriceUpdates() {
        updateTimer = new Timer(1000, e -> {
            Random random = new Random();
            for (StockPrice stock : stocks.values()) {
                // ランダムな価格変動（-2%～+2%）
                double change = (random.nextDouble() - 0.5) * 0.04;
                double newPrice = stock.getPrice() * (1 + change);
                stock.setPrice(newPrice);
            }
        });
        updateTimer.start();
    }
    
    private void stopPriceUpdates() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JavaBeansEventExample().setVisible(true);
        });
    }
}
```

## ドラッグ&ドロップの実装

### 基本的なドラッグ&ドロップ

コンポーネント間でのドラッグ&ドロップ機能の実装例です。

<span class="listing-number">**サンプルコード19-14**</span>

```java
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

public class DragAndDropExample extends JFrame {
    private JList<String> sourceList;
    private JList<String> targetList;
    private DefaultListModel<String> sourceModel;
    private DefaultListModel<String> targetModel;
    private JTextArea dropArea;
    
    public DragAndDropExample() {
        setTitle("ドラッグ&ドロップの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // リストの作成
        sourceModel = new DefaultListModel<>();
        sourceModel.addElement("アイテム1");
        sourceModel.addElement("アイテム2");
        sourceModel.addElement("アイテム3");
        sourceModel.addElement("アイテム4");
        sourceModel.addElement("アイテム5");
        
        targetModel = new DefaultListModel<>();
        
        sourceList = new JList<>(sourceModel);
        targetList = new JList<>(targetModel);
        
        // ドラッグ機能の設定
        sourceList.setDragEnabled(true);
        sourceList.setTransferHandler(new ListTransferHandler());
        
        // ドロップ機能の設定
        targetList.setTransferHandler(new ListTransferHandler());
        
        // パネルの作成
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        listsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane sourceScroll = new JScrollPane(sourceList);
        sourceScroll.setBorder(BorderFactory.createTitledBorder("ソース"));
        
        JScrollPane targetScroll = new JScrollPane(targetList);
        targetScroll.setBorder(BorderFactory.createTitledBorder("ターゲット"));
        
        listsPanel.add(sourceScroll);
        listsPanel.add(targetScroll);
        
        // ファイルドロップエリア
        dropArea = new JTextArea(10, 40);
        dropArea.setBorder(BorderFactory.createTitledBorder("ファイルをここにドロップ"));
        dropArea.setEditable(false);
        
        // ファイルドロップの設定
        new DropTarget(dropArea, new FileDropTargetListener());
        
        add(listsPanel, BorderLayout.CENTER);
        add(new JScrollPane(dropArea), BorderLayout.SOUTH);
        
        setSize(600, 400);
        setLocationRelativeTo(null);
    }
    
    // リスト用のTransferHandler
    class ListTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }
        
        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            
            JList<String> list = (JList<String>) support.getComponent();
            DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
            
            try {
                String data = (String) support.getTransferable()
                    .getTransferData(DataFlavor.stringFlavor);
                
                JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
                int index = dl.getIndex();
                
                if (index == -1) {
                    model.addElement(data);
                } else {
                    model.add(index, data);
                }
                
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            JList<String> list = (JList<String>) c;
            String value = list.getSelectedValue();
            return new StringSelection(value);
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }
        
        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == MOVE) {
                JList<String> list = (JList<String>) source;
                DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
                model.removeElement(list.getSelectedValue());
            }
        }
    }
    
    // ファイルドロップ用のリスナー
    class FileDropTargetListener extends DropTargetAdapter {
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = dtde.getTransferable();
                
                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    List<File> files = (List<File>) transferable
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    
                    for (File file : files) {
                        dropArea.append("ドロップされたファイル: " + file.getAbsolutePath() + "\n");
                        dropArea.append("  サイズ: " + file.length() + " bytes\n");
                        dropArea.append("  更新日時: " + new Date(file.lastModified()) + "\n\n");
                    }
                }
                
                dtde.dropComplete(true);
            } catch (Exception e) {
                e.printStackTrace();
                dtde.dropComplete(false);
            }
        }
        
        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            dropArea.setBackground(Color.LIGHT_GRAY);
        }
        
        @Override
        public void dragExit(DropTargetEvent dte) {
            dropArea.setBackground(Color.WHITE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DragAndDropExample().setVisible(true);
        });
    }
}
```

## パフォーマンスの最適化

### イベント処理の最適化技術

高頻度で発生するイベントの最適化例です。

<span class="listing-number">**サンプルコード19-15**</span>

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;

public class EventOptimizationExample extends JFrame {
    private JPanel drawPanel;
    private JLabel statusLabel;
    private JTextArea logArea;
    private Point currentMousePos;
    private Timer debounceTimer;
    private ScheduledExecutorService throttleExecutor;
    private long lastThrottleTime = 0;
    private static final int DEBOUNCE_DELAY = 300; // ミリ秒
    private static final int THROTTLE_DELAY = 100; // ミリ秒
    
    public EventOptimizationExample() {
        setTitle("イベント処理の最適化");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 描画パネル
        drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentMousePos != null) {
                    g.setColor(Color.RED);
                    g.fillOval(currentMousePos.x - 5, currentMousePos.y - 5, 10, 10);
                }
            }
        };
        drawPanel.setBackground(Color.WHITE);
        drawPanel.setPreferredSize(new Dimension(400, 300));
        drawPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // ステータスラベル
        statusLabel = new JLabel("マウスを動かしてください");
        
        // ログエリア
        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        // 通常のマウス移動イベント（最適化なし）
        drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            private int eventCount = 0;
            
            @Override
            public void mouseMoved(MouseEvent e) {
                eventCount++;
                statusLabel.setText(String.format("マウス位置: (%d, %d) - イベント数: %d", 
                    e.getX(), e.getY(), eventCount));
                
                // デバウンス処理
                debounceMouseMove(e.getPoint());
                
                // スロットル処理
                throttleMouseMove(e.getPoint());
            }
        });
        
        // デバウンスタイマーの初期化
        debounceTimer = new Timer(DEBOUNCE_DELAY, e -> {
            log("デバウンス: マウス移動が停止しました - " + currentMousePos);
        });
        debounceTimer.setRepeats(false);
        
        // スロットル用のExecutor
        throttleExecutor = Executors.newSingleThreadScheduledExecutor();
        
        // コントロールパネル
        JPanel controlPanel = new JPanel();
        JButton clearButton = new JButton("ログクリア");
        clearButton.addActionListener(e -> logArea.setText(""));
        controlPanel.add(clearButton);
        
        add(drawPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    // デバウンス: 最後のイベントから一定時間後に処理
    private void debounceMouseMove(Point point) {
        currentMousePos = point;
        debounceTimer.restart();
    }
    
    // スロットル: 一定間隔で処理
    private void throttleMouseMove(Point point) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastThrottleTime >= THROTTLE_DELAY) {
            lastThrottleTime = currentTime;
            drawPanel.repaint();
            log("スロットル: 描画更新 - " + point);
        }
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    @Override
    public void dispose() {
        super.dispose();
        throttleExecutor.shutdown();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EventOptimizationExample().setVisible(true);
        });
    }
}
```

### SwingWorkerによる非同期処理

長時間かかる処理をバックグラウンドで実行する例です。

<span class="listing-number">**サンプルコード19-16**</span>

```java
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class SwingWorkerExample extends JFrame {
    private JProgressBar progressBar;
    private JTextArea resultArea;
    private JButton startButton;
    private JButton cancelButton;
    private DataProcessingWorker currentWorker;
    
    public SwingWorkerExample() {
        setTitle("SwingWorkerによる非同期処理");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // プログレスバー
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        
        // 結果表示エリア
        resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        startButton = new JButton("処理開始");
        cancelButton = new JButton("キャンセル");
        cancelButton.setEnabled(false);
        
        startButton.addActionListener(e -> startProcessing());
        cancelButton.addActionListener(e -> cancelProcessing());
        
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);
        
        // レイアウト
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(progressBar, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void startProcessing() {
        startButton.setEnabled(false);
        cancelButton.setEnabled(true);
        resultArea.setText("処理を開始しています...\n");
        progressBar.setValue(0);
        
        currentWorker = new DataProcessingWorker();
        currentWorker.execute();
    }
    
    private void cancelProcessing() {
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
            resultArea.append("処理がキャンセルされました。\n");
        }
    }
    
    // SwingWorkerの実装
    class DataProcessingWorker extends SwingWorker<String, ProgressInfo> {
        private static final int TOTAL_ITEMS = 100;
        
        @Override
        protected String doInBackground() throws Exception {
            Random random = new Random();
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < TOTAL_ITEMS; i++) {
                if (isCancelled()) {
                    return "処理がキャンセルされました";
                }
                
                // 重い処理のシミュレーション
                Thread.sleep(50);
                
                // 仮のデータ処理
                int value = random.nextInt(1000);
                result.append("Item ").append(i).append(": ").append(value).append("\n");
                
                // 進捗を公開
                int progress = (i + 1) * 100 / TOTAL_ITEMS;
                publish(new ProgressInfo(progress, "処理中: アイテム " + (i + 1) + "/" + TOTAL_ITEMS));
            }
            
            return result.toString();
        }
        
        @Override
        protected void process(List<ProgressInfo> chunks) {
            // 最新の進捗情報を使用
            ProgressInfo latest = chunks.get(chunks.size() - 1);
            progressBar.setValue(latest.progress);
            progressBar.setString(latest.message);
            resultArea.append(latest.message + "\n");
        }
        
        @Override
        protected void done() {
            startButton.setEnabled(true);
            cancelButton.setEnabled(false);
            
            try {
                if (!isCancelled()) {
                    String result = get();
                    resultArea.append("\n処理が完了しました。\n");
                    resultArea.append("結果のサマリー:\n");
                    resultArea.append("処理したアイテム数: " + TOTAL_ITEMS + "\n");
                    progressBar.setValue(100);
                    progressBar.setString("完了");
                }
            } catch (InterruptedException | ExecutionException e) {
                resultArea.append("エラーが発生しました: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }
    
    // 進捗情報クラス
    class ProgressInfo {
        final int progress;
        final String message;
        
        ProgressInfo(int progress, String message) {
            this.progress = progress;
            this.message = message;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SwingWorkerExample().setVisible(true);
        });
    }
}
```

## まとめ

本章では、プロフェッショナルレベルのGUIアプリケーションを実装するための高度なイベント処理技術について学習しました。

### 重要なポイント
- マウスとキーボードイベントの完全な処理方法
- DocumentListenerによるリアルタイムテキスト監視
- カスタムイベントとJavaBeansイベントモデル
- ドラッグ&ドロップの実装
- パフォーマンス最適化（デバウンス、スロットル）
- SwingWorkerによる非同期処理

### 実践での活用
これらの技術を組み合わせることで、応答性が高く、ユーザーフレンドリーなGUIアプリケーションを開発できます。とくに、長時間処理とUI更新の分離、イベント処理の最適化は、実務での開発において重要な技術です。

## 章末演習

### 演習課題へのアクセス

本章の理解を深めるための演習課題を用意しています。以下のGitHubリポジトリで、実践的な問題に挑戦してください。

**演習課題URL**: https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter19-advanced/

### 課題構成

演習は以下の実践的な課題で構成されています。

- インタラクティブ描画アプリケーション: 高度なマウスイベント処理
- フォームバリデータ: DocumentListenerを活用したリアルタイム検証
- 高度なメニューシステム: Actionパターンと動的メニュー
- マウストラッカー: 詳細なマウス動作の分析

各演習には、本章で学んだ技術を実践的に活用するための詳細な要件が含まれています。