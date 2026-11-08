# 第20章 JTableとJListの基礎：よくあるエラーと対処法

書籍『Java入門』第20章「JTableとJListの基礎」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

高度なGUIプログラミングの学習において遭遇しやすい典型的なエラーとその対処法を以下にまとめます。

## カスタムコンポーネントの描画問題

### 問題：paintComponent()で描画した内容が表示されない

エラー症状。

```java
public class CustomPanel extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(0, 0, 100, 100);
        // 赤い矩形が表示されない
    }
}
```

原因
- `super.paintComponent(g)`が呼び出されていない
- コンポーネントのサイズが0になっている
- 透明度の設定に問題がある

対処法。

```java
public class CustomPanel extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // 必須：親クラスの描画処理を実行
        
        g.setColor(Color.RED);
        g.fillRect(0, 0, 100, 100);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 200);  // コンポーネントの推奨サイズを指定
    }
}
```

### 問題：カスタム描画がちらつく

エラー症状。

```java
@Override
public void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    // 複雑な描画処理
    for (int i = 0; i < 1000; i++) {
        g.drawLine(i, 0, i, getHeight());
    }
    // 描画時にちらつきが発生
}
```

原因
- ダブルバッファリングが無効になっている
- 描画処理が重すぎる
- 不要な再描画が発生している

対処法。

コンパイルできる全文：[chapter20-code/SmoothPanel/](chapter20-code/SmoothPanel/)

```java
public class SmoothPanel extends JPanel {
    private BufferedImage buffer;
    private boolean needsRedraw = true;
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // バッファが未作成または再描画が必要な場合のみ描画
        if (buffer == null || needsRedraw) {
            createBuffer();
            needsRedraw = false;
        }
        
        // バッファの内容を描画
        g.drawImage(buffer, 0, 0, this);
    }
    
    private void createBuffer() {
        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = buffer.createGraphics();
        
        // アンチエイリアシングを有効化
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 重い描画処理
        for (int i = 0; i < 1000; i++) {
            g2d.drawLine(i, 0, i, getHeight());
        }
        
        g2d.dispose();
    }
    
    public void invalidateBuffer() {
        needsRedraw = true;
        repaint();
    }
}
```

## スレッド間でのGUI更新

### 問題：背景スレッドからGUIを更新すると例外が発生

エラーメッセージ。
```
Exception in thread "Thread-1" java.lang.IllegalStateException: 
This component does not support a null LayoutManager
```

原因
- EDT（Event Dispatch Thread）以外からGUIコンポーネントを更新している
- スレッドセーフでない操作を実行している

対処法。

```java
// 間違った方法
new Thread(() -> {
    // 背景スレッドからGUIを直接更新（危険）
    label.setText("更新");
}).start();

// 正しい方法
new Thread(() -> {
    // 重い処理を背景スレッドで実行
    String result = performHeavyComputation();
    
    // GUI更新はEDTで実行
    SwingUtilities.invokeLater(() -> {
        label.setText(result);
    });
}).start();

// SwingWorkerを使用する方法（推奨）
SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
    @Override
    protected String doInBackground() throws Exception {
        return performHeavyComputation();
    }
    
    @Override
    protected void done() {
        try {
            label.setText(get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
};
worker.execute();
```

## リソース管理の問題

### 問題：大量の画像を扱うとメモリ不足になる

エラーメッセージ。
```
java.lang.OutOfMemoryError: Java heap space
```

原因
- 画像リソースが適切に解放されていない
- 大きな画像を縮小せずに使用している
- 画像のキャッシュが適切に管理されていない

対処法。

コンパイルできる全文：[chapter20-code/ImageManager/](chapter20-code/ImageManager/)

```java
public class ImageManager {
    private final Map<String, BufferedImage> imageCache = new HashMap<>();
    private final int MAX_CACHE_SIZE = 100;
    
    public BufferedImage loadImage(String path, int width, int height) {
        String key = path + "_" + width + "_" + height;
        
        // キャッシュから取得
        BufferedImage cached = imageCache.get(key);
        if (cached != null) {
            return cached;
        }
        
        // 画像を読み込み、リサイズ
        try {
            BufferedImage original = ImageIO.read(new File(path));
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g2d = resized.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                               RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(original, 0, 0, width, height, null);
            g2d.dispose();
            
            // 元画像を解放
            original.flush();
            
            // キャッシュサイズを制限
            if (imageCache.size() >= MAX_CACHE_SIZE) {
                clearOldestCache();
            }
            
            imageCache.put(key, resized);
            return resized;
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    // ...
}
```

## パフォーマンスの最適化

### 問題：大量のデータを表示するJTableの動作が遅い

エラー症状。

```java
// 10万行のデータを表示すると動作が非常に遅くなる
DefaultTableModel model = new DefaultTableModel();
for (int i = 0; i < 100000; i++) {
    model.addRow(new Object[]{i, "データ" + i, Math.random()});
}
table.setModel(model);
```

