import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * 成績の一覧を表にするモデル。
 * 列は 学籍番号・氏名・国語・数学・英語・合計。点数の列だけ編集できる。
 */
public class GradeTableModel extends AbstractTableModel {
    public static final int ID_COLUMN = 0;
    public static final int NAME_COLUMN = 1;
    public static final int FIRST_SUBJECT_COLUMN = 2;
    public static final int TOTAL_COLUMN = FIRST_SUBJECT_COLUMN + Student.SUBJECTS.length;

    private final List<Student> students = new ArrayList<>();

    public void addStudent(Student student) {
        students.add(student);
        int row = students.size() - 1;
        fireTableRowsInserted(row, row);
    }

    public Student getStudent(int row) {
        return students.get(row);
    }

    @Override
    public int getRowCount() {
        return students.size();
    }

    @Override
    public int getColumnCount() {
        return TOTAL_COLUMN + 1;
    }

    @Override
    public String getColumnName(int column) {
        if (column == ID_COLUMN) {
            return "学籍番号";
        } else if (column == NAME_COLUMN) {
            return "氏名";
        } else if (column == TOTAL_COLUMN) {
            return "合計";
        }
        return Student.SUBJECTS[column - FIRST_SUBJECT_COLUMN];
    }

    // 点数と合計をIntegerにすると、数として並べ替えられ、右寄せで表示される
    @Override
    public Class<?> getColumnClass(int column) {
        return isScoreColumn(column) || column == TOTAL_COLUMN ? Integer.class : String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return isScoreColumn(column);
    }

    @Override
    public Object getValueAt(int row, int column) {
        Student s = students.get(row);
        if (column == ID_COLUMN) {
            return s.getId();
        } else if (column == NAME_COLUMN) {
            return s.getName();
        } else if (column == TOTAL_COLUMN) {
            return s.getTotal();
        }
        return s.getScore(column - FIRST_SUBJECT_COLUMN);
    }

    /** 範囲外の点数は受け付けず、元の値のままにする */
    @Override
    public void setValueAt(Object value, int row, int column) {
        if (!isScoreColumn(column) || !(value instanceof Integer score)
                || !Student.isValidScore(score)) {
            return;
        }
        students.get(row).setScore(column - FIRST_SUBJECT_COLUMN, score);
        fireTableRowsUpdated(row, row);  // 合計の列も描き直される
    }

    /** 指定した行（モデルの行番号）だけで、列の平均を求める */
    public double average(int column, List<Integer> modelRows) {
        if (modelRows.isEmpty()) {
            return 0.0;
        }
        int sum = 0;
        for (int row : modelRows) {
            sum += (Integer) getValueAt(row, column);
        }
        return (double) sum / modelRows.size();
    }

    public static boolean isScoreColumn(int column) {
        return FIRST_SUBJECT_COLUMN <= column && column < TOTAL_COLUMN;
    }
}
