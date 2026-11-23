---
source: manuscripts/chapter19-event-advanced.md
summarized: 2026-11-23
sections:
  - key: "本章の学習目標"
    hash: 3c27ae30
  - key: "詳細なイベント処理の実装"
    hash: 2bc37498
  - key: "カスタムイベントとObserverパターン"
    hash: ede9558d
  - key: "ドラッグ&ドロップ"
    hash: c5ae2398
  - key: "パフォーマンスの最適化"
    hash: 9a116576
  - key: "よくあるエラーと対処法"
    hash: 3cc58820
  - key: "まとめ"
    hash: b7da6e7b
  - key: "章末演習"
    hash: 5f164c29
---

# 第19章 高度なイベント処理パターン 要約

行番号・サンプルコード番号の正確な対応は `docs/book-index/chapter19-event-advanced.md` を参照する。

## 章の概要

- 第18章の基本的なリスナーを前提に、マウス・キーボード・テキスト変更の詳細な処理、カスタムイベントとObserverパターン、SwingWorkerを扱う。`PropertyChangeSupport`・ドラッグ&ドロップ・デバウンスとスロットルは考え方を数文で述べ、コード例は付録F（`appendix/f-swing-advanced/`）に置く（2026-11-16）
- SwingWorkerを正式に説明する章（2026-11-11に第17章の説明をここへ集め、型パラメーターと各メソッドの役割を文章とリストで説明した）
- 19-1・19-2の冒頭で第18章の18-6・18-7から何を加えるかを示し、基本と応用の境界を明示する（2026-11-11）
- サンプルは19-1〜19-5の5つ
- 「よくあるエラーと対処法」は要点のリストだけで、エラーのコードは付録Eにある

## 構成上の問題

- まとめに次章への導入がない（章末演習の最後の1文で第20章へ誘導するだけ）
- 次の問題は解決済み
    + Observerパターン自体の説明がなかった（2026-11-12にカスタムイベントの節の冒頭にSubject/Observerの役割の説明を加え、前提知識から外した）
    + 学習目標の「メモリ効率を考慮した実装」を本文で扱っていなかった、ドラッグ&ドロップを学習目標でマウスと1項目にまとめていた（2026-11-12に学習目標を直した）
    + 19-4以降に実行結果の説明がなかった（2026-11-12に全文に合わせた説明を付けた）

## 節ごとの要約

- 本章の学習目標（L3–27）
    + 複雑なイベント処理（マウス、キーボード、DocumentListener、ドラッグ&ドロップの考え方）、カスタムイベントとパターン（Observer→カスタムイベント→`PropertyChangeSupport`）、パフォーマンスの最適化（デバウンスとスロットル、SwingWorker）の3本柱。付録Fにコード例を置く項目はそう断る
    + 前提は第18章の知識（リスナーの登録、EDT、`javax.swing.Timer`）。Observerパターンはその節で説明すると断る
- 詳細なイベント処理の実装（L28–262）
    + マウスイベントの完全な処理（L30）: 18-6に`addMouseMotionListener()`を加え、クリック回数・ボタン種別・押下/解放・ドラッグを処理し、ドラッグ中に線を描く（19-1）。`MouseAdapter`が`MouseListener`と`MouseMotionListener`の両方を実装しているので1つのオブジェクトを両方に登録できること、右クリックメニューは`setComponentPopupMenu()`で付けること（開く操作のOS差を吸収する）を説明する。実行結果の説明は、カーソルが十字に変わること、出入りも記録すること（出入り以外は座標も）を含む
    + キーボードイベントの高度な処理（L100）: 18-7に加えて、HashSetで押下中のキーを追跡して同時押し（Ctrl+Shift+D）を検出し、フォーカスを失ったら集合を空にする（ダイアログが開くとキーを離したイベントが届かないため）。InputMap/ActionMapによるキーバインディング（Ctrl+D、F5）を示し（19-2）、コードのあとでInputMap・ActionMap・KeyStroke・AbstractAction・`WHEN_FOCUSED`の役割と、ショートカットにキーバインディングが向く理由を説明する
    + DocumentListenerによるテキスト変更の監視（L188）: リスナーは`getDocument()`の`Document`に登録すること、insertUpdate・removeUpdate・changedUpdate（書式の変化。JTextFieldやJPasswordFieldでは呼ばれない）の違いを説明する。メール形式・パスワード長と強度・確認パスワード一致を入力のたびに検証し、送信ボタンの有効/無効を切り替える（19-3）
