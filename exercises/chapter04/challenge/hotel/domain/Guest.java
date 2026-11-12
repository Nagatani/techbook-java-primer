package hotel.domain;

/**
 * 宿泊客を表すクラス
 * 名前と電話番号は作るときに確かめ、あとから変えられないようにsetterを用意しない
 */
public class Guest {
    private String name;
    private String phone;

    public Guest(String name, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("名前を指定してください");
        }
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("電話番号は数字とハイフンで10〜13文字にしてください: " + phone);
        }
        this.name = name;
        this.phone = phone;
    }

    // 電話番号を省いたとき（電話で受けた予約など）は、未登録として扱う
    public Guest(String name) {
        this(name, "000-0000-0000");
    }

    private static boolean isValidPhone(String phone) {
        if (phone == null || phone.length() < 10 || phone.length() > 13) {
            return false;
        }
        for (int i = 0; i < phone.length(); i++) {
            char c = phone.charAt(i);
            if (!(c >= '0' && c <= '9') && c != '-') {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
