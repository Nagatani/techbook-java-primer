<!-- このファイルは scripts/check-java-samples.js --report が生成します -->
# Javaサンプルコードのコンパイル検査

javac 25.0.2（`--release 21`）で、番号付きのJavaサンプルのうち単体のコンパイル単位になっているものを1つずつコンパイルした結果。
「結合」は、単体では型が足りないが、同じ章の別のサンプルにある型を補うとコンパイルできたもの。「依存」は、補っても型が見つからないもの（外部ライブラリなど）。「意図」は、コード中のコメントで誤りの例と示しているもの。「省略」は、メソッドの本体を省略しているか、複数のファイルを1つに並べているもの。「エラー」は、それ以外のコンパイルエラーで、修正が必要な可能性が高い。

`--run` を付けると、コンパイルできたサンプルのうち直後に「実行結果：」があるものを実行し、出力（標準エラー出力を含む）と比べる。
「（例）」「（GUI表示）」など括弧書きの付いた実行結果は比べない。「数値差」は時刻・処理時間・乱数など数字だけの違い（スタックトレースの省略、絶対パスの違いも含む）で、一覧には載せない。
「実行不可」はGUIを表示するもの、外部のファイルやサーバーが必要なもの。

全文を sample-code/ に置いたサンプル 320件は対象外（npm run check:samples で検査）

検査したサンプル: OK 0、結合 0、依存 40、意図 1、省略 4、エラー 0（対象外の断片 115）

| 判定 | サンプル | 原稿の行 | 最初のエラー |
|---|---|---|---|
| 省略 | 4-4 | chapter04-classes-and-instances.md:198 | L13: class, interface, enum, or record expected |
| 省略 | 4-31 | chapter04-classes-and-instances.md:1274 | L16: missing return statement |
| 省略 | 4-32 | chapter04-classes-and-instances.md:1315 | L31: missing return statement |
| 省略 | 8-12 | chapter08-special-classes.md:443 | L14: missing return statement |
| 意図 | 3-56 | chapter03-oop-basics.md:2270 | L36: duplicate class: Point |
| 依存 | 3-8 | chapter03-oop-basics.md:550 | L4: cannot find symbol |
| 依存 | 3-29 | chapter03-oop-basics.md:1233 | L15: cannot find symbol |
| 依存 | 3-34 | chapter03-oop-basics.md:1420 | L4: cannot find symbol |
| 依存 | 3-39 | chapter03-oop-basics.md:1625 | L4: cannot find symbol |
| 依存 | 3-46 | chapter03-oop-basics.md:1898 | L4: cannot find symbol |
| 依存 | 3-53 | chapter03-oop-basics.md:2165 | L4: cannot find symbol |
| 依存 | 4-5 | chapter04-classes-and-instances.md:227 | L4: cannot find symbol |
| 依存 | 4-14 | chapter04-classes-and-instances.md:626 | L4: cannot find symbol |
| 依存 | 4-36 | chapter04-classes-and-instances.md:1551 | L2: cannot find symbol |
| 依存 | 5-5 | chapter05-inheritance-and-polymorphism.md:351 | L4: cannot find symbol |
| 依存 | 5-16 | chapter05-inheritance-and-polymorphism.md:933 | L4: cannot find symbol |
| 依存 | 5-21 | chapter05-inheritance-and-polymorphism.md:1119 | L4: cannot find symbol |
| 依存 | 5-25 | chapter05-inheritance-and-polymorphism.md:1302 | L4: cannot find symbol |
| 依存 | 5-31 | chapter05-inheritance-and-polymorphism.md:1715 | L3: cannot find symbol |
| 依存 | 5-33 | chapter05-inheritance-and-polymorphism.md:1786 | L2: cannot find symbol |
| 依存 | 8-7 | chapter08-special-classes.md:246 | L4: cannot find symbol |
| 依存 | 8-13 | chapter08-special-classes.md:497 | L3: cannot find symbol |
| 依存 | 8-19 | chapter08-special-classes.md:691 | L2: cannot find symbol |
| 依存 | 8-22 | chapter08-special-classes.md:776 | L1: cannot find symbol |
| 依存 | 8-24 | chapter08-special-classes.md:838 | L2: cannot find symbol |
| 依存 | 11-6 | chapter11-lambda-and-functional-interfaces.md:197 | L3: cannot find symbol |
| 依存 | 11-7 | chapter11-lambda-and-functional-interfaces.md:213 | L3: cannot find symbol |
| 依存 | 11-9 | chapter11-lambda-and-functional-interfaces.md:285 | L2: cannot find symbol |
| 依存 | 11-11 | chapter11-lambda-and-functional-interfaces.md:369 | L5: cannot find symbol |
| 依存 | 11-13 | chapter11-lambda-and-functional-interfaces.md:448 | L3: cannot find symbol |
| 依存 | 11-14 | chapter11-lambda-and-functional-interfaces.md:480 | L5: cannot find symbol |
| 依存 | 11-16 | chapter11-lambda-and-functional-interfaces.md:565 | L12: cannot find symbol |
| 依存 | 11-17 | chapter11-lambda-and-functional-interfaces.md:598 | L10: invalid method reference |
| 依存 | 11-18 | chapter11-lambda-and-functional-interfaces.md:635 | L13: invalid method reference |
| 依存 | 11-20 | chapter11-lambda-and-functional-interfaces.md:689 | L3: cannot find symbol |
| 依存 | 15-2 | chapter15-multithreading.md:131 | L6: cannot find symbol |
| 依存 | 17-25 | chapter17-gui-basics.md:1336 | L5: cannot find symbol |
| 依存 | 21-5 | chapter21-unit-testing.md:214 | L2: cannot find symbol |
| 依存 | 21-6 | chapter21-unit-testing.md:237 | L2: cannot find symbol |
| 依存 | 21-9 | chapter21-unit-testing.md:355 | L2: cannot find symbol |
| 依存 | 21-11 | chapter21-unit-testing.md:394 | L1: package org.junit.jupiter.api does not exist |
| 依存 | 21-12 | chapter21-unit-testing.md:437 | L1: package org.junit.jupiter.api does not exist |
| 依存 | 21-13 | chapter21-unit-testing.md:480 | L3: cannot find symbol |
| 依存 | 22-15 | chapter22-documentation-and-libraries.md:702 | L63: constructor User in class User cannot be applied to given types; |
| 依存 | 22-16 | chapter22-documentation-and-libraries.md:818 | L3: cannot find symbol |

## ほかのサンプルと結合するとエラーになるもの

見つからない型を同じ章のサンプルから補うと、別のエラーになるもの。補ったサンプルの版が違うだけのこともある。

| サンプル | 補ったサンプル | 原稿の行 | 最初のエラー |
|---|---|---|---|
| 4-5 | 4-4 | chapter04-classes-and-instances.md:227 | L13: class, interface, enum, or record expected |
| 8-13 | 8-12 | chapter08-special-classes.md:497 | L14: missing return statement |
