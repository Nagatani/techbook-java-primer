# 第19章 高度なイベント処理パターン：よくあるエラーと対処法

書籍『Java入門』第19章「高度なイベント処理パターン」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

## SwingWorkerから直接UIを更新してしまう

### エラー例

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

### 原因

- doInBackground()メソッドはバックグラウンドスレッドで実行される
- Swingコンポーネントの更新はEDT上でのみ安全

### 対処法

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

## DocumentListenerのなかで同じドキュメントを変更して例外が発生する

### エラー例

```java
textField.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
        textField.setText(textField.getText().toUpperCase());  // 例外が発生する
    }
    // 他のメソッドも同様
});
```

### エラーメッセージ

文字を入力すると、次の例外が発生し、テキストは書き換わりません。

```
Exception in thread "AWT-EventQueue-0" java.lang.IllegalStateException: Attempt to mutate in notification
	at java.desktop/javax.swing.text.AbstractDocument.writeLock(AbstractDocument.java:...)
```

### 原因

- `AbstractDocument`は、リスナーへ変更を通知しているあいだ、同じドキュメントへの変更を受け付けない
- 変更を許すと通知が入れ子になり、リスナーが再帰的に呼ばれてしまうため、例外にして防いでいる

### 対処法

#### 対処法1：`DocumentFilter`で入力そのものを書き換える

入力された文字を大文字にする、数字以外を受け付けないといった処理は、ドキュメントに入る前に書き換える`DocumentFilter`で行います。

```java
((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
    @Override
    public void insertString(FilterBypass fb, int offset, String text,
            AttributeSet attr) throws BadLocationException {
        super.insertString(fb, offset, text.toUpperCase(), attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text,
            AttributeSet attrs) throws BadLocationException {
        super.replace(fb, offset, length,
            text == null ? null : text.toUpperCase(), attrs);
    }
});
```

#### 対処法2：`invokeLater()`で変更を通知のあとに回す

リスナーのなかでどうしても変更するときは、`invokeLater()`で通知が終わったあとに変更します。
変更によって再びリスナーが呼ばれるので、フラグで二重の書き換えを防ぎます。

```java
private boolean updating = false;

textField.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
        if (!updating) {
            updating = true;
            SwingUtilities.invokeLater(() -> {
                try {
                    textField.setText(textField.getText().toUpperCase());
                } finally {
                    updating = false;
                }
            });
        }
    }
    // 他のメソッドも同様
});
```

## リスナーが削除されずメモリリークが発生する

### エラー例

```java
public class EventSource {
    private List<CustomListener> listeners = new ArrayList<>();
    
    public void addListener(CustomListener listener) {
        listeners.add(listener);  // 削除されない限り保持される
    }
    
    // removeListenerメソッドがない、または呼ばれない
}
```

### 原因

- イベントソースはリスナーへの参照を一覧に持ち続ける
- イベントソースが生存する限り、リスナーと、リスナーが参照する画面などのオブジェクトもGCされない

### 対処法

イベントソースに削除のメソッドを用意し、リスナーを登録した側が、不要になった時点で削除します。

```java
public class EventSource {
    private final List<CustomListener> listeners = new ArrayList<>();

    public void addListener(CustomListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CustomListener listener) {
        listeners.remove(listener);
    }
}
```

```java
// リスナーを登録した画面を閉じるときに削除する
CustomListener listener = event -> label.setText(event.toString());
source.addListener(listener);

dialog.addWindowListener(new WindowAdapter() {
    @Override
    public void windowClosed(WindowEvent e) {
        source.removeListener(listener);
    }
});
```

リスナーを`WeakReference`で持つ方法もありますが、ラムダ式や匿名クラスのリスナーをほかのどこからも参照していないと、すぐにGCされて通知が届かなくなります。
まずは登録と削除を対にする書き方を守ってください。

## ドラッグ&ドロップでドロップが受け入れられない

### エラー例

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

### 原因

- acceptDrop()メソッドを呼び出していない
- ドロップされたデータの形式（DataFlavor）に対応していない

### 対処法

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

## デバッグのヒント

### 1. EDT違反の検出

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

### 2. イベント処理のプロファイリング

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

### 3. 登録されているリスナーの数の確認

画面を開いたり閉じたりするたびにリスナーの数が増え続けるなら、削除し忘れています。
Swingのコンポーネントは、登録されているリスナーを配列で返すメソッドを持っています。

```java
IO.println("ActionListenerの数: " + button.getActionListeners().length);
IO.println("DocumentListenerの数: "
    + ((AbstractDocument) textField.getDocument()).getDocumentListeners().length);
```

これらの対処法を参考に、不具合の起きにくいイベント処理を実装してください。
