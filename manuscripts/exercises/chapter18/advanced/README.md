# 第18章 GUIプログラミング基礎 - 応用レベル課題

## 概要
応用レベル課題では、より複雑なGUIアプリケーションの開発に挑戦します。カスタムコンポーネント、高度なレイアウト、EDTの適切な使用など、実用的なデスクトップアプリケーション開発に必要な技術を習得します。

## 課題一覧

### 課題1: テキストエディタの実装
**SimpleTextEditor.java**

基本的な機能を持つテキストエディタを実装します。

**要求仕様：**
- JTextAreaを使用したテキスト編集機能
- メニューバー（ファイル、編集、表示メニュー）
- ファイルの開く/保存機能
- 検索・置換機能
- 行番号表示（オプション）

**実装のポイント：**
```java
public class SimpleTextEditor extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private File currentFile;
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // ファイルメニュー
        JMenu fileMenu = new JMenu("ファイル");
        JMenuItem openItem = new JMenuItem("開く");
        openItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> openFile());
        
        fileMenu.add(openItem);
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void openFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // ファイル読み込み処理
            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() throws Exception {
                    return Files.readString(fileChooser.getSelectedFile().toPath());
                }
                
                @Override
                protected void done() {
                    try {
                        textArea.setText(get());
                    } catch (Exception e) {
                        // エラー処理
                    }
                }
            };
            worker.execute();
        }
    }
}
```

**追加機能：**
- Undo/Redo機能
- 構文ハイライト（簡易版）
- ステータスバー（文字数、行数表示）
- 最近使ったファイルリスト

### 課題2: 描画アプリケーション
**SimplePaintApp.java**

マウスで絵を描けるペイントアプリケーションを作成します。

**要求仕様：**
- カスタムJComponentでの描画領域実装
- マウスドラッグによる自由線描画
- 色選択機能（JColorChooser使用）
- 線の太さ選択
- 描画内容のクリア機能

**実装のポイント：**
```java
public class DrawingPanel extends JPanel {
    private BufferedImage canvas;
    private Graphics2D g2d;
    private Point lastPoint;
    
    public DrawingPanel() {
        setBackground(Color.WHITE);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastPoint != null) {
                    g2d.setStroke(new BasicStroke(strokeWidth));
                    g2d.setColor(currentColor);
                    g2d.drawLine(lastPoint.x, lastPoint.y, e.getX(), e.getY());
                    lastPoint = e.getPoint();
                    repaint();
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvas != null) {
            g.drawImage(canvas, 0, 0, null);
        }
    }
}
```

**追加機能：**
- 図形描画（直線、四角形、円）
- 塗りつぶし機能
- 画像の保存/読み込み
- レイヤー機能（簡易版）

### 課題3: データビジュアライゼーション
**DataVisualizationApp.java**

データを視覚的に表示するアプリケーションを実装します。

**要求仕様：**
- カスタムコンポーネントでのグラフ描画
- 棒グラフ、折れ線グラフ、円グラフの実装
- CSVファイルからのデータ読み込み
- グラフの動的更新
- ツールチップによるデータ値表示

**実装のポイント：**
```java
public class ChartPanel extends JPanel {
    private List<DataPoint> data;
    private ChartType chartType;
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        switch (chartType) {
            case BAR_CHART:
                drawBarChart(g2d);
                break;
            case LINE_CHART:
                drawLineChart(g2d);
                break;
            case PIE_CHART:
                drawPieChart(g2d);
                break;
        }
    }
    
    private void drawBarChart(Graphics2D g2d) {
        int barWidth = getWidth() / data.size();
        int maxValue = data.stream()
            .mapToInt(DataPoint::getValue)
            .max().orElse(1);
        
        for (int i = 0; i < data.size(); i++) {
            int barHeight = (int) ((double) data.get(i).getValue() 
                / maxValue * getHeight() * 0.8);
            int x = i * barWidth + barWidth / 4;
            int y = getHeight() - barHeight - 20;
            
            g2d.setColor(getColorForIndex(i));
            g2d.fillRect(x, y, barWidth / 2, barHeight);
        }
    }
}
```

## 学習のポイント

1. **カスタムコンポーネントの作成**
   - JComponentやJPanelの継承
   - paintComponent()のオーバーライド
   - Graphics2Dの活用

2. **イベント処理の応用**
   - 複数のリスナーの組み合わせ
   - イベントの伝播と処理
   - カスタムイベントの作成

3. **EDT（Event Dispatch Thread）の理解**
   - SwingUtilities.invokeLater()の使用
   - SwingWorkerによる非同期処理
   - UIの応答性維持

4. **高度なSwing機能**
   - JMenuBarとアクセラレータキー
   - JFileChooserの活用
   - カスタムダイアログの作成

## 提出物

1. 各課題の完全なソースコード
2. 実行可能なJARファイル
3. ユーザーマニュアル（簡易版）
4. 設計ドキュメント（クラス図含む）

## 評価基準

- **機能の完成度（35%）**: すべての要求機能が実装されているか
- **UIデザイン（25%）**: 使いやすく美しいインターフェース
- **コード品質（25%）**: 適切な設計パターン、保守性
- **パフォーマンス（15%）**: スムーズな動作、メモリ効率

## 発展課題

- ドラッグ&ドロップ機能の実装
- 国際化（i18n）対応
- プラグインシステムの実装
- Look and Feelのカスタマイズ

## 参考リソース

- [Creating a GUI with Swing](https://docs.oracle.com/javase/tutorial/uiswing/)
- [2D Graphics](https://docs.oracle.com/javase/tutorial/2d/)
- [Concurrency in Swing](https://docs.oracle.com/javase/tutorial/uiswing/concurrency/)