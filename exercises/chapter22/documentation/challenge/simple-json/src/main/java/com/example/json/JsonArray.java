package com.example.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * JSONの配列（{@code ["Java","Python"]}のような、値の並び）を表します。
 *
 * <p>値に使える型は{@link JsonObject}と同じです。
 * 拡張for文で、入っている値を順に取り出せます（取り出した値は{@link JsonObject#get(String)}と同じ型）。
 *
 * <p>使用例:
 * {@snippet :
 * JsonArray skills = new JsonArray()
 *     .add("Java")
 *     .add("Python");
 * String first = skills.getString(0);   // "Java"
 * String text = skills.toString();      // ["Java","Python"]
 * }
 *
 * <p>このクラスはスレッドセーフではありません。
 *
 * @see JsonParser#parseArray(String)
 * @since 1.0
 */
public final class JsonArray implements Iterable<Object> {

    private final List<Object> elements = new ArrayList<>();

    /**
     * 空の配列を作ります。
     */
    public JsonArray() {
    }

    /**
     * 末尾に値を加えます。
     *
     * @param value 値（使える型は{@link JsonObject}の説明を参照）
     * @return この配列（{@code add}をつなげて呼べる）
     * @throws JsonException {@code value}がJSONで表せない値のとき
     */
    public JsonArray add(Object value) {
        elements.add(JsonValues.normalize(value));
        return this;
    }

    /**
     * 値をそのまま返します。
     *
     * @param index 位置（先頭は0）
     * @return 値
     * @throws JsonException 位置が範囲外のとき
     */
    public Object get(int index) {
        if (index < 0 || index >= elements.size()) {
            throw new JsonException("位置%dは範囲外です（要素は%d個）".formatted(index, elements.size()));
        }
        return elements.get(index);
    }

    /**
     * 文字列の値を返します。
     *
     * @param index 位置（先頭は0）
     * @return 値
     * @throws JsonException 位置が範囲外のとき、または値が文字列でないとき
     */
    public String getString(int index) {
        return JsonValues.cast(get(index), String.class, "文字列", label(index));
    }

    /**
     * 整数の値を{@code int}で返します。
     *
     * @param index 位置（先頭は0）
     * @return 値
     * @throws JsonException 位置が範囲外のとき、値が整数でないとき、または{@code int}の範囲を超えるとき
     */
    public int getInt(int index) {
        return JsonValues.toInt(get(index), label(index));
    }

    /**
     * 整数の値を{@code long}で返します。
     *
     * @param index 位置（先頭は0）
     * @return 値
     * @throws JsonException 位置が範囲外のとき、または値が整数でないとき
     */
    public long getLong(int index) {
        return JsonValues.toLong(get(index), label(index));
    }

    /**
     * 数値を{@code double}で返します。
     *
     * @param index 位置（先頭は0）
     * @return 値
     * @throws JsonException 位置が範囲外のとき、または値が数値でないとき
     */
    public double getDouble(int index) {
        return JsonValues.toDouble(get(index), label(index));
    }

    /**
     * 真偽値を返します。
     *
     * @param index 位置（先頭は0）
     * @return 値
     * @throws JsonException 位置が範囲外のとき、または値が真偽値でないとき
     */
    public boolean getBoolean(int index) {
        return JsonValues.cast(get(index), Boolean.class, "真偽値", label(index));
    }

    /**
     * 入れ子のオブジェクトを返します。
     *
     * @param index 位置（先頭は0）
     * @return 値
     * @throws JsonException 位置が範囲外のとき、または値がオブジェクトでないとき
     */
    public JsonObject getObject(int index) {
        return JsonValues.cast(get(index), JsonObject.class, "オブジェクト", label(index));
    }

    /**
     * 入れ子の配列を返します。
     *
     * @param index 位置（先頭は0）
     * @return 値
     * @throws JsonException 位置が範囲外のとき、または値が配列でないとき
     */
    public JsonArray getArray(int index) {
        return JsonValues.cast(get(index), JsonArray.class, "配列", label(index));
    }

    /**
     * 要素の数を返します。
     *
     * @return 要素の数
     */
    public int size() {
        return elements.size();
    }

    /**
     * 要素がないかを調べます。
     *
     * @return 要素がなければ{@code true}
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * 値を先頭から順に返すイテレーターを返します。
     * イテレーターの{@code remove}は使えません。
     *
     * @return イテレーター
     */
    @Override
    public Iterator<Object> iterator() {
        return Collections.unmodifiableList(elements).iterator();
    }

    /**
     * 空白を入れずに、1行のJSONの文字列にします。
     *
     * @return JSONの文字列（例: {@code ["Java","Python"]}）
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
     * 同じ値が同じ順に並んでいるかを比べます。
     *
     * @param obj 比べる相手
     * @return 同じ並びの{@code JsonArray}なら{@code true}
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonArray other && elements.equals(other.elements);
    }

    /**
     * {@link #equals(Object)}と矛盾しないハッシュ値を返します。
     *
     * @return ハッシュ値
     */
    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    /** {@link JsonBuilder}が出力に使う、中身の並びです。 */
    List<Object> elements() {
        return elements;
    }

    private static String label(int index) {
        return "位置%dの値".formatted(index);
    }
}
