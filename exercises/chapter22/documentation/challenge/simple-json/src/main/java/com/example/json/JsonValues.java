package com.example.json;

/**
 * {@link JsonObject}と{@link JsonArray}が共通で使う、値の変換と型の検査です。
 * パッケージの外には公開しません。
 */
final class JsonValues {

    private JsonValues() {
    }

    /**
     * JSONの値として持てる形にそろえます。
     * 整数（{@code int}・{@code long}など）は{@code Long}に、小数（{@code float}・{@code double}）は{@code Double}にします。
     */
    static Object normalize(Object value) {
        if (value == null || value instanceof String || value instanceof Boolean
                || value instanceof JsonObject || value instanceof JsonArray) {
            return value;
        }
        if (value instanceof Integer || value instanceof Long
                || value instanceof Short || value instanceof Byte) {
            return ((Number) value).longValue();
        }
        if (value instanceof Double || value instanceof Float) {
            double d = ((Number) value).doubleValue();
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                throw new JsonException("NaNと無限大はJSONで表せません");
            }
            return d;
        }
        throw new JsonException("JSONの値にできない型です: " + value.getClass().getName());
    }

    /** エラーメッセージに使う、値の種類の名前を返します。 */
    static String typeName(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "文字列";
        } else if (value instanceof Number) {
            return "数値";
        } else if (value instanceof Boolean) {
            return "真偽値";
        } else if (value instanceof JsonObject) {
            return "オブジェクト";
        } else {
            return "配列";
        }
    }

    /**
     * 値が指定した型なら、その型にして返します。違えば{@link JsonException}を投げます。
     *
     * @param label エラーメッセージで値を指す言葉（「キー"age"の値」など）
     */
    static <T> T cast(Object value, Class<T> type, String expected, String label) {
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        throw new JsonException("%sは%sではありません（%s）".formatted(label, expected, typeName(value)));
    }

    static long toLong(Object value, String label) {
        return cast(value, Long.class, "整数", label);
    }

    static int toInt(Object value, String label) {
        long n = toLong(value, label);
        if (n < Integer.MIN_VALUE || n > Integer.MAX_VALUE) {
            throw new JsonException("%sはintの範囲を超えています（%d）".formatted(label, n));
        }
        return (int) n;
    }

    static double toDouble(Object value, String label) {
        return cast(value, Number.class, "数値", label).doubleValue();
    }
}
