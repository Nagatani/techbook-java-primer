import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

// Personの一覧を表示するテーブルモデル（本文では省略している補助クラス）
public class PersonTableModel extends AbstractTableModel {
    private final String[] columnNames = {"ID", "名前", "年齢", "メールアドレス"};
    private final List<Person> people = new ArrayList<>(List.of(
        new Person(1, "田中太郎", 25, "tanaka@example.com"),
        new Person(2, "鈴木花子", 30, "suzuki@example.com"),
        new Person(3, "佐藤次郎", 28, "sato@example.com"),
        new Person(4, "山田美咲", 22, "yamada@example.com")
    ));

    @Override
    public int getRowCount() { return people.size(); }

    @Override
    public int getColumnCount() { return columnNames.length; }

    @Override
    public String getColumnName(int column) { return columnNames[column]; }

    // 列の型を返すと、数値の列は数値として並べ替えられる
    @Override
    public Class<?> getColumnClass(int column) {
        return (column == 0 || column == 2) ? Integer.class : String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Person p = people.get(row);
        return switch (column) {
            case 0 -> p.id();
            case 1 -> p.name();
            case 2 -> p.age();
            default -> p.email();
        };
    }

    // いまあるIDの最大値に1を足す（行を削除したあとでもIDが重複しない）
    public int nextId() {
        int max = 0;
        for (Person p : people) {
            max = Math.max(max, p.id());
        }
        return max + 1;
    }

    public void addPerson(Person person) {
        people.add(person);
        fireTableRowsInserted(people.size() - 1, people.size() - 1);
    }

    public void removePerson(int row) {
        people.remove(row);
        fireTableRowsDeleted(row, row);
    }
}
