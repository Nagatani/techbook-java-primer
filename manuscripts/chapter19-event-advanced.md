# <b>19章</b> <span>高度なイベント処理パターン</span> <small>プロフェッショナルなGUIアプリケーションの実装</small>

## 本章の学習目標

### この章で学ぶこと

本章では、プロフェッショナルレベルのGUIアプリケーションを実装するための高度なイベント処理技術を学習します。
具体的には以下の内容を扱います。

1. 複雑なイベント処理の実装
    - マウスイベントの完全な処理
    - キーボードイベントの高度な処理とショートカット
    - DocumentListenerによるテキスト変更の監視
    - ドラッグ&ドロップ
2. カスタムイベントとパターン
    - Observerパターンによるイベント通知
    - カスタムイベントの作成と活用
    - JavaBeansイベントモデルの実装
3. パフォーマンスの最適化
    - イベント処理の最適化技術
    - 非同期処理とSwingWorker

### この章を始める前に

第18章で学んだ基本的なイベント処理（リスナーの登録、EDT、`javax.swing.Timer`）を前提にします。
カスタムイベントの節で使うObserverパターンの考え方は、その節で説明します。

## 詳細なイベント処理の実装

### マウスイベントの完全な処理

第18章のサンプルコード18-6では、`MouseAdapter`でマウスのクリックとカーソルの出入りを検出しました。
実際のアプリケーションでは、ドラッグや右クリックメニューなど、より複雑なマウス操作が必要になります。
本節では、マウスの移動とドラッグを受け取る`addMouseMotionListener()`の登録も加えます。

以下のコードは、クリック、ダブルクリック、ドラッグ、右クリックメニューを1つのパネルで処理する例です。
`MouseAdapter`は`MouseListener`と`MouseMotionListener`の両方を実装しています。
そのため、必要なメソッドだけをオーバーライドした1つのオブジェクトを、`addMouseListener()`と`addMouseMotionListener()`の両方に登録できます。
各メソッドでは、イベントの種類と座標をログに記録します。
右クリックメニューは、リスナーではなく`setComponentPopupMenu()`でパネルに付けます。
メニューを開く操作がボタンを押したときか離したときかはOSによって違いますが、この方法ならどちらの場合もメニューを表示できます。

<span class="listing-number">**サンプルコード19-1**　MouseEventCompleteExample.java（5、11、36〜63、77〜82、97〜100、117、129行目）</span>

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

実行すると、「マウスイベント完全例」というウィンドウが開きます。白い描画パネルでクリック、ボタンの押下と解放、ドラッグをすると、下のログエリアにイベントの種類と座標が記録されます。ドラッグ中は押した位置から現在の位置まで青い線を描き、右クリックすると「切り取り」「コピー」「貼り付け」のメニューを表示します<span class="footnote">右クリックメニューを作る部分と描画の部分は、本文では省いています。全文は`https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code/chapter19/19-01/`にあります。</span>。

### キーボードイベントの高度な処理

第18章のサンプルコード18-7では、`KeyAdapter`で押されたキーを表示し、Ctrl+Sを検出しました。
本節では、それに加えて複数キーの同時押しと、`InputMap`と`ActionMap`によるキーバインディングを扱います。

以下のコードは、押されているキーを`HashSet`で追跡して同時押しを検出し、`InputMap`と`ActionMap`でショートカットキーを設定する例です。
同時押しの検出では、`keyPressed()`でキーコードを集合に加え、`keyReleased()`で取り除きます。
ダイアログが開くなどしてフォーカスが移ると、キーを離したイベントがテキストエリアに届かず、集合にキーが残ってしまいます。
そのため、フォーカスが外れたとき（`focusLost()`）に集合を空にしています。

<span class="listing-number">**サンプルコード19-2**　KeyboardEventAdvancedExample.java（7、12、26〜50、66〜75、85、87〜99、109、164行目）</span>

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
        
        // フォーカスが外れたら、押されているキーの記録を消す
        // （ダイアログが開くと、キーを離したイベントはテキストエリアに届かない）
        textArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                pressedKeys.clear();
            }
        });
        // ...
    }
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

