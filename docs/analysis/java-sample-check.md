<!-- このファイルは scripts/check-java-samples.js --report が生成します -->
# Javaサンプルコードのコンパイル検査

javac 25.0.2（`--release 21`）で、番号付きのJavaサンプルのうち単体のコンパイル単位になっているものを1つずつコンパイルした結果。
「結合」は、単体では型が足りないが、同じ章の別のサンプルにある型を補うとコンパイルできたもの。「依存」は、補っても型が見つからないもの（外部ライブラリなど）。「意図」は、コード中のコメントで誤りの例と示しているもの。「省略」は、メソッドの本体を省略しているか、複数のファイルを1つに並べているもの。「エラー」は、それ以外のコンパイルエラーで、修正が必要な可能性が高い。

`--run` を付けると、コンパイルできたサンプルのうち直後に「実行結果：」があるものを実行し、出力（標準エラー出力を含む）と比べる。
「（例）」「（GUI表示）」など括弧書きの付いた実行結果は比べない。「数値差」は時刻・処理時間・乱数など数字だけの違い（スタックトレースの省略、絶対パスの違いも含む）で、一覧には載せない。
「実行不可」はGUIを表示するもの、外部のファイルやサーバーが必要なもの。

全文を sample-code/ に置いたサンプル 330件は対象外（npm run check:samples で検査）

検査したサンプル: OK 0、結合 0、依存 58、意図 13、省略 4、エラー 0（対象外の断片 227）

