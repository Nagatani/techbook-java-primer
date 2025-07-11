/**
 * リスト19-16
 * AdvancedJavaBeansExampleクラス
 * 
 * 元ファイル: chapter19-gui-event-handling.md (1819行目)
 */

import java.beans.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

// 1. 制約付きプロパティ（Constrained Properties）の実装
class SmartThermostatBean {
    private double temperature = 20.0;
    private double targetTemperature = 22.0;
    private boolean heatingEnabled = false;
    private PropertyChangeSupport propertySupport;
    private VetoableChangeSupport vetoSupport;
    
    public SmartThermostatBean() {
        propertySupport = new PropertyChangeSupport(this);
        vetoSupport = new VetoableChangeSupport(this);
    }
    
    // 制約付きプロパティ：設定できる温度に制限がある
    public void setTargetTemperature(double newTarget) throws PropertyVetoException {
        double oldTarget = this.targetTemperature;
        
        // 制約チェック：範囲外の値は拒否
        if (newTarget < 10 || newTarget > 40) {
            throw new PropertyVetoException(
                "目標温度は10℃〜40℃の範囲で設定してください", 
                new PropertyChangeEvent(this, "targetTemperature", oldTarget, newTarget));
        }
        
        // VetoableChangeListenerに変更許可を確認
        vetoSupport.fireVetoableChange("targetTemperature", oldTarget, newTarget);
        
        // 変更を適用
        this.targetTemperature = newTarget;
        propertySupport.firePropertyChange("targetTemperature", oldTarget, newTarget);
        
        // 自動制御の実行
        updateHeatingStatus();
    }
    
    public double getTargetTemperature() {
        return targetTemperature;
    }
    
    public void setTemperature(double temperature) {
        double oldTemperature = this.temperature;
        this.temperature = temperature;
        propertySupport.firePropertyChange("temperature", oldTemperature, temperature);
        
        // 自動制御の実行
        updateHeatingStatus();
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public boolean isHeatingEnabled() {
        return heatingEnabled;
    }
    
    private void updateHeatingStatus() {
        boolean oldStatus = this.heatingEnabled;
        this.heatingEnabled = temperature < targetTemperature - 1.0;
        propertySupport.firePropertyChange("heatingEnabled", oldStatus, heatingEnabled);
    }
    
    // VetoableChangeListener関連
    public void addVetoableChangeListener(VetoableChangeListener listener) {
        vetoSupport.addVetoableChangeListener(listener);
    }
    
    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoSupport.removeVetoableChangeListener(listener);
    }
    
    public void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        vetoSupport.addVetoableChangeListener(propertyName, listener);
    }
    
    public void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        vetoSupport.removeVetoableChangeListener(propertyName, listener);
    }
    
    // PropertyChangeListener関連
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
}

// 2. 複合イベントとバッチ更新の実装
public class AdvancedJavaBeansExample extends JFrame {
    private SmartThermostatBean thermostat;
    private JSlider currentTempSlider;
    private JSlider targetTempSlider;
    private JLabel currentTempLabel;
    private JLabel targetTempLabel;
    private JLabel heatingStatusLabel;
    private JTextArea logArea;
    private JCheckBox vetoCheckBox;
    
