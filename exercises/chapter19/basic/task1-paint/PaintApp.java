import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

/**
 * マウスで線を描くお絵かきパネル（基礎課題1の解答例）
 */
public class PaintApp extends JFrame {
    private final Sketch sketch = new Sketch();
    private final JLabel statusLabel = new JLabel();
    private final CanvasPanel canvas = new CanvasPanel();
    private Color currentColor = Color.BLACK;
    private String currentColorName = "黒";
    private Point mousePoint = null;

    public PaintApp() {
        super("お絵かき");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 押す・ドラッグ・移動・外へ出るを、1つのMouseAdapterで受け取る
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 右ボタンはポップアップメニューに使うので、左ボタンのときだけ線を始める
                if (SwingUtilities.isLeftMouseButton(e)) {
                    sketch.startPath(currentColor, e.getX(), e.getY());
                    canvas.repaint();
                    updateStatus();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mousePoint = e.getPoint();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    sketch.addPoint(e.getX(), e.getY());
                    canvas.repaint();
                }
                updateStatus();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
                updateStatus();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mousePoint = null;
                updateStatus();
            }
        };
        // mouseDraggedとmouseMovedを受け取るには、addMouseMotionListenerでも登録する
        canvas.addMouseListener(mouseHandler);
        canvas.addMouseMotionListener(mouseHandler);
        canvas.setComponentPopupMenu(createPopupMenu());

        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(canvas, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        updateStatus();
        pack();
        setLocationRelativeTo(null);
    }

    // 右クリックで出るメニュー。色はラジオボタンのメニュー項目で1つだけ選べるようにする
    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        ButtonGroup colorGroup = new ButtonGroup();
        addColorItem(menu, colorGroup, "黒", Color.BLACK, true);
        addColorItem(menu, colorGroup, "赤", new Color(210, 40, 40), false);
        addColorItem(menu, colorGroup, "青", new Color(40, 90, 210), false);
        menu.addSeparator();

        JMenuItem undoItem = new JMenuItem("元に戻す");
        undoItem.addActionListener(e -> {
            sketch.undo();
            canvas.repaint();
            updateStatus();
        });
        JMenuItem clearItem = new JMenuItem("すべて消す");
        clearItem.addActionListener(e -> {
            sketch.clear();
            canvas.repaint();
            updateStatus();
        });
        menu.add(undoItem);
        menu.add(clearItem);
        return menu;
    }

    private void addColorItem(JPopupMenu menu, ButtonGroup group, String name, Color color,
            boolean selected) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(name, selected);
        item.addActionListener(e -> {
            currentColor = color;
            currentColorName = name;
            updateStatus();
        });
        group.add(item);
        menu.add(item);
    }

    private void updateStatus() {
        String position = mousePoint == null ? "(-, -)" : "(" + mousePoint.x + ", " + mousePoint.y + ")";
        statusLabel.setText("座標 " + position + "   線の数 " + sketch.getPathCount()
                + "   色 " + currentColorName + "   右クリックでメニュー");
    }

    // 記録した線を描くパネル
    private class CanvasPanel extends JPanel {
        CanvasPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(480, 360);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (Sketch.Path path : sketch.getPaths()) {
                g2.setColor(path.color());
                List<Point> points = path.points();
                if (points.size() == 1) {
                    // クリックしただけのときは点を打つ
                    Point p = points.get(0);
                    g2.fillOval(p.x - 2, p.y - 2, 4, 4);
                }
                for (int i = 1; i < points.size(); i++) {
                    Point a = points.get(i - 1);
                    Point b = points.get(i);
                    g2.drawLine(a.x, a.y, b.x, b.y);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaintApp().setVisible(true));
    }
}
