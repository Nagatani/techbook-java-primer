package hotel.domain;

/**
 * 客室を表すクラス
 *
 * 1か月（1日〜31日）のうち、どの日が予約で埋まっているかを持つ。
 * 予約で埋める・空ける処理（occupy・release）はパッケージプライベートにして、
 * 同じパッケージのHotelクラスからしか呼べないようにする。
 * hotel.appパッケージのプログラムは、Hotelを通さずに客室を埋められない。
 */
public class Room {
    public static final int LAST_DAY = 31;  // 予約を受け付ける最後の日

    private int number;
    private String type;
    private int pricePerNight;
    private boolean[] occupied;  // occupied[日]がtrueなら、その日の夜は埋まっている（0番は使わない）

    public Room(int number, String type, int pricePerNight) {
        if (number <= 0) {
            throw new IllegalArgumentException("部屋番号は1以上にしてください: " + number);
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("部屋の種類を指定してください");
        }
        if (pricePerNight <= 0) {
            throw new IllegalArgumentException("1泊の料金は1円以上にしてください: " + pricePerNight);
        }
        this.number = number;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.occupied = new boolean[LAST_DAY + 1];
    }

    // 料金を省いたときは、種類ごとの標準の料金にする
    public Room(int number, String type) {
        this(number, type, standardPrice(type));
    }

    // 種類ごとの標準の料金。オブジェクトの状態を使わないのでstaticメソッドにする
    private static int standardPrice(String type) {
        if ("シングル".equals(type)) {
            return 8000;
        } else if ("ダブル".equals(type)) {
            return 12000;
        } else if ("スイート".equals(type)) {
            return 30000;
        }
        throw new IllegalArgumentException("標準の料金がない種類です: " + type);
    }

    public int getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public int getPricePerNight() {
        return pricePerNight;
    }

    // checkIn日から nights泊のあいだ、すべての夜が空いているか
    public boolean isAvailable(int checkIn, int nights) {
        if (!isValidStay(checkIn, nights)) {
            return false;
        }
        for (int day = checkIn; day < checkIn + nights; day++) {
            if (occupied[day]) {
                return false;
            }
        }
        return true;
    }

    // 泊まる期間が1日〜LAST_DAYの範囲に収まっているか
    public static boolean isValidStay(int checkIn, int nights) {
        return checkIn >= 1 && nights >= 1 && checkIn + nights - 1 <= LAST_DAY;
    }

    // 予約で埋める（同じパッケージのHotelだけが呼ぶ）
    void occupy(int checkIn, int nights) {
        for (int day = checkIn; day < checkIn + nights; day++) {
            occupied[day] = true;
        }
    }

    // 取り消しで空ける（同じパッケージのHotelだけが呼ぶ）
    void release(int checkIn, int nights) {
        for (int day = checkIn; day < checkIn + nights; day++) {
            occupied[day] = false;
        }
    }

    public String describe() {
        return number + "号室（" + type + "、1泊" + pricePerNight + "円）";
    }
}