    public AdvancedJavaBeansExample() {
        setTitle("高度なJavaBeansパターン - スマートサーモスタット");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
        setupThermostatBean();
        layoutComponents();
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        currentTempSlider = new JSlider(0, 50, 20);
        currentTempSlider.setMajorTickSpacing(10);
        currentTempSlider.setPaintTicks(true);
        currentTempSlider.setPaintLabels(true);
        
        targetTempSlider = new JSlider(10, 40, 22);
        targetTempSlider.setMajorTickSpacing(5);
        targetTempSlider.setPaintTicks(true);
        targetTempSlider.setPaintLabels(true);
        
        currentTempLabel = new JLabel("現在温度: 20.0°C");
        targetTempLabel = new JLabel("目標温度: 22.0°C");
        heatingStatusLabel = new JLabel("暖房: OFF");
        
        vetoCheckBox = new JCheckBox("制約チェックを有効にする", true);
        
        logArea = new JTextArea(12, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
    }
    
    private void setupThermostatBean() {
        thermostat = new SmartThermostatBean();
        
        // PropertyChangeListenerの設定
        thermostat.addPropertyChangeListener("temperature", evt -> {
            double temp = (Double)evt.getNewValue();
            currentTempLabel.setText(String.format("現在温度: %.1f°C", temp));
            currentTempSlider.setValue((int)temp);
            log("温度変更", String.format("%.1f°C → %.1f°C", 
                (Double)evt.getOldValue(), temp));
        });
        
        thermostat.addPropertyChangeListener("targetTemperature", evt -> {
            double target = (Double)evt.getNewValue();
            targetTempLabel.setText(String.format("目標温度: %.1f°C", target));
            targetTempSlider.setValue((int)target);
            log("目標温度変更", String.format("%.1f°C → %.1f°C", 
                (Double)evt.getOldValue(), target));
        });
        
        thermostat.addPropertyChangeListener("heatingEnabled", evt -> {
            boolean enabled = (Boolean)evt.getNewValue();
            heatingStatusLabel.setText("暖房: " + (enabled ? "ON" : "OFF"));
            heatingStatusLabel.setForeground(enabled ? Color.RED : Color.BLUE);
            log("暖房状態", enabled ? "ON" : "OFF");
        });
        
        // VetoableChangeListenerの設定
        thermostat.addVetoableChangeListener("targetTemperature", evt -> {
            if (vetoCheckBox.isSelected()) {
                double newTarget = (Double)evt.getNewValue();
                double currentTemp = thermostat.getTemperature();
                
                // 制約：現在温度との差が15℃以上の場合は拒否
                if (Math.abs(newTarget - currentTemp) > 15) {
                    String message = String.format(
                        "目標温度と現在温度の差が大きすぎます（差: %.1f℃）", 
                        Math.abs(newTarget - currentTemp));
                    log("制約違反", message);
                    throw new PropertyVetoException(message, evt);
                }
            }
        });
        
        // スライダーイベントの設定
        currentTempSlider.addChangeListener(e -> {
            if (!currentTempSlider.getValueIsAdjusting()) {
                thermostat.setTemperature(currentTempSlider.getValue());
            }
        });
        
        targetTempSlider.addChangeListener(e -> {
            if (!targetTempSlider.getValueIsAdjusting()) {
                try {
                    thermostat.setTargetTemperature(targetTempSlider.getValue());
                } catch (PropertyVetoException ex) {
                    // 拒否された場合は元の値に戻す
                    SwingUtilities.invokeLater(() -> {
                        targetTempSlider.setValue((int)thermostat.getTargetTemperature());
                        JOptionPane.showMessageDialog(this, ex.getMessage(), 
                            "設定エラー", JOptionPane.WARNING_MESSAGE);
                    });
                }
            }
        });
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // コントロールパネル
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("現在温度:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(currentTempSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("目標温度:"), gbc);
        gbc.gridx = 1;
        controlPanel.add(targetTempSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        controlPanel.add(vetoCheckBox, gbc);
        
        // ステータスパネル
        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        statusPanel.setBorder(BorderFactory.createTitledBorder("サーモスタット状態"));
        statusPanel.add(currentTempLabel);
        statusPanel.add(targetTempLabel);
        statusPanel.add(heatingStatusLabel);
        
        // ボタンパネル
        JPanel buttonPanel = new JPanel();
        JButton presetButton = new JButton("プリセット設定");
        JButton batchButton = new JButton("バッチ更新");
        JButton clearLogButton = new JButton("ログクリア");
        
        presetButton.addActionListener(e -> setPresetTemperature());
        batchButton.addActionListener(e -> performBatchUpdate());
        clearLogButton.addActionListener(e -> logArea.setText(""));
        
        buttonPanel.add(presetButton);
        buttonPanel.add(batchButton);
        buttonPanel.add(clearLogButton);
        
        // 上部パネル
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.CENTER);
        topPanel.add(statusPanel, BorderLayout.EAST);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
    }
    
    private void setPresetTemperature() {
        String[] presets = {"夏季設定 (26℃)", "冬季設定 (22℃)", "節約設定 (20℃)"};
        String selected = (String)JOptionPane.showInputDialog(this, 
            "プリセット設定を選択してください", "プリセット", 
            JOptionPane.QUESTION_MESSAGE, null, presets, presets[1]);
        
        if (selected != null) {
            try {
                if (selected.contains("26")) {
                    thermostat.setTargetTemperature(26);
                } else if (selected.contains("22")) {
                    thermostat.setTargetTemperature(22);
                } else if (selected.contains("20")) {
                    thermostat.setTargetTemperature(20);
                }
                log("プリセット", selected + " を適用しました");
            } catch (PropertyVetoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), 
                    "プリセット設定エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void performBatchUpdate() {
        // バッチ更新の例：現在温度と目標温度を同時に変更
        double newCurrent = 18.0;
        double newTarget = 24.0;
        
        try {
            log("バッチ更新", "開始");
            thermostat.setTemperature(newCurrent);
            thermostat.setTargetTemperature(newTarget);
            currentTempSlider.setValue((int)newCurrent);
            targetTempSlider.setValue((int)newTarget);
            log("バッチ更新", "完了");
        } catch (PropertyVetoException ex) {
            log("バッチ更新", "失敗: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "バッチ更新に失敗しました: " + ex.getMessage(), 
                "更新エラー", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void log(String category, String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        logArea.append(String.format("[%s] %s: %s\n", timestamp, category, message));
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdvancedJavaBeansExample().setVisible(true);
        });
    }
}