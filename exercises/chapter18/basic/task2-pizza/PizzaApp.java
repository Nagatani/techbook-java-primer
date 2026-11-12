import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

/**
 * 選んだ内容に合わせて合計金額がすぐ変わるピザの注文画面（基礎課題2の解答例）
 */
public class PizzaApp extends JFrame {
    private final PizzaOrder order = new PizzaOrder();
    private final JLabel summaryLabel = new JLabel();
    private final JLabel totalLabel = new JLabel();

    public PizzaApp() {
        super("ピザの注文");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // サイズはラジオボタン。ButtonGroupで1つだけ選べるようにする
        JPanel sizePanel = new JPanel(new GridLayout(1, 0));
        sizePanel.setBorder(BorderFactory.createTitledBorder("サイズ"));
        ButtonGroup group = new ButtonGroup();
        for (PizzaOrder.Size size : PizzaOrder.Size.values()) {
            JRadioButton button = new JRadioButton(size.getLabel() + "（" + size.getPrice() + "円）");
            button.setSelected(size == PizzaOrder.Size.M);
            // 選ばれたときだけ反応する（選択が外れたときのイベントも届くため）
            button.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    order.setSize(size);
                    updateView();
                }
            });
            group.add(button);
            sizePanel.add(button);
        }

        // トッピングはチェックボックス。いくつでも選べる
        JPanel toppingPanel = new JPanel(new GridLayout(0, 2));
        toppingPanel.setBorder(BorderFactory.createTitledBorder("トッピング"));
        for (PizzaOrder.Topping topping : PizzaOrder.Topping.values()) {
            JCheckBox box = new JCheckBox(topping.getLabel() + "（+" + topping.getPrice() + "円）");
            box.addItemListener(e -> {
                order.setTopping(topping, e.getStateChange() == ItemEvent.SELECTED);
                updateView();
            });
            toppingPanel.add(box);
        }

        JPanel result = new JPanel(new GridLayout(0, 1));
        result.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        result.add(summaryLabel);
        result.add(totalLabel);

        add(sizePanel, BorderLayout.NORTH);
        add(toppingPanel, BorderLayout.CENTER);
        add(result, BorderLayout.SOUTH);
        updateView();
        pack();
        setLocationRelativeTo(null);
    }

    private void updateView() {
        summaryLabel.setText("注文: " + order.describe());
        totalLabel.setText("合計: " + order.getTotal() + "円");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PizzaApp().setVisible(true));
    }
}
