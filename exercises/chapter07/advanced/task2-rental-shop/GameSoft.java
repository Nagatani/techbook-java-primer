/**
 * ゲームソフト。レンタルも販売もできるので、2つのインターフェイスを実装する
 */
public class GameSoft implements Rentable, Sellable {
    private final String title;
    private final int price;

    public GameSoft(String title, int price) {
        this.title = title;
        this.price = price;
    }

    // getTitleは両方のインターフェイスにあるが、どちらも抽象メソッドなので1つ書けばよい
    @Override
    public String getTitle() {
        return title;
    }

    // レンタル料金は販売価格の5%（10円未満は切り捨て）
    @Override
    public int dailyFee() {
        return price * 5 / 100 / 10 * 10;
    }

    @Override
    public int price() {
        return price;
    }

    // describeは両方のインターフェイスにdefaultメソッドとしてあるので、
    // オーバーライドしないとコンパイルエラーになる。両方の結果をつなげる
    @Override
    public String describe() {
        return Rentable.super.describe() + " / " + Sellable.super.describe();
    }
}
