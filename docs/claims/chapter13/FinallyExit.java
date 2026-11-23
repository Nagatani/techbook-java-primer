// 第13章：tryのなかでSystem.exitを呼ぶと、finallyは実行されない
public class FinallyExit {
    public static void main(String[] args) {
        try {
            System.out.println("try");
            if (args.length > 0) System.exit(0);
            throw new RuntimeException("x");
        } catch (RuntimeException e) {
            System.out.println("catch");
        } finally {
            System.out.println("finally");
        }
    }
}
