/**
 * リスト19-14
 * CustomEventExampleクラス
 * 
 * 元ファイル: chapter19-gui-event-handling.md (1255行目)
 */

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