package com.example.json;

/**
 * JSONの文字列を解析して、{@link JsonObject}・{@link JsonArray}などのJavaの値にします。
 *
 * <p>RFC 8259のJSONの書き方に従います。値は次のJavaの型になります。
 * <table>
 *   <caption>JSONの値とJavaの型</caption>
 *   <tr><th>JSON</th><th>Java</th></tr>
 *   <tr><td>オブジェクト</td><td>{@link JsonObject}</td></tr>
 *   <tr><td>配列</td><td>{@link JsonArray}</td></tr>
 *   <tr><td>文字列</td><td>{@code String}</td></tr>
 *   <tr><td>整数（{@code long}の範囲）</td><td>{@code Long}</td></tr>
 *   <tr><td>小数・指数を含む数、{@code long}に入らない整数</td><td>{@code Double}</td></tr>
 *   <tr><td>{@code true}・{@code false}</td><td>{@code Boolean}</td></tr>
 *   <tr><td>{@code null}</td><td>{@code null}</td></tr>
 * </table>
 *
 * <p>次の入力は、書き方の誤りとして{@link JsonException}を投げます。
 * 例外の{@link JsonException#getPosition()}で、誤りの場所がわかります。
 * <ul>
 *   <li>閉じていない括弧や文字列、最後の要素のあとのカンマ</li>
 *   <li>{@code 01}のように先頭に0を付けた数、{@code .5}のように整数部のない数</li>
 *   <li>文字列の中の改行などの制御文字（{@code \n}のようにエスケープする）</li>
 *   <li>1つのオブジェクトの中で重複したキー</li>
 *   <li>512段を超える入れ子</li>
 * </ul>
 *
 * <p>使用例:
 * {@snippet :
 * JsonObject obj = JsonParser.parse("{\"name\":\"John\",\"age\":30}");
 * String name = obj.getString("name");   // "John"
 * int age = obj.getInt("age");           // 30
 * }
 *
 * @since 1.0
 */
public final class JsonParser {

    /** 入れ子の深さの上限。深すぎる入力でスタックがあふれないようにする。 */
    private static final int MAX_DEPTH = 512;

    private final String text;
    private int pos;
    private int depth;

    private JsonParser(String text) {
        this.text = text;
    }

    /**
     * いちばん外側がオブジェクトのJSONを解析します。
     *
     * @param json JSONの文字列
     * @return 解析したオブジェクト
     * @throws NullPointerException {@code json}が{@code null}のとき
     * @throws JsonException 書き方に誤りがあるとき、またはいちばん外側がオブジェクトでないとき
     */
    public static JsonObject parse(String json) {
        Object value = parseValue(json);
        if (value instanceof JsonObject obj) {
            return obj;
        }
        throw new JsonException("いちばん外側がオブジェクトではありません（%s）".formatted(JsonValues.typeName(value)), 0);
    }

    /**
     * いちばん外側が配列のJSONを解析します。
     *
     * @param json JSONの文字列
     * @return 解析した配列
     * @throws NullPointerException {@code json}が{@code null}のとき
     * @throws JsonException 書き方に誤りがあるとき、またはいちばん外側が配列でないとき
     */
    public static JsonArray parseArray(String json) {
        Object value = parseValue(json);
        if (value instanceof JsonArray array) {
            return array;
        }
        throw new JsonException("いちばん外側が配列ではありません（%s）".formatted(JsonValues.typeName(value)), 0);
    }

    /**
     * どの種類の値でもよいJSONを解析します（{@code "abc"}や{@code 42}だけの入力も解析できる）。
     *
     * @param json JSONの文字列
     * @return 解析した値（型はクラスの説明の表を参照）
     * @throws NullPointerException {@code json}が{@code null}のとき
     * @throws JsonException 書き方に誤りがあるとき
     */
    public static Object parseValue(String json) {
        if (json == null) {
            throw new NullPointerException("解析する文字列がnullです");
        }
        JsonParser parser = new JsonParser(json);
        parser.skipWhitespace();
        Object value = parser.readValue();
        parser.skipWhitespace();
        if (parser.pos < json.length()) {
            throw parser.error("値のあとに余分な文字があります");
        }
        return value;
    }

    private Object readValue() {
        if (pos >= text.length()) {
            throw error("値がありません");
        }
        char c = text.charAt(pos);
        return switch (c) {
            case '{' -> readObject();
            case '[' -> readArray();
            case '"' -> readString();
            case 't' -> readLiteral("true", Boolean.TRUE);
            case 'f' -> readLiteral("false", Boolean.FALSE);
            case 'n' -> readLiteral("null", null);
            default -> {
                if (c == '-' || isDigit(c)) {
                    yield readNumber();
                }
                throw error("予期しない文字'%c'があります".formatted(c));
            }
        };
    }

