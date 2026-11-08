# <b>19章</b> <span>高度なイベント処理パターン</span> <small>プロフェッショナルなGUIアプリケーションの実装</small>

## 本章の学習目標

### この章で学ぶこと

本章では、プロフェッショナルレベルのGUIアプリケーションを実装するための高度なイベント処理技術を学習します。
具体的には以下の内容を扱います。

1. 複雑なイベント処理の実装
    - マウスイベントの完全な処理とドラッグ&ドロップ
    - キーボードイベントの高度な処理とショートカット
    - DocumentListenerによるテキスト変更の監視
2. カスタムイベントとパターン
    - JavaBeansイベントモデルの実装
    - Observerパターンによるイベント通知
    - カスタムイベントの作成と活用
3. パフォーマンスの最適化
    - イベント処理の最適化技術
    - 非同期処理とSwingWorker
    - メモリ効率を考慮した実装

### この章を始める前に

前章の基本的なイベント処理の知識に加え、デザインパターン（とくにObserverパターン）の概念的理解があると、より深くカスタムイベントの設計を理解できます。

## 詳細なイベント処理の実装

### マウスイベントの完全な処理

前章では基本的なマウスクリックを扱いましたが、実際のアプリケーションでは、より複雑なマウス操作が必要になります。

以下のコードは、マウスイベントの完全な処理を実装した例です。クリック、ダブルクリック、ドラッグ、右クリックメニューなど、実際のアプリケーションで必要となるさまざまなマウス操作を処理しています。MouseAdapterを使用して必要なメソッドだけをオーバーライドし、イベントの詳細情報をログに記録します。

<span class="listing-number">**サンプルコード19-1**　MouseEventCompleteExample.java（5、11、36〜63、77〜82、97〜100、122、134行目）</span>

```java
public class MouseEventCompleteExample extends JFrame {
    // ...
    public MouseEventCompleteExample() {
        // ...
        // マウスリスナーの追加
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String button = getButtonName(e.getButton());
                int clickCount = e.getClickCount();
                log("クリック: " + button + " (回数: " + clickCount + ") at " + 
                    e.getPoint());
                
                // ダブルクリックの検出
                if (clickCount == 2) {
                    log("ダブルクリック検出！");
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                log("マウス押下: " + getButtonName(e.getButton()) + " at " + startPoint);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                log("マウス解放: " + getButtonName(e.getButton()) + " at " + e.getPoint());
                startPoint = null;
                currentPoint = null;
                drawPanel.repaint();
            }
            // ...
            @Override
            public void mouseDragged(MouseEvent e) {
                currentPoint = e.getPoint();
                drawPanel.repaint();
                log("ドラッグ中: " + currentPoint);
            }
            // ...
        };
        
        drawPanel.addMouseListener(mouseHandler);
        drawPanel.addMouseMotionListener(mouseHandler);
        // ...
    }
    // ...
}
```

実行すると、「マウスイベント完全例」というウィンドウが開きます。白い描画パネルでクリック、ボタンの押下と解放、ドラッグをすると、下のログエリアにイベントの種類と座標が記録されます。ドラッグ中は押した位置から現在の位置まで青い線を描き、右クリックすると「切り取り」「コピー」「貼り付け」のメニューを表示します。

### キーボードイベントの高度な処理

複数キーの同時押しやショートカットキーの実装例です。

以下のコードは、高度なキーボードイベント処理の実装例です。単一キーの押下だけでなく、複数キーの同時押し検出、ショートカットキーの実装、InputMapとActionMapを使用したより洗練されたキーバインディングの設定方法を示しています。HashSetを使用して現在押されているキーを追跡し、複雑なキーコンビネーションを検出できます。

<span class="listing-number">**サンプルコード19-2**　KeyboardEventAdvancedExample.java（7、12、26〜50、66、78〜90、100、155行目）</span>

