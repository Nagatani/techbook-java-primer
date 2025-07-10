# 第18章 GUIのイベント処理 - Part C: 高度なイベント処理

## DocumentListenerによるテキスト変更の監視

```java
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.*;

public class DocumentListenerExample extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextArea commentArea;
    private JLabel emailStatus;
    private JLabel passwordStatus;
    private JLabel confirmStatus;
    private JLabel charCountLabel;
    private JButton submitButton;
    
    public DocumentListenerExample() {
        setTitle("リアルタイムバリデーション");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Email入力
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        emailField = new JTextField(20);
        add(emailField, gbc);
        
        gbc.gridx = 2;
        emailStatus = new JLabel("必須");
        emailStatus.setForeground(Color.GRAY);
        add(emailStatus, gbc);
        
        // パスワード入力
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("パスワード:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        add(passwordField, gbc);
        
        gbc.gridx = 2;
        passwordStatus = new JLabel("8文字以上");
        passwordStatus.setForeground(Color.GRAY);
        add(passwordStatus, gbc);
        
        // パスワード確認
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("パスワード確認:"), gbc);
        
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        add(confirmPasswordField, gbc);
        
        gbc.gridx = 2;
        confirmStatus = new JLabel("一致確認");
        confirmStatus.setForeground(Color.GRAY);
        add(confirmStatus, gbc);
        
        // コメント入力
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("コメント:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        commentArea = new JTextArea(5, 30);
        commentArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        add(scrollPane, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 1;
        charCountLabel = new JLabel("0/200文字");
        add(charCountLabel, gbc);
        
        // 送信ボタン
        gbc.gridx = 1; gbc.gridy = 5;
        submitButton = new JButton("送信");
        submitButton.setEnabled(false);
        add(submitButton, gbc);
        
        // DocumentListenerの追加
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateEmail(); }
            public void removeUpdate(DocumentEvent e) { validateEmail(); }
            public void changedUpdate(DocumentEvent e) { validateEmail(); }
        });
        
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validatePassword(); }
            public void removeUpdate(DocumentEvent e) { validatePassword(); }
            public void changedUpdate(DocumentEvent e) { validatePassword(); }
        });
        
        confirmPasswordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validatePasswordConfirm(); }
            public void removeUpdate(DocumentEvent e) { validatePasswordConfirm(); }
            public void changedUpdate(DocumentEvent e) { validatePasswordConfirm(); }
        });
        
        // 文字数制限付きDocumentListenter
        commentArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCharCount(); }
            public void removeUpdate(DocumentEvent e) { updateCharCount(); }
            public void changedUpdate(DocumentEvent e) { updateCharCount(); }
            
            private void updateCharCount() {
                int length = commentArea.getText().length();
                charCountLabel.setText(length + "/200文字");
                if (length > 200) {
                    charCountLabel.setForeground(Color.RED);
                    // 文字数制限を超えた場合の処理
                    SwingUtilities.invokeLater(() -> {
                        try {
                            commentArea.setText(commentArea.getText(0, 200));
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    charCountLabel.setForeground(Color.BLACK);
                }
                checkFormValidity();
            }
        });
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void validateEmail() {
        String email = emailField.getText();
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        Matcher matcher = pattern.matcher(email);
        
        if (email.isEmpty()) {
            emailStatus.setText("必須");
            emailStatus.setForeground(Color.GRAY);
            emailField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        } else if (matcher.matches()) {
            emailStatus.setText("✓ OK");
            emailStatus.setForeground(Color.GREEN);
            emailField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        } else {
            emailStatus.setText("✗ 無効");
            emailStatus.setForeground(Color.RED);
            emailField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        checkFormValidity();
    }
    
    private void validatePassword() {
        String password = new String(passwordField.getPassword());
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        
        if (password.length() >= 8 && hasUpperCase && hasLowerCase && hasDigit) {
            passwordStatus.setText("✓ 強い");
            passwordStatus.setForeground(Color.GREEN);
            passwordField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        } else if (password.length() >= 8) {
            passwordStatus.setText("△ 普通");
            passwordStatus.setForeground(Color.ORANGE);
            passwordField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        } else {
            passwordStatus.setText("✗ 弱い");
            passwordStatus.setForeground(Color.RED);
            passwordField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        validatePasswordConfirm();
        checkFormValidity();
    }
    
    private void validatePasswordConfirm() {
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        
        if (confirm.isEmpty()) {
            confirmStatus.setText("一致確認");
            confirmStatus.setForeground(Color.GRAY);
            confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        } else if (password.equals(confirm)) {
            confirmStatus.setText("✓ 一致");
            confirmStatus.setForeground(Color.GREEN);
            confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        } else {
            confirmStatus.setText("✗ 不一致");
            confirmStatus.setForeground(Color.RED);
            confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
        checkFormValidity();
    }
    
    private void checkFormValidity() {
        boolean emailValid = emailStatus.getText().contains("✓");
        boolean passwordValid = passwordStatus.getText().contains("✓") || 
                              passwordStatus.getText().contains("△");
        boolean confirmValid = confirmStatus.getText().contains("✓");
        
        submitButton.setEnabled(emailValid && passwordValid && confirmValid);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DocumentListenerExample().setVisible(true);
        });
    }
}
```