    private JsonObject readObject() {
        enter();
        pos++; // {
        JsonObject obj = new JsonObject();
        skipWhitespace();
        if (peek() == '}') {
            pos++;
            leave();
            return obj;
        }
        while (true) {
            skipWhitespace();
            if (peek() != '"') {
                throw error(peek() == '}' ? "最後の要素のあとにカンマは書けません" : "キーは\"で囲んだ文字列で書きます");
            }
            int keyPos = pos;
            String key = readString();
            if (obj.has(key)) {
                throw new JsonException("キー\"%s\"が重複しています".formatted(key), keyPos);
            }
            skipWhitespace();
            expect(':');
            skipWhitespace();
            obj.put(key, readValue());
            skipWhitespace();
            char c = peek();
            if (c == ',') {
                pos++;
            } else if (c == '}') {
                pos++;
                leave();
                return obj;
            } else {
                throw error("','か'}'が必要です");
            }
        }
    }

    private JsonArray readArray() {
        enter();
        pos++; // [
        JsonArray array = new JsonArray();
        skipWhitespace();
        if (peek() == ']') {
            pos++;
            leave();
            return array;
        }
        while (true) {
            skipWhitespace();
            if (peek() == ']') {
                throw error("最後の要素のあとにカンマは書けません");
            }
            array.add(readValue());
            skipWhitespace();
            char c = peek();
            if (c == ',') {
                pos++;
            } else if (c == ']') {
                pos++;
                leave();
                return array;
            } else {
                throw error("','か']'が必要です");
            }
        }
    }

    private String readString() {
        int start = pos;
        pos++; // "
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (pos >= text.length()) {
                throw new JsonException("文字列が閉じていません", start);
            }
            char c = text.charAt(pos);
            if (c == '"') {
                pos++;
                return sb.toString();
            } else if (c == '\\') {
                sb.append(readEscape());
            } else if (c < 0x20) {
                throw error("文字列の中に制御文字があります（\\nのようにエスケープします）");
            } else {
                sb.append(c);
                pos++;
            }
        }
    }

    private char readEscape() {
        int start = pos;
        pos++; // \
        if (pos >= text.length()) {
            throw new JsonException("文字列が閉じていません", start);
        }
        char c = text.charAt(pos++);
        return switch (c) {
            case '"' -> '"';
            case '\\' -> '\\';
            case '/' -> '/';
            case 'b' -> '\b';
            case 'f' -> '\f';
            case 'n' -> '\n';
            case 'r' -> '\r';
            case 't' -> '\t';
            case 'u' -> readUnicodeEscape(start);
            default -> throw new JsonException("不正なエスケープ\\%cがあります".formatted(c), start);
        };
    }

    private char readUnicodeEscape(int start) {
        if (pos + 4 > text.length()) {
            throw new JsonException("\\uのあとには16進数4桁が必要です", start);
        }
        int code = 0;
        for (int i = 0; i < 4; i++) {
            int digit = Character.digit(text.charAt(pos + i), 16);
            if (digit < 0) {
                throw new JsonException("\\uのあとには16進数4桁が必要です", start);
            }
            code = code * 16 + digit;
        }
        pos += 4;
        return (char) code;
    }

    private Object readNumber() {
        int start = pos;
        if (peek() == '-') {
            pos++;
        }
        if (peek() == '0') {
            pos++;
            if (isDigit(peek())) {
                throw error("先頭に0を付けた数は書けません");
            }
        } else if (isDigit(peek())) {
            skipDigits();
        } else {
            throw error("'-'のあとに数字が必要です");
        }
        boolean integer = true;
        if (peek() == '.') {
            integer = false;
            pos++;
            if (!isDigit(peek())) {
                throw error("小数点のあとに数字が必要です");
            }
            skipDigits();
        }
        if (peek() == 'e' || peek() == 'E') {
            integer = false;
            pos++;
            if (peek() == '+' || peek() == '-') {
                pos++;
            }
            if (!isDigit(peek())) {
                throw error("指数の部分に数字が必要です");
            }
            skipDigits();
        }
        String number = text.substring(start, pos);
        if (integer) {
            try {
                return Long.parseLong(number);
            } catch (NumberFormatException e) {
                // longに入らない整数は、小数として扱う
            }
        }
        double d = Double.parseDouble(number);
        if (Double.isInfinite(d)) {
            throw new JsonException("数が大きすぎます", start);
        }
        return d;
    }

    private Object readLiteral(String word, Object value) {
        if (!text.startsWith(word, pos)) {
            throw error("予期しない文字'%c'があります".formatted(text.charAt(pos)));
        }
        pos += word.length();
        return value;
    }

    private void enter() {
        depth++;
        if (depth > MAX_DEPTH) {
            throw error("入れ子が深すぎます（%d段まで）".formatted(MAX_DEPTH));
        }
    }

    private void leave() {
        depth--;
    }

    private void expect(char c) {
        if (peek() != c) {
            throw error("'%c'が必要です".formatted(c));
        }
        pos++;
    }

    // 終わりに来たら、どの記号とも一致しない文字を返す
    private char peek() {
        return pos < text.length() ? text.charAt(pos) : '\0';
    }

    private void skipDigits() {
        while (isDigit(peek())) {
            pos++;
        }
    }

    private void skipWhitespace() {
        while (pos < text.length()) {
            char c = text.charAt(pos);
            if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                return;
            }
            pos++;
        }
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private JsonException error(String message) {
        if (pos >= text.length()) {
            return new JsonException("JSONが途中で終わっています（" + message + "）", pos);
        }
        return new JsonException(message, pos);
    }
}
