import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class BasicTreeExample extends JFrame {
    public BasicTreeExample() {
        setTitle("基本的なJTreeの例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ルートノードの作成
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("プロジェクト");
        
        // 子ノードの追加
        DefaultMutableTreeNode src = new DefaultMutableTreeNode("src");
        DefaultMutableTreeNode lib = new DefaultMutableTreeNode("lib");
        DefaultMutableTreeNode doc = new DefaultMutableTreeNode("doc");
        
        root.add(src);
        root.add(lib);
        root.add(doc);
        
        // srcフォルダーの中身
        DefaultMutableTreeNode main = new DefaultMutableTreeNode("main");
        DefaultMutableTreeNode test = new DefaultMutableTreeNode("test");
        src.add(main);
        src.add(test);
        
        // mainフォルダーの中身
        main.add(new DefaultMutableTreeNode("App.java"));
        main.add(new DefaultMutableTreeNode("Utils.java"));
        main.add(new DefaultMutableTreeNode("Config.java"));
        
        // JTreeの作成
        JTree tree = new JTree(root);
        tree.setShowsRootHandles(true);
        
        // スクロールペインに追加
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);
        
        setSize(400, 500);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BasicTreeExample().setVisible(true);
        });
    }
}
