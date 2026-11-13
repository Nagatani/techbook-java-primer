/**
 * 標準APIだけで書いた、小さなJSONライブラリ「SimpleJSON」です。
 *
 * <p>主なクラスは次の4つです。
 * <ul>
 *   <li>{@link com.example.json.JsonParser}: JSONの文字列を解析する</li>
 *   <li>{@link com.example.json.JsonObject}・{@link com.example.json.JsonArray}: JSONのオブジェクトと配列を表す。値を型を指定して取り出す</li>
 *   <li>{@link com.example.json.JsonBuilder}: Javaの値からJSONの文字列を作る</li>
 * </ul>
 *
 * <p>失敗はすべて非検査例外の{@link com.example.json.JsonException}で知らせます。
 * どのメソッドも、引数に{@code null}を渡せると書いていないものは、{@code null}を渡すと{@code NullPointerException}を投げます。
 *
 * <p>使用例:
 * {@snippet :
 * // JSONの解析
 * JsonObject obj = JsonParser.parse("{\"name\":\"John\",\"age\":30}");
 * String name = obj.getString("name");
 * int age = obj.getInt("age");
 *
 * // JSONの生成
 * JsonObject json = new JsonObject()
 *     .put("name", "John")
 *     .put("age", 30)
 *     .put("skills", new JsonArray()
 *         .add("Java")
 *         .add("Python"));
 * String result = json.toString();   // {"name":"John","age":30,"skills":["Java","Python"]}
 * }
 *
 * @since 1.0
 */
package com.example.json;
