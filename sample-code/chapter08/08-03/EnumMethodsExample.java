public class EnumMethodsExample {
    public static void main(String[] args) {
        // すべての曜日を出力
        for (DayOfWeek day : DayOfWeek.values()) {
            System.out.println(day.ordinal() + ": " + day.name());
        }
        
        // 文字列からenumへの変換
        DayOfWeek friday = DayOfWeek.valueOf("FRIDAY");
        System.out.println("金曜日: " + friday);
    }
}
