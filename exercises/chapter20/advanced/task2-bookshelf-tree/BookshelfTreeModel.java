import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * 蔵書のツリー。根 → 分類 → 本 の3段にする。
 * 分類のノードは文字列、本のノードはBookを持つ。
 */
public class BookshelfTreeModel extends DefaultTreeModel {

    public BookshelfTreeModel() {
        super(new DefaultMutableTreeNode("蔵書"));
    }

    public DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode) getRoot();
    }

    /** 分類を末尾に追加する。同じ名前の分類があれば、それを返す */
    public DefaultMutableTreeNode addCategory(String name) {
        DefaultMutableTreeNode existing = findCategory(name);
        if (existing != null) {
            return existing;
        }
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
        // insertNodeIntoを使うと、ツリーの表示も更新される
        insertNodeInto(node, getRootNode(), getRootNode().getChildCount());
        return node;
    }

    public DefaultMutableTreeNode findCategory(String name) {
        DefaultMutableTreeNode root = getRootNode();
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            if (child.getUserObject().equals(name)) {
                return child;
            }
        }
        return null;
    }

    /** 分類のノードに本を追加する。題名の順に並ぶ位置に入れる */
    public DefaultMutableTreeNode addBook(DefaultMutableTreeNode category, Book book) {
        if (!isCategory(category)) {
            throw new IllegalArgumentException("分類を選んでください");
        }
        int index = 0;
        while (index < category.getChildCount()
                && bookOf((DefaultMutableTreeNode) category.getChildAt(index))
                        .title().compareTo(book.title()) < 0) {
            index++;
        }
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(book, false);
        insertNodeInto(node, category, index);
        return node;
    }

    /** 本のノードだけを削除できる */
    public boolean removeBook(DefaultMutableTreeNode node) {
        if (!isBook(node)) {
            return false;
        }
        removeNodeFromParent(node);
        return true;
    }

    public int countBooks() {
        int count = 0;
        DefaultMutableTreeNode root = getRootNode();
        for (int i = 0; i < root.getChildCount(); i++) {
            count += root.getChildAt(i).getChildCount();
        }
        return count;
    }

    public static boolean isCategory(DefaultMutableTreeNode node) {
        return node != null && node.getLevel() == 1;
    }

    public static boolean isBook(DefaultMutableTreeNode node) {
        return node != null && node.getUserObject() instanceof Book;
    }

    public static Book bookOf(DefaultMutableTreeNode node) {
        return (Book) node.getUserObject();
    }
}
