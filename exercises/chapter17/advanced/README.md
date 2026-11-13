# 第17章 発展課題

## 学習目標

- 計算をするクラスを画面から分け、画面のクラスはその結果を並べる・描くことに専念させる
- GridLayoutで、行と列のそろったマス目を組み立てる
- `paintComponent`で、計算した座標をもとに図形を描く

## 課題一覧

### 課題1: 1か月分のカレンダー

**ファイル**（このフォルダーのファイルは解答例です）:

```
advanced/task1-calendar/
├── MonthCalendar.java       マス目に入る日付の計算（GUIなし）
├── MonthCalendarCheck.java  MonthCalendarの動作確認
└── CalendarApp.java         画面、mainメソッド
```

実行時の引数で指定した月のカレンダーを表示するプログラムを作成してください。

**要求仕様**:

- `MonthCalendar`（GUIに依存しない）
    - 年と月を受け取る。月が1〜12でなければ`IllegalArgumentException`を投げる
    - 「2026年11月」の形の見出しを返す`getTitle`
    - 1日が何列目に来るか（日曜を0とする）を返す`getFirstColumn`。`LocalDate.of(年, 月, 1).getDayOfWeek().getValue()`は月曜が1、日曜が7なので、7で割った余りにする
    - その月の日数を返す`getDaysInMonth`（`lengthOfMonth()`）
    - 7列×6行の42マスのそれぞれに入る日付を、`int`の配列で返す`getCells`。日付のないマスは0にする
- `CalendarApp`（画面）
    - 上に見出し、中央に`GridLayout(0, 7, 2, 2)`のJPanelを置く
    - 1行目に「日」〜「土」の曜日を並べ、2行目から42マスにJLabelを置く。日付のないマスにも空のラベルを置き、位置をそろえる
    - 日曜の列は赤、土曜の列は青の文字にする
    - 日付のあるマスは、白い背景（`setOpaque(true)`）と薄い灰色の枠（`BorderFactory.createLineBorder`）を付ける
    - 引数がなければ2026年11月を表示する

**実行例**:

`java CalendarApp`を実行すると、「カレンダー」というウィンドウが開きます。

- 上に「2026年11月」と表示され、その下に「日 月 火 水 木 金 土」が並ぶ
- 2026年11月1日は日曜なので、1日は左端の列から始まる。日曜の列（1, 8, 15, 22, 29）は赤、土曜の列（7, 14, 21, 28）は青
- 30日の後ろのマスは空のまま、6行目まで格子が続く

`java CalendarApp 2027 5`のように年と月を渡すと、その月を表示します。
2027年5月は1日が土曜なので、1日は右端の列にあり、31日は6行目に入ります。

`java MonthCalendarCheck`を実行すると、マス目の計算を確かめられます（最後に「11件中11件OK」と表示されます）。

### 課題2: アナログ時計の文字盤

**ファイル**（このフォルダーのファイルは解答例です）:

```
advanced/task2-clock/
├── ClockMath.java       針の角度と、円周上の点の計算（GUIなし）
├── ClockMathCheck.java  ClockMathの動作確認
├── ClockPanel.java      文字盤と針を描くパネル
└── ClockApp.java        mainメソッド
```

実行時の引数で指定した時刻を、アナログ時計で表示するプログラムを作成してください。

**要求仕様**:

- `ClockMath`（GUIに依存しない）。角度は12時の方向を0度とし、時計回りに増やす
    - 時針の角度`hourAngle(時, 分)`: 1時間で30度、さらに1分で0.5度進む。13時以降は12で割った余りで考える
    - 分針の角度`minuteAngle(分, 秒)`: 1分で6度、さらに1秒で0.1度進む
    - 秒針の角度`secondAngle(秒)`: 1秒で6度進む
    - 中心(cx, cy)から角度θ、長さrの点の座標を返す`pointX`・`pointY`。12時の方向が0度なので、x = cx + r・sinθ、y = cy − r・cosθになる（画面のy座標は下へ行くほど大きいので引く）。`Math.toRadians`で度をラジアンに直してから`Math.sin`・`Math.cos`に渡す
- `ClockPanel`（画面）
    - 時・分・秒を受け取り、`getPreferredSize`で320×320を返す
    - 文字盤の円、60本の目盛り（5分ごとの目盛りは長く太く）、1〜12の数字、時針・分針・秒針、中心の軸を描く
    - 時針は太く短く、分針は細く長く、秒針は赤く細くする
    - 数字は`FontMetrics`で文字の幅と高さを測り、文字の中心が円周上の点に来るように置く
- `ClockApp`は「時:分:秒」の形の引数を`split(":")`で分けて渡す。引数がなければ10:10:30を表示する

**実行例**:

`java ClockApp`を実行すると、「アナログ時計」というウィンドウが開きます。

- クリーム色の文字盤に、1〜12の数字と60本の目盛りが並ぶ
- 時針は10と11の間（10時10分なので、10から少し11へ進んだ位置）、分針は2の少し先、赤い秒針は6を指す
- 文字盤の下のほうに「10:10:30」と文字でも表示される

`java ClockApp 3:45:00`とすると、時針は3と4の間の4に近い位置、分針は9、秒針は12を指します。

`java ClockMathCheck`を実行すると、角度と座標の計算を確かめられます（最後に「11件中11件OK」と表示されます）。

## 本文で扱っていないAPI

課題2では、線の太さを変えるために、本文で扱っていない次のAPIを使います。

- `Graphics2D`: `paintComponent`が受け取る`Graphics`は、実際には`Graphics2D`のインスタンスなので、キャストして使う
- `BasicStroke`: `g2.setStroke(new BasicStroke(太さ))`で線の太さを指定する
- `RenderingHints.KEY_ANTIALIASING`: 斜めの線や円の縁のぎざぎざを目立たなくする

これらを使わず、`Graphics`の`drawLine`だけで描いてもかまいません（線は1ピクセルの太さになります）。

## コンパイルと実行

```bash
cd exercises/chapter17/advanced/task1-calendar
javac *.java
java CalendarApp
java MonthCalendarCheck
```

課題2は`task2-clock`フォルダーで`ClockApp`と`ClockMathCheck`を実行します。

## 実装のヒント

- 「何列目に来るか」と「その月の日数」がわかれば、42マスの配列は`cells[1日の列 + 日 − 1] = 日`で埋められる
- 角度の計算と座標の計算を`ClockMath`に分けておくと、目盛り・数字・針のどれを描くときも同じメソッドを使える
- 針を動かし続けるには、第18章で学ぶ`javax.swing.Timer`で1秒ごとに時刻を進めて`repaint()`を呼ぶ

## 完了チェックリスト

- [ ] 課題1: GridLayoutで、曜日と日付のマス目をそろえて並べられる
- [ ] 課題1: 日付の計算を画面のクラスから分けられる
- [ ] 課題2: 角度から円周上の点の座標を求められる
- [ ] 課題2: paintComponentで、計算した座標をもとに線や文字を描ける

**次のステップ**: 発展課題が終わったら、[challenge/](../challenge/)のチャレンジ課題に挑戦しましょう。
