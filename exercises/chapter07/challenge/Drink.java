/**
 * 飲み物。お茶やジュースは軽減税率8%、酒類は10%
 */
public class Drink extends Item {
    private final boolean alcohol;

    public Drink(String code, String name, int price, boolean alcohol) {
        super(code, name, price);
        this.alcohol = alcohol;
    }

    @Override
    public int taxRate() {
        return alcohol ? 10 : 8;
    }

    @Override
    public String category() {
        return alcohol ? "酒類" : "飲料";
    }
}
