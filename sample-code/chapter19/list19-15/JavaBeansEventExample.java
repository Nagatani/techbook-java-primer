/**
 * リスト19-15
 * JavaBeansEventExampleクラス
 * 
 * 元ファイル: chapter19-gui-event-handling.md (1483行目)
 */

import java.beans.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

// 1. イベントクラスの定義（PropertyChangeEventを継承または独自実装）
class TemperatureEvent extends PropertyChangeEvent {
    public TemperatureEvent(Object source, String propertyName, 
                           Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }
    
    public double getOldTemperature() {
        return ((Number)getOldValue()).doubleValue();
    }
    
    public double getNewTemperature() {
        return ((Number)getNewValue()).doubleValue();
    }
}

// 2. リスナーインターフェイスの定義
interface TemperatureListener extends EventListener {
    void temperatureChanged(TemperatureEvent e);
}

// 3. JavaBeans準拠のモデルクラス
class TemperatureSensorBean {
    private double temperature = 20.0;
    private String sensorName = "DefaultSensor";
    private PropertyChangeSupport propertySupport;
    private List<TemperatureListener> temperatureListeners;
    
    public TemperatureSensorBean() {
        propertySupport = new PropertyChangeSupport(this);
        temperatureListeners = new ArrayList<>();
    }
    
    // --- JavaBeans準拠のプロパティ ---
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double newTemperature) {
        double oldTemperature = this.temperature;
        this.temperature = newTemperature;
        
        // PropertyChangeEventを発行
        propertySupport.firePropertyChange("temperature", oldTemperature, newTemperature);
        
        // カスタムイベントを発行
        fireTemperatureChanged(oldTemperature, newTemperature);
    }
    
    public String getSensorName() {
        return sensorName;
    }
    
    public void setSensorName(String sensorName) {
        String oldName = this.sensorName;
        this.sensorName = sensorName;
        propertySupport.firePropertyChange("sensorName", oldName, sensorName);
    }
    
    // --- PropertyChangeListener関連（JavaBeans標準） ---
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }
    
    // --- カスタムリスナー関連 ---
    
    public void addTemperatureListener(TemperatureListener listener) {
        temperatureListeners.add(listener);
    }
    
    public void removeTemperatureListener(TemperatureListener listener) {
        temperatureListeners.remove(listener);
    }
    
    private void fireTemperatureChanged(double oldTemperature, double newTemperature) {
        TemperatureEvent event = new TemperatureEvent(this, "temperature", 
                                                     oldTemperature, newTemperature);
        for (TemperatureListener listener : temperatureListeners) {
            listener.temperatureChanged(event);
        }
    }
    
    // --- ユーティリティメソッド ---
    
    public void simulateTemperatureChange() {
        double change = (Math.random() - 0.5) * 4; // -2℃ から +2℃ の変化
        setTemperature(Math.max(-30, Math.min(50, temperature + change)));
    }
}

// 4. 複数の通知方式を実装したGUIアプリケーション
public class JavaBeansEventExample extends JFrame {
    private TemperatureSensorBean sensor;
    private JSlider temperatureSlider;
    private JLabel temperatureLabel;
    private JLabel sensorNameLabel;
    private JTextField sensorNameField;
    private JTextArea eventLogArea;
    private JPanel visualIndicator;
    private Timer simulationTimer;
    
    public JavaBeansEventExample() {
        setTitle("JavaBeansイベントモデルの実装");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        initializeComponents();
        setupSensorBean();
        layoutComponents();
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        // 温度制御
        temperatureSlider = new JSlider(-30, 50, 20);
        temperatureSlider.setMajorTickSpacing(10);
        temperatureSlider.setMinorTickSpacing(5);
        temperatureSlider.setPaintTicks(true);
        temperatureSlider.setPaintLabels(true);
        
        // 表示要素
        temperatureLabel = new JLabel("温度: 20.0°C");
        temperatureLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        sensorNameLabel = new JLabel("センサー名: DefaultSensor");
        sensorNameField = new JTextField("DefaultSensor", 15);
        
        visualIndicator = new JPanel();
        visualIndicator.setPreferredSize(new Dimension(100, 50));
        visualIndicator.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        eventLogArea = new JTextArea(10, 40);
        eventLogArea.setEditable(false);
        eventLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }
    
