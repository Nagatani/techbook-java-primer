package com.example.json;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Javaの値からJSONの文字列を作ります。
 *
 * <p>{@link JsonObject#toString()}と{@link JsonArray#toString()}は、このクラスを使っています。
 * 文字列の中の{@code "}・{@code \}・改行などの制御文字はエスケープします。
 * 日本語などのASCII以外の文字は、そのまま出力します。
 *
 * <p>使用例:
 * {@snippet :
 * JsonBuilder.toJson("改行\nを含む");   // "改行\nを含む"（\nは2文字のエスケープになる）
 * JsonBuilder.toJson(42);              // 42
 *
 * JsonObject json = new JsonObject().put("tags", new JsonArray().add("a"));
 * JsonBuilder.toPrettyJson(json, 2);
 * // {
 * //   "tags": [
 * //     "a"
 * //   ]
 * // }
 * }
 *
 * @since 1.0
 */
public final class JsonBuilder {

    private JsonBuilder() {
    }

    /**
     * 値を、空白を入れない1行のJSONの文字列にします。
     *
     * @param value 値（使える型は{@link JsonObject}の説明を参照）
     * @return JSONの文字列
     * @throws JsonException {@code value}がJSONで表せない値のとき
     */
    public static String toJson(Object value) {
        StringBuilder out = new StringBuilder();
        write(out, JsonValues.normalize(value), -1, 0);
        return out.toString();
    }

    /**
     * 値を、改行と字下げを入れたJSONの文字列にします。
     * オブジェクトでは、キーと値の間を「{@code ": "}」のように1文字空けます。
     * 空のオブジェクトと配列は{@code {}}・{@code []}と書きます。
     *
     * @param value 値（使える型は{@link JsonObject}の説明を参照）
     * @param indent 1段の字下げに使う空白の数（0以上）
     * @return JSONの文字列
     * @throws IllegalArgumentException {@code indent}が負の数のとき
     * @throws JsonException {@code value}がJSONで表せない値のとき
     */
    public static String toPrettyJson(Object value, int indent) {
        if (indent < 0) {
            throw new IllegalArgumentException("字下げは0以上にします: " + indent);
        }
        StringBuilder out = new StringBuilder();
        write(out, JsonValues.normalize(value), indent, 0);
        return out.toString();
    }

    /**
     * 文字列を、前後に{@code "}を付け、必要な文字をエスケープしたJSONの文字列にします。
     *
     * @param text 文字列
     * @return JSONの文字列（例: {@code "say \"hi\""}）
     */
    public static String quote(String text) {
        StringBuilder out = new StringBuilder(text.length() + 2);
        writeString(out, text);
        return out.toString();
    }

    // indentが負の数なら1行で書く
    private static void write(StringBuilder out, Object value, int indent, int level) {
        if (value == null) {
            out.append("null");
        } else if (value instanceof String s) {
            writeString(out, s);
        } else if (value instanceof JsonObject obj) {
            writeObject(out, obj.members(), indent, level);
        } else if (value instanceof JsonArray array) {
            writeArray(out, array.elements(), indent, level);
        } else {
            // Long・Double・Boolean
            out.append(value);
        }
    }

    private static void writeObject(StringBuilder out, Map<String, Object> members, int indent, int level) {
        if (members.isEmpty()) {
            out.append("{}");
            return;
        }
        out.append('{');
        Iterator<Map.Entry<String, Object>> it = members.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> e = it.next();
            newLine(out, indent, level + 1);
            writeString(out, e.getKey());
            out.append(indent < 0 ? ":" : ": ");
            write(out, e.getValue(), indent, level + 1);
            if (it.hasNext()) {
                out.append(',');
            }
        }
        newLine(out, indent, level);
        out.append('}');
    }

    private static void writeArray(StringBuilder out, List<Object> elements, int indent, int level) {
        if (elements.isEmpty()) {
            out.append("[]");
            return;
        }
        out.append('[');
        for (int i = 0; i < elements.size(); i++) {
            newLine(out, indent, level + 1);
            write(out, elements.get(i), indent, level + 1);
            if (i < elements.size() - 1) {
                out.append(',');
            }
        }
        newLine(out, indent, level);
        out.append(']');
    }

    private static void newLine(StringBuilder out, int indent, int level) {
        if (indent >= 0) {
            out.append('\n').append(" ".repeat(indent * level));
        }
    }

    private static void writeString(StringBuilder out, String text) {
        out.append('"');
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\b' -> out.append("\\b");
                case '\f' -> out.append("\\f");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        // そのほかの制御文字は\\u0001の形にする
                        out.append("\\u%04x".formatted((int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        out.append('"');
    }
}
