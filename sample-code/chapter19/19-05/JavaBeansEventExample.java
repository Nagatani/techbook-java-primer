import java.beans.*;
import javax.swing.*;
import javax.swing.Timer;
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