| 判定 | サンプル | 原稿の行 | 最初のエラー |
|---|---|---|---|
| 省略 | 4-4 | chapter04-classes-and-instances.md:198 | L13: class, interface, enum, or record expected |
| 省略 | 4-26 | chapter04-classes-and-instances.md:1259 | L16: missing return statement |
| 省略 | 4-27 | chapter04-classes-and-instances.md:1300 | L31: missing return statement |
| 省略 | 8-14 | chapter08-special-classes.md:475 | L14: missing return statement |
| 意図 | 3-40 | chapter03-oop-basics.md:2235 | L36: duplicate class: Point |
| 意図 | 5-30 | chapter05-inheritance-and-polymorphism.md:1815 | L7: constructor Parent in class Parent cannot be applied to given types; |
| 意図 | 5-32 | chapter05-inheritance-and-polymorphism.md:1856 | L10: duplicate class: Parent |
| 意図 | 6-13 | chapter06-immutability-and-final.md:583 | L4: cannot assign a value to final variable number |
| 意図 | 6-15 | chapter06-immutability-and-final.md:624 | L6: variable name might not have been initialized |
| 意図 | 7-22 | chapter07-abstract-classes-and-interfaces.md:1052 | L7: Rectangle is not abstract and does not override abstract method draw() in Shape |
| 意図 | 7-24 | chapter07-abstract-classes-and-interfaces.md:1114 | L7: Circle is not abstract and does not override abstract method setColor(String) in Drawable |
| 意図 | 7-26 | chapter07-abstract-classes-and-interfaces.md:1175 | L14: types Exportable and Printable are incompatible; |
| 意図 | 7-32 | chapter07-abstract-classes-and-interfaces.md:1372 | L5: PdfReportGenerator is not abstract and does not override abstract method generateReport() in ReportGenerator |
| 意図 | 10-19 | chapter10-generics.md:705 | L6: unexpected type |
| 意図 | 10-23 | chapter10-generics.md:788 | L6: generic array creation |
| 意図 | 10-25 | chapter10-generics.md:830 | L6: bad operand types for binary operator '*' |
| 意図 | 11-37 | chapter11-lambda-and-functional-interfaces.md:1441 | L2: Unexpected @FunctionalInterface annotation |
| 依存 | 3-20 | chapter03-oop-basics.md:1214 | L15: cannot find symbol |
| 依存 | 3-23 | chapter03-oop-basics.md:1396 | L4: cannot find symbol |
| 依存 | 3-33 | chapter03-oop-basics.md:1870 | L4: cannot find symbol |
| 依存 | 3-34 | chapter03-oop-basics.md:1942 | L3: cannot find symbol |
| 依存 | 4-31 | chapter04-classes-and-instances.md:1536 | L2: cannot find symbol |
| 依存 | 4-33 | chapter04-classes-and-instances.md:1630 | L8: cannot find symbol |
| 依存 | 4-38 | chapter04-classes-and-instances.md:1734 | L3: cannot find symbol |
| 依存 | 4-39 | chapter04-classes-and-instances.md:1752 | L2: cannot find symbol |
| 依存 | 5-17 | chapter05-inheritance-and-polymorphism.md:1111 | L4: cannot find symbol |
| 依存 | 5-21 | chapter05-inheritance-and-polymorphism.md:1294 | L4: cannot find symbol |
| 依存 | 5-27 | chapter05-inheritance-and-polymorphism.md:1707 | L3: cannot find symbol |
| 依存 | 5-29 | chapter05-inheritance-and-polymorphism.md:1778 | L2: cannot find symbol |
| 依存 | 5-31 | chapter05-inheritance-and-polymorphism.md:1832 | L1: cannot find symbol |
| 依存 | 5-33 | chapter05-inheritance-and-polymorphism.md:1880 | L1: cannot find symbol |
| 依存 | 6-17 | chapter06-immutability-and-final.md:666 | L3: cannot find symbol |
| 依存 | 7-23 | chapter07-abstract-classes-and-interfaces.md:1085 | L1: cannot find symbol |
| 依存 | 7-25 | chapter07-abstract-classes-and-interfaces.md:1146 | L1: cannot find symbol |
| 依存 | 7-27 | chapter07-abstract-classes-and-interfaces.md:1204 | L1: cannot find symbol |
| 依存 | 7-28 | chapter07-abstract-classes-and-interfaces.md:1231 | L17: cannot find symbol |
| 依存 | 7-33 | chapter07-abstract-classes-and-interfaces.md:1391 | L1: cannot find symbol |
| 依存 | 8-7 | chapter08-special-classes.md:246 | L4: cannot find symbol |
| 依存 | 8-15 | chapter08-special-classes.md:529 | L3: cannot find symbol |
| 依存 | 8-21 | chapter08-special-classes.md:723 | L2: cannot find symbol |
| 依存 | 8-24 | chapter08-special-classes.md:808 | L1: cannot find symbol |
| 依存 | 8-26 | chapter08-special-classes.md:870 | L2: cannot find symbol |
| 依存 | 10-20 | chapter10-generics.md:730 | L4: cannot find symbol |
| 依存 | 10-24 | chapter10-generics.md:810 | L7: cannot find symbol |
| 依存 | 11-3 | chapter11-lambda-and-functional-interfaces.md:88 | L4: cannot find symbol |
| 依存 | 11-6 | chapter11-lambda-and-functional-interfaces.md:197 | L3: cannot find symbol |
| 依存 | 11-7 | chapter11-lambda-and-functional-interfaces.md:213 | L3: cannot find symbol |
| 依存 | 11-9 | chapter11-lambda-and-functional-interfaces.md:285 | L2: cannot find symbol |
| 依存 | 11-10 | chapter11-lambda-and-functional-interfaces.md:323 | L6: incompatible types: Middleware is not a functional interface |
| 依存 | 11-11 | chapter11-lambda-and-functional-interfaces.md:385 | L5: cannot find symbol |
| 依存 | 11-13 | chapter11-lambda-and-functional-interfaces.md:464 | L3: cannot find symbol |
| 依存 | 11-14 | chapter11-lambda-and-functional-interfaces.md:496 | L5: cannot find symbol |
| 依存 | 11-16 | chapter11-lambda-and-functional-interfaces.md:581 | L12: cannot find symbol |
| 依存 | 11-17 | chapter11-lambda-and-functional-interfaces.md:614 | L10: invalid method reference |
| 依存 | 11-18 | chapter11-lambda-and-functional-interfaces.md:651 | L13: invalid method reference |
| 依存 | 11-20 | chapter11-lambda-and-functional-interfaces.md:705 | L3: cannot find symbol |
| 依存 | 14-28 | chapter14-file-io.md:1313 | L3: cannot find symbol |
| 依存 | 15-2 | chapter15-multithreading.md:131 | L6: cannot find symbol |
| 依存 | 17-25 | chapter17-gui-basics.md:1336 | L5: cannot find symbol |
| 依存 | 19-13 | chapter19-event-advanced.md:725 | L2: cannot find symbol |
| 依存 | 19-14 | chapter19-event-advanced.md:745 | L2: cannot find symbol |
| 依存 | 19-19 | chapter19-event-advanced.md:877 | L2: cannot find symbol |
| 依存 | 20-8 | chapter20-advanced-gui.md:508 | L1: cannot find symbol |
| 依存 | 20-9 | chapter20-advanced-gui.md:528 | L1: cannot find symbol |
| 依存 | 21-5 | chapter21-unit-testing.md:214 | L2: cannot find symbol |
| 依存 | 21-6 | chapter21-unit-testing.md:237 | L2: cannot find symbol |
| 依存 | 21-9 | chapter21-unit-testing.md:355 | L2: cannot find symbol |
| 依存 | 21-11 | chapter21-unit-testing.md:394 | L1: package org.junit.jupiter.api does not exist |
| 依存 | 21-12 | chapter21-unit-testing.md:437 | L1: package org.junit.jupiter.api does not exist |
| 依存 | 21-13 | chapter21-unit-testing.md:480 | L3: cannot find symbol |
| 依存 | 22-15 | chapter22-documentation-and-libraries.md:702 | L63: constructor User in class User cannot be applied to given types; |
| 依存 | 22-16 | chapter22-documentation-and-libraries.md:818 | L3: cannot find symbol |
| 依存 | 22-26 | chapter22-documentation-and-libraries.md:1170 | L22: cannot find symbol |
| 依存 | 22-43 | chapter22-documentation-and-libraries.md:1515 | L1: package org.apache.commons.lang3 does not exist |
| 依存 | 22-53 | chapter22-documentation-and-libraries.md:1736 | L3: cannot find symbol |

## ほかのサンプルと結合するとエラーになるもの

見つからない型を同じ章のサンプルから補うと、別のエラーになるもの。補ったサンプルの版が違うだけのこともある。

| サンプル | 補ったサンプル | 原稿の行 | 最初のエラー |
|---|---|---|---|
| 8-15 | 8-14 | chapter08-special-classes.md:529 | L14: missing return statement |

## 実行結果の照合

原稿の「実行結果：」と比べたサンプル: 一致 0、数値差 0、不一致 0、実行不可 0、打切 0

| 判定 | サンプル | 実行結果の行 | 内容 |
|---|---|---|---|
