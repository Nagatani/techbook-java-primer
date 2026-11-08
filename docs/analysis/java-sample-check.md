<!-- このファイルは scripts/check-java-samples.js --report が生成します -->
# Javaサンプルコードのコンパイル検査

javac 25.0.2（`--release 21`）で、番号付きのJavaサンプルのうち単体のコンパイル単位になっているものを1つずつコンパイルした結果。
「結合」は、単体では型が足りないが、同じ章の別のサンプルにある型を補うとコンパイルできたもの。「依存」は、補っても型が見つからないもの（外部ライブラリなど）。「意図」は、コード中のコメントで誤りの例と示しているもの。「省略」は、メソッドの本体を省略しているか、複数のファイルを1つに並べているもの。「エラー」は、それ以外のコンパイルエラーで、修正が必要な可能性が高い。

`--run` を付けると、コンパイルできたサンプルのうち直後に「実行結果：」があるものを実行し、出力（標準エラー出力を含む）と比べる。
「（例）」「（GUI表示）」など括弧書きの付いた実行結果は比べない。「数値差」は時刻・処理時間・乱数など数字だけの違い（スタックトレースの省略、絶対パスの違いも含む）で、一覧には載せない。
「実行不可」はGUIを表示するもの、外部のファイルやサーバーが必要なもの。

検査したサンプル: OK 273、結合 18、依存 97、意図 13、省略 4、エラー 0（対象外の断片 227）

