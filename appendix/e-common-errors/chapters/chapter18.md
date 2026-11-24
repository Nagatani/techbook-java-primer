# 第18章 基本的なイベント処理：よくあるエラーと対処法

書籍『Java入門』第18章「基本的なイベント処理」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。
エラーメッセージは、Java 25（日本語の環境）で確かめたものです。

## ラムダ式の中でローカル変数を変更しようとする

### エラー例

```java
JButton button = new JButton("+1");
int count = 0;
button.addActionListener(e -> {
    count++;                              // ローカル変数を変更している
    button.setText("回数: " + count);
});
```

### エラーメッセージ

```
CounterFrame.java:10: エラー: ラムダ式から参照されるローカル変数は、finalまたは事実上のfinalである必要があります
                count++;
                ^
CounterFrame.java:11: エラー: ラムダ式から参照されるローカル変数は、finalまたは事実上のfinalである必要があります
                button.setText("回数: " + count);
                                        ^
エラー2個
```

### 原因

- ラムダ式や匿名クラスの中から使えるローカル変数は、一度も値を変えない変数（finalまたは事実上のfinal）だけ（第11章）
- リスナーは、ボタンが押されたときにあとから呼ばれる。メソッドが終わったあとのローカル変数を書き換えることはできない
- `button`は値を変えていないので使えるが、`count`は`count++`で変えているので使えない。変えている箇所だけでなく、読んでいる行もエラーになる

### 対処法

コンパイルできる全文：[chapter18-code/CounterFrame/](chapter18-code/CounterFrame/)

```java
public class CounterFrame {
    private final JFrame frame = new JFrame("カウンター");
    private int count = 0; // フィールドなら、ラムダ式の中で変更できる
    private final JButton button = new JButton("+1");

    public CounterFrame() {
        button.addActionListener(e -> {
            count++;
            button.setText("回数: " + count);
        });
        // ...
    }
}
```

状態を持つ画面は、状態をフィールドに持つクラスにします（第18章のカウンターの例と同じ形）。
`int[] count = {0};`のように要素1つの配列にする書き方も見かけますが、何を表すのかが読み取りにくくなります。

## Timerの参照があいまいになる

### エラー例

```java
import java.util.*;
import javax.swing.*;

Timer timer = new Timer(1000, e -> IO.println("tick"));
```

### エラーメッセージ

```
ClockFrame.java:6: エラー: Timerの参照はあいまいです
        Timer timer = new Timer(1000, e -> IO.println("tick"));
        ^
  javax.swingのクラス javax.swing.Timerとjava.utilのクラス java.util.Timerの両方が一致します
```

### 原因

- `Timer`という名前のクラスは、`javax.swing`と`java.util`の両方にある
- 両方のパッケージを`*`でimportすると、どちらを使うのかをコンパイラーが決められない
- 画面を定期的に更新するのは`javax.swing.Timer`（リスナーをEDTで呼ぶ）。`java.util.Timer`は別のスレッドで動くので、Swingの部品を操作するのには使わない

### 対処法

コンパイルできる全文：[chapter18-code/ClockFrame/](chapter18-code/ClockFrame/)

- `import javax.swing.Timer;`のようにクラスを1つずつimportする（本書のコードの書き方）
- または、`javax.swing.Timer timer = new javax.swing.Timer(...)`のようにパッケージ名まで書く

## リスナーを書いたのに何も起きない

### エラー例

```java
JButton button = new JButton("送信");
ActionListener listener = e -> label.setText("送信しました");
frame.add(button);
// button.addActionListener(listener); を書き忘れている
```

### 原因

- リスナーのオブジェクトを作っただけでは、イベントソースとつながらない
- エラーも例外も出ないため、気づきにくい
- 別の部品（たとえば`button2`）に登録している、`addActionListener`ではなく別のリスナーの登録メソッドを呼んでいる、という誤りもある

### 対処法

- イベントソース・イベントオブジェクト・リスナーの3つがそろっているかを確かめる（第18章の「イベント処理の3要素」）
- リスナーの最初の行に`IO.println("呼ばれた");`を書いて、呼ばれているかを確かめる。呼ばれていなければ登録の誤り、呼ばれているなら処理の中身の誤り

## ボタンを押すと画面が固まる

### エラー例

```java
button.addActionListener(e -> {
    label.setText("処理中...");
    heavyTask();              // 数秒かかる処理をEDTで実行している
    label.setText("完了");
});
```

### 原因

- リスナーはイベントディスパッチスレッド（EDT）で動く。EDTが長い処理をしている間は、画面の再描画もほかのボタンへの反応もできない
- 「処理中...」の表示もEDTが描くので、処理が終わるまで表示されず、いきなり「完了」になる
- 例外もエラーも出ず、ウィンドウが応答しなくなったように見える

### 対処法

- 長い処理は別のスレッドで行い、画面の更新だけをEDTに戻す（第18章の「長時間処理の回避」）
- 処理の途中経過を表示したり、終わったあとに結果を画面に出したりするには、第19章で学ぶ`SwingWorker`を使う
- 一定の間隔で画面を更新するだけなら、`Thread.sleep`で待たずに`javax.swing.Timer`を使う

## ディスプレイのない環境で画面を開こうとする

### エラーメッセージ

Linuxのサーバーやコンテナーのように、画面（ディスプレイ）のない環境でSwingのプログラムを実行すると、次の例外が発生します（Dockerのeclipse-temurin:25-jdkで確かめたもの）。

```
Exception in thread "main" java.awt.HeadlessException: 
No X11 DISPLAY variable was set,
or no headful library support was found,
but this program performed an operation which requires it.
```

### 原因

- `JFrame`などのウィンドウを作るには、画面を表示する仕組み（LinuxではX11など）が必要
- リモートのサーバーにSSHでつないで実行した場合や、CIで実行した場合に起こる

### 対処法

- 画面のある手元の環境で実行する
- CIで画面のテストを動かすときは、仮想のディスプレイ（Linuxの`xvfb-run`など）の上で実行する
- 画面を開かずに確かめたい処理（計算や入力の検査）は、画面のクラスから分けておくと、ディスプレイがなくてもテストできる（第21章）