```java
public class KeyboardEventAdvancedExample extends JFrame {
    // ...
    public KeyboardEventAdvancedExample() {
        // ...
        // キーリスナーの追加
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                updateStatus(e, "押下");
                
                // ショートカットキーの検出
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    saveDocument();
                    e.consume(); // デフォルトの動作をキャンセル
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A) {
                    textArea.selectAll();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_F1) {
                    showHelp();
                }
                
                // 複数キーの同時押し検出
                if (pressedKeys.contains(KeyEvent.VK_CONTROL) && 
                    pressedKeys.contains(KeyEvent.VK_SHIFT) && 
                    pressedKeys.contains(KeyEvent.VK_D)) {
                    duplicateLine();
                }
            }
            // ...
        });
        // ...
    private void setupKeyBindings() {
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();
        
        // Ctrl+Dで行削除
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), 
            "delete-line");
        actionMap.put("delete-line", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteLine();
            }
        });
        // ...
    }
    // ...
}
```

実行すると、「高度なキーボードイベント処理」というウィンドウが開きます。キーを押したり離したりするたびに、下のステータスラベルに修飾キーとキーの名前を表示します。Ctrl+Sで保存のダイアログ、F1でヘルプを表示し、Ctrl+Aで全選択、Ctrl+Dで現在の行の削除、F5で内容のクリアを行います。

### DocumentListenerによるテキスト変更の監視

テキストフィールドやテキストエリアの内容変更をリアルタイムで監視する例です。

以下のコードは、DocumentListenerを使用してテキスト入力のリアルタイムバリデーションを実装する例です。メールアドレスの形式チェック、パスワードの強度判定、確認パスワードの一致チェックなど、実際のフォーム入力で必要となる検証を、ユーザーが入力するたびに即座に実行します。これにより、優れたユーザーエクスペリエンスを提供できます。

<span class="listing-number">**サンプルコード19-3**　DocumentListenerExample.java（7、66〜110、147〜149、178行目）</span>

```java
public class DocumentListenerExample extends JFrame {
    // ...
    
    private void setupDocumentListeners() {
        // Emailフィールドのリスナー
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateForm(); }
        });
        
        // パスワードフィールドのリスナー
        DocumentListener passwordListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void removeUpdate(DocumentEvent e) { validateForm(); }
            @Override
            public void changedUpdate(DocumentEvent e) { validateForm(); }
        };
        
        passwordField.getDocument().addDocumentListener(passwordListener);
        confirmField.getDocument().addDocumentListener(passwordListener);
    }
    
    private void validateForm() {
        StringBuilder status = new StringBuilder();
        boolean isValid = true;
        
        // Emailの検証
        String email = emailField.getText();
        if (email.isEmpty()) {
            status.append("❌ Emailを入力してください\n");
            emailField.setBackground(Color.PINK);
            isValid = false;
        } else if (!isValidEmail(email)) {
            status.append("❌ 有効なEmailアドレスを入力してください\n");
            emailField.setBackground(Color.PINK);
            isValid = false;
        } else {
            status.append("✓ Email: OK\n");
            emailField.setBackground(Color.WHITE);
        }
        
        // ...
        statusArea.setText(status.toString());
        submitButton.setEnabled(isValid);
    }
    // ...
}
```

実行すると、「DocumentListenerによるバリデーション」というウィンドウが開きます。Email、パスワード、確認の各欄に入力するたびに、下のステータスエリアに検証結果を表示し、条件を満たさない欄の背景をピンクにします。パスワードは8文字以上で強度（弱い・普通・強い）も表示し、すべての条件を満たすと「送信」ボタンが押せるようになります。

## カスタムイベントとObserverパターン

### カスタムイベントの作成

独自のイベントを作成して、コンポーネント間の疎結合な通信を実現します。

以下のコードは、JavaのEventObjectを継承したカスタムイベントの実装例です。温度センサーの値が変化したときにイベントを発生させ、複数のリスナーに通知する仕組みを構築しています。このパターンを使用することで、モデル（センサー）とビュー（表示部分）を疎結合に保ち、メンテナンス性の高いアプリケーションを開発できます。

<span class="listing-number">**サンプルコード19-4**　CustomEventExample.java（6〜33、43〜55、58、64、94〜102、128〜129、156、163行目）</span>

