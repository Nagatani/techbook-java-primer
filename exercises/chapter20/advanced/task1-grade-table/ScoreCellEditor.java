import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * 点数のセルの編集。0〜100の整数でなければ編集を終わらせない。
 */
public class ScoreCellEditor extends DefaultCellEditor {
    private final JTextField field;

    public ScoreCellEditor() {
        super(new JTextField());
        field = (JTextField) getComponent();
        field.setHorizontalAlignment(JTextField.RIGHT);
    }

    /** 編集を始めるときに呼ばれる。枠の色を元に戻す */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public Object getCellEditorValue() {
        return Integer.valueOf(field.getText().strip());
    }

    /** Enterキーや別のセルのクリックで呼ばれる。falseを返すと編集が続く */
    @Override
    public boolean stopCellEditing() {
        try {
            int score = Integer.parseInt(field.getText().strip());
            if (!Student.isValidScore(score)) {
                return markInvalid();
            }
        } catch (NumberFormatException e) {
            return markInvalid();
        }
        return super.stopCellEditing();
    }

    private boolean markInvalid() {
        field.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        return false;
    }
}