原因
- すべてのデータを一度にメモリに読み込んでいる
- 大量データの仮想化が実装されていない
- 不要な再描画が発生している

対処法。

コンパイルできる全文：[chapter20-code/LazyTableDemo/](chapter20-code/LazyTableDemo/)

```java
// 1. 遅延読み込み（Lazy Loading）の実装
public class LazyTableModel extends AbstractTableModel {
    private final List<Object[]> dataCache = new ArrayList<>();
    private final DataSource dataSource;
    private final int pageSize = 1000;
    // ...
    @Override
    public Object getValueAt(int row, int column) {
        // 必要に応じてデータを読み込み
        ensureDataLoaded(row);
        return dataCache.get(row)[column];
    }
    
    private void ensureDataLoaded(int row) {
        if (row >= dataCache.size() || dataCache.get(row) == null) {
            // ページ単位でデータを読み込み
            int startIndex = (row / pageSize) * pageSize;
            List<Object[]> pageData = dataSource.getData(startIndex, pageSize);
            
            // キャッシュを拡張
            while (dataCache.size() < startIndex + pageData.size()) {
                dataCache.add(null);
            }
            
            // データを設定
            for (int i = 0; i < pageData.size(); i++) {
                dataCache.set(startIndex + i, pageData.get(i));
            }
        }
    }
}

            // 2. テーブルの最適化設定
            table.setAutoCreateRowSorter(true);
            table.setFillsViewportHeight(true);
            table.getTableHeader().setReorderingAllowed(false);

            // 3. 不要な更新を避ける
            table.setUpdateSelectionOnSort(false);
            table.setRowHeight(20);  // 固定の行高さを設定
```

## 複雑なレイアウトの管理

### 問題：動的にコンポーネントを追加・削除すると配置が崩れる

エラー症状。

```java
JPanel panel = new JPanel(new GridBagLayout());
GridBagConstraints gbc = new GridBagConstraints();

// 動的にコンポーネントを追加
for (int i = 0; i < 5; i++) {
    gbc.gridx = i;
    panel.add(new JButton("Button " + i), gbc);
}

// 中間のコンポーネントを削除すると配置が崩れる
panel.remove(2);
panel.revalidate();
```

原因
- GridBagLayoutの制約が適切に管理されていない
- レイアウトの再計算が正しく行われていない
- コンポーネントの削除後に制約が更新されていない

対処法。

コンパイルできる全文：[chapter20-code/DynamicPanel/](chapter20-code/DynamicPanel/)

```java
public class DynamicPanel extends JPanel {
    private final List<JComponent> components = new ArrayList<>();
    private final GridBagConstraints gbc = new GridBagConstraints();
    // ...
    
    public void addComponent(JComponent component) {
        components.add(component);
        refreshLayout();
    }
    // ...
    
    private void refreshLayout() {
        // 全てのコンポーネントを一度削除
        removeAll();
        
        // 制約を再計算して追加
        for (int i = 0; i < components.size(); i++) {
            gbc.gridx = i % 3;  // 3列のグリッド
            gbc.gridy = i / 3;
            add(components.get(i), (GridBagConstraints)gbc.clone());
        }
        
        // レイアウトを更新
        revalidate();
        repaint();
    }
    
    // ...
}
```

## カスタムレンダラーの問題

### 問題：JTableのカスタムレンダラーで選択状態が正しく表示されない

エラー症状。

```java
table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        JLabel label = new JLabel(value.toString());
        label.setBackground(Color.YELLOW);  // 常に黄色になってしまう
        return label;
    }
});
```

原因
- 選択状態やフォーカス状態を適切に処理していない
- コンポーネントの設定が不完全

対処法。

```java
table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        // 親クラスの処理を利用
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
            setForeground(table.getForeground());
        }
        
        if (hasFocus) {
            setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        } else {
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }
        
        // 値に応じたカスタム表示
        if (value instanceof Number) {
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }
        
        return this;
    }
});
```

## デバッグのヒント

### 1. 描画領域の可視化

```java
@Override
public void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    // デバッグ用：境界を表示
    if (DEBUG_MODE) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.RED);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2d.dispose();
    }
}
```

### 2. パフォーマンスの測定

```java
long startTime = System.nanoTime();
// 処理
long endTime = System.nanoTime();
System.out.println("処理時間: " + (endTime - startTime) / 1_000_000 + "ms");
```

### 3. メモリ使用量の監視

```java
Runtime runtime = Runtime.getRuntime();
long usedMemory = runtime.totalMemory() - runtime.freeMemory();
System.out.println("使用メモリ: " + usedMemory / 1024 / 1024 + "MB");
```

これらの対処法を参考に、高度なGUIアプリケーションの開発を進めてください。複雑な問題に直面した場合は、問題を小さく分割し、段階的に解決することが重要です。
