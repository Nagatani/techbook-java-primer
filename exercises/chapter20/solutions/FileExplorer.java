/**
 * 第20章 課題2: ファイルの一覧（解答例）
 *
 * 左にフォルダーの木（JTree）、右上にファイルの表（JTable）、右下に中身のプレビューを並べます。
 * 子のフォルダーはノードを開いたときに初めて読み込み、フォルダーの中身の読み込みはSwingWorker（第19章）で
 * 別のスレッドに任せます。
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class FileExplorer extends JFrame {

    // プレビューに出す行の数
    private static final int PREVIEW_LINES = 50;

    private JTree directoryTree;
    private DirectoryTreeModel treeModel;
    private JTable fileTable;
    private FileTableModel fileTableModel;
    private JTextArea previewArea;
    private JTextField searchField;
    private JLabel statusLabel;
    private JSplitPane mainSplitPane, rightSplitPane;
    private Path currentDirectory;

    // ファイルの情報（雛形のまま）
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

    // ツリーのモデル。ノードの子を読み込んだあとに、木の表示へ知らせる
    private class DirectoryTreeModel extends DefaultTreeModel {
        public DirectoryTreeModel(TreeNode root) {
            super(root);
        }

        public void loadChildrenOf(DirectoryTreeNode node) {
            if (!node.isLoaded()) {
                node.loadChildren();
                nodeStructureChanged(node);
            }
        }
    }

    // ファイルの表のモデル
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
            return switch (columnIndex) {
                case 0 -> file.getName();
                case 1 -> file.getFormattedSize();
                case 2 -> file.isDirectory() ? "フォルダー" : getFileType(file.getName());
                case 3 -> file.getFormattedDate();
                default -> "";
            };
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

        // 名前に検索の文字を含むものだけを残し、表に知らせる
        private void applyFilter() {
            filteredFiles = new ArrayList<>();
            for (FileInfo file : files) {
                if (filter.isEmpty() || file.getName().toLowerCase().contains(filter)) {
                    filteredFiles.add(file);
                }
            }
            // 第12章のStream APIを使うと、次のように書ける
            // filteredFiles = files.stream().filter(f -> f.getName().toLowerCase().contains(filter)).toList();
            fireTableDataChanged();
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

    // フォルダーのノード。開かれるまで子を読み込まない（遅延読み込み）
    private class DirectoryTreeNode extends DefaultMutableTreeNode {
        private final Path path;
        private boolean loaded;

        public DirectoryTreeNode(Path path) {
            super(path.getFileName() == null ? path.toString() : path.getFileName().toString());
            this.path = path;
            this.loaded = false;
        }

        public Path getDirectoryPath() {
            return path;
        }

        public boolean isLoaded() {
            return loaded;
        }

        // 子のフォルダーだけを名前順にノードにする。隠しフォルダーと読めないフォルダーは飛ばす
        public void loadChildren() {
            if (loaded) return;

            for (Path child : listSorted(path)) {
                try {
                    if (Files.isDirectory(child) && !Files.isHidden(child)) {
                        add(new DirectoryTreeNode(child));
                    }
                } catch (IOException e) {
                    // 属性を読めないフォルダーは木に出さない
                }
            }
            loaded = true;
        }

        @Override
        public boolean isLeaf() {
            if (!loaded) {
                return !Files.isDirectory(path);
            }
            return getChildCount() == 0;
        }
    }

    public FileExplorer() {
        setTitle("ファイルの一覧");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadInitialDirectory();
    }

    private void initializeComponents() {
        DirectoryTreeNode root = new DirectoryTreeNode(Path.of(System.getProperty("user.home")));
        treeModel = new DirectoryTreeModel(root);
        treeModel.loadChildrenOf(root);
        directoryTree = new JTree(treeModel);
        directoryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        fileTableModel = new FileTableModel();
        fileTable = new JTable(fileTableModel);
        fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 名前の列に、OSのファイルのアイコンを付けて表示する
        fileTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setIcon(getFileIcon(fileTableModel.getFileAt(row).getPath()));
                return this;
            }
        });

        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        searchField = new JTextField(20);
        statusLabel = new JLabel(" ");
    }

    private void setupLayout() {
        // 右側を上下に、全体を左右に分ける。境目はドラッグで動かせる
        rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(fileTable), new JScrollPane(previewArea));
        rightSplitPane.setResizeWeight(0.7);
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(directoryTree), rightSplitPane);
        mainSplitPane.setDividerLocation(250);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("名前で絞り込み:"));
        topPanel.add(searchField);

        add(topPanel, BorderLayout.NORTH);
        add(mainSplitPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        // ノードが開かれる直前に、子のフォルダーを読み込む
        directoryTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) {
                Object node = event.getPath().getLastPathComponent();
                if (node instanceof DirectoryTreeNode dirNode) {
                    treeModel.loadChildrenOf(dirNode);
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) {
            }
        });

        // ツリーで選んだフォルダーの中身を表に出す
        directoryTree.addTreeSelectionListener(e -> {
            Object node = directoryTree.getLastSelectedPathComponent();
            if (node instanceof DirectoryTreeNode dirNode) {
                loadDirectoryContents(dirNode.getDirectoryPath());
            }
        });

        // 表で選んだファイルの中身を下にプレビューする
        fileTable.getSelectionModel().addListSelectionListener(e -> {
            int row = fileTable.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0) {
                previewFile(fileTableModel.getFileAt(row).getPath());
            }
        });

        // 右クリックで、選んだファイルの操作メニューを出す
        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { showPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { showPopup(e); }

            private void showPopup(MouseEvent e) {
                // ポップアップを出す操作はOSで違う（押したとき・離したとき）ので、両方で確かめる
                if (!e.isPopupTrigger()) {
                    return;
                }
                int row = fileTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    fileTable.setRowSelectionInterval(row, row);
                    boolean isDirectory = fileTableModel.getFileAt(row).isDirectory();
                    createContextMenu(isDirectory).show(fileTable, e.getX(), e.getY());
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { searchFiles(searchField.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { searchFiles(searchField.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { searchFiles(searchField.getText()); }
        });
    }

    private void loadInitialDirectory() {
        directoryTree.setSelectionRow(0);
    }

    // フォルダーの中身を別のスレッドで読み、読み終えたらEDTで表に入れる
    private void loadDirectoryContents(Path directory) {
        currentDirectory = directory;
        statusLabel.setText(directory + " を読み込んでいます...");
        new SwingWorker<List<FileInfo>, Void>() {
            @Override
            protected List<FileInfo> doInBackground() {
                List<FileInfo> result = new ArrayList<>();
                for (Path child : listSorted(directory)) {
                    try {
                        FileInfo info = new FileInfo(child);
                        if (!info.isHidden()) {
                            result.add(info);
                        }
                    } catch (IOException e) {
                        // 属性を読めないファイルは一覧に出さない
                    }
                }
                // フォルダーを先に、同じ種類どうしは名前順に並べる
                result.sort(Comparator.comparing(FileInfo::isDirectory).reversed()
                        .thenComparing(FileInfo::getName));
                return result;
            }

            @Override
            protected void done() {
                try {
                    List<FileInfo> files = get();
                    fileTableModel.setFiles(files);
                    previewArea.setText("");
                    statusLabel.setText(directory + "（" + files.size() + "件）");
                } catch (InterruptedException | ExecutionException e) {
                    statusLabel.setText("読み込めませんでした: " + e.getMessage());
                }
            }
        }.execute();
    }

    // テキストのファイルなら先頭の行を、フォルダーなら中の件数を表示する
    private void previewFile(Path filePath) {
        if (Files.isDirectory(filePath)) {
            previewArea.setText("フォルダー: " + filePath.getFileName()
                    + "\n中の項目: " + listSorted(filePath).size() + "件");
            return;
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < PREVIEW_LINES) {
                sb.append(line).append('\n');
                count++;
            }
            if (line != null) {
                sb.append("...（先頭の").append(PREVIEW_LINES).append("行だけを表示しています）\n");
            }
            previewArea.setText(sb.toString());
        } catch (CharacterCodingException e) {
            // UTF-8の文字として読めないファイル（画像など）
            previewArea.setText("このファイルはプレビューできません");
        } catch (IOException e) {
            previewArea.setText("読み込めませんでした: " + e.getMessage());
        }
        previewArea.setCaretPosition(0);
    }

    private void searchFiles(String searchTerm) {
        fileTableModel.setFilter(searchTerm.trim());
        statusLabel.setText(fileTableModel.getRowCount() + "件が一致しました");
    }

    private void copyFile(Path source, Path destination) {
        try {
            Files.copy(source, destination);
            statusLabel.setText(source.getFileName() + " をコピーしました");
        } catch (FileAlreadyExistsException e) {
            showError("同じ名前のファイルがあります: " + destination);
        } catch (IOException e) {
            showError("コピーできませんでした: " + e.getMessage());
        }
        refreshCurrentDirectory();
    }

    private void moveFile(Path source, Path destination) {
        try {
            // Files.moveは、Files.copyと同じ形で使える移動（名前の変更）のメソッド
            Files.move(source, destination);
            statusLabel.setText(source.getFileName() + " を移動しました");
        } catch (FileAlreadyExistsException e) {
            showError("同じ名前のファイルがあります: " + destination);
        } catch (IOException e) {
            showError("移動できませんでした: " + e.getMessage());
        }
        refreshCurrentDirectory();
    }

    private void deleteFile(Path filePath) {
        int answer = JOptionPane.showConfirmDialog(this, filePath.getFileName() + " を削除しますか？",
                "削除の確認", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            // Files.deleteは、中身のあるフォルダーを消せない（DirectoryNotEmptyException）
            Files.delete(filePath);
            statusLabel.setText(filePath.getFileName() + " を削除しました");
        } catch (DirectoryNotEmptyException e) {
            showError("中身のあるフォルダーは削除できません");
        } catch (IOException e) {
            showError("削除できませんでした: " + e.getMessage());
        }
        refreshCurrentDirectory();
    }

    private JPopupMenu createContextMenu(boolean isDirectory) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem copyItem = new JMenuItem("コピー...");
        JMenuItem moveItem = new JMenuItem("移動...");
        JMenuItem deleteItem = new JMenuItem("削除");

        copyItem.addActionListener(e -> {
            Path source = selectedPath();
            Path folder = chooseFolder("コピー先のフォルダー");
            if (source != null && folder != null) {
                copyFile(source, folder.resolve(source.getFileName()));
            }
        });
        moveItem.addActionListener(e -> {
            Path source = selectedPath();
            Path folder = chooseFolder("移動先のフォルダー");
            if (source != null && folder != null) {
                moveFile(source, folder.resolve(source.getFileName()));
            }
        });
        deleteItem.addActionListener(e -> {
            Path target = selectedPath();
            if (target != null) {
                deleteFile(target);
            }
        });

        // Files.copyはフォルダーの中身までは写さないので、フォルダーのコピーは出さない
        if (!isDirectory) {
            menu.add(copyItem);
        }
        menu.add(moveItem);
        menu.addSeparator();
        menu.add(deleteItem);
        return menu;
    }

    private Icon getFileIcon(Path path) {
        return FileSystemView.getFileSystemView().getSystemIcon(path.toFile());
    }

    private Path selectedPath() {
        int row = fileTable.getSelectedRow();
        return row < 0 ? null : fileTableModel.getFileAt(row).getPath();
    }

    private Path chooseFolder(String title) {
        JFileChooser chooser = new JFileChooser(currentDirectory.toFile());
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showDialog(this, "選択") == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().toPath();
        }
        return null;
    }

    private void refreshCurrentDirectory() {
        if (currentDirectory != null) {
            loadDirectoryContents(currentDirectory);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }

    // フォルダーの中の項目を名前順のリストで返す。Files.listの順序はOSで変わるので並べ替える
    private static List<Path> listSorted(Path directory) {
        List<Path> result = new ArrayList<>();
        // Files.listはStream（第12章）を返す。閉じる必要があるので、try-with-resourcesで囲む
        try (Stream<Path> children = Files.list(directory)) {
            children.forEach(result::add);
        } catch (IOException e) {
            // 読めないフォルダーは空として扱う
        }
        result.sort(Comparator.comparing(p -> p.getFileName().toString()));
        return result;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileExplorer().setVisible(true));
    }
}
