import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class ChangeListenerExample extends JFrame {
    private JSlider slider;
    private JSpinner spinner;
    private JProgressBar progressBar;
    private JLabel valueLabel;
    
    public ChangeListenerExample() {
        setTitle("ChangeListener の例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        JPanel controlPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // スライダー
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.setBorder(BorderFactory.createTitledBorder("音量"));
        slider = new JSlider(0, 100, 50);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider.getValue();
                valueLabel.setText("現在の値: " + value);
                progressBar.setValue(value);
                spinner.setValue(value);
                
                // ドラッグ終了時のみ処理
                if (!slider.getValueIsAdjusting()) {
                    IO.println("最終値: " + value);
                }
            }
        });
        
        sliderPanel.add(slider);
        
        // スピナー
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spinnerPanel.setBorder(BorderFactory.createTitledBorder("数値入力"));
        SpinnerModel model = new SpinnerNumberModel(50, 0, 100, 1);
        spinner = new JSpinner(model);
        
        spinner.addChangeListener(e -> {
            int value = (Integer) spinner.getValue();
            slider.setValue(value);
            progressBar.setValue(value);
            valueLabel.setText("現在の値: " + value);
        });
        
        spinnerPanel.add(new JLabel("値:"));
        spinnerPanel.add(spinner);
        
        // プログレスバー
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createTitledBorder("進捗"));
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(50);
        progressBar.setStringPainted(true);
        progressPanel.add(progressBar);
        
        // コントロールの配置
        controlPanel.add(sliderPanel);
        controlPanel.add(spinnerPanel);
        controlPanel.add(progressPanel);
        
        // 値表示ラベル
        valueLabel = new JLabel("現在の値: 50");
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(controlPanel, BorderLayout.CENTER);
        add(valueLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChangeListenerExample().setVisible(true);
        });
    }
}