| 判定 | サンプル | 原稿の行 | 最初のエラー |
|---|---|---|---|
| 省略 | 4-4 | chapter04-classes-and-instances.md:198 | L13: class, interface, enum, or record expected |
| 省略 | 4-26 | chapter04-classes-and-instances.md:1304 | L16: missing return statement |
| 省略 | 4-27 | chapter04-classes-and-instances.md:1345 | L31: missing return statement |
| 省略 | 8-14 | chapter08-special-classes.md:475 | L14: missing return statement |
| 意図 | 3-40 | chapter03-oop-basics.md:2268 | L36: duplicate class: Point |
| 意図 | 5-30 | chapter05-inheritance-and-polymorphism.md:2022 | L7: constructor Parent in class Parent cannot be applied to given types; |
| 意図 | 5-32 | chapter05-inheritance-and-polymorphism.md:2063 | L10: duplicate class: Parent |
| 意図 | 6-13 | chapter06-immutability-and-final.md:681 | L4: cannot assign a value to final variable number |
| 意図 | 6-15 | chapter06-immutability-and-final.md:722 | L6: variable name might not have been initialized |
| 意図 | 7-22 | chapter07-abstract-classes-and-interfaces.md:1317 | L7: Rectangle is not abstract and does not override abstract method draw() in Shape |
| 意図 | 7-24 | chapter07-abstract-classes-and-interfaces.md:1379 | L7: Circle is not abstract and does not override abstract method setColor(String) in Drawable |
| 意図 | 7-26 | chapter07-abstract-classes-and-interfaces.md:1440 | L14: types Exportable and Printable are incompatible; |
| 意図 | 7-32 | chapter07-abstract-classes-and-interfaces.md:1667 | L5: PdfReportGenerator is not abstract and does not override abstract method generateReport() in ReportGenerator |
| 意図 | 10-19 | chapter10-generics.md:786 | L6: unexpected type |
| 意図 | 10-23 | chapter10-generics.md:869 | L6: generic array creation |
| 意図 | 10-25 | chapter10-generics.md:911 | L6: bad operand types for binary operator '*' |
| 意図 | 11-37 | chapter11-lambda-and-functional-interfaces.md:1488 | L2: Unexpected @FunctionalInterface annotation |
| 依存 | 3-20 | chapter03-oop-basics.md:1213 | L15: cannot find symbol |
| 依存 | 3-23 | chapter03-oop-basics.md:1395 | L4: cannot find symbol |
| 依存 | 3-33 | chapter03-oop-basics.md:1889 | L4: cannot find symbol |
| 依存 | 3-34 | chapter03-oop-basics.md:1961 | L3: cannot find symbol |
| 依存 | 4-31 | chapter04-classes-and-instances.md:1581 | L2: cannot find symbol |
| 依存 | 4-33 | chapter04-classes-and-instances.md:1675 | L8: cannot find symbol |
| 依存 | 4-38 | chapter04-classes-and-instances.md:1779 | L3: cannot find symbol |
| 依存 | 4-39 | chapter04-classes-and-instances.md:1797 | L2: cannot find symbol |
| 依存 | 5-2 | chapter05-inheritance-and-polymorphism.md:220 | L2: cannot find symbol |
| 依存 | 5-3 | chapter05-inheritance-and-polymorphism.md:321 | L4: cannot find symbol |
| 依存 | 5-11 | chapter05-inheritance-and-polymorphism.md:886 | L37: cannot find symbol |
| 依存 | 5-17 | chapter05-inheritance-and-polymorphism.md:1225 | L4: cannot find symbol |
| 依存 | 5-21 | chapter05-inheritance-and-polymorphism.md:1425 | L4: cannot find symbol |
| 依存 | 5-22 | chapter05-inheritance-and-polymorphism.md:1465 | L26: cannot find symbol |
| 依存 | 5-23 | chapter05-inheritance-and-polymorphism.md:1528 | L22: cannot find symbol |
| 依存 | 5-27 | chapter05-inheritance-and-polymorphism.md:1914 | L3: cannot find symbol |
| 依存 | 5-29 | chapter05-inheritance-and-polymorphism.md:1985 | L2: cannot find symbol |
| 依存 | 5-31 | chapter05-inheritance-and-polymorphism.md:2039 | L1: cannot find symbol |
| 依存 | 5-33 | chapter05-inheritance-and-polymorphism.md:2087 | L1: cannot find symbol |
| 依存 | 6-17 | chapter06-immutability-and-final.md:764 | L3: cannot find symbol |
| 依存 | 7-14 | chapter07-abstract-classes-and-interfaces.md:727 | L3: cannot find symbol |
| 依存 | 7-15 | chapter07-abstract-classes-and-interfaces.md:812 | L67: cannot find symbol |
| 依存 | 7-16 | chapter07-abstract-classes-and-interfaces.md:896 | L20: cannot find symbol |
| 依存 | 7-17 | chapter07-abstract-classes-and-interfaces.md:975 | L103: cannot find symbol |
| 依存 | 7-23 | chapter07-abstract-classes-and-interfaces.md:1350 | L1: cannot find symbol |
| 依存 | 7-25 | chapter07-abstract-classes-and-interfaces.md:1411 | L1: cannot find symbol |
| 依存 | 7-27 | chapter07-abstract-classes-and-interfaces.md:1469 | L1: cannot find symbol |
| 依存 | 7-28 | chapter07-abstract-classes-and-interfaces.md:1496 | L17: cannot find symbol |
| 依存 | 7-33 | chapter07-abstract-classes-and-interfaces.md:1686 | L1: cannot find symbol |
| 依存 | 8-7 | chapter08-special-classes.md:246 | L4: cannot find symbol |
| 依存 | 8-15 | chapter08-special-classes.md:529 | L3: cannot find symbol |
| 依存 | 8-21 | chapter08-special-classes.md:723 | L2: cannot find symbol |
| 依存 | 8-24 | chapter08-special-classes.md:806 | L1: cannot find symbol |
| 依存 | 8-26 | chapter08-special-classes.md:866 | L2: cannot find symbol |
| 依存 | 10-16 | chapter10-generics.md:662 | L35: cannot find symbol |
| 依存 | 10-20 | chapter10-generics.md:811 | L4: cannot find symbol |
| 依存 | 10-24 | chapter10-generics.md:891 | L7: cannot find symbol |
| 依存 | 11-3 | chapter11-lambda-and-functional-interfaces.md:88 | L4: cannot find symbol |
| 依存 | 11-6 | chapter11-lambda-and-functional-interfaces.md:197 | L3: cannot find symbol |
| 依存 | 11-7 | chapter11-lambda-and-functional-interfaces.md:213 | L3: cannot find symbol |
| 依存 | 11-8 | chapter11-lambda-and-functional-interfaces.md:251 | L3: cannot find symbol |
| 依存 | 11-9 | chapter11-lambda-and-functional-interfaces.md:295 | L2: cannot find symbol |
| 依存 | 11-10 | chapter11-lambda-and-functional-interfaces.md:333 | L6: incompatible types: Middleware is not a functional interface |
| 依存 | 11-11 | chapter11-lambda-and-functional-interfaces.md:395 | L5: cannot find symbol |
| 依存 | 11-12 | chapter11-lambda-and-functional-interfaces.md:427 | L8: cannot find symbol |
| 依存 | 11-13 | chapter11-lambda-and-functional-interfaces.md:474 | L3: cannot find symbol |
| 依存 | 11-14 | chapter11-lambda-and-functional-interfaces.md:506 | L5: cannot find symbol |
| 依存 | 11-15 | chapter11-lambda-and-functional-interfaces.md:549 | L17: cannot find symbol |
| 依存 | 11-16 | chapter11-lambda-and-functional-interfaces.md:610 | L12: cannot find symbol |
| 依存 | 11-17 | chapter11-lambda-and-functional-interfaces.md:643 | L10: invalid method reference |
| 依存 | 11-18 | chapter11-lambda-and-functional-interfaces.md:680 | L13: invalid method reference |
| 依存 | 11-20 | chapter11-lambda-and-functional-interfaces.md:734 | L3: cannot find symbol |
| 依存 | 11-22 | chapter11-lambda-and-functional-interfaces.md:819 | L4: cannot find symbol |
| 依存 | 11-26 | chapter11-lambda-and-functional-interfaces.md:1086 | L30: incompatible types: invalid method reference |
| 依存 | 11-28 | chapter11-lambda-and-functional-interfaces.md:1227 | L3: cannot find symbol |
| 依存 | 13-12 | chapter13-exception-handling.md:647 | L12: cannot find symbol |
| 依存 | 13-13 | chapter13-exception-handling.md:712 | L2: cannot find symbol |
| 依存 | 13-15 | chapter13-exception-handling.md:860 | L43: cannot find symbol |
| 依存 | 13-16 | chapter13-exception-handling.md:933 | L11: cannot find symbol |
| 依存 | 14-28 | chapter14-file-io.md:1774 | L3: cannot find symbol |
| 依存 | 15-2 | chapter15-multithreading.md:142 | L6: cannot find symbol |
| 依存 | 15-15 | chapter15-multithreading.md:1189 | L67: cannot find symbol |
| 依存 | 15-18 | chapter15-multithreading.md:1485 | L4: cannot find symbol |
| 依存 | 15-20 | chapter15-multithreading.md:1680 | L59: cannot find symbol |
| 依存 | 15-21 | chapter15-multithreading.md:1764 | L68: cannot find symbol |
| 依存 | 16-11 | chapter16-network-programming.md:790 | L5: package com.fasterxml.jackson.databind does not exist |
| 依存 | 16-13 | chapter16-network-programming.md:984 | L9: cannot find symbol |
| 依存 | 17-19 | chapter17-gui-basics.md:1137 | L24: cannot find symbol |
| 依存 | 17-25 | chapter17-gui-basics.md:1590 | L5: cannot find symbol |
| 依存 | 19-6 | chapter19-event-advanced.md:946 | L141: cannot find symbol |
| 依存 | 19-13 | chapter19-event-advanced.md:1518 | L2: cannot find symbol |
| 依存 | 19-14 | chapter19-event-advanced.md:1538 | L2: cannot find symbol |
| 依存 | 19-19 | chapter19-event-advanced.md:1670 | L2: cannot find symbol |
| 依存 | 20-2 | chapter20-advanced-gui.md:109 | L6: cannot find symbol |
| 依存 | 20-7 | chapter20-advanced-gui.md:807 | L12: cannot find symbol |
| 依存 | 20-8 | chapter20-advanced-gui.md:1115 | L1: cannot find symbol |
| 依存 | 20-9 | chapter20-advanced-gui.md:1135 | L1: cannot find symbol |
| 依存 | 20-11 | chapter20-advanced-gui.md:1180 | L1: cannot find symbol |
| 依存 | 20-13 | chapter20-advanced-gui.md:1295 | L2: cannot find symbol |
| 依存 | 20-17 | chapter20-advanced-gui.md:1471 | L1: cannot find symbol |
| 依存 | 21-5 | chapter21-unit-testing.md:214 | L2: cannot find symbol |
| 依存 | 21-6 | chapter21-unit-testing.md:237 | L2: cannot find symbol |
| 依存 | 21-9 | chapter21-unit-testing.md:355 | L2: cannot find symbol |
| 依存 | 21-11 | chapter21-unit-testing.md:394 | L1: package org.junit.jupiter.api does not exist |
| 依存 | 21-12 | chapter21-unit-testing.md:437 | L1: package org.junit.jupiter.api does not exist |
| 依存 | 21-13 | chapter21-unit-testing.md:480 | L3: cannot find symbol |
| 依存 | 22-5 | chapter22-documentation-and-libraries.md:388 | L1: package com.google.gson does not exist |
| 依存 | 22-7 | chapter22-documentation-and-libraries.md:539 | L1: package com.fasterxml.jackson.databind does not exist |
| 依存 | 22-9 | chapter22-documentation-and-libraries.md:711 | L1: package org.apache.commons.lang3 does not exist |
| 依存 | 22-11 | chapter22-documentation-and-libraries.md:854 | L1: package org.apache.commons.io does not exist |
| 依存 | 22-13 | chapter22-documentation-and-libraries.md:989 | L1: package okhttp3 does not exist |
| 依存 | 22-15 | chapter22-documentation-and-libraries.md:1151 | L63: constructor User in class User cannot be applied to given types; |
| 依存 | 22-16 | chapter22-documentation-and-libraries.md:1267 | L3: cannot find symbol |
| 依存 | 22-18 | chapter22-documentation-and-libraries.md:1306 | L1: package com.google.common.collect does not exist |
| 依存 | 22-26 | chapter22-documentation-and-libraries.md:1677 | L22: cannot find symbol |
| 依存 | 22-43 | chapter22-documentation-and-libraries.md:2022 | L1: package org.apache.commons.lang3 does not exist |
| 依存 | 22-53 | chapter22-documentation-and-libraries.md:2243 | L3: cannot find symbol |

