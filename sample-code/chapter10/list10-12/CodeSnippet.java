/**
 * リスト10-12
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (326行目)
 */

Map<String, String> map = new HashMap<>();

// 追加・更新
map.put("JP", "日本");
map.put("US", "アメリカ");

// 取得
String country = map.get("JP");
String defaultValue = map.getOrDefault("UK", "不明");

// 削除
map.remove("US");

// 存在確認
boolean hasKey = map.containsKey("JP");
boolean hasValue = map.containsValue("日本");

// すべてのキー・値を処理
for (String key : map.keySet()) {
    System.out.println(key + ": " + map.get(key));
}

// エントリーセットで処理（より効率的）
for (Map.Entry<String, String> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}