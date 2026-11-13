# 第19章 基礎課題

## 学習目標

- MouseListenerとMouseMotionListenerで、押す・ドラッグ・移動を組み合わせて処理する
- InputMapとActionMapで、キーと処理を対応づける（キーバインディング）
- DocumentListenerで、文字の入力や削除のたびに表示を更新する
- EventObjectとEventListenerで自分のイベントを作り、1つの変化を複数のリスナーに知らせる

## 課題一覧

このフォルダーのJavaファイルは解答例です。課題ごとにフォルダーを分けてあります。

```
basic/
├── task1-paint/      課題1
├── task2-keys/       課題2
├── task3-post/       課題3
└── task4-inventory/  課題4
```

GUIのプログラムなので、実行例ではウィンドウの見た目と、操作したときの反応を説明します。
画面に依存しないクラスには、動作確認のプログラム（`〜Check.java`）を付けています。

### 課題1: お絵かきパネル

**ファイル**: `task1-paint/Sketch.java`、`task1-paint/SketchCheck.java`、`task1-paint/PaintApp.java`

マウスのドラッグで線を描くパネルを作ります。

**要求仕様**:

- `Sketch`は画面に依存しないクラスで、描いた線を記録する
    - 1本の線は、色と点の並びを持つレコード`Path`で表す
    - `startPath(色, x, y)`で新しい線を始め、`addPoint(x, y)`でいま描いている線に点を足す。線を始める前の`addPoint`は何もしない
    - 最後の線を消す`undo`、すべて消す`clear`、線の数を返す`getPathCount`を持つ
- `PaintApp`（画面）
    - 1つの`MouseAdapter`を、`addMouseListener`と`addMouseMotionListener`の両方で登録する
    - 左ボタンを押したら線を始め（`mousePressed`）、ドラッグしたら点を足して`repaint()`する（`mouseDragged`）。右ボタンはメニューに使うので、`SwingUtilities.isLeftMouseButton(e)`で左ボタンだけを処理する
    - マウスを動かしたら（`mouseMoved`）座標を、パネルの外へ出たら（`mouseExited`）「(-, -)」を、下の表示に出す
    - 右クリックのメニュー（`JPopupMenu`を`setComponentPopupMenu`で付ける）に、色（黒・赤・青から1つ）・元に戻す・すべて消すを置く
    - 下の表示は「座標 (120, 80)   線の数 2   色 黒   右クリックでメニュー」の形にする

**実行例**:

- 起動すると白いパネルが開き、下に「座標 (-, -)   線の数 0   色 黒」と表示される
- パネルの上でマウスを動かすと、座標の表示が変わる
- 左ボタンでドラッグすると、黒い線が引ける。2本引くと「線の数 2」になる
- 右クリックするとメニューが開く。「赤」を選んでから描くと赤い線になり、表示が「色 赤」に変わる
- メニューの「元に戻す」で最後の線が消え、「すべて消す」で白紙に戻る

### 課題2: 矢印キーで四角を動かす

**ファイル**: `task2-keys/Board.java`、`task2-keys/BoardCheck.java`、`task2-keys/KeyMoveApp.java`

盤面の中の四角を、キーボードで動かします。

**要求仕様**:

- `Board`は画面に依存しないクラスで、盤面の幅・高さ、四角の大きさと位置を持つ
    - 最初と`reset`のあとは、四角を盤面の中央に置く
    - `move(dx, dy)`で動かす。盤面の外へははみ出さない
- `KeyMoveApp`（画面）
    - 400×300の盤面に、40×40の四角を描く
    - キーとアクションの名前を`InputMap`に、名前と処理（`AbstractAction`）を`ActionMap`に登録する
        - 矢印キー: 10ずつ動かす
        - Shift＋矢印キー: 50ずつ動かす（`KeyStroke.getKeyStroke("shift LEFT")`）
        - Ctrl＋R: 中央に戻す
    - 1つのキーを登録する処理はメソッドにまとめ、8つの矢印キーの登録を短く書く
    - 下に「位置 (180, 130)   最後の操作: 右へ」の形で表示する

**実行例**:

- 起動すると、灰色の盤面の中央に青い四角があり、「位置 (180, 130)   最後の操作: なし」と表示される
- 右矢印キーを2回押すと「位置 (200, 130)   最後の操作: 右へ」になる
- Shift＋上矢印キーで「位置 (200, 80)   最後の操作: 上へ大きく」、続けてShift＋左矢印キーで「位置 (150, 80)」になる
- 左矢印キーを押し続けても、四角は左端（x = 0）で止まる
- Ctrl＋Rで中央に戻る

### 課題3: 文字数を数えながら書く投稿画面

**ファイル**: `task3-post/PostRules.java`、`task3-post/PostRulesCheck.java`、`task3-post/PostApp.java`

140文字までの投稿を書く画面を作ります。

**要求仕様**:

- `PostRules`は画面に依存しないクラスで、次の静的メソッドを持つ
    - `remaining(文字列)`: あと何文字書けるか。超えたら負の数
    - `canPost(文字列)`: 空白だけでなく、140文字以内なら`true`
    - `countHashtags(文字列)`: 空白で区切った語のうち、「#」で始まり後ろに1文字以上続く語の数
- `PostApp`（画面）
    - 入力欄（`JTextArea`）の`Document`に`DocumentListener`を登録し、`insertUpdate`・`removeUpdate`・`changedUpdate`のどれが来ても表示を合わせ直す
    - 入力欄の下に「残り 122文字   ハッシュタグ 2個」と表示する。残りが負の数になったら赤い文字にする
    - 「投稿」ボタンは`canPost`が`true`のときだけ押せる
    - 「投稿」を押すと、下の「投稿した内容」に番号付きで追加し、入力欄を空にする。入力欄を空にする処理はボタンのActionListenerで行う（DocumentListenerの中で同じ`Document`を書き換えると例外が起きる）

