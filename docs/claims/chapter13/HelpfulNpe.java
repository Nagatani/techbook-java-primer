// 第13章：nullだった式を示すNullPointerExceptionのメッセージ（Java 15から既定で表示）
public class HelpfulNpe {
    public static void main(String[] args) {
        String str = args.length > 100 ? "x" : null;
        try {
            str.length();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }
}
