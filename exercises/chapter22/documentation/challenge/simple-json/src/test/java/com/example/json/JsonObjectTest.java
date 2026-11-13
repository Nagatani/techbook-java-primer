package com.example.json;

import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JsonObjectとJsonArrayのテスト")
class JsonObjectTest {

    @Nested
    @DisplayName("値を入れる")
    class Put {

        @Test
        @DisplayName("putはthisを返すので、つなげて呼べる")
        void chaining() {
            JsonObject obj = new JsonObject();
            assertSame(obj, obj.put("a", 1));
            assertSame(obj, obj.put("b", 2).put("c", 3));
            assertEquals(3, obj.size());
        }

        @Test
        @DisplayName("同じキーにputすると値を置き換え、並び順は変わらない")
        void replace() {
            JsonObject obj = new JsonObject().put("a", 1).put("b", 2).put("a", 10);
            assertEquals(10, obj.getInt("a"));
            assertEquals("{\"a\":10,\"b\":2}", obj.toString());
        }

        @Test
        @DisplayName("intはLongに、floatはDoubleにそろえて持つ")
        void normalize() {
            JsonObject obj = new JsonObject().put("i", 30).put("f", 1.5f);
            assertEquals(30L, obj.get("i"));
            assertEquals(1.5, obj.get("f"));
            // 同じ値を解析した結果と等しくなる
            assertEquals(JsonParser.parse("{\"i\":30,\"f\":1.5}"), obj);
        }

        @Test
        @DisplayName("JSONで表せない値を入れるとJsonExceptionになる")
        void unsupportedValues() {
            JsonObject obj = new JsonObject();
            assertAll(
                () -> assertThrows(JsonException.class, () -> obj.put("nan", Double.NaN)),
                () -> assertThrows(JsonException.class, () -> obj.put("inf", Double.POSITIVE_INFINITY)),
                () -> assertThrows(JsonException.class, () -> obj.put("date", new java.util.Date())),
                () -> assertThrows(JsonException.class, () -> new JsonArray().add('c'))
            );
            assertEquals(0, obj.size());
        }

        @Test
        @DisplayName("nullのキーはNullPointerExceptionになり、nullの値は入れられる")
        void nulls() {
            JsonObject obj = new JsonObject();
            assertThrows(NullPointerException.class, () -> obj.put(null, 1));
            obj.put("n", null);
            assertTrue(obj.has("n"));
            assertTrue(obj.isNull("n"));
            assertFalse(obj.isNull("none"));
        }

        @Test
        @DisplayName("removeでキーを取り除ける")
        void remove() {
            JsonObject obj = new JsonObject().put("a", 1).put("b", 2).remove("a").remove("none");
            assertEquals(Set.of("b"), obj.keySet());
        }
    }

    @Nested
    @DisplayName("型を指定して取り出す")
    class Getters {

        private final JsonObject obj = JsonParser.parse("""
                {"s":"text","i":7,"big":3000000000,"d":2.5,"b":true,
                 "o":{"x":1},"a":[1,"two",3.0,false,{},[]]}
                """);

        @Test
        @DisplayName("値の型に合ったgetメソッドで取り出せる")
        void matchingTypes() {
            assertAll(
                () -> assertEquals("text", obj.getString("s")),
                () -> assertEquals(7, obj.getInt("i")),
                () -> assertEquals(3000000000L, obj.getLong("big")),
                () -> assertEquals(2.5, obj.getDouble("d")),
                () -> assertEquals(7.0, obj.getDouble("i")),
                () -> assertTrue(obj.getBoolean("b")),
                () -> assertEquals(1, obj.getObject("o").getInt("x")),
                () -> assertEquals(6, obj.getArray("a").size())
            );
        }