```java
// カスタムイベントクラス
class TemperatureChangeEvent extends EventObject {
    private final double oldTemperature;
    private final double newTemperature;
    
    public TemperatureChangeEvent(Object source, double oldTemp, double newTemp) {
        super(source);
        this.oldTemperature = oldTemp;
        this.newTemperature = newTemp;
    }
    
    public double getOldTemperature() { return oldTemperature; }
    public double getNewTemperature() { return newTemperature; }
}

// カスタムリスナーインターフェース
interface TemperatureChangeListener extends EventListener {
    void temperatureChanged(TemperatureChangeEvent e);
}

// 温度センサーモデル
class TemperatureSensor {
    private double temperature = 20.0;
    private List<TemperatureChangeListener> listeners = new ArrayList<>();
    
    public void addTemperatureChangeListener(TemperatureChangeListener listener) {
        listeners.add(listener);
    }
    // ...
    public void setTemperature(double newTemperature) {
        double oldTemperature = this.temperature;
        this.temperature = newTemperature;
        fireTemperatureChanged(oldTemperature, newTemperature);
    }
    
    protected void fireTemperatureChanged(double oldTemp, double newTemp) {
        TemperatureChangeEvent event = new TemperatureChangeEvent(this, oldTemp, newTemp);
        for (TemperatureChangeListener listener : new ArrayList<>(listeners)) {
            listener.temperatureChanged(event);
        }
    }
}
// ...
public class CustomEventExample extends JFrame {
    // ...
    public CustomEventExample() {
        // ...
        // 温度変化リスナーを登録
        sensor.addTemperatureChangeListener(new TemperatureChangeListener() {
            @Override
            public void temperatureChanged(TemperatureChangeEvent e) {
                double newTemp = e.getNewTemperature();
                double oldTemp = e.getOldTemperature();
                
                // 表示を更新
                temperatureLabel.setText(String.format("%.1f°C", newTemp));
                // ...
            }
        });
        // ...
    }
    // ...
}
```

### JavaBeansイベントモデルの実装

より高度なイベント管理のためのJavaBeansパターンの実装例です。

以下のコードは、JavaBeansのPropertyChangeSupportを使用した高度なイベント管理の実装例です。株価データの変更を自動的に複数のビューコンポーネントに通知し、リアルタイムで更新される株価モニターアプリケーションを実現しています。PropertyChangeSupportにより、プロパティごとに異なるリスナーを登録でき、より柔軟なイベント処理が可能になります。

<span class="listing-number">**サンプルコード19-5**　JavaBeansEventExample.java（7〜11、18〜20、34〜42、64〜79、95〜100、151〜162、175行目）</span>

```java
// モデルクラス（PropertyChangeSupport を使用）
class StockPrice {
    private String symbol;
    private double price;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    // ...
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    // ...
    public void setPrice(double newPrice) {
        double oldPrice = this.price;
        this.price = newPrice;
        pcs.firePropertyChange("price", oldPrice, newPrice);
    }
}

// 株価表示コンポーネント
class StockPricePanel extends JPanel implements PropertyChangeListener {
        // ...
        previousPrice = stock.getPrice();
        stock.addPropertyChangeListener("price", this);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("price".equals(evt.getPropertyName())) {
            double newPrice = (Double) evt.getNewValue();
            double oldPrice = (Double) evt.getOldValue();
            
            priceLabel.setText(String.format("$%.2f", newPrice));
            
            double change = newPrice - oldPrice;
            double changePercent = (change / oldPrice) * 100;
            
            changeLabel.setText(String.format("%+.2f (%+.2f%%)", change, changePercent));
            // ...
        }
    }
}

// メインアプリケーション
public class JavaBeansEventExample extends JFrame {
    // ...
    private void startPriceUpdates() {
        updateTimer = new Timer(1000, e -> {
            Random random = new Random();
            for (StockPrice stock : stocks.values()) {
                // ランダムな価格変動（-2%～+2%）
                double change = (random.nextDouble() - 0.5) * 0.04;
                double newPrice = stock.getPrice() * (1 + change);
                stock.setPrice(newPrice);
            }
        });
        updateTimer.start();
    }
    // ...
}
```

## ドラッグ&ドロップの実装

### 基本的なドラッグ&ドロップ

コンポーネント間でのドラッグ&ドロップ機能の実装例です。

