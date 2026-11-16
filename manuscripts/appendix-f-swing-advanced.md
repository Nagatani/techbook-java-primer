# <b>付録F</b> <span>Swingの発展的な機能</span> <small>イベントの通知・ドラッグ&ドロップ・ツリーとセルの編集</small>

## 付録Fについて

本付録では、第19章と第20章で要点だけを説明したSwingの発展的な機能を、まとまったコード例とともに解説します。
本文の基礎課題では使わず、第19章・第20章の発展課題とチャレンジ課題で使う機能です。

### 内容

1. `PropertyChangeSupport`によるイベントの通知（株価モニターの例）
2. `TransferHandler`と`DropTarget`によるドラッグ&ドロップ（リスト間の移動、ファイルのドロップ）
3. デバウンスとスロットルによるイベント処理の最適化
4. `DefaultTreeModel`によるツリーのノードの動的な追加・削除（右クリックのメニュー）
5. `JTable`のカスタムセルエディター（コンボボックス・色の選択・チェックボックス）

### 関連する章

- 第19章「高度なイベント処理パターン」
    + 項目1〜3の要点と、カスタムイベント・SwingWorker
- 第20章「JTable・JList・JTree」
    + 項目4・5の要点と、モデル・レンダラーの基本

### オンライン資料

本付録の解説とコンパイルできる全文は、GitHubリポジトリで公開しています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/f-swing-advanced/`
