public class TypeConversionExample {
    public static void main(String[] args) {
        // 暗黙的な型変換（拡大変換）
        int i = 100;
        long l = i;        // OK: int → long（32ビット → 64ビット）
        double d = i;      // OK: int → double（整数 → 浮動小数点）
        
        IO.println("int: " + i);     // 100
        IO.println("long: " + l);    // 100
        IO.println("double: " + d);  // 100.0
        
        // 明示的な型変換（縮小変換）
        double pi = 3.14159;
        int truncated = (int) pi;  // 3（小数部分は切り捨て）
        
        // オーバーフローの例
        int largeInt = 130;
        byte smallByte = (byte) largeInt;  // -126（オーバーフロー）
        
        IO.println("元の値: " + largeInt);
        IO.println("byte変換後: " + smallByte);
        
        // 文字列との変換
        String str = "123";
        int num = Integer.parseInt(str);     // 文字列→整数
        String str2 = String.valueOf(num);   // 整数→文字列
        IO.println("文字列から変換した整数: " + (num + 1));  // 124
        IO.println("整数から変換した文字列: " + (str2 + 4)); // 1234
    }
}
