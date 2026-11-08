import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

// カラーセルエディター
class ColorCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JButton button;
    private Color currentColor;
    private JColorChooser colorChooser;
    private JDialog dialog;
    
    public ColorCellEditor() {
        button = new JButton();
        button.setActionCommand("edit");
        button.addActionListener(e -> {
            if ("edit".equals(e.getActionCommand())) {
                // カラーチューザーを表示
                Color newColor = JColorChooser.showDialog(
                    button, "色を選択", currentColor);
                if (newColor != null) {
                    currentColor = newColor;
                }
                fireEditingStopped();
            }
        });
        button.setBorderPainted(false);
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        currentColor = (Color) value;
        button.setBackground(currentColor);
        return button;
    }
    
    @Override
    public Object getCellEditorValue() {
        return currentColor;
    }
}

// カラーセルレンダラー
class ColorCellRenderer extends JLabel implements TableCellRenderer {
    public ColorCellRenderer() {
        setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Color color = (Color) value;
        setBackground(color);
        setText(String.format("RGB(%d,%d,%d)", 
            color.getRed(), color.getGreen(), color.getBlue()));
        setForeground(getContrastColor(color));
        
        if (isSelected) {
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        } else {
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
        
        return this;
    }
    
    private Color getContrastColor(Color color) {
        // 背景色に対してコントラストの高い文字色を返す
        int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
        return gray > 128 ? Color.BLACK : Color.WHITE;
    }
}

// コンボボックスエディター
class PriorityCellEditor extends DefaultCellEditor {
    public PriorityCellEditor() {
        super(new JComboBox<>(new String[]{"低", "中", "高", "緊急"}));
    }
}

// メインアプリケーション
public class CustomEditorExample extends JFrame {
    public CustomEditorExample() {
        setTitle("カスタムエディターの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // テーブルモデルの作成
        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{
                {"タスク1", "高", Color.RED, false},
                {"タスク2", "中", Color.YELLOW, true},
                {"タスク3", "低", Color.GREEN, false},
                {"タスク4", "緊急", Color.ORANGE, true}
            },
            new String[]{"タスク名", "優先度", "色", "完了"}
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;
                    case 1: return String.class;
                    case 2: return Color.class;
                    case 3: return Boolean.class;
                    default: return Object.class;
                }
            }
        };
        
        // テーブルの作成
        JTable table = new JTable(model);
        table.setRowHeight(30);
        
        // カスタムエディターとレンダラーの設定
        table.getColumnModel().getColumn(1).setCellEditor(new PriorityCellEditor());
        table.getColumnModel().getColumn(2).setCellRenderer(new ColorCellRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ColorCellEditor());
        
        // スクロールペインに追加
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // 説明パネル
        JPanel infoPanel = new JPanel();
        infoPanel.add(new JLabel("優先度はドロップダウン、色はクリックで変更できます"));
        add(infoPanel, BorderLayout.SOUTH);
        
        setSize(600, 300);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomEditorExample().setVisible(true);
        });
    }
}
