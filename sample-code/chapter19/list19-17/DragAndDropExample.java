/**
 * リスト19-17
 * DragAndDropExampleクラス
 * 
 * 元ファイル: chapter19-gui-event-handling.md (2150行目)
 */

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

public class DragAndDropExample extends JFrame {
    private JList<String> sourceList;
    private JList<String> targetList;
    private DefaultListModel<String> sourceModel;
    private DefaultListModel<String> targetModel;
    private JTextArea dropArea;
    
    public DragAndDropExample() {
        setTitle("ドラッグ＆ドロップの実装");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // リスト間のドラッグ＆ドロップ
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        listsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ソースリスト
        sourceModel = new DefaultListModel<>();
        sourceModel.addElement("アイテム 1");
        sourceModel.addElement("アイテム 2");
        sourceModel.addElement("アイテム 3");
        sourceModel.addElement("アイテム 4");
        sourceModel.addElement("アイテム 5");
        
        sourceList = new JList<>(sourceModel);
        sourceList.setDragEnabled(true);
        sourceList.setTransferHandler(new ListTransferHandler());
        
        // ターゲットリスト
        targetModel = new DefaultListModel<>();
        targetList = new JList<>(targetModel);
        targetList.setTransferHandler(new ListTransferHandler());
        
        JScrollPane sourceScroll = new JScrollPane(sourceList);
        sourceScroll.setBorder(BorderFactory.createTitledBorder("ソース"));
        JScrollPane targetScroll = new JScrollPane(targetList);
        targetScroll.setBorder(BorderFactory.createTitledBorder("ターゲット"));
        
        listsPanel.add(sourceScroll);
        listsPanel.add(targetScroll);
        
        // ファイルドロップエリア
        dropArea = new JTextArea(10, 40);
        dropArea.setText("ここにファイルをドロップしてください...");
        dropArea.setEditable(false);
        JScrollPane dropScroll = new JScrollPane(dropArea);
        dropScroll.setBorder(BorderFactory.createTitledBorder("ファイルドロップエリア"));
        
        // ドロップターゲットの設定
        new DropTarget(dropArea, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                dropArea.setBackground(Color.LIGHT_GRAY);
            }
            
            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                // 必要に応じて処理
            }
            
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
                // 必要に応じて処理
            }
            
            @Override
            public void dragExit(DropTargetEvent dte) {
                dropArea.setBackground(Color.WHITE);
            }
            
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable transferable = dtde.getTransferable();
                    
                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) transferable.getTransferData(
                            DataFlavor.javaFileListFlavor);
                        
                        dropArea.setText("ドロップされたファイル:\n");
                        for (File file : files) {
                            dropArea.append("- " + file.getAbsolutePath() + "\n");
                            dropArea.append("  サイズ: " + file.length() + " bytes\n");
                            dropArea.append("  更新日時: " + java.time.LocalDateTime.ofInstant(
                                java.time.Instant.ofEpochMilli(file.lastModified()), 
                                java.time.ZoneId.systemDefault()) + "\n\n");
                        }
                    }
                    
                    dtde.dropComplete(true);
                    dropArea.setBackground(Color.WHITE);
                } catch (Exception e) {
                    e.printStackTrace();
                    dtde.dropComplete(false);
                }
            }
        });
        
        add(listsPanel, BorderLayout.CENTER);
        add(dropScroll, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    // カスタムTransferHandler
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
            
            JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
            int index = dl.getIndex();
            
            try {
                String data = (String) support.getTransferable().getTransferData(
                    DataFlavor.stringFlavor);
                
                if (support.getComponent() == targetList) {
                    if (index == -1) {
                        targetModel.addElement(data);
                    } else {
                        targetModel.add(index, data);
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return false;
        }
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            @SuppressWarnings("unchecked")
            JList<String> list = (JList<String>) c;
            String value = list.getSelectedValue();
            return new StringSelection(value);
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DragAndDropExample().setVisible(true);
        });
    }
}