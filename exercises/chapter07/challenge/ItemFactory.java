/**
 * 商品マスターの1行から商品のオブジェクトを作るファクトリ（Simple Factory）
 *
 * 1行は「種類,商品コード,名前,税抜き価格」の形。食品だけは末尾に値引き率を書ける。
 * 呼び出し側はFoodやDrinkのコンストラクターを知らなくてよい。
 */
public class ItemFactory {
    private ItemFactory() {
    }

    // 種類がわからない行や、項目の数が合わない行にはnullを返す
    public static Item create(String line) {
        String[] f = line.split(",");
        if (f.length < 4) {
            return null;
        }
        String kind = f[0];
        String code = f[1];
        String name = f[2];
        int price = Integer.parseInt(f[3]);
        return switch (kind) {
            case "食品" -> new Food(code, name, price, f.length >= 5 ? Integer.parseInt(f[4]) : 0);
            case "飲料" -> new Drink(code, name, price, false);
            case "酒類" -> new Drink(code, name, price, true);
            case "日用品" -> new DailyGoods(code, name, price);
            default -> null;
        };
    }
}
