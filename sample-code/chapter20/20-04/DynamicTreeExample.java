import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

public class DynamicTreeExample extends JFrame {
    private DefaultTreeModel treeModel;
    private JTree tree;
    
    public DynamicTreeExample() {
        setTitle("動的なツリー操作");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ルートノードとモデルの作成
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("ファイルシステム");
        treeModel = new DefaultTreeModel(root);
        
        // 初期フォルダーの追加
        DefaultMutableTreeNode documents = new DefaultMutableTreeNode("Documents");
        DefaultMutableTreeNode downloads = new DefaultMutableTreeNode("Downloads");
        DefaultMutableTreeNode desktop = new DefaultMutableTreeNode("Desktop");
        
        root.add(documents);
        root.add(downloads);
        root.add(desktop);
        
        // JTreeの作成
        tree = new JTree(treeModel);
        tree.setEditable(true); // ノード名の編集を許可
        tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        // ポップアップメニューの作成
        JPopupMenu popup = new JPopupMenu();
        JMenuItem addFolder = new JMenuItem("フォルダーを追加");
        JMenuItem addFile = new JMenuItem("ファイルを追加");
        JMenuItem delete = new JMenuItem("削除");
        JMenuItem rename = new JMenuItem("名前変更");
        
        popup.add(addFolder);
        popup.add(addFile);
        popup.addSeparator();
        popup.add(rename);
        popup.add(delete);
        
        // ポップアップメニューのアクション
        addFolder.addActionListener(e -> addNode("新しいフォルダー", true));
        addFile.addActionListener(e -> addNode("新しいファイル.txt", false));
        delete.addActionListener(e -> deleteNode());
        rename.addActionListener(e -> renameNode());
        
        // マウスリスナーでポップアップを表示
        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }
            
            private void showPopup(java.awt.event.MouseEvent e) {
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    tree.setSelectionPath(path);
                    popup.show(tree, e.getX(), e.getY());
                }
            }
        });
        
        // カスタムレンダラーの設定
        tree.setCellRenderer(new FileSystemTreeCellRenderer());
        
        // レイアウト
        add(new JScrollPane(tree), BorderLayout.CENTER);
        
        // 情報パネル
        JPanel infoPanel = new JPanel();
        JLabel infoLabel = new JLabel("右クリックでメニューを表示");
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
        
        setSize(500, 600);
        setLocationRelativeTo(null);
    }
    
    private void addNode(String name, boolean isFolder) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();
        
        if (parentPath == null) {
            parentNode = (DefaultMutableTreeNode) treeModel.getRoot();
        } else {
            parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
        }
        
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
        newNode.setAllowsChildren(isFolder);
        treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
        
        // 新しいノードを表示
        tree.scrollPathToVisible(new TreePath(newNode.getPath()));
    }
    
    private void deleteNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = 
                (DefaultMutableTreeNode) currentSelection.getLastPathComponent();
            MutableTreeNode parent = (MutableTreeNode) currentNode.getParent();
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
            }
        }
    }
    
    private void renameNode() {
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            tree.startEditingAtPath(path);
        }
    }
    
    // カスタムツリーセルレンダラー
    class FileSystemTreeCellRenderer extends DefaultTreeCellRenderer {
        private Icon folderIcon = UIManager.getIcon("Tree.closedIcon");
        private Icon fileIcon = UIManager.getIcon("Tree.leafIcon");
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            
            if (node.getAllowsChildren()) {
                setIcon(folderIcon);
            } else {
                setIcon(fileIcon);
            }
            
            return this;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DynamicTreeExample().setVisible(true);
        });
    }
}