実行すると、「高度なキーボードイベント処理」というウィンドウが開きます。キーを押したり離したりするたびに、下のステータスラベルに修飾キーとキーの名前を表示します。Ctrl+Sで保存のダイアログ、F1でヘルプを表示し、Ctrl+Aで全選択、Ctrl+Dで現在の行の削除、Ctrl+Shift+Dで現在の行の複製、F5で内容のクリアを行います。

キーバインディングでは、2つの対応表を使います。
`InputMap`は、キーの組み合わせ（`KeyStroke`）と、処理に付けた名前を対応づけます。
`ActionMap`は、その名前と、実行する処理（`Action`。ここでは`AbstractAction`を継承した匿名クラス）を対応づけます。
`getInputMap()`の引数の`JComponent.WHEN_FOCUSED`は、その部品がフォーカスを持っているときだけキーを受け付けるという指定です<span class="footnote">ほかに、部品を含むウィンドウがフォーカスを持っていれば受け付ける`WHEN_IN_FOCUSED_WINDOW`などがあります。</span>。
テキストのコピーや貼り付けなど、Swingの部品が既定で持つキー操作も、この仕組みで登録されています。
ショートカットキーは、`KeyListener`で`if`文を並べるより、キーバインディングで登録するほうが、キーと処理の対応を管理しやすくなります。

### DocumentListenerによるテキスト変更の監視

