import java.util.Arrays;

public class OverloadResolution {
    public static void test(int x) {
        IO.println("int: " + x);
    }
    
    public static void test(long x) {
        IO.println("long: " + x);
    }
    
    public static void test(Integer x) {
        IO.println("Integer: " + x);
    }
    
    public static void test(int... x) {
        IO.println("可変長引数: " + Arrays.toString(x));
    }
    
    public static void main(String[] args) {
        byte b = 10;
        test(b);      // int版が呼ばれる（自動拡大変換）
        test(10);     // int版が呼ばれる（完全一致）
        test(10L);    // long版が呼ばれる（完全一致）
        test(Integer.valueOf(10)); // Integer版が呼ばれる
        test(1, 2, 3); // 可変長引数版が呼ばれる
    }
}
