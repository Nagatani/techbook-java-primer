import java.util.*;
import java.util.List;
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

// カスタムリスナーインターフェイス
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
