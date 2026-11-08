<!-- このファイルは scripts/check-java-samples.js --report が生成します -->
# Javaサンプルコードのコンパイル検査

javac 25.0.2（`--release 21`）で、番号付きのJavaサンプルのうち単体のコンパイル単位になっているものを1つずつコンパイルした結果。
「結合」は、単体では型が足りないが、同じ章の別のサンプルにある型を補うとコンパイルできたもの。「依存」は、補っても型が見つからないもの（外部ライブラリなど）。「意図」は、コード中のコメントで誤りの例と示しているもの。「省略」は、メソッドの本体を省略しているか、複数のファイルを1つに並べているもの。「エラー」は、それ以外のコンパイルエラーで、修正が必要な可能性が高い。

`--run` を付けると、コンパイルできたサンプルのうち直後に「実行結果：」があるものを実行し、出力（標準エラー出力を含む）と比べる。
「（例）」「（GUI表示）」など括弧書きの付いた実行結果は比べない。「数値差」は時刻・処理時間・乱数など数字だけの違い（スタックトレースの省略、絶対パスの違いも含む）で、一覧には載せない。
「実行不可」はGUIを表示するもの、外部のファイルやサーバーが必要なもの。

全文を sample-code/ に置いたサンプル 353件は対象外（npm run check:samples で検査）

検査したサンプル: OK 1、結合 0、依存 62、意図 14、省略 4、エラー 0（対象外の断片 265）

