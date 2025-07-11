/**
 * リスト18-13
 * CombinedLayoutExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (595行目)
 */

import javax.swing.*;
import java.awt.*;

public class CombinedLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("レイアウト組み合わせの例");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // 上部：メニューバーとツールバー
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // メニューバー
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("ファイル");
        fileMenu.add(new JMenuItem("新規"));
        fileMenu.add(new JMenuItem("開く"));
        fileMenu.add(new JMenuItem("保存"));
        menuBar.add(fileMenu);
        
        JMenu editMenu = new JMenu("編集");
        editMenu.add(new JMenuItem("切り取り"));
        editMenu.add(new JMenuItem("コピー"));
        editMenu.add(new JMenuItem("貼り付け"));
        menuBar.add(editMenu);
        
        frame.setJMenuBar(menuBar);
        
        // ツールバー
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(new JButton("新規"));
        toolbar.add(new JButton("開く"));
        toolbar.add(new JButton("保存"));
        topPanel.add(toolbar, BorderLayout.NORTH);
        
        frame.add(topPanel, BorderLayout.NORTH);
        
        // 中央：分割パネル
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);
        
        // 左側パネル
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(
            BorderFactory.createTitledBorder("プロジェクト"));
        
        // ツリー構造
        JTree tree = new JTree();
        leftPanel.add(new JScrollPane(tree), BorderLayout.CENTER);
        
        // 左下のボタンパネル
        JPanel leftButtonPanel = new JPanel(new FlowLayout());
        leftButtonPanel.add(new JButton("追加"));
        leftButtonPanel.add(new JButton("削除"));
        leftPanel.add(leftButtonPanel, BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(leftPanel);
        
        // 右側パネル（タブ付き）
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // エディタタブ
        JTextArea editor = new JTextArea();
        editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        tabbedPane.addTab("エディタ", new JScrollPane(editor));
        
        // コンソールタブ
        JTextArea console = new JTextArea();
        console.setBackground(Color.BLACK);
        console.setForeground(Color.WHITE);
        console.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        tabbedPane.addTab("コンソール", new JScrollPane(console));
        
        splitPane.setRightComponent(tabbedPane);
        
        frame.add(splitPane, BorderLayout.CENTER);
        
        // 下部：ステータスバー
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(
            BorderFactory.createLoweredBevelBorder());
        statusBar.add(new JLabel("準備完了"), BorderLayout.WEST);
        
        JPanel rightStatus = new JPanel(
            new FlowLayout(FlowLayout.RIGHT));
        rightStatus.add(new JLabel("行: 1"));
        rightStatus.add(new JLabel("列: 1"));
        rightStatus.add(new JLabel("エンコーディング: UTF-8"));
        statusBar.add(rightStatus, BorderLayout.EAST);
        
        frame.add(statusBar, BorderLayout.SOUTH);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}