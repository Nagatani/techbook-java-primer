# SimpleJSON

Javaの標準APIだけで書いた、小さなJSONライブラリです。
JSONの文字列を解析してJavaのオブジェクトにすることと、JavaのオブジェクトからJSONの文字列を作ることができます。

- 外部ライブラリに依存しない（JDK 25だけで動く）
- 値は`getString`・`getInt`のように型を指定して取り出す。型が違えば`JsonException`で知らせる
- 解析の失敗では、誤りの場所（何文字目か）と理由を知らせる
- オブジェクトのキーは、書いた順・入れた順に並ぶ

## 使い方

```java
// JSONの解析
String jsonString = "{\"name\":\"John\",\"age\":30}";
JsonObject obj = JsonParser.parse(jsonString);
String name = obj.getString("name");   // "John"
int age = obj.getInt("age");           // 30

// JSONの生成
JsonObject json = new JsonObject()
    .put("name", "John")
    .put("age", 30)
    .put("skills", new JsonArray()
        .add("Java")
        .add("Python"));
String result = json.toString();
// {"name":"John","age":30,"skills":["Java","Python"]}

// 読みやすい形（2文字ずつ字下げ）
String pretty = json.toString(2);
```

インストールの方法と、もう少し長い例は[入門ガイド](docs/getting-started.md)にあります。

## 主なクラス

| クラス | 役割 |
|---|---|
| `JsonParser` | JSONの文字列を解析する（`parse`・`parseArray`・`parseValue`） |
| `JsonObject` | JSONのオブジェクト。`put`で値を入れ、`getString`などで取り出す |
| `JsonArray` | JSONの配列。`add`で値を足し、`getString(0)`などで取り出す |
| `JsonBuilder` | Javaの値からJSONの文字列を作る（`toJson`・`toPrettyJson`） |
| `JsonException` | 解析・生成・取り出しの失敗を知らせる非検査例外 |

## ビルド

JDK 25と、MavenかGradleを使います。

| 作業 | Maven | Gradle |
|---|---|---|
| テストを実行する | `mvn test` | `gradle test` |
| JARを作る | `mvn package`（`target/`） | `gradle jar`（`build/libs/`） |
| APIドキュメントを作る | `mvn javadoc:javadoc`（`target/reports/apidocs/`） | `gradle javadoc`（`build/docs/javadoc/`） |

APIドキュメントは`-Xdoclint:all`で検査し、警告が1つでもあればビルドを失敗させます。

## 制限

- JSONの数は、整数を`long`、それ以外を`double`で持ちます。`long`に入らない整数や、`double`で表せない桁数の小数は、値が丸められます
- 1つのオブジェクトの中で重複したキーは、書き方の誤りとして扱います
- 入れ子は512段までです
- ファイルを少しずつ読む機能（ストリーミング）はありません。文字列全体を読み込んでから解析します
- スレッドセーフではありません。複数のスレッドから同じ`JsonObject`を書き換えないでください

## 貢献

不具合の報告や改善の提案は歓迎します。手順は[CONTRIBUTING.md](CONTRIBUTING.md)を読んでください。

## ライセンス

[MITライセンス](LICENSE)で公開しています。