        @Test
        @DisplayName("型が違うと、キーと実際の型をメッセージに入れたJsonExceptionになる")
        void wrongType() {
            JsonException e = assertThrows(JsonException.class, () -> obj.getInt("s"));
            assertEquals("キー\"s\"の値は整数ではありません（文字列）", e.getMessage());
            assertEquals(-1, e.getPosition());
            assertAll(
                () -> assertThrows(JsonException.class, () -> obj.getString("i")),
                () -> assertThrows(JsonException.class, () -> obj.getInt("d")),
                () -> assertThrows(JsonException.class, () -> obj.getBoolean("s")),
                () -> assertThrows(JsonException.class, () -> obj.getObject("a")),
                () -> assertThrows(JsonException.class, () -> obj.getArray("o"))
            );
        }

        @Test
        @DisplayName("intの範囲を超える整数をgetIntで取り出すとJsonExceptionになる")
        void intOverflow() {
            JsonException e = assertThrows(JsonException.class, () -> obj.getInt("big"));
            assertEquals("キー\"big\"の値はintの範囲を超えています（3000000000）", e.getMessage());
        }

        @Test
        @DisplayName("ないキーを取り出すとJsonExceptionになる")
        void missingKey() {
            JsonException e = assertThrows(JsonException.class, () -> obj.getString("none"));
            assertEquals("キー\"none\"がありません", e.getMessage());
        }

        @Test
        @DisplayName("配列からも位置を指定して型ごとに取り出せ、範囲外ならJsonExceptionになる")
        void arrayGetters() {
            JsonArray a = obj.getArray("a");
            assertAll(
                () -> assertEquals(1, a.getInt(0)),
                () -> assertEquals(1L, a.getLong(0)),
                () -> assertEquals("two", a.getString(1)),
                () -> assertEquals(3.0, a.getDouble(2)),
                () -> assertFalse(a.getBoolean(3)),
                () -> assertEquals(0, a.getObject(4).size()),
                () -> assertTrue(a.getArray(5).isEmpty())
            );
            JsonException e = assertThrows(JsonException.class, () -> a.get(6));
            assertEquals("位置6は範囲外です（要素は6個）", e.getMessage());
            JsonException e2 = assertThrows(JsonException.class, () -> a.getString(0));
            assertEquals("位置0の値は文字列ではありません（数値）", e2.getMessage());
        }
    }

    @Nested
    @DisplayName("中身を外から壊せない")
    class Encapsulation {

        @Test
        @DisplayName("keySetは変更できない")
        void keySetIsUnmodifiable() {
            JsonObject obj = new JsonObject().put("a", 1);
            assertThrows(UnsupportedOperationException.class, () -> obj.keySet().remove("a"));
        }

        @Test
        @DisplayName("配列のイテレーターで要素を消せない")
        void iteratorIsUnmodifiable() {
            JsonArray a = new JsonArray().add(1).add(2);
            Iterator<Object> it = a.iterator();
            it.next();
            assertThrows(UnsupportedOperationException.class, it::remove);
        }

        @Test
        @DisplayName("拡張for文で、配列の値を順に取り出せる")
        void forEach() {
            StringBuilder sb = new StringBuilder();
            for (Object value : JsonParser.parseArray("[1,\"a\",null]")) {
                sb.append(value).append(' ');
            }
            assertEquals("1 a null ", sb.toString());
        }
    }

    @Nested
    @DisplayName("比較")
    class Equality {

        @Test
        @DisplayName("オブジェクトはキーの順に関係なく、同じ組なら等しい")
        void objectEquality() {
            JsonObject a = new JsonObject().put("x", 1).put("y", 2);
            JsonObject b = new JsonObject().put("y", 2).put("x", 1);
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
            assertNotEquals(a, new JsonObject().put("x", 1));
        }

        @Test
        @DisplayName("配列は順番が違えば等しくない。整数の1と小数の1.0も区別する")
        void arrayEquality() {
            assertEquals(new JsonArray().add(1).add(2), JsonParser.parseArray("[1,2]"));
            assertNotEquals(new JsonArray().add(1).add(2), JsonParser.parseArray("[2,1]"));
            assertNotEquals(JsonParser.parseArray("[1]"), JsonParser.parseArray("[1.0]"));
        }
    }
}
