// 第2章：+=は結果を左辺の型へ自動で変換する
public class ByteCompound {
    public static void main(String[] args) {
        byte b = 127;
        b += 1;
        System.out.println("b += 1: " + b);
        byte c = 10;
        c = (byte) (c + 1);
        System.out.println("cast: " + c);
    }
}