以下のコードは、Swingのドラッグ&ドロップAPIを使用した実装例です。リスト間でのアイテムの移動と、外部からのファイルドロップの両方に対応しています。TransferHandlerとDropTargetを組み合わせることで、直感的なユーザーインターフェイスを実現できます。ファイルドロップ時にはファイル情報を表示し、実践的なアプリケーションでの活用例を示しています。

このサンプルで使う`JList`と`DefaultListModel`は、第20章「JTableとJListの基礎」で詳しく学びます。
ここでは、`DefaultListModel`に要素を追加・削除すると`JList`の表示が更新される、という点だけを押さえてください。

<span class="listing-number">**サンプルコード19-6**　DragAndDropExample.java（9、34〜39、69〜103、125〜146、151〜152、162、169行目）</span>

```java
public class DragAndDropExample extends JFrame {
        // ...
        // ドラッグ機能の設定
        sourceList.setDragEnabled(true);
        sourceList.setTransferHandler(new ListTransferHandler());
        
        // ドロップ機能の設定
        targetList.setTransferHandler(new ListTransferHandler());
        // ...
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
        // ...
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
                // ...
        }
        
        // ...
    }
    // ...
}
```

## パフォーマンスの最適化

### イベント処理の最適化技術

高頻度で発生するイベントの最適化例です。

以下のコードは、マウス移動のような高頻度イベントを効率的に処理するための最適化技術を示しています。デバウンス（一定時間イベントが発生しなくなってから処理）とスロットル（一定間隔でのみ処理）の両方を実装し、パフォーマンスを改善しています。これらの技術は、リアルタイム検索や自動保存などの機能でも活用できます。

<span class="listing-number">**サンプルコード19-7**　EventOptimizationExample.java（6、11、13〜15、45〜46、49〜61、63〜67、85〜101、121行目）</span>

```java
public class EventOptimizationExample extends JFrame {
    // ...
    private Timer debounceTimer;
    // ...
    private long lastThrottleTime = 0;
    private static final int DEBOUNCE_DELAY = 300; // ミリ秒
    private static final int THROTTLE_DELAY = 100; // ミリ秒
        // ...
        // 通常のマウス移動イベント（最適化なし）
        drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            // ...
            @Override
            public void mouseMoved(MouseEvent e) {
                eventCount++;
                statusLabel.setText(String.format("マウス位置: (%d, %d) - イベント数: %d", 
                    e.getX(), e.getY(), eventCount));
                
                // デバウンス処理
                debounceMouseMove(e.getPoint());
                
                // スロットル処理
                throttleMouseMove(e.getPoint());
            }
        });
        // ...
        // デバウンスタイマーの初期化
        debounceTimer = new Timer(DEBOUNCE_DELAY, e -> {
            log("デバウンス: マウス移動が停止しました - " + currentMousePos);
        });
        debounceTimer.setRepeats(false);
        // ...
    }
    
    // デバウンス: 最後のイベントから一定時間後に処理
    private void debounceMouseMove(Point point) {
        currentMousePos = point;
        debounceTimer.restart();
    }
    
    // スロットル: 一定間隔で処理
    private void throttleMouseMove(Point point) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastThrottleTime >= THROTTLE_DELAY) {
            lastThrottleTime = currentTime;
            drawPanel.repaint();
            log("スロットル: 描画更新 - " + point);
        }
    }
    // ...
}
```

### SwingWorkerによる非同期処理

長時間かかる処理をバックグラウンドで実行する例です。

以下のコードは、SwingWorkerを使用してUIをフリーズさせることなく長時間の処理を実行する実装例です。進捗状況のリアルタイム更新、処理のキャンセル機能、EDT上での安全なUI更新など、実践的なアプリケーションで必要となる機能を網羅しています。大量データの処理、ネットワーク通信、ファイル操作などで活用できます。

<span class="listing-number">**サンプルコード19-8**　SwingWorkerExample.java（7、70〜75、79〜82、91〜97、99〜115、121〜127、145行目）</span>

