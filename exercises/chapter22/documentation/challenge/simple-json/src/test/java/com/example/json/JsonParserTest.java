package com.example.json;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JsonParserのテスト")
class JsonParserTest {

    @Nested
    @DisplayName("正しいJSON")
    class ValidJson {

        @Test
        @DisplayName("READMEの使用例のJSONから、名前と年齢を取り出せる")
        void readmeExample() {
            JsonObject obj = JsonParser.parse("{\"name\":\"John\",\"age\":30}");
            assertEquals("John", obj.getString("name"));
            assertEquals(30, obj.getInt("age"));
        }

        @Test
        @DisplayName("文字列・数値・真偽値・nullを、それぞれのJavaの型にする")
        void valueTypes() {
            JsonObject obj = JsonParser.parse(
                    "{\"s\":\"abc\",\"i\":-12,\"d\":3.5,\"t\":true,\"f\":false,\"n\":null}");
            assertAll(
                () -> assertEquals("abc", obj.get("s")),
                () -> assertEquals(-12L, obj.get("i")),
                () -> assertEquals(3.5, obj.get("d")),
                () -> assertEquals(Boolean.TRUE, obj.get("t")),
                () -> assertEquals(Boolean.FALSE, obj.get("f")),
                () -> assertNull(obj.get("n")),
                () -> assertTrue(obj.isNull("n"))
            );
        }

        @Test
        @DisplayName("入れ子のオブジェクトと配列をたどれる")
        void nested() {
            JsonObject obj = JsonParser.parse("""
                    {
                      "user": {"name": "花子", "tags": ["admin", "dev"]},
                      "matrix": [[1, 2], [3, 4]]
                    }
                    """);
            assertAll(
                () -> assertEquals("花子", obj.getObject("user").getString("name")),
                () -> assertEquals("dev", obj.getObject("user").getArray("tags").getString(1)),
                () -> assertEquals(4, obj.getArray("matrix").getArray(1).getInt(1))
            );
        }

        @Test
        @DisplayName("キーは書いた順に並ぶ")
        void keyOrder() {
            JsonObject obj = JsonParser.parse("{\"b\":1,\"a\":2,\"c\":3}");
            assertEquals("[b, a, c]", obj.keySet().toString());
        }

        @Test
        @DisplayName("前後と記号の間の空白・タブ・改行は読み飛ばす")
        void whitespace() {
            JsonObject obj = JsonParser.parse(" \t\r\n{ \"a\" :\n[ 1 ,\t2 ] } \n");
            assertEquals(2, obj.getArray("a").size());
        }

        @Test
        @DisplayName("数は整数ならLong、小数や指数を含めばDoubleになる")
        void numbers() {
            JsonArray a = JsonParser.parseArray("[0, -0, 42, 1.25, -2.5e3, 1E2, 9223372036854775807, 9223372036854775808]");
            assertAll(
                () -> assertEquals(0L, a.get(0)),
                () -> assertEquals(0L, a.get(1)),
                () -> assertEquals(42L, a.get(2)),
                () -> assertEquals(1.25, a.get(3)),
                () -> assertEquals(-2500.0, a.get(4)),
                () -> assertEquals(100.0, a.get(5)),
                () -> assertEquals(Long.MAX_VALUE, a.get(6)),
                // longに入らない整数はDoubleになる
                () -> assertEquals(9.223372036854775808E18, a.get(7))
            );
        }

        @Test
        @DisplayName("parseValueは、オブジェクト以外の値だけの入力も解析できる")
        void topLevelValues() {
            assertAll(
                () -> assertEquals("abc", JsonParser.parseValue("\"abc\"")),
                () -> assertEquals(42L, JsonParser.parseValue(" 42 ")),
                () -> assertEquals(Boolean.TRUE, JsonParser.parseValue("true")),
                () -> assertNull(JsonParser.parseValue("null"))
            );
        }
    }

    @Nested
    @DisplayName("空のオブジェクトと配列")
    class Empty {

        @Test
        @DisplayName("{}は要素が0個のオブジェクトになる")
        void emptyObject() {
            assertEquals(0, JsonParser.parse("{}").size());
            assertEquals(0, JsonParser.parse("{ \n }").size());
        }

        @Test
        @DisplayName("[]は要素が0個の配列になる")
        void emptyArray() {
            assertTrue(JsonParser.parseArray("[]").isEmpty());
            assertTrue(JsonParser.parseArray("[ ]").isEmpty());
        }

        @Test
        @DisplayName("空のオブジェクトと配列を値に持てる")
        void emptyAsValues() {
            JsonObject obj = JsonParser.parse("{\"o\":{},\"a\":[]}");
            assertEquals(0, obj.getObject("o").size());
            assertTrue(obj.getArray("a").isEmpty());
        }
    }

    @Nested
    @DisplayName("文字列のエスケープ")
    class Escapes {

        @Test
        @DisplayName("\\\" \\\\ \\/ \\b \\f \\n \\r \\tを元の文字に戻す")
        void simpleEscapes() {
            Object value = JsonParser.parseValue("\"a\\\"b\\\\c\\/d\\be\\ff\\ng\\rh\\ti\"");
            assertEquals("a\"b\\c/d\be\ff\ng\rh\ti", value);
        }

        @Test
        @DisplayName("\\uXXXXを、その番号の文字に戻す（大文字と小文字の16進数）")
        void unicodeEscapes() {
            assertEquals("Aあé", JsonParser.parseValue("\"\\u0041\\u3042\\u00E9\""));
        }

