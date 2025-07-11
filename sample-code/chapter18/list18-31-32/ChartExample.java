/**
 * リスト18-32
 * ChartExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (2043行目)
 */

import javax.swing.*;
import java.awt.*;

public class ChartExample extends JFrame {
    public ChartExample() {
        setTitle("カスタムチャートの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // カスタムチャートコンポーネントを作成
        SimpleBarChart chart = new SimpleBarChart();
        chart.setTitle("売上実績");
        
        // データを追加
        chart.addData("1月", 120);
        chart.addData("2月", 150);
        chart.addData("3月", 180);
        chart.addData("4月", 200);
        chart.addData("5月", 170);
        chart.addData("6月", 190);
        
        // パネルに追加
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder("月別売上"));
        chartPanel.add(chart, BorderLayout.CENTER);
        
        // コントロールパネルを作成
        JPanel controlPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("更新");
        JButton clearButton = new JButton("クリア");
        
        refreshButton.addActionListener(e -> {
            chart.clearData();
            // ランダムデータを生成
            String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
            for (String month : months) {
                chart.addData(month, Math.random() * 300 + 50);
            }
        });
        
        clearButton.addActionListener(e -> chart.clearData());
        
        controlPanel.add(refreshButton);
        controlPanel.add(clearButton);
        
        add(chartPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChartExample().setVisible(true);
        });
    }
}