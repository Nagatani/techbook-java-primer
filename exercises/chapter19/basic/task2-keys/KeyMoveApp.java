import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * 矢印キーで四角を動かすプログラム（基礎課題2の解答例）
 *
 * キーと処理の対応をInputMapとActionMapで登録します（キーバインディング）。
 */
public class KeyMoveApp extends JFrame {
    private static final int STEP = 10;
    private static final int FAST_STEP = 50;

    private final Board board = new Board(400, 300, 40);
    private final BoardPanel boardPanel = new BoardPanel();
    private final JLabel statusLabel = new JLabel();
    private String lastAction = "なし";

    public KeyMoveApp() {
        super("キーで動かす");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // WHEN_IN_FOCUSED_WINDOW: ウィンドウのどこにフォーカスがあってもキーを受け取る
        InputMap inputMap = boardPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = boardPanel.getActionMap();

        bind(inputMap, actionMap, "LEFT", "左へ", -STEP, 0);
        bind(inputMap, actionMap, "RIGHT", "右へ", STEP, 0);
        bind(inputMap, actionMap, "UP", "上へ", 0, -STEP);
        bind(inputMap, actionMap, "DOWN", "下へ", 0, STEP);
        // Shiftを押しながらだと大きく動かす
        bind(inputMap, actionMap, "shift LEFT", "左へ大きく", -FAST_STEP, 0);
        bind(inputMap, actionMap, "shift RIGHT", "右へ大きく", FAST_STEP, 0);
        bind(inputMap, actionMap, "shift UP", "上へ大きく", 0, -FAST_STEP);
        bind(inputMap, actionMap, "shift DOWN", "下へ大きく", 0, FAST_STEP);

        // Ctrl+Rで中央に戻す
        inputMap.put(KeyStroke.getKeyStroke("control R"), "reset");
        actionMap.put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.reset();
                lastAction = "中央に戻す";
                updateView();
            }
        });

        JLabel helpLabel = new JLabel("矢印キー: 10ずつ移動   Shift+矢印キー: 50ずつ移動   Ctrl+R: 中央に戻す");
        helpLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        add(helpLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        updateView();
        pack();
        setLocationRelativeTo(null);
    }

    // 1つのキーに、名前と動かす量の決まった処理を割り当てる
    private void bind(InputMap inputMap, ActionMap actionMap, String key, String name, int dx, int dy) {
        inputMap.put(KeyStroke.getKeyStroke(key), name);
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.move(dx, dy);
                lastAction = name;
                updateView();
            }
        });
    }

    private void updateView() {
        statusLabel.setText("位置 (" + board.getX() + ", " + board.getY() + ")   最後の操作: " + lastAction);
        boardPanel.repaint();
    }

    private class BoardPanel extends JPanel {
        BoardPanel() {
            setBackground(new Color(240, 240, 240));
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(board.getWidth(), board.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(70, 130, 180));
            g.fillRect(board.getX(), board.getY(), board.getSize(), board.getSize());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KeyMoveApp().setVisible(true));
    }
}
