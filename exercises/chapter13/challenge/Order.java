/** 1件の注文 */
public record Order(String orderId, String itemCode, int quantity, int lineNumber) {

    /** 「注文番号,商品コード,数量」の1行を注文に変換する */
    public static Order parse(String line, int lineNumber) throws InvalidOrderException {
        String[] fields = line.split(",");
        if (fields.length != 3) {
            throw new InvalidOrderException("項目の数が3つではありません: " + line, lineNumber);
        }
        int quantity;
        try {
            quantity = Integer.parseInt(fields[2].strip());
        } catch (NumberFormatException e) {
            throw new InvalidOrderException("数量が整数ではありません: " + fields[2], lineNumber, e);
        }
        if (quantity <= 0) {
            throw new InvalidOrderException("数量は1以上にしてください: " + quantity, lineNumber);
        }
        return new Order(fields[0].strip(), fields[1].strip(), quantity, lineNumber);
    }
}
