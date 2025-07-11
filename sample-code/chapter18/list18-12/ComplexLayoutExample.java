/**
 * リスト18-12
 * ComplexLayoutExampleクラス
 * 
 * 元ファイル: chapter18-gui-basics.md (529行目)
 */

import javax.swing.*;
import java.awt.*;

public class ComplexLayoutExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("複雑なレイアウトの例");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // ツールバー（上部）
        JPanel toolbarPanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT));
        toolbarPanel.add(new JButton("新規"));
        toolbarPanel.add(new JButton("開く"));
        toolbarPanel.add(new JButton("保存"));
        toolbarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolbarPanel.add(new JButton("切り取り"));
        toolbarPanel.add(new JButton("コピー"));
        toolbarPanel.add(new JButton("貼り付け"));
        frame.add(toolbarPanel, BorderLayout.NORTH);
        
        // メインエリア（中央）
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 左側のサイドバー
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBorder(
            BorderFactory.createTitledBorder("ファイル一覧"));
        String[] files = {"ファイル1.txt", "ファイル2.txt", "ファイル3.txt"};
        JList<String> fileList = new JList<>(files);
        sidebarPanel.add(new JScrollPane(fileList), BorderLayout.CENTER);
        sidebarPanel.setPreferredSize(new Dimension(150, 0));
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // 中央のテキストエリア
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane textScrollPane = new JScrollPane(textArea);
        textScrollPane.setBorder(
            BorderFactory.createTitledBorder("エディタ"));
        mainPanel.add(textScrollPane, BorderLayout.CENTER);
        
        frame.add(mainPanel, BorderLayout.CENTER);
        
        // ステータスバー（下部）
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(
            BorderFactory.createLoweredBevelBorder());
        statusPanel.add(new JLabel("準備完了"), BorderLayout.WEST);
        statusPanel.add(new JLabel("行: 1, 列: 1"), BorderLayout.EAST);
        frame.add(statusPanel, BorderLayout.SOUTH);
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}