/**
 * 第18章 GUI イベント処理応用
 * 演習課題: InteractiveDrawing.java
 * 
 * 【課題概要】
 * マウス操作で図形を描画できるインタラクティブな描画アプリケーションを作成してください。
 * 
 * 【要求仕様】
 * 1. マウスドラッグによる線・矩形・楕円の描画
 * 2. 色選択機能（パレットまたはカラーチューザー）
 * 3. 線の太さ調整スライダー
 * 4. 描画モード選択（ペン、矩形、楕円、消しゴム）
 * 5. 図形の移動・サイズ変更機能
 * 6. 描画のアンドゥ・リドゥ機能
 * 7. 描画内容のファイル保存・読み込み（画像形式）
 * 8. ツールバーとステータスバー
 * 9. 右クリックコンテキストメニュー
 * 10. キーボードショートカット対応
 * 
 * 【学習ポイント】
 * - MouseListener、MouseMotionListener の実装
 * - カスタム描画（Graphics2D の使用）
 * - 座標系とマウス位置の取得
 * - Shape クラスとその派生クラス
 * - BufferedImage を使った画像操作
 * - Command パターンによるアンドゥ・リドゥ
 * - PopupMenu の作成と表示
 * 
 * 【実装のヒント】
 * 1. JPanel を継承したカスタム描画パネルを作成
 * 2. ArrayList で描画済み図形のリストを管理
 * 3. Graphics2D の描画設定（色、線の太さ、アンチエイリアス）
 * 4. マウスイベントでの座標取得と描画判定
 * 5. BufferedImage でオフスクリーン描画
 * 
 * 【よくある間違いと対策】
 * - 描画時のちらつき
 *   → ダブルバッファリング、オフスクリーン描画を使用
 * - メモリリークによる動作の重さ
 *   → 不要な図形データの削除、適切なリスト管理
 * - マウス座標の計算ミス
 *   → getPoint() メソッドの正確な使用
 * - リアルタイム描画の実装不備
 *   → mouseDragged イベントでの適切な再描画
 * 
 * 【段階的な実装指針】
 * 1. 基本的なウィンドウとマウスイベント処理の実装
 * 2. 単純な線描画機能の追加
 * 3. 矩形・楕円描画機能の実装
 * 4. 色選択機能とツールバーの追加
 * 5. 図形選択・移動機能の実装
 * 6. アンドゥ・リドゥ機能の追加
 * 7. ファイル保存・読み込み機能の実装
 * 8. UI の改善とキーボードショートカット追加
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class InteractiveDrawing extends JFrame {
    private DrawingPanel drawingPanel;
    private JToolBar toolBar;
    private JLabel statusLabel;
    private JComboBox<DrawingMode> modeComboBox;
    private JSlider strokeSlider;
    private JButton colorButton;
    private Color currentColor = Color.BLACK;
    private int currentStroke = 2;
    private DrawingMode currentMode = DrawingMode.PEN;
    
    // 描画モード列挙型
    enum DrawingMode {
        PEN("ペン"), 
        RECTANGLE("矩形"), 
        ELLIPSE("楕円"), 
        ERASER("消しゴム"),
        SELECT("選択");
        
        private final String displayName;
        
        DrawingMode(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public InteractiveDrawing() {
        initializeComponents();
        setupToolBar();
        setupMenuBar();
        setupLayout();
        setupEventHandlers();
        setupWindow();
    }
    
    private void initializeComponents() {
        drawingPanel = new DrawingPanel();
        statusLabel = new JLabel("準備完了 - ペンモード");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // ツールバーコンポーネント
        modeComboBox = new JComboBox<>(DrawingMode.values());
        modeComboBox.setSelectedItem(DrawingMode.PEN);
        
        strokeSlider = new JSlider(1, 20, 2);
        strokeSlider.setMajorTickSpacing(5);
        strokeSlider.setMinorTickSpacing(1);
        strokeSlider.setPaintTicks(true);
        strokeSlider.setPaintLabels(true);
        
        colorButton = new JButton();
        colorButton.setPreferredSize(new Dimension(40, 30));
        colorButton.setBackground(currentColor);
        colorButton.setOpaque(true);
        colorButton.setToolTipText("色選択");
    }
    
    private void setupToolBar() {
        toolBar = new JToolBar("描画ツール");
        toolBar.setFloatable(false);
        
        // モード選択
        toolBar.add(new JLabel("モード:"));
        toolBar.add(modeComboBox);
        toolBar.addSeparator();
        
        // 色選択
        toolBar.add(new JLabel("色:"));
        toolBar.add(colorButton);
        toolBar.addSeparator();
        
        // 線の太さ
        toolBar.add(new JLabel("太さ:"));
        toolBar.add(strokeSlider);
        toolBar.addSeparator();
        
        // アクションボタン
        JButton clearButton = new JButton("クリア");
        clearButton.addActionListener(e -> drawingPanel.clearDrawing());
        toolBar.add(clearButton);
        
        JButton undoButton = new JButton("戻す");
        undoButton.addActionListener(e -> drawingPanel.undo());
        toolBar.add(undoButton);
        
        JButton redoButton = new JButton("進む");
        redoButton.addActionListener(e -> drawingPanel.redo());
        toolBar.add(redoButton);
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // ファイルメニュー
        JMenu fileMenu = new JMenu("ファイル");
        
        JMenuItem newItem = new JMenuItem("新規作成");
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newItem.addActionListener(e -> drawingPanel.clearDrawing());
        
        JMenuItem saveItem = new JMenuItem("保存");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveDrawing());
        
        JMenuItem openItem = new JMenuItem("開く");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> openDrawing());
        
        JMenuItem exitItem = new JMenuItem("終了");
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // 編集メニュー
        JMenu editMenu = new JMenu("編集");
        
        JMenuItem undoItem = new JMenuItem("元に戻す");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> drawingPanel.undo());
        
        JMenuItem redoItem = new JMenuItem("やり直し");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redoItem.addActionListener(e -> drawingPanel.redo());
        
        JMenuItem clearItem = new JMenuItem("すべてクリア");
        clearItem.addActionListener(e -> drawingPanel.clearDrawing());
        
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(clearItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(drawingPanel), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // モード変更イベント
        modeComboBox.addActionListener(e -> {
            currentMode = (DrawingMode) modeComboBox.getSelectedItem();
            drawingPanel.setDrawingMode(currentMode);
            updateStatus();
        });
        
        // 色選択イベント
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "色を選択", currentColor);
            if (newColor != null) {
                currentColor = newColor;
                colorButton.setBackground(currentColor);
                drawingPanel.setCurrentColor(currentColor);
            }
        });
        
        // 線の太さ変更イベント
        strokeSlider.addChangeListener(e -> {
            currentStroke = strokeSlider.getValue();
            drawingPanel.setStrokeWidth(currentStroke);
            updateStatus();
        });
    }
    
    private void setupWindow() {
        setTitle("Interactive Drawing Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private void updateStatus() {
        String modeText = currentMode.toString();
        String strokeText = "太さ: " + currentStroke;
        statusLabel.setText(modeText + " - " + strokeText);
    }
    
    private void saveDrawing() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "PNG Images", "png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            
            try {
                drawingPanel.saveToFile(file);
                JOptionPane.showMessageDialog(this, "描画を保存しました: " + file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "保存に失敗しました: " + ex.getMessage(),
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void openDrawing() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image Files", "png", "jpg", "jpeg", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                drawingPanel.loadFromFile(file);
                JOptionPane.showMessageDialog(this, "画像を読み込みました: " + file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "読み込みに失敗しました: " + ex.getMessage(),
                    "エラー", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // カスタム描画パネル
    private class DrawingPanel extends JPanel {
        private List<DrawingShape> shapes;
        private List<List<DrawingShape>> undoStack;
        private int undoIndex;
        private Point startPoint;
        private Point currentPoint;
        private DrawingShape currentShape;
        private BufferedImage backgroundImage;
        
        public DrawingPanel() {
            shapes = new ArrayList<>();
            undoStack = new ArrayList<>();
            undoIndex = -1;
            
            setPreferredSize(new Dimension(800, 600));
            setBackground(Color.WHITE);
            
            setupMouseHandlers();
            setupPopupMenu();
            
            // 初期状態を保存
            saveState();
        }
        
        private void setupMouseHandlers() {
            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    startPoint = e.getPoint();
                    currentPoint = e.getPoint();
                    
                    if (SwingUtilities.isRightMouseButton(e)) {
                        // 右クリックメニューは別途処理
                        return;
                    }
                    
                    // 新しい図形の作成
                    switch (currentMode) {
                        case PEN:
                            currentShape = new DrawingShape(DrawingShape.Type.PATH, currentColor, currentStroke);
                            currentShape.addPoint(startPoint);
                            break;
                        case RECTANGLE:
                            currentShape = new DrawingShape(DrawingShape.Type.RECTANGLE, currentColor, currentStroke);
                            break;
                        case ELLIPSE:
                            currentShape = new DrawingShape(DrawingShape.Type.ELLIPSE, currentColor, currentStroke);
                            break;
                        case ERASER:
                            eraseAt(startPoint);
                            break;
                        case SELECT:
                            // 選択モード（今回は簡略化）
                            break;
                    }
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        return;
                    }
                    
                    currentPoint = e.getPoint();
                    
                    switch (currentMode) {
                        case PEN:
                            if (currentShape != null) {
                                currentShape.addPoint(currentPoint);
                            }
                            break;
                        case ERASER:
                            eraseAt(currentPoint);
                            break;
                    }
                    
                    repaint();
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        return;
                    }
                    
                    if (currentShape != null) {
                        switch (currentMode) {
                            case RECTANGLE:
                                currentShape.setBounds(startPoint, currentPoint);
                                break;
                            case ELLIPSE:
                                currentShape.setBounds(startPoint, currentPoint);
                                break;
                        }
                        
                        if (currentMode != DrawingMode.ERASER) {
                            shapes.add(currentShape);
                            saveState();
                        }
                    }
                    
                    currentShape = null;
                    repaint();
                }
            };
            
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }
        
        private void setupPopupMenu() {
            JPopupMenu popupMenu = new JPopupMenu();
            
            JMenuItem clearItem = new JMenuItem("クリア");
            clearItem.addActionListener(e -> clearDrawing());
            
            JMenuItem undoItem = new JMenuItem("元に戻す");
            undoItem.addActionListener(e -> undo());
            
            JMenuItem redoItem = new JMenuItem("やり直し");
            redoItem.addActionListener(e -> redo());
            
            popupMenu.add(clearItem);
            popupMenu.addSeparator();
            popupMenu.add(undoItem);
            popupMenu.add(redoItem);
            
            setComponentPopupMenu(popupMenu);
        }
        
        private void eraseAt(Point point) {
            shapes.removeIf(shape -> shape.contains(point));
            repaint();
        }
        
        public void setDrawingMode(DrawingMode mode) {
            currentMode = mode;
        }
        
        public void setCurrentColor(Color color) {
            currentColor = color;
        }
        
        public void setStrokeWidth(int width) {
            currentStroke = width;
        }
        
        public void clearDrawing() {
            shapes.clear();
            backgroundImage = null;
            saveState();
            repaint();
        }
        
        public void undo() {
            if (undoIndex > 0) {
                undoIndex--;
                shapes = new ArrayList<>(undoStack.get(undoIndex));
                repaint();
            }
        }
        
        public void redo() {
            if (undoIndex < undoStack.size() - 1) {
                undoIndex++;
                shapes = new ArrayList<>(undoStack.get(undoIndex));
                repaint();
            }
        }
        
        private void saveState() {
            // 現在より後の履歴を削除
            if (undoIndex < undoStack.size() - 1) {
                undoStack = undoStack.subList(0, undoIndex + 1);
            }
            
            undoStack.add(new ArrayList<>(shapes));
            undoIndex = undoStack.size() - 1;
            
            // 履歴の最大数を制限
            if (undoStack.size() > 50) {
                undoStack.remove(0);
                undoIndex--;
            }
        }
        
        public void saveToFile(File file) throws IOException {
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            
            // 背景を白で塗りつぶし
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // 描画内容を画像に描画
            paintShapes(g2d);
            
            g2d.dispose();
            ImageIO.write(image, "png", file);
        }
        
        public void loadFromFile(File file) throws IOException {
            backgroundImage = ImageIO.read(file);
            clearDrawing();
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // アンチエイリアス設定
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 背景画像の描画
            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, null);
            }
            
            // 図形の描画
            paintShapes(g2d);
            
            // 現在描画中の図形の描画
            if (currentShape != null && currentPoint != null) {
                switch (currentMode) {
                    case RECTANGLE:
                        g2d.setColor(currentColor);
                        g2d.setStroke(new BasicStroke(currentStroke));
                        Rectangle rect = new Rectangle(
                            Math.min(startPoint.x, currentPoint.x),
                            Math.min(startPoint.y, currentPoint.y),
                            Math.abs(currentPoint.x - startPoint.x),
                            Math.abs(currentPoint.y - startPoint.y)
                        );
                        g2d.draw(rect);
                        break;
                    case ELLIPSE:
                        g2d.setColor(currentColor);
                        g2d.setStroke(new BasicStroke(currentStroke));
                        Ellipse2D ellipse = new Ellipse2D.Double(
                            Math.min(startPoint.x, currentPoint.x),
                            Math.min(startPoint.y, currentPoint.y),
                            Math.abs(currentPoint.x - startPoint.x),
                            Math.abs(currentPoint.y - startPoint.y)
                        );
                        g2d.draw(ellipse);
                        break;
                }
            }
            
            g2d.dispose();
        }
        
        private void paintShapes(Graphics2D g2d) {
            for (DrawingShape shape : shapes) {
                shape.draw(g2d);
            }
        }
    }
    
    // 描画図形クラス
    private static class DrawingShape {
        enum Type { PATH, RECTANGLE, ELLIPSE }
        
        private Type type;
        private Color color;
        private int strokeWidth;
        private List<Point> points;
        private Rectangle bounds;
        
        public DrawingShape(Type type, Color color, int strokeWidth) {
            this.type = type;
            this.color = color;
            this.strokeWidth = strokeWidth;
            this.points = new ArrayList<>();
        }
        
        public void addPoint(Point point) {
            points.add(new Point(point));
        }
        
        public void setBounds(Point start, Point end) {
            int x = Math.min(start.x, end.x);
            int y = Math.min(start.y, end.y);
            int width = Math.abs(end.x - start.x);
            int height = Math.abs(end.y - start.y);
            bounds = new Rectangle(x, y, width, height);
        }
        
        public boolean contains(Point point) {
            switch (type) {
                case PATH:
                    for (Point p : points) {
                        if (point.distance(p) <= strokeWidth) {
                            return true;
                        }
                    }
                    return false;
                case RECTANGLE:
                case ELLIPSE:
                    return bounds != null && bounds.contains(point);
                default:
                    return false;
            }
        }
        
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            switch (type) {
                case PATH:
                    if (points.size() > 1) {
                        for (int i = 1; i < points.size(); i++) {
                            Point p1 = points.get(i - 1);
                            Point p2 = points.get(i);
                            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }
                    }
                    break;
                case RECTANGLE:
                    if (bounds != null) {
                        g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
                    }
                    break;
                case ELLIPSE:
                    if (bounds != null) {
                        g2d.drawOval(bounds.x, bounds.y, bounds.width, bounds.height);
                    }
                    break;
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new InteractiveDrawing().setVisible(true);
        });
    }
}