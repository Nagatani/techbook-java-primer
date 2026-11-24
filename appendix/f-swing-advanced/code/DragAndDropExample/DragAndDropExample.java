import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;
import java.util.Date;

public class DragAndDropExample {
    private final JFrame frame = new JFrame("ドラッグ&ドロップの例");
    private JList<String> sourceList;
    private JList<String> targetList;
    private DefaultListModel<String> sourceModel;
    private DefaultListModel<String> targetModel;
    private JTextArea dropArea;
    
    public DragAndDropExample() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // リストの作成
        sourceModel = new DefaultListModel<>();
        sourceModel.addElement("アイテム1");
        sourceModel.addElement("アイテム2");
        sourceModel.addElement("アイテム3");
        sourceModel.addElement("アイテム4");
        sourceModel.addElement("アイテム5");
        
        targetModel = new DefaultListModel<>();
        
        sourceList = new JList<>(sourceModel);
        targetList = new JList<>(targetModel);
        
        // ドラッグ機能の設定
        sourceList.setDragEnabled(true);
        sourceList.setTransferHandler(new ListTransferHandler());
        
        // ドロップ機能の設定
        targetList.setTransferHandler(new ListTransferHandler());
        
        // パネルの作成
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        listsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane sourceScroll = new JScrollPane(sourceList);
        sourceScroll.setBorder(BorderFactory.createTitledBorder("ソース"));
        
        JScrollPane targetScroll = new JScrollPane(targetList);
        targetScroll.setBorder(BorderFactory.createTitledBorder("ターゲット"));
        
        listsPanel.add(sourceScroll);
        listsPanel.add(targetScroll);
        
        // ファイルドロップエリア
        dropArea = new JTextArea(10, 40);
        dropArea.setBorder(BorderFactory.createTitledBorder("ファイルをここにドロップ"));
        dropArea.setEditable(false);
        
        // ファイルドロップの設定
        new DropTarget(dropArea, new FileDropTargetListener());
        
        frame.add(listsPanel, BorderLayout.CENTER);
        frame.add(new JScrollPane(dropArea), BorderLayout.SOUTH);
        
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
    }
    
    // リスト用のTransferHandler
    class ListTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }
        
        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            
            JList<String> list = (JList<String>) support.getComponent();
            DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
            
            try {
                String data = (String) support.getTransferable()
                    .getTransferData(DataFlavor.stringFlavor);
                
                JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
                int index = dl.getIndex();
                
                if (index == -1) {
                    model.addElement(data);
                } else {
                    model.add(index, data);
                }
                
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            JList<String> list = (JList<String>) c;
            String value = list.getSelectedValue();
            return new StringSelection(value);
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }
        
        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == MOVE) {
                JList<String> list = (JList<String>) source;
                DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
                model.removeElement(list.getSelectedValue());
            }
        }
    }
    
    // ファイルドロップ用のリスナー
    class FileDropTargetListener extends DropTargetAdapter {
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = dtde.getTransferable();
                
                if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    List<File> files = (List<File>) transferable
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    
                    for (File file : files) {
                        dropArea.append("ドロップされたファイル: " + file.getAbsolutePath() + "\n");
                        dropArea.append("  サイズ: " + file.length() + " bytes\n");
                        dropArea.append("  更新日時: " + new Date(file.lastModified()) + "\n\n");
                    }
                }
                
                dtde.dropComplete(true);
            } catch (Exception e) {
                e.printStackTrace();
                dtde.dropComplete(false);
            }
        }
        
        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            dropArea.setBackground(Color.LIGHT_GRAY);
        }
        
        @Override
        public void dragExit(DropTargetEvent dte) {
            dropArea.setBackground(Color.WHITE);
        }
    }
    
    private void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DragAndDropExample().show();
        });
    }
}