| 判定 | サンプル | 原稿の行 | 最初のエラー |
|---|---|---|---|
| 省略 | 4-4 | chapter04-classes-and-instances.md:198 | L13: class, interface, enum, or record expected |
| 省略 | 4-31 | chapter04-classes-and-instances.md:1274 | L16: missing return statement |
| 省略 | 4-32 | chapter04-classes-and-instances.md:1315 | L31: missing return statement |
| 省略 | 8-14 | chapter08-special-classes.md:475 | L14: missing return statement |
| 意図 | 1-14 | chapter01-introduction.md:681 | L8: duplicate class: HelloWorld |
| 意図 | 3-56 | chapter03-oop-basics.md:2270 | L36: duplicate class: Point |
| 意図 | 5-34 | chapter05-inheritance-and-polymorphism.md:1823 | L7: constructor Parent in class Parent cannot be applied to given types; |
| 意図 | 5-36 | chapter05-inheritance-and-polymorphism.md:1864 | L10: duplicate class: Parent |
| 意図 | 6-12 | chapter06-immutability-and-final.md:583 | L4: cannot assign a value to final variable number |
| 意図 | 6-14 | chapter06-immutability-and-final.md:624 | L6: variable name might not have been initialized |
| 意図 | 7-22 | chapter07-abstract-classes-and-interfaces.md:1052 | L7: Rectangle is not abstract and does not override abstract method draw() in Shape |
| 意図 | 7-24 | chapter07-abstract-classes-and-interfaces.md:1114 | L7: Circle is not abstract and does not override abstract method setColor(String) in Drawable |
| 意図 | 7-26 | chapter07-abstract-classes-and-interfaces.md:1175 | L14: types Exportable and Printable are incompatible; |
| 意図 | 7-32 | chapter07-abstract-classes-and-interfaces.md:1372 | L5: PdfReportGenerator is not abstract and does not override abstract method generateReport() in ReportGenerator |
| 意図 | 10-19 | chapter10-generics.md:705 | L6: unexpected type |
| 意図 | 10-23 | chapter10-generics.md:788 | L6: generic array creation |
| 意図 | 10-25 | chapter10-generics.md:830 | L6: bad operand types for binary operator '*' |
| 意図 | 11-37 | chapter11-lambda-and-functional-interfaces.md:1425 | L2: Unexpected @FunctionalInterface annotation |
| 依存 | 3-8 | chapter03-oop-basics.md:550 | L4: cannot find symbol |
| 依存 | 3-29 | chapter03-oop-basics.md:1233 | L15: cannot find symbol |
| 依存 | 3-34 | chapter03-oop-basics.md:1420 | L4: cannot find symbol |
| 依存 | 3-39 | chapter03-oop-basics.md:1625 | L4: cannot find symbol |
| 依存 | 3-46 | chapter03-oop-basics.md:1898 | L4: cannot find symbol |
| 依存 | 3-53 | chapter03-oop-basics.md:2165 | L4: cannot find symbol |
| 依存 | 4-5 | chapter04-classes-and-instances.md:227 | L4: cannot find symbol |
| 依存 | 4-14 | chapter04-classes-and-instances.md:626 | L4: cannot find symbol |
| 依存 | 4-36 | chapter04-classes-and-instances.md:1551 | L2: cannot find symbol |
| 依存 | 4-38 | chapter04-classes-and-instances.md:1645 | L8: cannot find symbol |
| 依存 | 4-43 | chapter04-classes-and-instances.md:1749 | L3: cannot find symbol |
| 依存 | 4-44 | chapter04-classes-and-instances.md:1767 | L2: cannot find symbol |
| 依存 | 5-5 | chapter05-inheritance-and-polymorphism.md:351 | L4: cannot find symbol |
| 依存 | 5-16 | chapter05-inheritance-and-polymorphism.md:933 | L4: cannot find symbol |
| 依存 | 5-21 | chapter05-inheritance-and-polymorphism.md:1119 | L4: cannot find symbol |
| 依存 | 5-25 | chapter05-inheritance-and-polymorphism.md:1302 | L4: cannot find symbol |
| 依存 | 5-31 | chapter05-inheritance-and-polymorphism.md:1715 | L3: cannot find symbol |
| 依存 | 5-33 | chapter05-inheritance-and-polymorphism.md:1786 | L2: cannot find symbol |
| 依存 | 5-35 | chapter05-inheritance-and-polymorphism.md:1840 | L1: cannot find symbol |
| 依存 | 5-37 | chapter05-inheritance-and-polymorphism.md:1888 | L1: cannot find symbol |
| 依存 | 6-16 | chapter06-immutability-and-final.md:666 | L3: cannot find symbol |
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
| 依存 | 14-28 | chapter14-file-io.md:1313 | L3: cannot find symbol |
| 依存 | 15-2 | chapter15-multithreading.md:131 | L6: cannot find symbol |
| 依存 | 17-25 | chapter17-gui-basics.md:1336 | L5: cannot find symbol |
| 依存 | 19-13 | chapter19-event-advanced.md:687 | L2: cannot find symbol |
| 依存 | 19-14 | chapter19-event-advanced.md:707 | L2: cannot find symbol |
| 依存 | 19-19 | chapter19-event-advanced.md:839 | L2: cannot find symbol |
| 依存 | 20-8 | chapter20-advanced-gui.md:477 | L1: cannot find symbol |
| 依存 | 20-9 | chapter20-advanced-gui.md:497 | L1: cannot find symbol |
| 依存 | 21-5 | chapter21-unit-testing.md:214 | L2: cannot find symbol |
| 依存 | 21-6 | chapter21-unit-testing.md:237 | L2: cannot find symbol |
| 依存 | 21-9 | chapter21-unit-testing.md:355 | L2: cannot find symbol |
| 依存 | 21-11 | chapter21-unit-testing.md:394 | L1: package org.junit.jupiter.api does not exist |
| 依存 | 21-12 | chapter21-unit-testing.md:437 | L1: package org.junit.jupiter.api does not exist |
| 依存 | 21-13 | chapter21-unit-testing.md:480 | L3: cannot find symbol |
| 依存 | 22-15 | chapter22-documentation-and-libraries.md:702 | L63: constructor User in class User cannot be applied to given types; |
| 依存 | 22-16 | chapter22-documentation-and-libraries.md:818 | L3: cannot find symbol |
| 依存 | 22-25 | chapter22-documentation-and-libraries.md:1124 | L22: cannot find symbol |
| 依存 | 22-42 | chapter22-documentation-and-libraries.md:1469 | L1: package org.apache.commons.lang3 does not exist |
| 依存 | 22-52 | chapter22-documentation-and-libraries.md:1690 | L3: cannot find symbol |

## ほかのサンプルと結合するとエラーになるもの

見つからない型を同じ章のサンプルから補うと、別のエラーになるもの。補ったサンプルの版が違うだけのこともある。

| サンプル | 補ったサンプル | 原稿の行 | 最初のエラー |
|---|---|---|---|
| 4-5 | 4-4 | chapter04-classes-and-instances.md:227 | L13: class, interface, enum, or record expected |
| 8-15 | 8-14 | chapter08-special-classes.md:529 | L14: missing return statement |
