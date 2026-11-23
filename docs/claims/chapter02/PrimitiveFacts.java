// 第2章：プリミティブ型の大きさ・範囲・既定値、文字列の同一性
public class PrimitiveFacts {
    static int intField;
    static double doubleField;
    static boolean booleanField;
    static char charField;

    public static void main(String[] args) {
        System.out.println("byte: " + Byte.SIZE + " " + Byte.MIN_VALUE + " " + Byte.MAX_VALUE);
        System.out.println("short: " + Short.SIZE + " " + Short.MIN_VALUE + " " + Short.MAX_VALUE);
        System.out.println("int: " + Integer.SIZE + " " + Integer.MIN_VALUE + " " + Integer.MAX_VALUE);
        System.out.println("long: " + Long.SIZE + " " + Long.MIN_VALUE + " " + Long.MAX_VALUE);
        System.out.println("float: " + Float.SIZE + " " + Float.MAX_VALUE);
        System.out.println("double: " + Double.SIZE + " " + Double.MAX_VALUE);
        System.out.println("char: " + Character.SIZE);
        // 有効桁数の目安（10進で何桁を正確に往復できるか）
        System.out.println("float 0.1f*3 = " + (0.1f * 3) + ", double 0.1*3 = " + (0.1 * 3));
        System.out.println("fields: " + intField + " " + doubleField + " " + booleanField + " "
                + (int) charField);
        int[] ints = new int[2];
        String[] strs = new String[2];
        System.out.println("arrays: " + ints[0] + " " + strs[0]);

        String s1 = "Java";
        String s2 = "Java";
        String s3 = new String("Java");
        System.out.println("literal==literal: " + (s1 == s2));
        System.out.println("literal==new: " + (s1 == s3));
        System.out.println("equals: " + s1.equals(s3));
        // 入力などから実行中に組み立てた文字列は、リテラルと同じオブジェクトにならない
        String built = new StringBuilder("Ja").append("va").toString();
        System.out.println("literal==built: " + (s1 == built));
    }
}
