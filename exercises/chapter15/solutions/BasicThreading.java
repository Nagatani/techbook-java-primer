/**
 * スレッドを作って待つ（課題1の解答例）
 */
public class BasicThreading {
    // Threadを継承したスレッド: 範囲の合計を求める
    static class SumThread extends Thread {
        private final int from;
        private final int to;
        private long result;

        SumThread(String name, int from, int to) {
            super(name);
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            long sum = 0;
            for (int i = from; i <= to; i++) {
                sum += i;
            }
            result = sum;
        }

        long getResult() {
            return result;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        IO.println("メインスレッド: " + Thread.currentThread().getName());

        IO.println("\n--- Threadを継承する ---");
        SumThread first = new SumThread("前半", 1, 500_000);
        SumThread second = new SumThread("後半", 500_001, 1_000_000);
        IO.println("start前の状態: " + first.getState());
        first.start();
        second.start();
        first.join();
        second.join();
        IO.println("join後の状態: " + first.getState());
        IO.println(first.getName() + "の合計: " + first.getResult());
        IO.println(second.getName() + "の合計: " + second.getResult());
        IO.println("全体の合計: " + (first.getResult() + second.getResult()));

        IO.println("\n--- Runnableをラムダ式で渡す ---");
        String[] results = new String[3];
        Thread[] workers = new Thread[3];
        for (int i = 0; i < workers.length; i++) {
            int index = i;
            workers[i] = new Thread(() -> {
                results[index] = Thread.currentThread().getName() + " が " + (index + 1) + "番目の仕事を終えた";
            }, "作業者" + (i + 1));
            workers[i].start();
        }
        for (Thread worker : workers) {
            worker.join();
        }
        // 表示はjoinのあとにまとめて行うので、順序が決まる
        for (String r : results) {
            IO.println(r);
        }
        IO.println("\nメインスレッド終了");
    }
}
