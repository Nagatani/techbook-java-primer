import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.regex.Pattern;

public class SortableTableExample {
    private final JFrame frame = new JFrame("ソート可能なテーブル");
    private PersonTableModel model;
    private JTable table;
    private TableRowSorter<PersonTableModel> sorter;
    
    public SortableTableExample() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        model = new PersonTableModel();
        table = new JTable(model);
        
        // ソーターの設定
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        // フィルタリング機能の追加
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel filterLabel = new JLabel("フィルター:");
        JTextField filterField = new JTextField(20);
        JButton filterButton = new JButton("適用");
        
        filterButton.addActionListener(e -> {
            String text = filterField.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                // 名前列でフィルタリング（入力した記号も文字として探す）
                sorter.setRowFilter(RowFilter.regexFilter(
                        "(?i)" + Pattern.quote(text), 1));
            }
        });
        
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);
        filterPanel.add(filterButton);
        
        // レイアウト
        frame.add(filterPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // 操作パネル
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("追加");
        JButton deleteButton = new JButton("削除");
        
        addButton.addActionListener(e -> {
            int newId = model.nextId();
            model.addPerson(new Person(newId, "新規ユーザー", 20, "new@example.com"));
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                model.removePerson(modelRow);
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.setSize(700, 400);
        frame.setLocationRelativeTo(null);
    }
    
    private void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SortableTableExample().show();
        });
    }
}
