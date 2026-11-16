# <b>付録F</b> <span>Javaのよくあるエラーと対処法</span> <small>エラーメッセージの読み方とデバッグ</small>

## 付録Fについて

本付録では、Javaのプログラムで出会いやすいエラーを、エラーの種類ごとに集めて解説します。
各章末の「よくあるエラーと対処法」は、本文ではエラーと原因の要点だけを挙げています。
エラーメッセージの例、修正したコード、デバッグのヒントは、本付録で提供します。

### 内容

1. コンパイルエラーの読み方と直し方
2. 実行時の例外（`NullPointerException`・`ClassCastException`・`ConcurrentModificationException`など）の原因と対処
3. スタックトレースの読み方と、デバッガーやログを使った原因の探し方
4. 各章末の「よくあるエラーと対処法」の詳しい内容（章ごとのファイル）

### 関連する章

すべての章の「よくあるエラーと対処法」から、章ごとのファイルを案内しています。
例外の仕組みそのものは、第13章「例外処理」で学びます。

### オンライン資料

本付録の内容は、GitHubリポジトリで公開しています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/f-common-errors/`

章ごとのファイルは`chapters/chapterXX.md`（XXは章の番号）で、コンパイルできる全文は`chapters/chapterXX-code/`にあります。
