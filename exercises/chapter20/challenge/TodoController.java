import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.RowFilter;

/**
 * ToDoリストのController。画面の操作をTaskManagerへの指示に変え、
 * TaskManagerの変更を受けて画面のモデルを更新する。
 */
public class TodoController {
    private final TaskManager manager;
    private final CategoryListModel categoryModel;
    private final TaskTableModel tableModel;
    private final TodoFrame view;
    private final LocalDate today;

    public TodoController(TaskManager manager, CategoryListModel categoryModel,
            TaskTableModel tableModel, TodoFrame view, LocalDate today) {
        this.manager = manager;
        this.categoryModel = categoryModel;
        this.tableModel = tableModel;
        this.view = view;
        this.today = today;

        // Modelが変わったら、2つの画面用モデルと状態の表示を更新する
        manager.addListener(this::modelChanged);

        view.categoryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                categorySelected();
            }
        });
        view.hideDoneCheck.addActionListener(e -> applyFilter());
        view.table.getSelectionModel().addListSelectionListener(e -> updateButtons());
        view.addButton.addActionListener(e -> addTask());
        view.titleField.addActionListener(e -> addTask());
        view.removeButton.addActionListener(e -> removeTask());

        refreshCategoryBox();
        updateStatus();
        updateButtons();
    }

    private void modelChanged() {
        categoryModel.refresh();
        tableModel.refresh();
        refreshCategoryBox();
        updateStatus();
    }

    private void categorySelected() {
        String item = view.categoryList.getSelectedValue();
        if (item == null) {
            return;
        }
        String category = CategoryListModel.toCategory(item);
        tableModel.setCategory(category);
        if (category != null) {
            view.categoryBox.setSelectedItem(category);  // 追加先の分類の初期値にする
        }
        updateStatus();
    }

    /** 「完了したタスクを隠す」の状態で絞り込みを切り替える */
    private void applyFilter() {
        if (view.hideDoneCheck.isSelected()) {
            view.sorter.setRowFilter(new RowFilter<TaskTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends TaskTableModel, ? extends Integer> entry) {
                    return !entry.getModel().getTask(entry.getIdentifier()).isDone();
                }
            });
        } else {
            view.sorter.setRowFilter(null);
        }
        updateStatus();
    }

    private void addTask() {
        String title = view.titleField.getText().strip();
        Object categoryItem = view.categoryBox.getSelectedItem();
        String category = categoryItem == null ? "" : categoryItem.toString().strip();
        LocalDate due;
        try {
            due = LocalDate.parse(view.dueField.getText().strip());
        } catch (DateTimeParseException e) {
            view.messageLabel.setText("期限は2026-12-01の形で入力してください");
            return;
        }
        try {
            manager.addTask(new Task(title, category,
                    (Priority) view.priorityBox.getSelectedItem(), due));
        } catch (IllegalArgumentException e) {
            view.messageLabel.setText(e.getMessage());
            return;
        }
        view.titleField.setText("");
        view.messageLabel.setText(" ");
    }

    private void removeTask() {
        int viewRow = view.table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        // 並べ替え・絞り込みをしているので、表示上の行番号をモデルの行番号に直す
        Task task = tableModel.getTask(view.table.convertRowIndexToModel(viewRow));
        manager.removeTask(task);
    }

    /** 追加欄の分類の選択肢を作り直す。入力中の文字は残す */
    private void refreshCategoryBox() {
        Object current = view.categoryBox.getSelectedItem();
        List<String> categories = manager.getCategories();
        view.categoryBox.setModel(new DefaultComboBoxModel<>(categories.toArray(new String[0])));
        view.categoryBox.setSelectedItem(current);
    }

    private void updateButtons() {
        view.removeButton.setEnabled(view.table.getSelectedRow() >= 0);
    }

    private void updateStatus() {
        int overdue = 0;
        for (Task t : manager.getTasks(null)) {
            if (t.isOverdue(today)) {
                overdue++;
            }
        }
        view.statusLabel.setText("表示 %d件　全体: 未完了 %d / %d件（期限切れ %d件）".formatted(
                view.table.getRowCount(), manager.countUndone(null), manager.countAll(null), overdue));
    }
}