    private void setupSensorBean() {
        sensor = new TemperatureSensorBean();
        
        // PropertyChangeListenerの登録（JavaBeans標準）
        sensor.addPropertyChangeListener("temperature", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                double newTemp = (Double)evt.getNewValue();
                temperatureLabel.setText(String.format("温度: %.1f°C", newTemp));
                temperatureSlider.setValue((int)newTemp);
                
                // 視覚的インジケータの更新
                Color color = getTemperatureColor(newTemp);
                visualIndicator.setBackground(color);
                
                logEvent("PropertyChange", String.format("温度変更: %.1f°C → %.1f°C", 
                    (Double)evt.getOldValue(), newTemp));
            }
        });
        
        sensor.addPropertyChangeListener("sensorName", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String newName = (String)evt.getNewValue();
                sensorNameLabel.setText("センサー名: " + newName);
                logEvent("PropertyChange", String.format("センサー名変更: %s → %s", 
                    evt.getOldValue(), newName));
            }
        });
        
        // カスタムTemperatureListenerの登録
        sensor.addTemperatureListener(new TemperatureListener() {
            @Override
            public void temperatureChanged(TemperatureEvent e) {
                double temp = e.getNewTemperature();
                String message = "";
                
                if (temp > 35) {
                    message = "⚠️ 高温警告!";
                } else if (temp < 0) {
                    message = "❄️ 氷点下警告!";
                } else if (temp >= 20 && temp <= 25) {
                    message = "✅ 適温です";
                } else {
                    message = "🌡️ 温度監視中";
                }
                
                logEvent("TemperatureListener", String.format("温度監視: %.1f°C - %s", 
                    temp, message));
            }
        });
        
        // スライダーのイベントリスナー
        temperatureSlider.addChangeListener(e -> {
            if (!temperatureSlider.getValueIsAdjusting()) {
                sensor.setTemperature(temperatureSlider.getValue());
            }
        });
        
        // センサー名フィールドのイベントリスナー
        sensorNameField.addActionListener(e -> {
            sensor.setSensorName(sensorNameField.getText());
        });
    }
    
    private void layoutComponents() {
        // 上部コントロールパネル
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("温度設定:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        controlPanel.add(temperatureSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        controlPanel.add(new JLabel("センサー名:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(sensorNameField, gbc);
        gbc.gridx = 2;
        JButton updateButton = new JButton("更新");
        updateButton.addActionListener(e -> sensor.setSensorName(sensorNameField.getText()));
        controlPanel.add(updateButton, gbc);
        
        // 中央表示パネル
        JPanel displayPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        displayPanel.setBorder(BorderFactory.createTitledBorder("センサー情報"));
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(temperatureLabel);
        infoPanel.add(sensorNameLabel);
        
        JPanel indicatorPanel = new JPanel(new BorderLayout());
        indicatorPanel.add(new JLabel("温度インジケータ", JLabel.CENTER), BorderLayout.NORTH);
        indicatorPanel.add(visualIndicator, BorderLayout.CENTER);
        
        // シミュレーションコントロール
        JPanel simPanel = new JPanel(new GridLayout(3, 1));
        JButton simButton = new JButton("シミュレーション開始");
        JButton stopButton = new JButton("シミュレーション停止");
        JButton resetButton = new JButton("リセット");
        
        simButton.addActionListener(e -> startSimulation());
        stopButton.addActionListener(e -> stopSimulation());
        resetButton.addActionListener(e -> resetSensor());
        
        simPanel.add(simButton);
        simPanel.add(stopButton);
        simPanel.add(resetButton);
        
        displayPanel.add(infoPanel);
        displayPanel.add(indicatorPanel);
        displayPanel.add(simPanel);
        
        // イベントログエリア
        JScrollPane logScrollPane = new JScrollPane(eventLogArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("イベントログ"));
        
        add(controlPanel, BorderLayout.NORTH);
        add(displayPanel, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.SOUTH);
    }
    
    private Color getTemperatureColor(double temperature) {
        if (temperature < 0) return Color.BLUE;
        if (temperature < 10) return Color.CYAN;
        if (temperature < 20) return Color.GREEN;
        if (temperature < 30) return Color.YELLOW;
        if (temperature < 40) return Color.ORANGE;
        return Color.RED;
    }
    
    private void logEvent(String source, String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        eventLogArea.append(String.format("[%s] %s: %s\n", timestamp, source, message));
        eventLogArea.setCaretPosition(eventLogArea.getDocument().getLength());
    }
    
    private void startSimulation() {
        if (simulationTimer != null && simulationTimer.isRunning()) {
            simulationTimer.stop();
        }
        
        logEvent("System", "自動シミュレーション開始");
        simulationTimer = new Timer(500, e -> sensor.simulateTemperatureChange());
        simulationTimer.start();
    }
    
    private void stopSimulation() {
        if (simulationTimer != null && simulationTimer.isRunning()) {
            simulationTimer.stop();
            logEvent("System", "自動シミュレーション停止");
        }
    }
    
    private void resetSensor() {
        stopSimulation();
        sensor.setTemperature(20.0);
        sensor.setSensorName("DefaultSensor");
        sensorNameField.setText("DefaultSensor");
        eventLogArea.setText("");
        logEvent("System", "センサーをリセットしました");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JavaBeansEventExample().setVisible(true);
        });
    }
}