- カスタムイベントとObserverパターン（L263–370）
    + Subject（観察される側）とObserver（観察する側）の役割を文章で説明し、`addActionListener()`などのリスナーもObserverパターンだと述べる。`java.util.Observer`/`Observable`はJava 9で非推奨になったことを脚注で断る
    + カスタムイベントの作成（L265）: EventObjectを継承したTemperatureChangeEvent、EventListenerを継承したリスナーインターフェイス、リスナー一覧を持つセンサーモデル。表示更新と最高/最低記録の2つのリスナーに通知する（19-4、図chapter19-observer）。40°C超・-10°C未満で警告ダイアログ
    + 標準のしくみを使う：PropertyChangeSupport（L364）: リスナーの一覧の管理と通知を任せられること、`firePropertyChange`と`PropertyChangeListener`・`PropertyChangeEvent`の関係を数文で述べ、株価の例は付録Fへ誘導する
- ドラッグ&ドロップ（L371–377）
    + 部品どうしは`TransferHandler`（`setDragEnabled(true)`、`canImport`・`importData`）、デスクトップからのファイルは`DropTarget`で受け付けると数文で述べ、コード例は付録Fへ誘導する
- パフォーマンスの最適化（L378–493）
    + イベントの処理回数を減らす: デバウンスとスロットルの定義をリストで示し、デバウンスは`setRepeats(false)`の`javax.swing.Timer`を`restart()`すれば実現できると述べる。コード例は付録F
    + SwingWorkerによる非同期処理: 18-12のThread＋invokeLaterとの対比から入り、`SwingWorker<T, V>`の型パラメーター、doInBackground・process・done、execute、cancel/isCancelled、setProgressと`"progress"`プロパティを説明する。`cancel(true)`はスレッドへの割り込みで`sleep`中なら`InterruptedException`で終わること、キャンセル後の`get()`は`CancellationException`になるので`isCancelled()`を先に確かめることも書く。図（chapter19-swingworker）と、publish/processによる進捗表示とキャンセルの例（19-5、`done()`で`get()`の結果からアイテム数を出す）。実行結果では、processが最新の1件だけを書くので番号が飛ぶことと、キャンセルのメッセージはボタンのリスナーが出すことを断る
- よくあるエラーと対処法（L494–509）
    + 本文に挙げる問題: SwingWorkerから直接UIを更新する、DocumentListenerのなかでのテキストの変更（`IllegalStateException`、`DocumentFilter`か`invokeLater()`で対処）、リスナーの削除忘れによるメモリリーク、ドロップが受け入れられない（`DropTarget`で受け付ける場合の`acceptDrop()`の呼び忘れ、`DataFlavor`）。詳細は`appendix/e-common-errors/chapters/chapter19.md`
- まとめ（L510–525）
    + 重要なポイント6項目と「実践での活用」の段落。`PropertyChangeSupport`・ドラッグ&ドロップ・デバウンスのコード例は付録F「Swingの発展的な機能」にあると案内する
- 章末演習（L526–547）
    + GitHubの`exercises/chapter19/`への案内と課題構成（基礎: お絵かきパネル・キーバインディング・投稿画面・カスタムイベント、発展: デバウンスの検索・SwingWorkerで素数を数える、チャレンジ: タスクボード）。`examples/`の大きめの例にも触れる。第20章へ誘導する（2026-11-13に演習を用意した）

## 他章との関係

