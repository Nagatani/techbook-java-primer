package com.example.json;

/**
 * JSONの解析・生成・値の取り出しに失敗したときに投げる例外です。
 *
 * <p>次の場合に投げます。
 * <ul>
 *   <li>{@link JsonParser}が解析できない文字列を受け取ったとき（{@link #getPosition()}で場所がわかる）</li>
 *   <li>{@link JsonObject}・{@link JsonArray}から、ない値や型の違う値を取り出そうとしたとき</li>
 *   <li>JSONで表せない値（{@code NaN}や、対応していない型のオブジェクト）を入れようとしたとき</li>
 * </ul>
 *
 * <p>非検査例外なので、{@code try}で囲まなくても呼び出せます。
 * 利用者の入力を解析するときは、この例外を受け止めて、メッセージを表示してください。
 *
 * @since 1.0
 */
public class JsonException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 解析に失敗した場所（先頭を0とする文字の位置）。解析以外の失敗では-1。
     */
    private final int position;

    /**
     * 解析以外の失敗を表す例外を作ります。
     *
     * @param message 失敗の理由
     */
    public JsonException(String message) {
        super(message);
        this.position = -1;
    }

    /**
     * 解析の失敗を表す例外を作ります。
     * メッセージの先頭には「5文字目: 」のように、失敗した場所（1から数える）を付けます。
     *
     * @param message 失敗の理由
     * @param position 失敗した場所（先頭を0とする文字の位置）
     */
    public JsonException(String message, int position) {
        super("%d文字目: %s".formatted(position + 1, message));
        this.position = position;
    }

    /**
     * 解析に失敗した場所を返します。
     *
     * @return 先頭を0とする文字の位置。解析以外の失敗では-1
     */
    public int getPosition() {
        return position;
    }
}
