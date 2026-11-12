import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * 本棚の分類ツリー。分類を選んで本を追加し、本を選んで詳細を見る・削除する。
 */
public class BookshelfApp {
    private final BookshelfTreeModel model = new BookshelfTreeModel();
    private final JTree tree = new JTree(model);
    private final JLabel detailLabel = new JLabel(" ");
    private final JLabel messageLabel = new JLabel(" ");
    private final JTextField titleField = new JTextField(12);
    private final JTextField authorField = new JTextField(8);
    private final JTextField yearField = new JTextField(4);
    private final JButton addBookButton = new JButton("選択した分類に追加");
    private final JButton removeButton = new JButton("選択した本を削除");

    public BookshelfApp() {
        DefaultMutableTreeNode programming = model.addCategory("プログラミング");
        model.addBook(programming, new Book("Javaプログラミング入門", "山田一郎", 2024));
        model.addBook(programming, new Book("アルゴリズム図鑑", "佐藤次郎", 2021));
        DefaultMutableTreeNode math = model.addCategory("数学");
        model.addBook(math, new Book("線形代数の基礎", "田中花子", 2019));
        DefaultMutableTreeNode novel = model.addCategory("小説");
        model.addBook(novel, new Book("坊っちゃん", "夏目漱石", 1906));
        model.addBook(novel, new Book("銀河鉄道の夜", "宮沢賢治", 1934));

        tree.setCellRenderer(new BookshelfRenderer());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        for (int row = 0; row < tree.getRowCount(); row++) {
            tree.expandRow(row);  // 展開すると行が増えるので、増えた行も順に展開される
        }

        tree.addTreeSelectionListener(e -> updateSelection());
        addBookButton.addActionListener(e -> addBook());
        removeButton.addActionListener(e -> removeBook());

        JButton addCategoryButton = new JButton("分類を追加");
        addCategoryButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(tree, "分類の名前");
            if (name != null && !name.isBlank()) {
                select(model.addCategory(name.strip()));
            }
        });

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("題名"));
        form.add(titleField);
        form.add(new JLabel("著者"));
        form.add(authorField);
        form.add(new JLabel("出版年"));
        form.add(yearField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addBookButton);
        buttons.add(removeButton);
        buttons.add(addCategoryButton);

        messageLabel.setForeground(Color.RED);
        JPanel south = new JPanel(new GridLayout(4, 1));
        south.add(detailLabel);
        south.add(form);
        south.add(buttons);
        south.add(messageLabel);
        south.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        JFrame frame = new JFrame("本棚");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(tree), BorderLayout.CENTER);
        frame.add(south, BorderLayout.SOUTH);
        frame.setSize(560, 460);
        frame.setLocationRelativeTo(null);

        updateSelection();
        frame.setVisible(true);
    }

    private DefaultMutableTreeNode selectedNode() {
        return (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    }

    /** 選んだノードの種類に合わせて、詳細とボタンを切り替える */
    private void updateSelection() {
        DefaultMutableTreeNode node = selectedNode();
        if (BookshelfTreeModel.isBook(node)) {
            Book b = BookshelfTreeModel.bookOf(node);
            detailLabel.setText("「%s」 著者: %s　出版年: %d年　分類: %s".formatted(
                    b.title(), b.author(), b.year(),
                    ((DefaultMutableTreeNode) node.getParent()).getUserObject()));
        } else if (BookshelfTreeModel.isCategory(node)) {
            detailLabel.setText("分類「%s」 %d冊".formatted(node.getUserObject(), node.getChildCount()));
        } else {
            detailLabel.setText("蔵書 全%d冊".formatted(model.countBooks()));
        }
        addBookButton.setEnabled(BookshelfTreeModel.isCategory(node));
        removeButton.setEnabled(BookshelfTreeModel.isBook(node));
    }

    private void addBook() {
        String title = titleField.getText().strip();
        String author = authorField.getText().strip();
        if (title.isEmpty() || author.isEmpty()) {
            messageLabel.setText("題名と著者を入力してください");
            return;
        }
        int year;
        try {
            year = Integer.parseInt(yearField.getText().strip());
        } catch (NumberFormatException e) {
            messageLabel.setText("出版年は整数で入力してください");
            return;
        }
        DefaultMutableTreeNode category = selectedNode();
        DefaultMutableTreeNode added = model.addBook(category, new Book(title, author, year));
        model.nodeChanged(category);  // 冊数の表示を更新する
        select(added);
        titleField.setText("");
        authorField.setText("");
        yearField.setText("");
        messageLabel.setText(" ");
    }

    private void removeBook() {
        DefaultMutableTreeNode node = selectedNode();
        DefaultMutableTreeNode category = (DefaultMutableTreeNode) node.getParent();
        if (model.removeBook(node)) {
            model.nodeChanged(category);
            select(category);
        }
    }

    /** ノードを選び、見える位置まで展開する */
    private void select(DefaultMutableTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        tree.setSelectionPath(path);
        tree.scrollPathToVisible(path);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookshelfApp());
    }
}
