package com.example.json;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * JSONのオブジェクト（{@code {"name":"John","age":30}}のような、名前と値の組の集まり）を表します。
 *
 * <p>キーは入れた順に並び、{@link #toString()}もその順で出力します。
 * 値に使える型は次のとおりです。
 * <ul>
 *   <li>{@code String}</li>
 *   <li>整数（{@code int}・{@code long}など。中では{@code Long}として持つ）</li>
 *   <li>小数（{@code double}・{@code float}。中では{@code Double}として持つ。{@code NaN}と無限大は使えない）</li>
 *   <li>{@code Boolean}</li>
 *   <li>{@code JsonObject}と{@link JsonArray}（入れ子にできる）</li>
 *   <li>{@code null}（JSONの{@code null}）</li>
 * </ul>
 *
 * <p>使用例:
 * {@snippet :
 * JsonObject json = new JsonObject()
 *     .put("name", "John")
 *     .put("age", 30);
 * String name = json.getString("name");   // "John"
 * int age = json.getInt("age");           // 30
 * String text = json.toString();          // {"name":"John","age":30}
 * }
 *
 * <p>このクラスはスレッドセーフではありません。
 *
 * @see JsonParser#parse(String)
 * @since 1.0
 */
public final class JsonObject {

    private final Map<String, Object> members = new LinkedHashMap<>();

    /**
     * 空のオブジェクトを作ります。
     */
    public JsonObject() {
    }

    /**
     * キーと値の組を加えます。同じキーがあれば、値を置き換えます。
     *
     * @param key キー
     * @param value 値（使える型はクラスの説明を参照）
     * @return このオブジェクト（{@code put}をつなげて呼べる）
     * @throws NullPointerException {@code key}が{@code null}のとき
     * @throws JsonException {@code value}がJSONで表せない値のとき
     */
    public JsonObject put(String key, Object value) {
        if (key == null) {
            throw new NullPointerException("キーにnullは使えません");
        }
        members.put(key, JsonValues.normalize(value));
        return this;
    }

    /**
     * キーと値の組を取り除きます。キーがなければ何もしません。
     *
     * @param key キー
     * @return このオブジェクト
     */
    public JsonObject remove(String key) {
        members.remove(key);
        return this;
    }

    /**
     * キーがあるかを調べます。値が{@code null}の組も「ある」とみなします。
     *
     * @param key キー
     * @return キーがあれば{@code true}
     */
    public boolean has(String key) {
        return members.containsKey(key);
    }

    /**
     * キーがあり、その値が{@code null}かを調べます。
     *
     * @param key キー
     * @return 値が{@code null}なら{@code true}。キーがないときは{@code false}
     */
    public boolean isNull(String key) {
        return has(key) && members.get(key) == null;
    }

    /**
     * 値をそのまま返します。型がわからない値を調べるときに使います。
     *
     * @param key キー
     * @return 値（{@code String}・{@code Long}・{@code Double}・{@code Boolean}・{@code JsonObject}・{@code JsonArray}・{@code null}のどれか）
     * @throws JsonException キーがないとき
     */
    public Object get(String key) {
        if (!has(key)) {
            throw new JsonException("キー\"%s\"がありません".formatted(key));
        }
        return members.get(key);
    }

    /**
     * 文字列の値を返します。
     *
     * @param key キー
     * @return 値
     * @throws JsonException キーがないとき、または値が文字列でないとき
     */
    public String getString(String key) {
        return JsonValues.cast(get(key), String.class, "文字列", label(key));
    }

    /**
     * 整数の値を{@code int}で返します。
     *
     * @param key キー
     * @return 値
     * @throws JsonException キーがないとき、値が整数でないとき（{@code 1.5}など）、または{@code int}の範囲を超えるとき
     */
    public int getInt(String key) {
        return JsonValues.toInt(get(key), label(key));
    }

    /**
     * 整数の値を{@code long}で返します。
     *
     * @param key キー
     * @return 値
     * @throws JsonException キーがないとき、または値が整数でないとき
     */
    public long getLong(String key) {
        return JsonValues.toLong(get(key), label(key));
    }

    /**
     * 数値を{@code double}で返します。整数の値も小数として返します。
     *
     * @param key キー
     * @return 値
     * @throws JsonException キーがないとき、または値が数値でないとき
     */
    public double getDouble(String key) {
        return JsonValues.toDouble(get(key), label(key));
    }

    /**
     * 真偽値を返します。
     *
     * @param key キー
     * @return 値
     * @throws JsonException キーがないとき、または値が真偽値でないとき
     */
    public boolean getBoolean(String key) {
        return JsonValues.cast(get(key), Boolean.class, "真偽値", label(key));
    }

    /**
     * 入れ子のオブジェクトを返します。
     *
     * @param key キー
     * @return 値
     * @throws JsonException キーがないとき、または値がオブジェクトでないとき
     */
    public JsonObject getObject(String key) {
        return JsonValues.cast(get(key), JsonObject.class, "オブジェクト", label(key));
    }

    /**
     * 配列を返します。
     *
     * @param key キー
     * @return 値
     * @throws JsonException キーがないとき、または値が配列でないとき
     */
    public JsonArray getArray(String key) {
        return JsonValues.cast(get(key), JsonArray.class, "配列", label(key));
    }

    /**
     * キーの一覧を、入れた順で返します。
     *
     * @return 変更できないキーの集合
     */
    public Set<String> keySet() {
        return Collections.unmodifiableSet(members.keySet());
    }

    /**
     * キーと値の組の数を返します。
     *
     * @return 組の数
     */
    public int size() {
        return members.size();
    }

    /**
     * 空白を入れずに、1行のJSONの文字列にします。
     *
     * @return JSONの文字列（例: {@code {"name":"John","age":30}}）
     */
    @Override
    public String toString() {
        return JsonBuilder.toJson(this);
    }

    /**
     * 改行と字下げを入れた、読みやすいJSONの文字列にします。
     *
     * @param indent 1段の字下げに使う空白の数（0以上）
     * @return JSONの文字列
     * @throws IllegalArgumentException {@code indent}が負の数のとき
     */
    public String toString(int indent) {
        return JsonBuilder.toPrettyJson(this, indent);
    }

    /**
     * 同じキーと値の組を持つか（並び順は問わない）を比べます。
     *
     * @param obj 比べる相手
     * @return 同じ組を持つ{@code JsonObject}なら{@code true}
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonObject other && members.equals(other.members);
    }

    /**
     * {@link #equals(Object)}と矛盾しないハッシュ値を返します。
     *
     * @return ハッシュ値
     */
    @Override
    public int hashCode() {
        return members.hashCode();
    }

    /** {@link JsonBuilder}が出力に使う、中身の組です。 */
    Map<String, Object> members() {
        return members;
    }

    private static String label(String key) {
        return "キー\"%s\"の値".formatted(key);
    }
}