## カスタムイベントとObserverパターン

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// カスタムイベントクラス
class TemperatureChangeEvent extends EventObject {
    private final double temperature;
    
    public TemperatureChangeEvent(Object source, double temperature) {
        super(source);
        this.temperature = temperature;
    }
    
    public double getTemperature() {
        return temperature;
    }
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
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
        fireTemperatureChanged();
    }
    
    private void fireTemperatureChanged() {
        TemperatureChangeEvent event = new TemperatureChangeEvent(this, temperature);
        for (TemperatureChangeListener listener : listeners) {
            listener.temperatureChanged(event);
        }
    }
    
    public double getTemperature() {
        return temperature;
    }
}

// メインアプリケーション
public class CustomEventExample extends JFrame {
    private TemperatureSensor sensor;
    private JSlider temperatureSlider;
    private JLabel temperatureLabel;
    private JProgressBar temperatureBar;
    private JPanel colorPanel;
    private List<JLabel> observerLabels;
    
    public CustomEventExample() {
        setTitle("カスタムイベントとObserverパターン");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        sensor = new TemperatureSensor();
        
        // コントロールパネル
        JPanel controlPanel = new JPanel();
        temperatureSlider = new JSlider(-20, 50, 20);
        temperatureSlider.setMajorTickSpacing(10);
        temperatureSlider.setPaintTicks(true);
        temperatureSlider.setPaintLabels(true);
        
        temperatureSlider.addChangeListener(e -> {
            if (!temperatureSlider.getValueIsAdjusting()) {
                sensor.setTemperature(temperatureSlider.getValue());
            }
        });
        
        controlPanel.add(new JLabel("温度設定:"));
        controlPanel.add(temperatureSlider);
        
        // 表示パネル
        JPanel displayPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 温度表示
        temperatureLabel = new JLabel("現在の温度: 20.0°C");
        temperatureLabel.setFont(new Font("Arial", Font.BOLD, 20));
        displayPanel.add(temperatureLabel);
        
        // 温度バー
        temperatureBar = new JProgressBar(-20, 50);
        temperatureBar.setValue(20);
        temperatureBar.setStringPainted(true);
        displayPanel.add(temperatureBar);
        
        // 色表示パネル
        colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(200, 50));
        colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        displayPanel.add(colorPanel);
        
