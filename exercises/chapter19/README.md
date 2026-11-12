# 第19章 高度なイベント処理パターン - 演習課題

本章の演習課題は準備中です。

それまでは、本文のサンプルコードを動かし、SwingWorkerやDocumentListenerの動きを確かめてください。
コンパイルできる全文は[sample-code/chapter19/](../../sample-code/chapter19/)にあります。

## 参考の例（[examples/](examples/)）

マウス・キーボード・入力の監視を組み合わせた、500〜800行ほどの大きめのプログラムです。
第18章と第19章の内容をまとめて使っているので、本章を読み終えてから読むと理解しやすくなります。
本文で扱っていないAPI（`UndoManager`、`Preferences`、`ImageIO`など）も使っています。

- `InteractiveDrawing.java`: マウスのドラッグで線や図形を描く。元に戻す・やり直す、画像の保存
- `MouseTracker.java`: マウスの座標・軌跡・速度・クリックを記録して表示する
- `FormValidator.java`: DocumentListenerで入力のたびに検査するフォーム。Enterで次の欄へ移るキーバインディング
- `KeyboardShortcuts.java`: InputMapとActionMapによるショートカットキー、UndoManagerによる元に戻す機能を持つテキストエディター
- `MenuSystem.java`: 階層のあるメニュー、右クリックのメニュー、最近使ったファイルの一覧
- `ResponsiveGUI.java`: スライダーと入力欄に合わせて表示がすぐ変わる画面、Timerによる進捗の表示
- `EventHandlingBasics.java`: マウス・キーボード・フォーカスのイベントを記録して一覧にする

どれもクラスを1つのファイルにまとめてあり、そのフォルダーで`javac`と`java`を実行すると起動できます。

```bash
cd exercises/chapter19/examples
javac InteractiveDrawing.java
java InteractiveDrawing
```
