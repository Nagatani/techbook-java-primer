/**
 * 第19章 課題3: ファイルエクスプローラーGUI
 * 
 * JTreeとJTableを組み合わせたファイルエクスプローラーを作成してください。
 * 
 * 要求仕様:
 * - ディレクトリ構造をJTreeで表示
 * - ファイル一覧をJTableで表示
 * - ファイル・フォルダの操作機能（コピー、移動、削除）
 * - ファイルの検索機能
 * - ファイルプレビュー機能
 * 
 * 学習ポイント:
 * - JTreeとTreeModelの活用
 * - ファイルシステムAPIの使用
 * - 分割ペイン（JSplitPane）の活用
 * - ファイル監視機能の実装
 * - 複雑なUIレイアウトの構築
 */

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.text.SimpleDateFormat;
import javax.swing.filechooser.FileSystemView;

public class FileExplorer extends JFrame {
    
    // ここに実装してください
    
    // 必要なコンポーネントの例:
    // private JTree directoryTree;
    // private JTable fileTable;
    // private FileTableModel fileTableModel;
    // private JTextArea previewArea;
    // private JTextField searchField;
    // private JProgressBar progressBar;
    // private JSplitPane mainSplitPane, rightSplitPane;
    
    // ファイル情報クラス
    public static class FileInfo {
        private final Path path;
        private final String name;
        private final long size;
        private final long lastModified;
        private final boolean isDirectory;
        private final boolean isHidden;
        
        public FileInfo(Path path) throws IOException {
            this.path = path;
            this.name = path.getFileName().toString();
            
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            this.size = attrs.size();
            this.lastModified = attrs.lastModifiedTime().toMillis();
            this.isDirectory = attrs.isDirectory();
            this.isHidden = Files.isHidden(path);
        }
        
        // getters
        public Path getPath() { return path; }
        public String getName() { return name; }
        public long getSize() { return size; }
        public long getLastModified() { return lastModified; }
        public boolean isDirectory() { return isDirectory; }
        public boolean isHidden() { return isHidden; }
        
        public String getFormattedSize() {
            if (isDirectory) return "";
            
            if (size < 1024) return size + " B";
            if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
            if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
        
        public String getFormattedDate() {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date(lastModified));
        }
    }
    
    // ディレクトリツリーモデル
    private class DirectoryTreeModel extends DefaultTreeModel {
        public DirectoryTreeModel(TreeNode root) {
            super(root);
        }
        
        // ここにディレクトリツリーモデルを実装してください
    }
    
    // ファイルテーブルモデル
    private class FileTableModel extends AbstractTableModel {
        private final String[] columnNames = {"名前", "サイズ", "種類", "更新日時"};
        private List<FileInfo> files;
        private List<FileInfo> filteredFiles;
        private String filter;
        
        public FileTableModel() {
            this.files = new ArrayList<>();
            this.filteredFiles = new ArrayList<>();
            this.filter = "";
        }
        
        @Override
        public int getRowCount() {
            return filteredFiles.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            FileInfo file = filteredFiles.get(rowIndex);
            switch (columnIndex) {
                case 0: return file.getName();
                case 1: return file.getFormattedSize();
                case 2: return file.isDirectory() ? "フォルダ" : getFileType(file.getName());
                case 3: return file.getFormattedDate();
                default: return "";
            }
        }
        
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
        
        public void setFiles(List<FileInfo> files) {
            this.files = new ArrayList<>(files);
            applyFilter();
        }
        
        public void setFilter(String filter) {
            this.filter = filter.toLowerCase();
            applyFilter();
        }
        
        private void applyFilter() {
            // TODO: フィルタリング処理を実装
        }
        
        public FileInfo getFileAt(int index) {
            return filteredFiles.get(index);
        }
        
