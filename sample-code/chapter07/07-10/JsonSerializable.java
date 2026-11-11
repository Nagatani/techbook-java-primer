public interface JsonSerializable {
    String toJson();
    
    // 複数のオブジェクトをJSONの配列にまとめるユーティリティメソッド
    static String serializeAll(JsonSerializable[] items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(items[i].toJson());
        }
        sb.append("]");
        return sb.toString();
    }
    
    // ユーティリティメソッド
    static String escapeJson(String input) {
        return input.replace("\\", "\\\\")  // 先に「\」自身をエスケープする
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}