`DocumentListener`を使うと、テキストフィールドやテキストエリアの内容が変わるたびに処理を実行できます。
リスナーはテキスト部品そのものではなく、`getDocument()`で取り出した`Document`（部品が表示する文字列を保持するオブジェクト）に登録します。
`insertUpdate()`は文字が挿入されたとき、`removeUpdate()`は文字が削除されたときに呼ばれます。
`changedUpdate()`は文字の書式（属性）が変わったときに呼ばれるメソッドで、書式を持たない`JTextField`や`JPasswordField`では呼ばれません。
この例では、どの変更でも同じ検証を行うので、3つのメソッドから同じ`validateForm()`を呼んでいます。
以下のコードは、入力のたびにメールアドレスの形式、パスワードの長さと強度、確認用パスワードの一致を検証する例です。

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
            status.append("× Emailを入力してください\n");
            emailField.setBackground(Color.PINK);
            isValid = false;
        } else if (!isValidEmail(email)) {
            status.append("× 有効なEmailアドレスを入力してください\n");
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

Observerパターンは、あるオブジェクトの状態が変わったことを、それに関心のある複数のオブジェクトへ知らせるためのデザインパターン（よく使われる設計の型）です。
状態を持ち、変化を知らせる側をSubject（観察される側）、知らせを受け取る側をObserver（観察する側）と呼びます。
Subjectは、Observerを登録する一覧と、変化があったときに一覧の全員を呼び出す処理を持ちます。
SubjectはObserverをインターフェイスとしてだけ知っているので、受け取る側の種類が増えてもSubjectのコードは変わりません。
これまで使ってきた`addActionListener()`などのリスナーの仕組みも、ボタンをSubject、リスナーをObserverとするObserverパターンです。

Swingの部品が用意するイベントだけでなく、独自のイベントを作ってこの仕組みを自分のクラスに組み込むこともできます。
以下のコードは、`EventObject`を継承したカスタムイベントの例です<span class="footnote">標準ライブラリには`java.util.Observer`と`java.util.Observable`もありますが、Java 9で非推奨になりました。独自のイベントは、このように`java.util`の`EventObject`と`EventListener`を使って作ります。</span>。
温度センサーの値が変化したときにイベントを発生させ、登録された複数のリスナーに通知します。
センサーがSubject、リスナーがObserverに当たり、センサー（モデル）は表示部分（ビュー）のクラスを直接参照しません。

<div class="figure">

![カスタムイベントによるObserverパターン（サンプルコード19-4）](images/diagrams/chapter19-observer.svg)

</div>

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

// カスタムリスナーインターフェイス
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

実行すると、「カスタムイベントの例」というウィンドウが開きます。
スライダーで温度を変えて離すと、上の温度表示と背景色が変わり、下のログに「温度上昇: 20.0°C → 25.0°C」のような変化と、最高・最低温度の更新を記録します。
40°Cを超えるか-10°Cを下回ると、警告のダイアログを表示します。
2つのリスナーは互いを知らず、センサーからの通知をそれぞれ受け取っています。

### JavaBeansイベントモデルの実装

サンプルコード19-4では、イベントクラス、リスナーインターフェイス、リスナーの一覧をすべて自分で書きました。
JavaBeans<span class="footnote">JavaBeansは、Javaで再利用できる部品を作るための仕様で、JDK 1.1から使えます。名前は、Javaという言葉がコーヒーを連想させることにちなんだ「コーヒー豆」のしゃれです。</span>の`PropertyChangeSupport`を使うと、リスナーの一覧の管理と通知をこのクラスに任せられます。
以下のコードは、株価の変化を複数の表示パネルに通知する株価モニターの例です。
`PropertyChangeSupport`では、プロパティの名前を指定して、そのプロパティの変化だけを受け取るリスナーを登録できます。

<span class="listing-number">**サンプルコード19-5**　JavaBeansEventExample.java（7〜11、18〜20、34〜42、48、64〜79、95〜100、104、152〜162、175行目）</span>

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
    public StockPricePanel(StockPrice stock) {
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
    private final Random random = new Random();
    // ...
    private void startPriceUpdates() {
        updateTimer = new Timer(1000, e -> {
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

実行すると、4つの銘柄の株価を並べたウィンドウが開きます。
「更新開始」ボタンを押すと、1秒ごとに株価がランダムに変わり、各パネルが価格と変化量を表示します。
値上がりした銘柄は緑、値下がりした銘柄は赤で表示し、「更新停止」ボタンで更新を止めます。

## ドラッグ&ドロップの実装

### 基本的なドラッグ&ドロップ

以下のコードは、2つのリストの間でアイテムをドラッグして移動する処理と、外部からファイルをドロップする処理を実装した例です。
リスト間の移動は`TransferHandler`で、ファイルのドロップは`DropTarget`で受け付け、ドロップされたファイルの情報を表示します。

このサンプルで使う`JList`と`DefaultListModel`は、第20章「JTableとJListの基礎」で詳しく学びます。
ここでは、`DefaultListModel`に要素を追加・削除すると`JList`の表示が更新される、という点だけを押さえてください。

<span class="listing-number">**サンプルコード19-6**　DragAndDropExample.java（9、16、34〜39、67、69〜103、125〜146、151〜152、162、169行目）</span>

```java
public class DragAndDropExample extends JFrame {
    // ...
    public DragAndDropExample() {
        // ...
        // ドラッグ機能の設定
        sourceList.setDragEnabled(true);
        sourceList.setTransferHandler(new ListTransferHandler());
        
        // ドロップ機能の設定
        targetList.setTransferHandler(new ListTransferHandler());
        // ...
    }
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

実行すると、「ソース」と「ターゲット」の2つのリストと、ファイルをドロップする欄が並んだウィンドウが開きます。
「ソース」のアイテムを「ターゲット」へドラッグすると、アイテムが移動します。
デスクトップなどからファイルを下の欄へドロップすると、ファイルのパス、サイズ、更新日時を表示します。

## パフォーマンスの最適化

### イベント処理の最適化技術

マウスの移動のように短い間隔で大量に発生するイベントでは、イベントごとに重い処理をすると画面の反応が遅くなります。
処理の回数を減らす方法として、デバウンスとスロットルがあります<span class="footnote">デバウンス（debounce）は電子回路の用語に由来します。スイッチを押した瞬間に接点が細かく跳ねて（bounce）信号が何度も切り替わる現象を、1回の入力にまとめる処理をこう呼びます。</span>。

- デバウンス
    + イベントが一定時間発生しなくなってから、1回だけ処理する
- スロットル
    + 前回の処理から一定時間がたつまでのイベントは処理せず、一定の間隔でだけ処理する

以下のコードは、`mouseMoved`に対して両方を実装した例です。
デバウンスは、入力が止まってから検索する機能や自動保存にも使えます。

<span class="listing-number">**サンプルコード19-7**　EventOptimizationExample.java（5、10〜13、15、43〜44、47〜59、61〜65、80、82〜96、110行目）</span>

```java
public class EventOptimizationExample extends JFrame {
    // ...
    private Timer debounceTimer;
    private long lastThrottleTime = 0;
    private static final int DEBOUNCE_DELAY = 300; // ミリ秒
    private static final int THROTTLE_DELAY = 100; // ミリ秒
    // ...
    public EventOptimizationExample() {
        // ...
        // マウス移動イベント（位置の表示は毎回、描画とログはデバウンスとスロットルで間引く）
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
    // ...
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

実行すると、マウスの位置とイベントの数を表示するラベルと、白い描画パネルを並べたウィンドウが開きます。
パネルでマウスを動かすと、イベントの数は動かしているあいだ増え続けます。
一方で、スロットルによる描画の更新は100ミリ秒に1回までになり、右のログに記録します。
マウスを止めて300ミリ秒たつと、デバウンスによる「マウス移動が停止しました」をログに1回だけ記録します。

### SwingWorkerによる非同期処理

第18章のサンプルコード18-12では、長時間の処理を`Thread`で別のスレッドに任せ、結果の表示を`invokeLater()`でEDTに頼みました。
進捗を途中で表示したり、処理を途中で止めたりしたいときは、この組み立てを自分で書くと複雑になります。
`javax.swing.SwingWorker`<span class="footnote">`SwingWorker`がJavaの標準ライブラリに入ったのはJava 6からです。</span>は、バックグラウンドのスレッドでの処理と、EDTでの画面の更新を分けて書けるようにしたクラスです。

`SwingWorker<T, V>`を継承し、次のメソッドをオーバーライドします。
型パラメーターの`T`は処理の最終結果の型、`V`は途中経過として渡すデータの型です。

- `doInBackground()`
    + バックグラウンドのスレッドで実行する重い処理を書く。戻り値（`T`型）が最終結果になる。ここではコンポーネントを操作しない
- `process(List<V> chunks)`
    + `doInBackground()`のなかで`publish()`に渡した途中経過を、EDT上で受け取る。続けて呼ばれた`publish()`の値はまとめて渡されることがあるので、リストで受け取る
- `done()`
    + 処理が終わったあとにEDT上で呼ばれる。`get()`で最終結果を受け取り、画面に反映する

処理は`execute()`で開始します。
`cancel(true)`で中断を頼むと、`doInBackground()`のなかの`isCancelled()`がtrueを返すようになるので、それを見て処理を打ち切ります。
引数の`true`は、処理中のスレッドに割り込みをかけるという指定です。
割り込まれると、`Thread.sleep()`などで待っている処理は`InterruptedException`で終わります。
キャンセルしたあとは`get()`で結果を受け取れない（`CancellationException`が発生する）ので、`done()`では`isCancelled()`を確かめてから`get()`を呼びます。

以下のコードは、SwingWorkerで100件のデータを順に処理し、`publish()`と`process()`で進捗を表示する例です<span class="footnote">進捗の割合（0〜100）だけを知らせたいときは、`publish()`の代わりに`setProgress()`を使う方法もあります。`addPropertyChangeListener()`で登録したリスナーが、`"progress"`プロパティの変化として受け取ります。</span>。
処理中も画面は操作でき、「キャンセル」ボタンで処理を中断できます。

<div class="figure">

![SwingWorkerによる処理の流れ（サンプルコード19-8）](images/diagrams/chapter19-swingworker.svg)

</div>

<span class="listing-number">**サンプルコード19-8**　SwingWorkerExample.java（7、70〜75、79〜83、92〜98、100〜128、146行目）</span>

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
                    // キャンセルしたあとはget()で結果を受け取れないので、戻り値は使われない
                    return null;
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
                    resultArea.append("\n処理が完了しました。\n");
                    resultArea.append("結果のサマリー:\n");
                    resultArea.append("処理したアイテム数: " + result.lines().count() + "\n");
                    progressBar.setValue(100);
                    progressBar.setString("完了");
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

実行して「処理開始」ボタンを押すと、プログレスバーが0%から100%へ進み、下のエリアに「処理中: アイテム 1/100」のような進捗を表示します。
`process()`は、まとめて渡された進捗のうち最新の1件だけを書くので、番号が飛ぶことがあります。
処理は約5秒かかりますが、そのあいだもウィンドウの移動や「キャンセル」ボタンの操作ができます。
最後まで進むと「処理が完了しました。」と結果のサマリー（処理したアイテム数）を表示します。
途中でキャンセルすると、「キャンセル」ボタンのリスナーが「処理がキャンセルされました。」と表示します。

## よくあるエラーと対処法

高度なイベント処理では、スレッドの使い分けや、リスナーの登録と解除の管理を誤ることで問題が起こります。本章の内容に関連して出会いやすい問題は次のとおりです。

- SwingWorkerから直接UIを更新する
    + `doInBackground()`はEDT以外で動くため、UIの更新は`process()`や`done()`で行う
- DocumentListenerのなかでのテキストの変更
    + 通知を受けているあいだに同じドキュメントを変更すると、`IllegalStateException`（Attempt to mutate in notification）が発生する。入力を書き換えるときは`DocumentFilter`を使うか、`invokeLater()`で変更を後回しにする
- リスナーの削除忘れによるメモリリーク
    + 不要になったリスナーを削除しないと、オブジェクトがガベージコレクションで回収されない
- ドロップが受け入れられない
    + `drop()`のなかで`acceptDrop()`を呼び出していない、ドロップされたデータの形式（`DataFlavor`）に対応していないといった原因がある

問題のあるコードと修正したコード、EDT違反の検出などのデバッグのヒントは、GitHubリポジトリにまとめています。<br>
`https://github.com/Nagatani/techbook-java-primer/blob/main/appendix/h-common-errors/chapters/chapter19.md`

## まとめ

本章では、プロフェッショナルレベルのGUIアプリケーションを実装するための高度なイベント処理技術について学習しました。

### 重要なポイント
- マウスとキーボードの詳細なイベント処理
- DocumentListenerによるリアルタイムテキスト監視
- カスタムイベントとJavaBeansイベントモデル
- ドラッグ&ドロップの実装
- パフォーマンス最適化（デバウンス、スロットル）
- SwingWorkerによる非同期処理

### 実践での活用
これらの技術を組み合わせることで、応答性が高く、ユーザーフレンドリーなGUIアプリケーションを開発できます。とくに、長時間処理とUI更新の分離、イベント処理の最適化は、実務での開発において重要な技術です。

## 章末演習

### 演習課題へのアクセス

本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter19/`

### 課題構成

演習は、本章で学んだイベント処理の応用を使う次の課題で構成されています。

- 基礎課題
    + お絵かきパネル（マウスのドラッグと右クリックのメニュー）、矢印キーで四角を動かす（キーバインディング）、文字数を数えながら書く投稿画面（DocumentListener）、在庫の変化を知らせる（カスタムイベント）
- 発展課題
    + 入力が止まってから検索する（デバウンス）、素数を数える（SwingWorkerによる進捗の表示とキャンセル）
- チャレンジ課題
    + タスクボード（ドラッグ&ドロップ・キーバインディング・PropertyChangeSupport）

各課題には、詳細な要件と実装のヒントが含まれています。
第18章と第19章の内容をまとめて使う大きめのプログラムも、同じ場所の`examples/`に置いています。

本章の内容を理解できたら、第20章「JTableとJListの基礎」に進みましょう。