        // 複数のオブザーバー
        JPanel observerPanel = new JPanel(new GridLayout(3, 1));
        observerLabels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            JLabel label = new JLabel("Observer " + (i+1) + ": 待機中");
            observerLabels.add(label);
            observerPanel.add(label);
        }
        displayPanel.add(observerPanel);
        
        // リスナーの登録
        sensor.addTemperatureChangeListener(new TemperatureChangeListener() {
            @Override
            public void temperatureChanged(TemperatureChangeEvent e) {
                double temp = e.getTemperature();
                temperatureLabel.setText(String.format("現在の温度: %.1f°C", temp));
            }
        });
        
        sensor.addTemperatureChangeListener(new TemperatureChangeListener() {
            @Override
            public void temperatureChanged(TemperatureChangeEvent e) {
                int value = (int)e.getTemperature();
                temperatureBar.setValue(value);
                temperatureBar.setString(value + "°C");
            }
        });
        
        sensor.addTemperatureChangeListener(new TemperatureChangeListener() {
            @Override
            public void temperatureChanged(TemperatureChangeEvent e) {
                double temp = e.getTemperature();
                Color color;
                if (temp < 0) {
                    color = Color.BLUE;
                } else if (temp < 15) {
                    color = Color.CYAN;
                } else if (temp < 25) {
                    color = Color.GREEN;
                } else if (temp < 35) {
                    color = Color.ORANGE;
                } else {
                    color = Color.RED;
                }
                colorPanel.setBackground(color);
            }
        });
        
        // 個別のオブザーバー
        for (int i = 0; i < observerLabels.size(); i++) {
            final int index = i;
            sensor.addTemperatureChangeListener(new TemperatureChangeListener() {
                @Override
                public void temperatureChanged(TemperatureChangeEvent e) {
                    double temp = e.getTemperature();
                    String status;
                    if (index == 0) {
                        status = temp > 30 ? "警告: 高温！" : "正常";
                    } else if (index == 1) {
                        status = temp < 5 ? "警告: 低温！" : "正常";
                    } else {
                        status = String.format("%.1f°C 受信", temp);
                    }
                    observerLabels.get(index).setText("Observer " + (index+1) + ": " + status);
                }
            });
        }
        
        // 自動温度変化シミュレーション
        JButton simulateButton = new JButton("温度変化シミュレーション");
        simulateButton.addActionListener(e -> {
            Timer timer = new Timer(100, null);
            timer.addActionListener(evt -> {
                double current = sensor.getTemperature();
                double random = (Math.random() - 0.5) * 2;
                double newTemp = Math.max(-20, Math.min(50, current + random));
                sensor.setTemperature(newTemp);
                temperatureSlider.setValue((int)newTemp);
                
                if (timer.isRunning() && 
                    ((Timer)evt.getSource()).getDelay() * 
                    ((Timer)evt.getSource()).getActionListeners().length > 5000) {
                    timer.stop();
                }
            });
            timer.start();
            
            // 5秒後に停止
            Timer stopTimer = new Timer(5000, evt -> timer.stop());
            stopTimer.setRepeats(false);
            stopTimer.start();
        });
        
        controlPanel.add(simulateButton);
        
        add(controlPanel, BorderLayout.NORTH);
        add(displayPanel, BorderLayout.CENTER);
        
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