```java
public class SwingWorkerExample extends JFrame {
    // ...
    // SwingWorkerの実装
    class DataProcessingWorker extends SwingWorker<String, ProgressInfo> {
        private static final int TOTAL_ITEMS = 100;
        
        @Override
        protected String doInBackground() throws Exception {
            // ...
            for (int i = 0; i < TOTAL_ITEMS; i++) {
                if (isCancelled()) {
                    return "処理がキャンセルされました";
                }
                // ...
                // 進捗を公開
                int progress = (i + 1) * 100 / TOTAL_ITEMS;
                publish(new ProgressInfo(progress, "処理中: アイテム " + (i + 1) + "/" + TOTAL_ITEMS));
            }
            
            return result.toString();
        }
        // ...
        @Override
        protected void process(List<ProgressInfo> chunks) {
            // 最新の進捗情報を使用
            ProgressInfo latest = chunks.get(chunks.size() - 1);
            progressBar.setValue(latest.progress);
            progressBar.setString(latest.message);
            resultArea.append(latest.message + "\n");
        }
        
        @Override
        protected void done() {
            startButton.setEnabled(true);
            cancelButton.setEnabled(false);
            
            try {
                if (!isCancelled()) {
                    String result = get();
                    // ...
                }
            } catch (InterruptedException | ExecutionException e) {
                resultArea.append("エラーが発生しました: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }
    // ...
}
```

## よくあるエラーと対処法

高度なイベント処理を実装する際に遭遇しやすいエラーとその対処法をまとめます。

### SwingWorkerでの典型的なエラー

#### 問題：SwingWorkerから直接UIを更新してしまう

エラー症状

<span class="listing-number">**サンプルコード19-9**</span>

```java
SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
    @Override
    protected Void doInBackground() throws Exception {
        for (int i = 0; i <= 100; i++) {
            progressBar.setValue(i);  // エラー：EDT外からUIを更新
            Thread.sleep(50);
        }
        return null;
    }
};
```

原因
- doInBackground()メソッドはバックグラウンドスレッドで実行される
- Swingコンポーネントの更新はEDT上でのみ安全

対処法

<span class="listing-number">**サンプルコード19-10**</span>

```java
SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
    @Override
    protected Void doInBackground() throws Exception {
        for (int i = 0; i <= 100; i++) {
            publish(i);  // 進捗を公開
            Thread.sleep(50);
        }
        return null;
    }
    
    @Override
    protected void process(List<Integer> chunks) {
        // EDT上で実行される
        int latest = chunks.get(chunks.size() - 1);
        progressBar.setValue(latest);
    }
};
```

### DocumentListenerでの無限ループ

#### 問題：DocumentListener内でテキストを変更して無限ループ

エラー症状

<span class="listing-number">**サンプルコード19-11**</span>

```java
textField.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
        textField.setText(textField.getText().toUpperCase());  // 無限ループ
    }
    // 他のメソッドも同様
});
```

原因
- DocumentListener内でドキュメントを変更すると、再度イベントが発生
- 再帰的にリスナーが呼び出される

対処法

<span class="listing-number">**サンプルコード19-12**</span>

```java
private boolean updating = false;

textField.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
        if (!updating) {
            updating = true;
            SwingUtilities.invokeLater(() -> {
                try {
                    String text = e.getDocument().getText(0, e.getDocument().getLength());
                    textField.setText(text.toUpperCase());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                } finally {
                    updating = false;
                }
            });
        }
    }
    // 他のメソッドも同様
});
```

### カスタムイベントのメモリリーク

#### 問題：リスナーが削除されずメモリリークが発生

エラー症状

<span class="listing-number">**サンプルコード19-13**</span>

```java
public class EventSource {
    private List<CustomListener> listeners = new ArrayList<>();
    
    public void addListener(CustomListener listener) {
        listeners.add(listener);  // 削除されない限り保持される
    }
    
    // removeListenerメソッドがない、または呼ばれない
}
```

原因
- リスナーが強参照で保持される
- イベントソースが生存する限り、リスナーもGCされない

対処法

<span class="listing-number">**サンプルコード19-14**</span>

```java
public class EventSource {
    private List<WeakReference<CustomListener>> listeners = new ArrayList<>();
    
    public void addListener(CustomListener listener) {
        listeners.add(new WeakReference<>(listener));
    }
    
    public void removeListener(CustomListener listener) {
        listeners.removeIf(ref -> ref.get() == null || ref.get() == listener);
    }
    
    private void fireEvent(CustomEvent event) {
        // 無効な参照を削除しながら通知
        Iterator<WeakReference<CustomListener>> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            CustomListener listener = iterator.next().get();
            if (listener != null) {
                listener.customEventOccurred(event);
            } else {
                iterator.remove();  // GCされたリスナーを削除
            }
        }
    }
}
```

