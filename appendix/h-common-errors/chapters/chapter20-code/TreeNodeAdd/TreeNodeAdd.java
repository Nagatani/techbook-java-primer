import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class TreeNodeAdd {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TreeNodeAdd::createAndShow);
    }

    private static void createAndShow() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("本棚");
        root.add(new DefaultMutableTreeNode("小説"));
        DefaultTreeModel model = new DefaultTreeModel(root);
        JTree tree = new JTree(model);

        JButton addButton = new JButton("分類を追加");
        addButton.addActionListener(e -> {
            DefaultMutableTreeNode child =
                    new DefaultMutableTreeNode("分類" + (root.getChildCount() + 1));
            // root.add(child)だけではツリーに知らせが届かず、画面が変わらない
            model.insertNodeInto(child, root, root.getChildCount());
            tree.scrollPathToVisible(new TreePath(child.getPath()));
        });

        JFrame frame = new JFrame("ツリーへのノードの追加");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(tree), BorderLayout.CENTER);
        frame.add(addButton, BorderLayout.SOUTH);
        frame.setSize(300, 250);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