## ドラッグ&ドロップの実装

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
        setTitle("ドラッグ＆ドロップの実装");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // リスト間のドラッグ＆ドロップ
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        listsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ソースリスト
        sourceModel = new DefaultListModel<>();
        sourceModel.addElement("アイテム 1");
        sourceModel.addElement("アイテム 2");
        sourceModel.addElement("アイテム 3");
        sourceModel.addElement("アイテム 4");
        sourceModel.addElement("アイテム 5");
        
        sourceList = new JList<>(sourceModel);
        sourceList.setDragEnabled(true);
        sourceList.setTransferHandler(new ListTransferHandler());
        
        // ターゲットリスト
        targetModel = new DefaultListModel<>();
        targetList = new JList<>(targetModel);
        targetList.setTransferHandler(new ListTransferHandler());
        
        JScrollPane sourceScroll = new JScrollPane(sourceList);
        sourceScroll.setBorder(BorderFactory.createTitledBorder("ソース"));
        JScrollPane targetScroll = new JScrollPane(targetList);
        targetScroll.setBorder(BorderFactory.createTitledBorder("ターゲット"));
        
        listsPanel.add(sourceScroll);
        listsPanel.add(targetScroll);
        
        // ファイルドロップエリア
        dropArea = new JTextArea(10, 40);
        dropArea.setText("ここにファイルをドロップしてください...");
        dropArea.setEditable(false);
        JScrollPane dropScroll = new JScrollPane(dropArea);
        dropScroll.setBorder(BorderFactory.createTitledBorder("ファイルドロップエリア"));
        
        // ドロップターゲットの設定
        new DropTarget(dropArea, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                dropArea.setBackground(Color.LIGHT_GRAY);
            }
            
            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                // 必要に応じて処理
            }
            
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
                // 必要に応じて処理
            }
            
            @Override
            public void dragExit(DropTargetEvent dte) {
                dropArea.setBackground(Color.WHITE);
            }
            
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = dtde.getTransferable();
                    
                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) transferable.getTransferData(
                            DataFlavor.javaFileListFlavor);
                        
                        dropArea.setText("ドロップされたファイル:\n");
                        for (File file : files) {
                            dropArea.append("- " + file.getAbsolutePath() + "\n");
                            dropArea.append("  サイズ: " + file.length() + " bytes\n");
                            dropArea.append("  更新日時: " + new java.util.Date(
                                file.lastModified()) + "\n\n");
                        }
                    }
                    
                    dtde.dropComplete(true);
                    dropArea.setBackground(Color.WHITE);
                } catch (Exception e) {
                    e.printStackTrace();
                    dtde.dropComplete(false);
                }
            }
        });
        
        add(listsPanel, BorderLayout.CENTER);
        add(dropScroll, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    // カスタムTransferHandler
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
            
            JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
            int index = dl.getIndex();
            
            try {
                String data = (String) support.getTransferable().getTransferData(
                    DataFlavor.stringFlavor);
                
                if (support.getComponent() == targetList) {
                    if (index == -1) {
                        targetModel.addElement(data);
                    } else {
                        targetModel.add(index, data);
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return false;
        }
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            @SuppressWarnings("unchecked")
            JList<String> list = (JList<String>) c;
            String value = list.getSelectedValue();
            return new StringSelection(value);
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DragAndDropExample().setVisible(true);
        });
    }
}
```

## タイマーによる定期的なイベント処理

```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimerEventExample extends JFrame {
    private JLabel clockLabel;
    private JProgressBar progressBar;
    private JButton startButton;
    private JButton stopButton;
    private Timer clockTimer;
    private Timer progressTimer;
    private int progress = 0;
    
    public TimerEventExample() {
        setTitle("Timerによる定期的なイベント処理");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 時計パネル
        JPanel clockPanel = new JPanel();
        clockPanel.setBorder(BorderFactory.createTitledBorder("デジタル時計"));
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        clockPanel.add(clockLabel);
        
        // プログレスパネル
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createTitledBorder("プログレスアニメーション"));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        startButton = new JButton("開始");
        stopButton = new JButton("停止");
        stopButton.setEnabled(false);
        
        startButton.addActionListener(e -> startProgress());
        stopButton.addActionListener(e -> stopProgress());
        
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        progressPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 時計タイマーの設定（1秒ごと）
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock(); // 初回表示
        
        // アニメーションパネル
        AnimationPanel animationPanel = new AnimationPanel();
        animationPanel.setBorder(BorderFactory.createTitledBorder("アニメーション"));
        
        // レイアウト
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(progressPanel);
        centerPanel.add(animationPanel);
        
        add(clockPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        setSize(600, 500);
        setLocationRelativeTo(null);
    }
    
    private void updateClock() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        clockLabel.setText(now.format(formatter));
    }
    
    private void startProgress() {
        progress = 0;
        progressBar.setValue(0);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        
        progressTimer = new Timer(50, e -> {
            progress++;
            progressBar.setValue(progress);
            
            if (progress >= 100) {
                stopProgress();
                JOptionPane.showMessageDialog(this, "処理が完了しました！");
            }
        });
        progressTimer.start();
    }
    
    private void stopProgress() {
        if (progressTimer != null) {
            progressTimer.stop();
        }
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }
    
    // アニメーションパネル
    class AnimationPanel extends JPanel {
        private int x = 0;
        private int y = 100;
        private int dx = 2;
        private int dy = 2;
        private Timer animationTimer;
        
        public AnimationPanel() {
            setPreferredSize(new Dimension(400, 200));
            setBackground(Color.BLACK);
            
            animationTimer = new Timer(20, e -> {
                x += dx;
                y += dy;
                
                if (x <= 0 || x >= getWidth() - 50) {
                    dx = -dx;
                }
                if (y <= 0 || y >= getHeight() - 50) {
                    dy = -dy;
                }
                
                repaint();
            });
            animationTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // アンチエイリアシング
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
            
            // ボールを描画
            g2d.setColor(Color.RED);
            g2d.fillOval(x, y, 50, 50);
            
            // 軌跡を表示
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.drawLine(x + 25, y + 25, x + 25 - dx * 10, y + 25 - dy * 10);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TimerEventExample().setVisible(true);
        });
    }
}
```

## まとめ

このパートでは、高度なイベント処理技術について学習しました：

- **DocumentListener**：テキストの変更をリアルタイムで監視し、バリデーションを実装
- **カスタムイベント**：独自のイベントとリスナを作成し、Observerパターンを実装
- **ドラッグ&ドロップ**：ファイルやコンポーネント間でのデータ転送を実現
- **Timer**：定期的なイベント処理によるアニメーションや自動更新

これらの高度な技術を組み合わせることで、プロフェッショナルレベルの対話的なGUIアプリケーションを作成できます。



次のパート：[Part D - 章末演習](chapter18d-exercises.md)