# サンプルコード

書籍「Java入門」の本文に載せたサンプルコードの全文です。

- `chapterXX/XX-YY/` が本文の「サンプルコードX-Y」に対応します
- 各ディレクトリは単独でコンパイルできます（Java 21以降）
- 本文には全文の一部だけを載せていることがあります。本文のキャプションに、ファイル名と掲載した行の範囲を示しています
- 章ごとの `README.md` に、ファイルの一覧と実行のコマンドがあります

## コンパイルと実行

```
cd chapter05/05-03
javac -d out *.java
java -cp out ShapeDemo
```

## 本文との対応（執筆者向け）

本文に載せる範囲は、章ごとの `listings.json` にファイル名と行番号で記録しています。

```json
"5-3": {
  "dir": "05-03",
  "main": "ShapeDemo",
  "excerpt": [
    { "file": "Shape.java", "from": 12, "to": 30 },
    { "file": "ShapeDemo.java", "from": 3, "to": 15 }
  ]
}
```

`excerpt` の範囲を上から順に連結したものが、本文のコードブロックと1文字単位で一致します。
範囲のあいだには、同じファイルで行が離れていれば `// ...` の行（字下げは次の範囲の1行目に合わせる）、別のファイルに移れば空行1行が入ります（`"sep": "elision" | "blank" | "none"` で個別に指定できます）。

- `npm run check:samples` で、本文と全文の一致、キャプション、コンパイル、本文の「実行結果：」との一致を検査します
- `npm run samples:sync` で、`listings.json` から本文のキャプションと章ごとの `README.md` を作り直します
- `npm run samples:extract` で、本文のサンプルのうちコンパイルできてまだ `listings.json` にないものを全文として書き出します

全文を編集したら、行番号がずれていないかを `npm run check:samples` で確かめてください。
