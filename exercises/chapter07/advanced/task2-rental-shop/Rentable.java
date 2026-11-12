/**
 * レンタルできる商品が持つ能力（can-do）を表すインターフェイス
 */
public interface Rentable {
    // 一度に借りられる最長の日数（インターフェイスのフィールドは public static final になる）
    int MAX_DAYS = 7;

    String getTitle();

    // 1日あたりのレンタル料金。実装クラスで決める
    int dailyFee();

    // 指定した日数で借りられるか
    default boolean canRent(int days) {
        return days >= 1 && days <= MAX_DAYS;
    }

    // 日数分のレンタル料金。借りられない日数なら-1を返す
    default int rentalFee(int days) {
        if (!canRent(days)) {
            return -1;
        }
        return dailyFee() * days;
    }

    default String describe() {
        return "レンタル 1日" + dailyFee() + "円";
    }
}
