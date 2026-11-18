/**
 * スレッドごとのスタックの大きさと、StackOverflowErrorまでの再帰の深さの関係を観察する。
 * 深さは、JITコンパイルの進み具合によって実行のたびに変わる。
 *
 *   javac StackDepth.java && java StackDepth
 *   java -Xss4m StackDepth     （-Xssはスレッドのスタックの大きさの既定値を変える）
 */
public class StackDepth {
    private static int depth;

    private static void recurse() {
        depth++;
        recurse();
    }

    /** 指定した大きさのスタックを持つスレッドで、StackOverflowErrorになるまで再帰する */
    static int measure(long stackSize) throws InterruptedException {
        int[] result = new int[1];
        Thread thread = new Thread(null, () -> {
            depth = 0;
            try {
                recurse();
            } catch (StackOverflowError e) {
                result[0] = depth;
            }
        }, "stack-" + stackSize, stackSize);
        thread.start();
        thread.join();
        return result[0];
    }

    public static void main(String[] args) throws InterruptedException {
        int small = measure(512 * 1024);
        int large = measure(4 * 1024 * 1024);
        IO.println("スタック512KBのスレッド: " + small + "段でStackOverflowError");
        IO.println("スタック4MBのスレッド: " + large + "段でStackOverflowError");
        IO.println("4MBのほうが深く呼べる: " + (large > small));
    }
}
