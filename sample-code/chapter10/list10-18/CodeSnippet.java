/**
 * リスト10-18
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (608行目)
 */

Iterator<String> iterator = cityList.iterator();
while (iterator.hasNext()) {
    String city = iterator.next();
    if (city.equals("Kyoto")) {
        iterator.remove(); // 安全に削除
    }
}