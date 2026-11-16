public class EnumExample {
    public static void main(String[] args) {
        DayOfWeek today = DayOfWeek.WEDNESDAY;
        
        IO.println("今日は " + today + " です。");
        // 列挙型のtoString()は既定で列挙子の名前を返すので、"WEDNESDAY"と表示される
        
        // 型安全なので、不正な値はコンパイルエラーになる
        // DayOfWeek tomorrow = "Thursday"; // エラー
    }
}
