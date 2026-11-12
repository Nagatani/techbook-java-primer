/**
 * カップのサイズ。表示用の記号・容量・追加料金をフィールドに持つ
 */
public enum Size {
    SHORT("S", 240, 0),
    TALL("T", 350, 50),
    GRANDE("G", 470, 100);

    private final String label;
    private final int milliliters;
    private final int extraCharge;

    Size(String label, int milliliters, int extraCharge) {
        this.label = label;
        this.milliliters = milliliters;
        this.extraCharge = extraCharge;
    }

    public String getLabel() {
        return label;
    }

    public int getMilliliters() {
        return milliliters;
    }

    public int getExtraCharge() {
        return extraCharge;
    }

    // 記号（"S"など）から列挙子を探す。見つからなければnull
    public static Size fromLabel(String label) {
        for (Size size : values()) {
            if (size.label.equals(label)) {
                return size;
            }
        }
        return null;
    }
}
