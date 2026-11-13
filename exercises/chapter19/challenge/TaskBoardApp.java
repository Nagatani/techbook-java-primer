import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 * ドラッグ&ドロップでタスクを動かすボード（チャレンジ課題の解答例）
 *
 * ドラッグ&ドロップ・キーバインディング・PropertyChangeListenerを組み合わせます。
 * JListとDefaultListModelの詳しい使い方は第20章で学びます。
 */
public class TaskBoardApp extends JFrame {
    private final TaskBoard board = new TaskBoard();
    private final Map<Column, DefaultListModel<String>> models = new EnumMap<>(Column.class);
    private final Map<Column, JList<String>> lists = new EnumMap<>(Column.class);
    private final JTextField newTaskField = new JTextField(20);
    private final JLabel statusLabel = new JLabel();

    public TaskBoardApp() {
        super("タスクボード");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ボードが変わったら、3つのリストと件数の表示を作り直す
        board.addPropertyChangeListener(e -> refresh());

        JPanel columnsPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        TransferHandler handler = new TaskTransferHandler();
        for (Column column : Column.values()) {
            DefaultListModel<String> model = new DefaultListModel<>();
            JList<String> list = new JList<>(model);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setDragEnabled(true);
            list.setDropMode(DropMode.INSERT); // 項目と項目の間に差し込む
            list.setTransferHandler(handler);
            bindDeleteKey(list);
            models.put(column, model);
            lists.put(column, list);

            JScrollPane scroll = new JScrollPane(list);
            scroll.setBorder(BorderFactory.createTitledBorder(column.getLabel()));
            columnsPanel.add(scroll);
        }

        // 追加: ボタンでも、テキストフィールドでEnterを押しても追加する
        JButton addButton = new JButton("追加");
        addButton.addActionListener(e -> addTask());
        newTaskField.addActionListener(e -> addTask());
        bindNewTaskKey();

        JPanel top = new JPanel(new BorderLayout(6, 0));
        top.add(new JLabel("新しいタスク"), BorderLayout.WEST);
        top.add(newTaskField, BorderLayout.CENTER);
        top.add(addButton, BorderLayout.EAST);

        JLabel helpLabel = new JLabel("ドラッグで列の間を移動   Delete: 選んだタスクを削除   Ctrl+N: 新しいタスクの入力へ");
        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.add(statusLabel);
        bottom.add(helpLabel);

        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        content.add(top, BorderLayout.NORTH);
        content.add(columnsPanel, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);
        setContentPane(content);

        board.add("演習の課題を読む");
        board.add("サンプルコードを動かす");
        board.add("レポートを書く");
        board.add("環境を構築する");
        board.move("環境を構築する", Column.DONE, -1);
        board.move("演習の課題を読む", Column.DOING, -1);

        setSize(620, 360);
        setLocationRelativeTo(null);
    }

    private void addTask() {
        if (board.add(newTaskField.getText())) {
            newTaskField.setText("");
        } else {
            statusLabel.setText("空の名前と、すでにある名前は追加できません");
        }
        newTaskField.requestFocusInWindow();
    }

    // 選んでいるリストでDeleteキーを押したら、そのタスクを消す
    private void bindDeleteKey(JList<String> list) {
        list.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DELETE"), "deleteTask");
        list.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("BACK_SPACE"), "deleteTask");
        list.getActionMap().put("deleteTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = list.getSelectedValue();
                if (task != null) {
                    board.remove(task);
                }
            }
        });
    }

    // ウィンドウのどこにいても、Ctrl+Nで入力欄へ移る
    private void bindNewTaskKey() {
        JRootPane root = getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control N"), "newTask");
        root.getActionMap().put("newTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newTaskField.requestFocusInWindow();
            }
        });
    }

    // ボードの中身をリストのモデルに写し、件数を表示する
    private void refresh() {
        for (Column column : Column.values()) {
            DefaultListModel<String> model = models.get(column);
            model.clear();
            for (String task : board.getTasks(column)) {
                model.addElement(task);
            }
        }
        Map<Column, Integer> counts = board.counts();
        StringBuilder text = new StringBuilder();
        for (Column column : Column.values()) {
            if (!text.isEmpty()) {
                text.append(" / ");
            }
            text.append(column.getLabel()).append(" ").append(counts.get(column));
        }
        statusLabel.setText(text.toString());
    }

    // リストがどの列のものかを調べる
    private Column columnOf(JComponent component) {
        for (Column column : Column.values()) {
            if (lists.get(column) == component) {
                return column;
            }
        }
        return null;
    }

    /**
     * 3つのリストで共有するTransferHandler
     *
     * ドラッグで運ぶのはタスクの名前（文字列）だけにし、ドロップされたら
     * TaskBoardのmoveを呼ぶ。リストの表示はPropertyChangeListenerが作り直すので、
     * exportDoneで元のリストから消す処理は書かない。
     */
    private class TaskTransferHandler extends TransferHandler {

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            @SuppressWarnings("unchecked")
            JList<String> list = (JList<String>) c;
            String task = list.getSelectedValue();
            return task == null ? null : new StringSelection(task);
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDrop() && support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            Column to = columnOf((JComponent) support.getComponent());
            JList.DropLocation location = (JList.DropLocation) support.getDropLocation();
            try {
                String task = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                // ボードにないタスク（ほかのアプリから運んだ文字など）は受け付けない
                return to != null && board.move(task, to, location.getIndex());
            } catch (UnsupportedFlavorException | IOException e) {
                return false;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TaskBoardApp().setVisible(true));
    }
}
