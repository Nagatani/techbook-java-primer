# 第19章 高度なイベント処理パターン：よくあるエラーと対処法

書籍『Java入門』第19章「高度なイベント処理パターン」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

高度なイベント処理を実装する際に遭遇しやすいエラーとその対処法をまとめます。

## SwingWorkerでの典型的なエラー

### 問題：SwingWorkerから直接UIを更新してしまう

エラー症状

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

## DocumentListenerでの無限ループ

### 問題：DocumentListener内でテキストを変更して無限ループ

エラー症状

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

## カスタムイベントのメモリリーク

### 問題：リスナーが削除されずメモリリークが発生

エラー症状

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

## ドラッグ&ドロップの実装エラー

### 問題：ドロップが受け入れられない

エラー症状

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

### 3. WeakReferenceの使用状況確認

```java
public class ListenerManager {
    private List<WeakReference<EventListener>> listeners = new ArrayList<>();
    
    public void debugListeners() {
        int total = listeners.size();
        int alive = 0;
        for (WeakReference<EventListener> ref : listeners) {
            if (ref.get() != null) alive++;
        }
        IO.println("リスナー状況: " + alive + "/" + total + " 生存");
    }
}
```

これらの対処法を参考に、不具合の起きにくいイベント処理を実装してください。
