/**
 * 第7章 チャレンジ課題: コンビニのレジ（解答例）
 *
 * 商品マスターの行からファクトリで商品を作り、レジで読み取ってレシートを出します。
 *
 * 学習ポイント:
 * - 抽象クラスItemと抽象メソッド（税率・種類）
 * - 値引きの能力を表すインターフェイスDiscountableと、そのdefaultメソッド・staticメソッド
 * - 抽象クラスを継承しつつインターフェイスも実装するクラス（Food）
 * - 生成の手順を隠すファクトリ（ItemFactory）
 */
public class RegisterApp {
    public static void main(String[] args) {
        String[] master = {
            "食品,F01,幕の内弁当,550,30",
            "食品,F02,おにぎり 鮭,160",
            "飲料,D01,緑茶 500ml,140",
            "酒類,D02,缶ビール 350ml,220",
            "日用品,G01,ボールペン,120",
            "文具,S01,ノート,150"
        };

        IO.println("=== 商品の登録 ===");
        Register register = new Register(10);
        for (String line : master) {
            Item item = ItemFactory.create(line);
            if (item == null) {
                IO.println("登録できません: " + line);
                continue;
            }
            register.scan(item);
            IO.println(item.getCode() + " " + item.getName() + "（" + item.category() + "、税率" + item.taxRate() + "%）");
        }

        IO.println();
        register.printReceipt(2000);

        IO.println();
        IO.println("=== 値引き率の確認 ===");
        IO.println("30%: " + Discountable.isValidPercent(30));
        IO.println("80%: " + Discountable.isValidPercent(80));
        Item tooMuch = ItemFactory.create("食品,F03,サンドイッチ,300,80");
        IO.println(tooMuch.receiptLine() + " ← 範囲外の値引き率は使わない");
    }
}