        @Test
        @DisplayName("サロゲートペアの2つの\\uで、BMP外の文字（𠮷）を表せる")
        void surrogatePair() {
            assertEquals("𠮷", JsonParser.parseValue("\"\\ud842\\udfb7\""));
        }

        @Test
        @DisplayName("日本語はエスケープしなくても読める")
        void unescapedNonAscii() {
            assertEquals("日本語", JsonParser.parse("{\"k\":\"日本語\"}").getString("k"));
        }
    }

    @Nested
    @DisplayName("不正なJSON")
    class InvalidJson {

        // 例外が起き、メッセージに理由が入り、場所が合っていることを確かめる
        private void assertInvalid(String json, int position, String reason) {
            JsonException e = assertThrows(JsonException.class, () -> JsonParser.parseValue(json));
            assertAll(
                () -> assertEquals(position, e.getPosition(), "場所"),
                () -> assertTrue(e.getMessage().contains(reason), "メッセージ: " + e.getMessage())
            );
        }

        @Test
        @DisplayName("空の入力と空白だけの入力")
        void empty() {
            assertInvalid("", 0, "値がありません");
            assertInvalid("   ", 3, "値がありません");
        }

        @Test
        @DisplayName("閉じていない括弧と文字列")
        void unclosed() {
            assertInvalid("{\"a\":1", 6, "途中で終わっています");
            assertInvalid("[1,2", 4, "途中で終わっています");
            assertInvalid("\"abc", 0, "文字列が閉じていません");
        }

        @Test
        @DisplayName("最後の要素のあとのカンマ")
        void trailingComma() {
            assertInvalid("[1,2,]", 5, "最後の要素のあとにカンマは書けません");
            assertInvalid("{\"a\":1,}", 7, "最後の要素のあとにカンマは書けません");
        }

        @Test
        @DisplayName("区切りの記号がない・違う")
        void missingSeparators() {
            assertInvalid("{\"a\" 1}", 5, "':'が必要です");
            assertInvalid("{\"a\":1 \"b\":2}", 7, "','か'}'が必要です");
            assertInvalid("[1 2]", 3, "','か']'が必要です");
        }

        @Test
        @DisplayName("キーを\"で囲んでいない")
        void unquotedKey() {
            assertInvalid("{name:\"John\"}", 1, "キーは\"で囲んだ文字列で書きます");
            assertInvalid("{'name':1}", 1, "キーは\"で囲んだ文字列で書きます");
        }

        @Test
        @DisplayName("JSONにない値の書き方")
        void unknownValues() {
            assertInvalid("[undefined]", 1, "予期しない文字'u'があります");
            assertInvalid("[True]", 1, "予期しない文字'T'があります");
            assertInvalid("[nul]", 1, "予期しない文字'n'があります");
        }

        @Test
        @DisplayName("数の誤った書き方")
        void badNumbers() {
            assertInvalid("01", 1, "先頭に0を付けた数は書けません");
            assertInvalid("[.5]", 1, "予期しない文字'.'があります");
            assertInvalid("1.", 2, "小数点のあとに数字が必要です");
            assertInvalid("-", 1, "'-'のあとに数字が必要です");
            assertInvalid("1e", 2, "指数の部分に数字が必要です");
            assertInvalid("1e400", 0, "数が大きすぎます");
        }

        @Test
        @DisplayName("文字列の誤ったエスケープと、エスケープしていない制御文字")
        void badStrings() {
            assertInvalid("\"a\\x\"", 2, "不正なエスケープ\\xがあります");
            assertInvalid("\"\\u12G4\"", 1, "\\uのあとには16進数4桁が必要です");
            assertInvalid("\"\\u12\"", 1, "\\uのあとには16進数4桁が必要です");
            assertInvalid("\"改\n行\"", 2, "制御文字");
        }

        @Test
        @DisplayName("値のあとの余分な文字")
        void trailingCharacters() {
            assertInvalid("{} {}", 3, "値のあとに余分な文字があります");
            assertInvalid("truex", 4, "値のあとに余分な文字があります");
        }

        @Test
        @DisplayName("重複したキー")
        void duplicateKey() {
            assertInvalid("{\"a\":1,\"a\":2}", 7, "キー\"a\"が重複しています");
        }

        @Test
        @DisplayName("512段を超える入れ子")
        void tooDeep() {
            String ok = "[".repeat(512) + "]".repeat(512);
            assertEquals(1, JsonParser.parseArray(ok).size());
            assertInvalid("[".repeat(513) + "]".repeat(513), 512, "入れ子が深すぎます");
        }

        @Test
        @DisplayName("parseはオブジェクト以外、parseArrayは配列以外を受け付けない")
        void wrongTopLevel() {
            JsonException e1 = assertThrows(JsonException.class, () -> JsonParser.parse("[1]"));
            assertTrue(e1.getMessage().contains("オブジェクトではありません（配列）"));
            JsonException e2 = assertThrows(JsonException.class, () -> JsonParser.parseArray("{}"));
            assertTrue(e2.getMessage().contains("配列ではありません（オブジェクト）"));
        }

        @Test
        @DisplayName("nullを渡すとNullPointerExceptionになる")
        void nullInput() {
            assertThrows(NullPointerException.class, () -> JsonParser.parse(null));
        }

        @Test
        @DisplayName("メッセージの先頭に、1から数えた場所を付ける")
        void messageHasPosition() {
            JsonException e = assertThrows(JsonException.class, () -> JsonParser.parse("{\"a\":?}"));
            assertEquals("6文字目: 予期しない文字'?'があります", e.getMessage());
            assertFalse(e.getPosition() < 0);
        }
    }
}
