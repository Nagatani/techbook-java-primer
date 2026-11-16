# サンプルコード

書籍「Java入門」の本文に載せたサンプルコードの全文です。

- `chapterXX/XX-YY/` が本文の「サンプルコードX-Y」に対応します
- 各ディレクトリは単独でコンパイルできます（Java 25以降）
- 標準入出力には、Java 25で加わった`IO`クラス（`IO.println`、`IO.print`、`IO.readln`）を使います。エラーメッセージの出力だけは`System.err`を使います
- 本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています
- 章ごとの `README.md` に、ファイルの一覧と実行のコマンドがあります

## コンパイルと実行

```
cd chapter05/05-03
javac -d out *.java
java -cp out Main
```

## 本文との対応（執筆者向け）

本文に載せる範囲は、章ごとの `listings.json` にファイル名と行番号で記録しています。

```json
"5-3": {
  "dir": "05-03",
  "main": "Main",
  "excerpt": [
    { "file": "Character.java", "from": 1, "to": 9 },
    { "file": "Hero.java", "from": 1, "to": 6 },
    { "file": "Wizard.java", "from": 1, "to": 8 },
    { "file": "Main.java", "from": 1, "to": 18 }
  ]
}
```

外部ライブラリを使うサンプルは、`"libs": ["com.google.code.gson:gson:2.11.0"]`のようにMaven Centralの座標を書きます（検査のときに`.cache/sample-code-libs/`へ取得します）。

`excerpt` の範囲を上から順に連結したものが、本文のコードブロックと1文字単位で一致します。
範囲のあいだには、同じファイルで行が離れていれば `// ...` の行（字下げは前後の行の深いほうに合わせる）、別のファイルに移れば空行1行が入ります（`"sep": "elision" | "blank" | "none"` で個別に指定できます）。

- `npm run check:samples` で、本文と全文の一致、キャプション、コンパイル、本文の「実行結果：」との一致を検査します
- `npm run samples:sync` で、`listings.json` と全文から本文のキャプションとコードブロック、章ごとの `README.md` を作り直します
- `node scripts/sample-code.js show 5-3` で全文を行番号付きで表示し、`node scripts/sample-code.js set 5-3 Character.java:1-9 Main.java:3-10,12-16` で本文に載せる範囲を変えます（ファイルごとに範囲をカンマで区切り、範囲を省くとファイル全体）
- `node scripts/sample-code.js add X-Y` で、本文のコードブロックをそのまま全文として登録します（外部ライブラリは`--libs=グループ:名前:バージョン`）。全文にimportや補助クラスを書き足して行がずれたら、`node scripts/sample-code.js relocate X-Y` で範囲を探し直します
- 本文にコードブロックを足したら `node scripts/sample-code.js number` で、番号のないブロックに番号を付け、章ごとに出現順に振り直します（`sample-code/`のディレクトリ名と`listings.json`のキーも変わります）
- `npm run samples:extract` で、本文のサンプルのうちコンパイルできてまだ `listings.json` にないものを全文として書き出します

本文のコードは全文から生成します。コードを直すときは全文を編集し、必要なら範囲を直してから `npm run samples:sync` を実行してください。