        private String getFileType(String fileName) {
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                return fileName.substring(dotIndex + 1).toUpperCase() + " ファイル";
            }
            return "ファイル";
        }
    }
    
    // ディレクトリツリーノード
    private class DirectoryTreeNode extends DefaultMutableTreeNode {
        private final Path path;
        private boolean loaded;
        
        public DirectoryTreeNode(Path path) {
            super(path.getFileName().toString());
            this.path = path;
            this.loaded = false;
        }
        
        public Path getPath() {
            return path;
        }
        
        public boolean isLoaded() {
            return loaded;
        }
        
        public void loadChildren() {
            if (loaded) return;
            
            // TODO: 子ディレクトリの読み込みを実装
            loaded = true;
        }
        
        @Override
        public boolean isLeaf() {
            if (!loaded) {
                try {
                    return !Files.isDirectory(path);
                } catch (Exception e) {
                    return true;
                }
            }
            return getChildCount() == 0;
        }
    }
    
    public FileExplorer() {
        // ここにコンストラクタを実装してください
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadInitialDirectory();
    }
    
    // コンポーネントの初期化
    private void initializeComponents() {
        // ここにコンポーネント初期化を実装してください
    }
    
    // レイアウトの設定
    private void setupLayout() {
        // ここにレイアウト設定を実装してください
    }
    
    // イベントリスナーの設定
    private void setupEventListeners() {
        // ここにイベントリスナーを実装してください
    }
    
    // 初期ディレクトリの読み込み
    private void loadInitialDirectory() {
        // ここに初期ディレクトリ読み込みを実装してください
    }
    
    // ディレクトリの内容を読み込み
    private void loadDirectoryContents(Path directory) {
        // ここにディレクトリ内容読み込みを実装してください
    }
    
    // ファイルプレビュー
    private void previewFile(Path filePath) {
        // ここにファイルプレビューを実装してください
    }
    
    // ファイル検索
    private void searchFiles(String searchTerm) {
        // ここにファイル検索を実装してください
    }
    
    // ファイル操作（コピー、移動、削除）
    private void copyFile(Path source, Path destination) {
        // ここにファイルコピーを実装してください
    }
    
    private void moveFile(Path source, Path destination) {
        // ここにファイル移動を実装してください
    }
    
    private void deleteFile(Path filePath) {
        // ここにファイル削除を実装してください
    }
    
    // コンテキストメニューの作成
    private JPopupMenu createContextMenu(boolean isDirectory) {
        // ここにコンテキストメニューを実装してください
        return new JPopupMenu();
    }
    
    // ファイルアイコンの取得
    private Icon getFileIcon(Path path) {
        // ここにファイルアイコン取得を実装してください
        return null;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new FileExplorer().setVisible(true);
        });
    }
}

/*
 * 実装のヒント:
 * 
 * 1. ツリー構造の管理
 *    - 遅延読み込み（Lazy Loading）の実装
 *    - TreeExpansionListenerによる動的読み込み
 *    - ルートノードの設定（複数ドライブ対応）
 *    - ツリー選択時のテーブル更新
 * 
 * 2. ファイルシステムAPI
 *    - java.nio.file.Pathの活用
 *    - Files.walkFileTreeによるディレクトリ走査
 *    - WatchServiceによるファイル監視
 *    - FileSystemViewによるシステム情報取得
 * 
 * 3. パフォーマンス最適化
 *    - バックグラウンドスレッドでのファイル読み込み
 *    - SwingWorkerによるプログレス表示
 *    - キャッシュ機能の実装
 *    - 大量ファイル処理時のメモリ管理
 * 
 * 4. ユーザビリティ
 *    - キーボードショートカット対応
 *    - ドラッグ&ドロップ機能
 *    - ブックマーク機能
 *    - 履歴機能
 * 
 * 5. ファイル操作
 *    - 進捗表示付きのファイル操作
 *    - エラーハンドリング
 *    - 権限チェック
 *    - 確認ダイアログ
 * 
 * よくある間違い:
 * - UIスレッドでの重い処理実行
 * - ファイル権限の考慮不足
 * - メモリリークの発生
 * - 例外処理の不備
 * 
 * 発展課題:
 * - タブ表示機能
 * - 圧縮ファイル対応
 * - FTP/SFTP対応
 * - クラウドストレージ連携
 * - プラグイン機能
 */