- 第15章（マルチスレッド）: SwingWorkerの背景にあるスレッドの前提
- 第17章: SwingWorkerの説明は2026-11-11に本章へ集め、重複を解消した
- 第18章（基本的なイベント処理）
    + 「18章が各リスナーの基本、19章が組み合わせと応用」で分かれ、19-1・19-2・SwingWorkerの冒頭で18-6・18-7・18-12を参照する。デバウンスでは第18章の`javax.swing.Timer`を使う
    + 18章の「次のステップ」は、マウス・キーボードの詳細、DocumentListener、カスタムイベント、SwingWorkerを扱い、ドラッグ&ドロップとイベントの間引きは要点を紹介すると予告し、本章の扱いと合っている（2026-11-18に予告を直した）
- 第20章（JTable・JList・JTree）: 章末演習の最後の文で誘導する
- 第7章: Observerパターンの概念は本章の節の冒頭で説明しており、ほかの章では扱っていない
- 付録E: エラー集の詳細を置く
- 付録F（Swingの発展的な機能）: PropertyChangeSupport（株価モニター）・ドラッグ&ドロップ・デバウンスとスロットルのコード例を置く

## 校正で見つけた個別の問題

- 2026-11-12に解消したもの
    + DocumentListener内で同じDocumentを変更したときの説明（無限ループではなく`IllegalStateException`）とDocumentFilterの対処法
    + 前置き文の重複と誇張、デバウンスとスロットルの定義
- 2026-11-14の校閲で次のものを直した
    + 19-2の同時押しの検出で、Ctrl+Sのダイアログのあとに`VK_CONTROL`が集合に残り、Shift+Dで誤って行を複製していた（focusLostで集合を空にした）
    + キーバインディングとDocumentListenerの基本の説明が足りなかった
    + SwingWorkerの例（現19-5）のキャンセル時の戻り値が使われない死んだコードで、`get()`の結果も使っていなかった。`cancel(true)`の割り込みの説明がなかった
    + 抜粋で、コンストラクターの宣言行や閉じ括弧が抜けて括弧の対応が崩れていた
    + 学習目標の並びが本文と逆、まとめの「完全な処理方法」の誇張、19-1の`MouseAdapter`の二重登録と`setComponentPopupMenu()`の説明不足、株価の例で`Random`をタイマーのたびに作っていた
- 19-1〜19-3の実行結果の不一致、19-3の絵文字、19-4のコメントの「インターフェース」、エラー集の誤りは、2026-11-09〜11-11の修正で解消した
- 2026-11-16: 付録の再編に合わせ、付録の文字とエラー集のURLを`appendix/e-common-errors/`に直した
- 2026-11-16: 著者の指示（特定のSwingの部品やAPIの解説は簡素に）で、JavaBeansイベントモデル（旧19-5）・ドラッグ&ドロップ（旧19-6）・イベント処理の最適化（旧19-7）のコード例を付録F（`appendix/f-swing-advanced/`）へ移した。SwingWorkerは19-5になった。演習のREADMEに付録Fへの案内を加えた（旧19-6の`new Date(file.lastModified())`の扱いは付録F側の話になった）
- 2026-11-18の校閲で直したもの
    + 19-1・19-4の抜粋の折り返しを直し（`String.format`を`formatted`に）、キャプションの行範囲が変わった
    + 19-1の実行結果の説明に、十字のカーソルとパネルへの出入りの記録を加えた
    + よくあるエラーの「ドロップが受け入れられない」を、`DropTarget`で受け付ける場合に限った
- 2026-11-23（流れ・矛盾・過不足の校閲）: 19-2の前に、比べるためにKeyListenerとキーバインディングの両方で書いていることを、後ろに修飾キーはisControlDownなどで調べられ、集合での追跡が要るのは修飾キー以外の同時押しだと書いた。よくあるエラーのドラッグ&ドロップの項目を、本文で説明したTransferHandlerのcanImportの誤りに差し替えた（DropTargetの注意は付録Fへ）