### ドラッグ&ドロップの実装エラー

#### 問題：ドロップが受け入れられない

エラー症状

<span class="listing-number">**サンプルコード19-15**</span>

```java
new DropTarget(component, new DropTargetListener() {
    @Override
    public void drop(DropTargetDropEvent dtde) {
        // ドロップを受け入れていない
        Transferable transferable = dtde.getTransferable();
        // 処理...
    }
});
```

原因
- acceptDrop()メソッドを呼び出していない
- ドロップされたデータの形式（DataFlavor）に対応していない

対処法

<span class="listing-number">**サンプルコード19-16**</span>

```java
new DropTarget(component, new DropTargetListener() {
    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            // ドロップを受け入れる
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            
            Transferable transferable = dtde.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                // 処理...
                dtde.dropComplete(true);
            } else {
                dtde.rejectDrop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            dtde.dropComplete(false);
        }
    }
    
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // 適切なDataFlavorをチェック
        if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY);
        } else {
            dtde.rejectDrag();
        }
    }
    // 他のメソッドも実装
});
```

### デバッグのヒント

#### 1. EDT違反の検出

<span class="listing-number">**サンプルコード19-17**</span>

```java
// アプリケーション起動時に設定
RepaintManager.setCurrentManager(new RepaintManager() {
    @Override
    public void addInvalidComponent(JComponent component) {
        if (!SwingUtilities.isEventDispatchThread()) {
            Exception e = new Exception("EDT違反検出");
            e.printStackTrace();
        }
        super.addInvalidComponent(component);
    }
});
```

#### 2. イベント処理のプロファイリング

<span class="listing-number">**サンプルコード19-18**</span>

```java
button.addActionListener(e -> {
    long start = System.currentTimeMillis();
    try {
        // 処理
        performAction();
    } finally {
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed > 100) {  // 100ms以上かかった場合
            System.err.println("警告: イベント処理に" + elapsed + "msかかりました");
        }
    }
});
```

#### 3. WeakReferenceの使用状況確認

<span class="listing-number">**サンプルコード19-19**</span>

```java
public class ListenerManager {
    private List<WeakReference<EventListener>> listeners = new ArrayList<>();
    
    public void debugListeners() {
        int total = listeners.size();
        int alive = 0;
        for (WeakReference<EventListener> ref : listeners) {
            if (ref.get() != null) alive++;
        }
        System.out.println("リスナー状況: " + alive + "/" + total + " 生存");
    }
}
```

これらの対処法を参考に、不具合の起きにくいイベント処理を実装してください。

## まとめ

本章では、プロフェッショナルレベルのGUIアプリケーションを実装するための高度なイベント処理技術について学習しました。

### 重要なポイント
- マウスとキーボードイベントの完全な処理方法
- DocumentListenerによるリアルタイムテキスト監視
- カスタムイベントとJavaBeansイベントモデル
- ドラッグ&ドロップの実装
- パフォーマンス最適化（デバウンス、スロットル）
- SwingWorkerによる非同期処理

### 実践での活用
これらの技術を組み合わせることで、応答性が高く、ユーザーフレンドリーなGUIアプリケーションを開発できます。とくに、長時間処理とUI更新の分離、イベント処理の最適化は、実務での開発において重要な技術です。

## 章末演習

### 演習課題へのアクセス

本章の理解を深めるための演習課題を用意しています。以下のGitHubリポジトリで、実践的な問題に挑戦してください。

**演習課題URL**: https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter19/

### 課題構成

演習は以下の実践的な課題で構成されています。

- インタラクティブ描画アプリケーション（高度なマウスイベント処理）
- フォームバリデータ（DocumentListenerを活用したリアルタイム検証）
- 高度なメニューシステム（Actionパターンと動的メニュー）
- マウストラッカー（詳細なマウス動作の分析）

各演習には、本章で学んだ技術を実践的に活用するための詳細な要件が含まれています。
