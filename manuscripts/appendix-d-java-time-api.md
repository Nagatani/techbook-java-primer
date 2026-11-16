# <b>付録D</b> <span>java.time APIによる日付と時刻の処理</span> <small>日付・時刻・期間・タイムゾーン</small>

## 付録Dについて

本付録では、Java 8で加わった日付と時刻のAPI（`java.time`パッケージ）を解説します<span class="footnote">java.time APIはJSR 310として策定されました。広く使われていた日付時刻ライブラリJoda-Timeの作者スティーブン・コルボーンが、仕様策定を主導しました。</span>。
本文では、第24章で日付と時刻の列を読み書きするときなどに`LocalDate`や`LocalDateTime`を使いました。
本付録は、それらのクラスの使い方をまとめて学ぶための資料です。

### 内容

1. 古い`Date`・`Calendar`の問題点と、`java.time`での解決
2. 主なクラス（`LocalDate`・`LocalTime`・`LocalDateTime`・`ZonedDateTime`・`Instant`）の選び方
3. 日付と時刻の作成と計算
4. 書式を指定した表示と読み取り（`DateTimeFormatter`）
5. 期間の計算（`Period`・`Duration`）
6. タイムゾーンの扱い
7. ファイルの更新日時などの扱い
8. 古いAPIとの相互変換

`java.time`のクラスは不変（第6章）なので、`plusDays`などの計算は元のオブジェクトを変えずに新しいオブジェクトを返します。
不変であるため、複数のスレッドから安全に使えます。

### 関連する章

- 第6章「不変性とfinalキーワード」
    + `java.time`のクラスが不変である理由
- 第12章「Stream APIと高度なコレクション操作」
    + タイムゾーンの一覧を`Stream`で絞り込む例
- 第13章「例外処理」
    + 日付の文字列を読み取れないときの`DateTimeParseException`
- 第14章「ファイルI/O」
    + ファイルの更新日時の取得
- 第24章「データベースプログラミング」
    + 日付と時刻の列と`java.time`のクラスの対応

### オンライン資料

本付録の解説とサンプルコードは、GitHubリポジトリで公開しています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/d-java-time-api/`

リポジトリには、解説（README.md）、7つの基本例と追加の実践例（妥当性の検査、営業日の計算など）のサンプルコード、演習問題があります。
