import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * 在庫の変化を3つの表示に知らせる画面（基礎課題4の解答例）
 *
 * 在庫の一覧・少ない在庫の警告・履歴の3つが、それぞれStockListenerとして通知を受け取ります。
 */
public class InventoryApp extends JFrame {
    private static final int LOW_STOCK = 3;

    private final Inventory inventory = new Inventory();
    private final JTextArea stockArea = new JTextArea(5, 16);
    private final JLabel warningLabel = new JLabel(" ");
    private final JTextArea historyArea = new JTextArea(8, 24);

    public InventoryApp() {
        super("在庫管理");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inventory.register("ノート", 10);
        inventory.register("ボールペン", 5);
        inventory.register("消しゴム", 4);

        // 1つ目のリスナー: 在庫の一覧を表示し直す
        inventory.addStockListener(e -> showStock());

        // 2つ目のリスナー: 少なくなったら警告を出す
        inventory.addStockListener(e -> {
            if (e.getNewQuantity() < LOW_STOCK) {
                warningLabel.setText(e.getItem() + "の在庫が残り" + e.getNewQuantity() + "個です");
            } else {
                warningLabel.setText(" ");
            }
        });

        // 3つ目のリスナー: 変化を履歴に足す
        inventory.addStockListener(e -> {
            String kind = e.getChange() > 0 ? "入荷" : "出荷";
            historyArea.append("%s %s %d個（%d → %d）%n".formatted(
                    e.getItem(), kind, Math.abs(e.getChange()), e.getOldQuantity(), e.getNewQuantity()));
        });

        JComboBox<String> itemBox = new JComboBox<>(inventory.getItems().toArray(new String[0]));
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        JButton receiveButton = new JButton("入荷");
        JButton shipButton = new JButton("出荷");

        // ボタンの処理はInventoryを呼ぶだけ。表示の更新はリスナーに任せる
        receiveButton.addActionListener(e ->
                inventory.receive((String) itemBox.getSelectedItem(), (Integer) quantitySpinner.getValue()));
        shipButton.addActionListener(e -> {
            String item = (String) itemBox.getSelectedItem();
            if (!inventory.ship(item, (Integer) quantitySpinner.getValue())) {
                warningLabel.setText(item + "の在庫が足りません");
            }
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(itemBox);
        controls.add(quantitySpinner);
        controls.add(new JLabel("個"));
        controls.add(receiveButton);
        controls.add(shipButton);

        stockArea.setEditable(false);
        stockArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        historyArea.setEditable(false);
        warningLabel.setForeground(new Color(200, 40, 40));
        JScrollPane stockScroll = new JScrollPane(stockArea);
        stockScroll.setBorder(BorderFactory.createTitledBorder("在庫"));
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setBorder(BorderFactory.createTitledBorder("履歴"));
        JPanel views = new JPanel(new GridLayout(1, 2, 8, 0));
        views.add(stockScroll);
        views.add(historyScroll);

        JPanel content = new JPanel(new BorderLayout(0, 6));
        content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        content.add(controls, BorderLayout.NORTH);
        content.add(views, BorderLayout.CENTER);
        content.add(warningLabel, BorderLayout.SOUTH);
        setContentPane(content);

        showStock();
        pack();
        setLocationRelativeTo(null);
    }

    private void showStock() {
        StringBuilder text = new StringBuilder();
        for (String item : inventory.getItems()) {
            text.append("%3d個  %s%n".formatted(inventory.getQuantity(item), item));
        }
        stockArea.setText(text.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryApp().setVisible(true));
    }
}
