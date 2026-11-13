package com.example.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("JsonBuilderのテスト")
class JsonBuilderTest {

    @Nested
    @DisplayName("1行のJSON")
    class Compact {

        @Test
        @DisplayName("READMEの使用例のとおりにJSONを作れる")
        void readmeExample() {
            JsonObject json = new JsonObject()
                .put("name", "John")
                .put("age", 30)
                .put("skills", new JsonArray()
                    .add("Java")
                    .add("Python"));
            assertEquals("{\"name\":\"John\",\"age\":30,\"skills\":[\"Java\",\"Python\"]}", json.toString());
        }

        @Test
        @DisplayName("値の種類ごとの書き方")
        void values() {
            assertAll(
                () -> assertEquals("null", JsonBuilder.toJson(null)),
                () -> assertEquals("true", JsonBuilder.toJson(true)),
                () -> assertEquals("-7", JsonBuilder.toJson(-7)),
                () -> assertEquals("9007199254740993", JsonBuilder.toJson(9007199254740993L)),
                () -> assertEquals("2.5", JsonBuilder.toJson(2.5)),
                () -> assertEquals("\"abc\"", JsonBuilder.toJson("abc"))
            );
        }

        @Test
        @DisplayName("空のオブジェクトと配列は{}と[]になる")
        void empty() {
            assertEquals("{}", new JsonObject().toString());
            assertEquals("[]", new JsonArray().toString());
            assertEquals("{\"a\":[],\"o\":{}}",
                    new JsonObject().put("a", new JsonArray()).put("o", new JsonObject()).toString());
        }

        @Test
        @DisplayName("JSONで表せない値はJsonExceptionになる")
        void unsupported() {
            assertThrows(JsonException.class, () -> JsonBuilder.toJson(Double.NaN));
            assertThrows(JsonException.class, () -> JsonBuilder.toJson(new StringBuilder("x")));
        }
    }

    @Nested
    @DisplayName("文字列のエスケープ")
    class Escapes {

        @Test
        @DisplayName("\"と\\と、名前の付いた制御文字をエスケープする")
        void namedEscapes() {
            assertEquals("\"a\\\"b\\\\c\\bd\\fe\\nf\\rg\\th\"",
                    JsonBuilder.quote("a\"b\\c\bd\fe\nf\rg\th"));
        }

        @Test
        @DisplayName("そのほかの制御文字は\\u00XXにする")
        void otherControlCharacters() {
            assertEquals("\"\\u0000\\u001f\"", JsonBuilder.quote("\u0000\u001f"));
        }

        @Test
        @DisplayName("/と日本語（BMP外の文字を含む）はエスケープしない")
        void notEscaped() {
            assertEquals("\"a/b 日本語 𠮷\"", JsonBuilder.quote("a/b 日本語 𠮷"));
        }

        @Test
        @DisplayName("キーもエスケープする")
        void keys() {
            assertEquals("{\"say \\\"hi\\\"\":1}", new JsonObject().put("say \"hi\"", 1).toString());
        }
    }

    @Nested
    @DisplayName("読みやすいJSON")
    class Pretty {

        @Test
        @DisplayName("指定した数の空白で字下げし、キーと値の間を1文字空ける")
        void indent() {
            JsonObject json = new JsonObject()
                .put("name", "John")
                .put("skills", new JsonArray().add("Java").add(new JsonObject()))
                .put("empty", new JsonArray());
            String expected = """
                    {
                      "name": "John",
                      "skills": [
                        "Java",
                        {}
                      ],
                      "empty": []
                    }""";
            assertEquals(expected, json.toString(2));
        }

        @Test
        @DisplayName("字下げを0にすると、改行だけが入る")
        void zeroIndent() {
            assertEquals("[\n1,\n2\n]", new JsonArray().add(1).add(2).toString(0));
        }

        @Test
        @DisplayName("負の字下げはIllegalArgumentExceptionになる")
        void negativeIndent() {
            assertThrows(IllegalArgumentException.class, () -> new JsonObject().toString(-1));
        }
    }

    @Nested
    @DisplayName("解析と生成の往復")
    class RoundTrip {

        @Test
        @DisplayName("作ったJSONを解析すると、元と等しいオブジェクトに戻る")
        void buildThenParse() {
            JsonObject original = new JsonObject()
                .put("text", "改行\nタブ\t引用符\"")
                .put("control", "\u0001")
                .put("number", -12)
                .put("decimal", 0.1)
                .put("flag", false)
                .put("nothing", null)
                .put("list", new JsonArray().add(1).add(new JsonArray()).add(new JsonObject().put("k", "v")));
            assertEquals(original, JsonParser.parse(original.toString()));
            assertEquals(original, JsonParser.parse(original.toString(4)));
        }

        @Test
        @DisplayName("解析したJSONを書き出すと、空白を除いて元の文字列に戻る")
        void parseThenBuild() {
            String json = "{\"a\":[1,2.5,\"x\",true,null],\"b\":{\"c\":{}}}";
            assertEquals(json, JsonParser.parse(json).toString());
        }
    }
}
