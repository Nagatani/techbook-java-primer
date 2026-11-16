<!-- このファイルは scripts/check-java-samples.js --report が生成します -->
# Javaサンプルコードのコンパイル検査

javac 25.0.2（`--release 25`）で、番号付きのJavaサンプルのうち単体のコンパイル単位になっているものを1つずつコンパイルした結果。
「結合」は、単体では型が足りないが、同じ章の別のサンプルにある型を補うとコンパイルできたもの。「依存」は、補っても型が見つからないもの（外部ライブラリなど）。「意図」は、コード中のコメントで誤りの例と示しているもの。「省略」は、メソッドの本体を省略しているか、複数のファイルを1つに並べているもの。「エラー」は、それ以外のコンパイルエラーで、修正が必要な可能性が高い。

`--run` を付けると、コンパイルできたサンプルのうち直後に「実行結果：」があるものを実行し、出力（標準エラー出力を含む）と比べる。
「（例）」「（GUI表示）」など括弧書きの付いた実行結果は比べない。「数値差」は時刻・処理時間・乱数など数字だけの違い（スタックトレースの省略、絶対パスの違いも含む）で、一覧には載せない。
「実行不可」はGUIを表示するもの、外部のファイルやサーバーが必要なもの。

全文を sample-code/ に置いたサンプル 328件は対象外（npm run check:samples で検査）

検査したサンプル: OK 3、結合 0、依存 11、意図 0、省略 4、エラー 0（対象外の断片 88）

| 判定 | サンプル | 原稿の行 | 最初のエラー |
|---|---|---|---|
| 省略 | 4-4 | chapter04-classes-and-instances.md:201 | L13: class, interface, enum, or record expected |
| 省略 | 4-29 | chapter04-classes-and-instances.md:1229 | L16: missing return statement |
| 省略 | 4-30 | chapter04-classes-and-instances.md:1265 | L31: missing return statement |
| 省略 | 8-12 | chapter08-special-classes.md:470 | L14: missing return statement |
| 依存 | 3-7 | chapter03-oop-basics.md:536 | L4: cannot find symbol |
| 依存 | 3-39 | chapter03-oop-basics.md:1704 | L4: cannot find symbol |
| 依存 | 3-46 | chapter03-oop-basics.md:1990 | L4: cannot find symbol |
| 依存 | 4-5 | chapter04-classes-and-instances.md:230 | L2: package com.example.cars does not exist |
| 依存 | 4-14 | chapter04-classes-and-instances.md:632 | L4: cannot find symbol |
| 依存 | 5-7 | chapter05-inheritance-and-polymorphism.md:398 | L4: cannot find symbol |
| 依存 | 5-13 | chapter05-inheritance-and-polymorphism.md:819 | L3: cannot find symbol |
| 依存 | 21-5 | chapter21-unit-testing.md:233 | L2: cannot find symbol |
| 依存 | 21-6 | chapter21-unit-testing.md:253 | L2: cannot find symbol |
| 依存 | 21-9 | chapter21-unit-testing.md:386 | L2: cannot find symbol |
| 依存 | 21-16 | chapter21-unit-testing.md:613 | L3: cannot find symbol |

## ほかのサンプルと結合するとエラーになるもの

見つからない型を同じ章のサンプルから補うと、別のエラーになるもの。補ったサンプルの版が違うだけのこともある。

| サンプル | 補ったサンプル | 原稿の行 | 最初のエラー |
|---|---|---|---|
| 4-5 | 4-4 | chapter04-classes-and-instances.md:230 | L13: class, interface, enum, or record expected |
