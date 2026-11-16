# JSONライブラリの比較（解答例）

調べた日付: 2026年11月16日
調べた場所: Maven Central（`https://repo1.maven.org/maven2/`）の版の一覧、各版のpom.xmlとJARファイル

## 比較表

| 観点 | Gson 2.15.0 | Jackson 2.23.0（jackson-databind） | Jackson 3.3.0（jackson-databind） |
|---|---|---|---|
| グループID | `com.google.code.gson` | `com.fasterxml.jackson.core` | `tools.jackson.core` |
| 最新版の公開日 | 2026年10月24日 | 2026年11月8日 | 2026年11月8日（3.0.0は2025年10月3日） |
| ライセンス | Apache License 2.0 | Apache License 2.0 | Apache License 2.0 |
| 推移的な依存（compile） | error_prone_annotations（注釈だけの小さなJAR） | jackson-core、jackson-annotations | jackson-core、jackson-annotations |
| JARの大きさ | 約310KB | 約1.7MB（依存を含めて約2.4MB） | 約2.0MB（依存は別） |
| Javaの版 | Java 8以上（pom.xmlの`maven.compiler.release`が8） | Java 8以上 | Java 17以上（jackson-baseのpom.xmlの`javac.src.version`が17） |
| Recordの読み書き | 対応 | 対応 | 対応 |
| 本書での扱い | 第22章で使い方を紹介 | 第22章で使い方を紹介 | 本書では扱っていない |

目的に合うか（学生と成績をJSONにし、読み戻す）を、小さな試作で確かめました。
成績のリストを持つ`Student`のRecordのリストを、Gson 2.15.0とJackson 2.23.0で書き出して読み戻し、どちらも元と等しくなりました（Java 25）。
Jackson 3は、パッケージ名が違うため試作を書き直す必要があり、今回は試していません。

## 設計判断記録：JSONの書き出しに使うライブラリ

- 状況
    + 学生と成績をJSONファイルに書き出し、別のパソコンで読み込む機能を加える。標準APIにはJSONを読み書きするクラスがない
- 選択肢
    + 案Aは、Gsonを使う。依存が少なく、JARも小さい。設定が少なく、すぐに使える
    + 案Bは、Jackson 2を使う。機能が多く、多くのフレームワークが使っている。依存が2つ増え、JARは約8倍の大きさになる
    + 案Cは、Jackson 3を使う。新しい版だが、Jackson 2とはグループIDとパッケージ名が違い、本書の第22章の例はそのままでは動かない
- 決定
    + 案Aを採用する
- 理由
    + 学生管理システムでは、JSONの書き出しと読み込みができれば足り、Jacksonの豊富な機能（ストリーミング処理、多くのデータ形式への対応など）は使わない
    + 単独のアプリケーションとして配布するので、配布の大きさが小さいほうがよい（第25章の「依存を1つ増やすことの重さ」）
- 影響
    + Gsonのクラスを使うのは、JSONの読み書きをするクラス1つだけにする。将来、Spring BootなどJacksonを使うフレームワークに移るときは、そのクラスだけを書き換える
    + Gsonの版を上げるときは、書き出したJSONを古い版で読めるかを確かめる

## 補足

Jackson 2はいまも更新が続いているので、Jackson 2を選ぶことも誤りではありません。
すでにJacksonを使っているプロジェクトや、JSON以外の形式（YAMLなど）も扱う予定があるなら、案Bが有力になります。
選ぶ理由が要求に結びついているかどうかが大切です。
