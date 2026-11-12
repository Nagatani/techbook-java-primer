/**
 * 住所（課題4の解答例）
 */
public record Address(String street, String city, String postalCode, String country) {

    public Address {
        if (isBlank(street) || isBlank(city) || isBlank(postalCode) || isBlank(country)) {
            throw new IllegalArgumentException("住所の項目はすべて必須です");
        }
        for (int i = 0; i < postalCode.length(); i++) {
            char c = postalCode.charAt(i);
            if (!Character.isDigit(c) && c != '-') {
                throw new IllegalArgumentException("郵便番号は数字とハイフンで書きます: " + postalCode);
            }
        }
    }

    // コンパクトコンストラクターから呼ぶので、staticにする
    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public String getFormattedAddress() {
        return postalCode + " " + city + "\n" + street + "\n" + country;
    }

    public boolean isJapanese() {
        return country.equals("Japan") || country.equals("日本");
    }
}