## ほかのサンプルと結合するとエラーになるもの

見つからない型を同じ章のサンプルから補うと、別のエラーになるもの。補ったサンプルの版が違うだけのこともある。

| サンプル | 補ったサンプル | 原稿の行 | 最初のエラー |
|---|---|---|---|
| 7-27 | 7-4 | chapter07-abstract-classes-and-interfaces.md:1469 | L1: ReportDocument is not abstract and does not override abstract method exportToFormat(String) in Exportable |
| 8-15 | 8-14 | chapter08-special-classes.md:529 | L14: missing return statement |
| 13-12 | 13-14 | chapter13-exception-handling.md:647 | L44: SystemException is abstract; cannot be instantiated |

## 実行結果の照合

原稿の「実行結果：」と比べたサンプル: 一致 77、数値差 6、不一致 0、実行不可 5、打切 0

| 判定 | サンプル | 実行結果の行 | 内容 |
|---|---|---|---|
| 実行不可 | 14-2 | chapter14-file-io.md:148 | 外部のファイルやサーバーが必要 |
| 実行不可 | 14-10 | chapter14-file-io.md:689 | GUIを表示する |
| 実行不可 | 23-1 | chapter23-build-and-deploy.md:72 | GUIを表示する |
| 実行不可 | 23-2 | chapter23-build-and-deploy.md:140 | GUIを表示する |
| 実行不可 | 23-6 | chapter23-build-and-deploy.md:223 | GUIを表示する |
