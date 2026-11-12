import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * 分類のノードに冊数を、本のノードに出版年を添えて表示する。
 */
public class BookshelfRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        // まず標準の描き方（アイコンや選択時の色）を設定してもらう
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (BookshelfTreeModel.isCategory(node)) {
            setText("%s（%d冊）".formatted(node.getUserObject(), node.getChildCount()));
        } else if (BookshelfTreeModel.isBook(node)) {
            Book book = BookshelfTreeModel.bookOf(node);
            setText("%s（%d年）".formatted(book.title(), book.year()));
        }
        return this;
    }
}