**実行例**:

- 起動すると「残り 140文字   ハッシュタグ 0個」と表示され、「投稿」は押せない
- 「Hello #java #swing」と打つと、1文字打つたびに数が変わり、「残り 122文字   ハッシュタグ 2個」になる。「投稿」が押せるようになる
- 「投稿」を押すと、下に「1: Hello #java #swing」と追加され、入力欄が空に戻る
- 141文字以上入れると、残りが「-1文字」のように赤く表示され、「投稿」が押せなくなる

### 課題4: 在庫の変化を知らせる

**ファイル**: `task4-inventory/StockEvent.java`、`task4-inventory/StockListener.java`、`task4-inventory/Inventory.java`、`task4-inventory/InventoryCheck.java`、`task4-inventory/InventoryApp.java`

在庫の数が変わったことを、自分で作ったイベントで複数の表示に知らせます。

**要求仕様**:

- `StockEvent`は`EventObject`を継承し、商品名と変化の前後の数を持つ。増えた数（減ったときは負の数）を返す`getChange`も持つ
- `StockListener`は`EventListener`を継承したインターフェイスで、`stockChanged(StockEvent e)`を持つ
- `Inventory`は画面に依存しないクラスで、商品ごとの在庫数とリスナーの一覧を持つ
    - `addStockListener`・`removeStockListener`でリスナーを登録・解除する
    - `receive`（入荷）は数を増やし、`ship`（出荷）は在庫が足りるときだけ数を減らして`true`を返す。どちらも数が変わったときだけ、登録されたすべてのリスナーに同じ`StockEvent`を渡す
    - 画面の部品のことは知らない
- `InventoryApp`（画面）
    - 商品を選ぶ`JComboBox`、数の`JSpinner`、「入荷」「出荷」ボタンを上に並べる
    - ボタンの処理は`Inventory`を呼ぶだけにし、表示の更新は次の3つのリスナーに任せる
        - 在庫の一覧を表示し直す
        - 3個未満になったら「〜の在庫が残りN個です」と警告を出す
        - 「ノート 出荷 1個（10 → 9）」の形で履歴に足す
    - 在庫が足りずに出荷できなかったときは、警告の欄に「〜の在庫が足りません」と出す

**実行例**:

- 起動すると、在庫の欄に「10個 ノート」「5個 ボールペン」「4個 消しゴム」と表示される
- ノートを1個出荷すると、在庫の欄が「9個 ノート」になり、履歴に「ノート 出荷 1個（10 → 9）」と追加される
- 消しゴムを2個出荷すると、在庫が2個になり、下に赤い文字で「消しゴムの在庫が残り2個です」と表示される
- 消しゴムを5個出荷しようとすると、「消しゴムの在庫が足りません」と表示され、在庫も履歴も変わらない

`InventoryCheck`は、画面の代わりに受け取ったイベントを記録するだけのリスナーを登録して、通知の届き方を確かめます。

## 本文で扱っていないAPI

- `Graphics2D`・`BasicStroke`・`RenderingHints`（課題1）: 太い線を滑らかに描くために使う。第17章の発展課題のREADMEで説明している
- `JRadioButtonMenuItem`（課題1）: メニューの中のラジオボタン。`ButtonGroup`にまとめると、1つだけ選べる
- `SwingUtilities.isLeftMouseButton`（課題1）: 左ボタンを押しているかを調べる。本文の`e.getButton()`は、`mouseDragged`では押しているボタンを返さない（`MouseEvent.NOBUTTON`になる）ので、ドラッグ中の判定にはこちらを使う
- `JComponent.WHEN_IN_FOCUSED_WINDOW`（課題2）: 本文の`WHEN_FOCUSED`はその部品にフォーカスがあるときだけ反応する。こちらは同じウィンドウのどこにフォーカスがあっても反応する

## コンパイルと実行

課題のフォルダーごとにコンパイルして実行します。

```bash
cd exercises/chapter19/basic/task1-paint
javac *.java
java PaintApp
java SketchCheck
```

課題2は`KeyMoveApp`と`BoardCheck`、課題3は`PostApp`と`PostRulesCheck`、課題4は`InventoryApp`と`InventoryCheck`を実行します。

## 実装のヒント

- ドラッグ中の`mouseDragged`は、どのボタンでドラッグしても届く。右ボタンのドラッグで線が伸びないよう、ボタンを調べる
- キーバインディングでは、キーを押したときの処理を`ActionMap`に名前で登録するので、同じ処理を別のキーやメニューから呼べる
- DocumentListenerの`changedUpdate`は文字の属性の変化で届く。プレーンなテキストの部品では届かないが、3つとも同じ処理にしておくと迷わない
- リスナーの一覧を持つクラスは、通知の前に状態を変え終えておく。リスナーの中で`getQuantity`を呼んだときに、新しい数が返るようにするため

## 完了チェックリスト

- [ ] 課題1: マウスの押す・ドラッグ・移動を組み合わせて処理できる
- [ ] 課題1: 右クリックのメニューを付けられる
- [ ] 課題2: InputMapとActionMapでキーと処理を対応づけられる
- [ ] 課題3: DocumentListenerで入力のたびに表示を更新できる
- [ ] 課題4: EventObjectを継承したイベントと、そのリスナーを作れる
- [ ] 課題4: 1つの変化を複数のリスナーに知らせられる

**次のステップ**: 基礎課題が終わったら、[advanced/](../advanced/)の発展課題に進みましょう。
