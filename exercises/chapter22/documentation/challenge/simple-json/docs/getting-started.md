# SimpleJSON 入門ガイド

SimpleJSONを使って、JSONを読み書きする最初のプログラムを動かします。

## 必要なもの

- JDK 25
- MavenかGradle（ライブラリをビルドするとき）

## インストール

SimpleJSONはMaven Centralに公開していないので、手元でビルドして使います。

### Mavenのローカルリポジトリに入れる

```bash
cd simple-json
mvn install
```

`~/.m2/repository/com/example/simple-json/1.0.0/`にJARが入ります。
使う側のプロジェクトでは、次の依存を書きます。

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>simple-json</artifactId>
    <version>1.0.0</version>
</dependency>
```

Gradleのプロジェクトから使うときは、`repositories`に`mavenLocal()`を足し、`implementation 'com.example:simple-json:1.0.0'`と書きます。

### JARをクラスパスに置く

ビルドツールを使わないときは、`mvn package`（または`gradle jar`）で作ったJARをクラスパスに加えます。

```bash
mvn package        # target/simple-json-1.0.0.jar ができる
javac -cp target/simple-json-1.0.0.jar GettingStarted.java
java -cp target/simple-json-1.0.0.jar:. GettingStarted
```

Windowsでは、クラスパスの区切りを`:`ではなく`;`にします。

## 最初のプログラム

`GettingStarted.java`を作ります。

```java
import com.example.json.JsonArray;
import com.example.json.JsonException;
import com.example.json.JsonObject;
import com.example.json.JsonParser;

public class GettingStarted {
    public static void main(String[] args) {
        // 1. JSONを解析して、型を指定して値を取り出す
        String text = """
                {"title": "Java入門", "pages": 480, "price": 3200.5,
                 "authors": ["山田", "佐藤"], "published": true, "series": null}
                """;
        JsonObject book = JsonParser.parse(text);
        IO.println(book.getString("title"));
        IO.println(book.getInt("pages"));
        IO.println(book.getDouble("price"));
        IO.println(book.getArray("authors").getString(0));
        IO.println(book.isNull("series"));

        // 2. JSONを組み立てて、文字列にする
        JsonObject order = new JsonObject()
            .put("id", 1001)
            .put("items", new JsonArray()
                .add(new JsonObject().put("name", "ノート").put("qty", 3))
                .add(new JsonObject().put("name", "ペン").put("qty", 2)))
            .put("note", "午前中に\n届けてください");
        IO.println(order);              // 1行のJSON
        IO.println(order.toString(2));  // 2文字ずつ字下げしたJSON

        // 3. 失敗はJsonExceptionで知らされる
        try {
            JsonParser.parse("{\"name\": \"John\",}");
        } catch (JsonException e) {
            IO.println(e.getMessage());
        }
        try {
            book.getInt("title");
        } catch (JsonException e) {
            IO.println(e.getMessage());
        }
    }
}
```

実行すると、次のように表示されます。

```
Java入門
480
3200.5
山田
true
{"id":1001,"items":[{"name":"ノート","qty":3},{"name":"ペン","qty":2}],"note":"午前中に\n届けてください"}
{
  "id": 1001,
  "items": [
    {
      "name": "ノート",
      "qty": 3
    },
    {
      "name": "ペン",
      "qty": 2
    }
  ],
  "note": "午前中に\n届けてください"
}
17文字目: 最後の要素のあとにカンマは書けません
キー"title"の値は整数ではありません（文字列）
```

文字列の中の改行は、JSONでは`\n`の2文字で書かれます。
解析の失敗では、メッセージの先頭に誤りの場所（1から数えた文字の位置）が付きます。

## 値の型

JSONの値は、次のJavaの型で取り出します。

| JSON | 取り出すメソッド | Javaの型 |
|---|---|---|
| 文字列 | `getString` | `String` |
| 整数 | `getInt`・`getLong` | `int`・`long` |
| 数（整数・小数） | `getDouble` | `double` |
| `true`・`false` | `getBoolean` | `boolean` |
| オブジェクト | `getObject` | `JsonObject` |
| 配列 | `getArray` | `JsonArray` |
| `null` | `isNull`で調べる | なし |

型の違うメソッドで取り出したときと、ないキーを取り出したときは`JsonException`になります。
キーがあるかは`has`で、型がわからない値は`get`（`Object`を返す）で調べます。

## 次に読むもの

- APIドキュメント: `mvn javadoc:javadoc`で`target/reports/apidocs/index.html`、`gradle javadoc`で`build/docs/javadoc/index.html`ができます
- 解析できる書き方とできない書き方: `JsonParser`のAPIドキュメント
