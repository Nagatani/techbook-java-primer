public class ZooApp {
    public static void main(String[] args) {
        Zoo zoo = new Zoo("みなと動物園", 10);

        IO.println("=== 動物の登録 ===");
        zoo.add(new Lion("レオ", 5, 190));
        zoo.add(new Elephant("ハナコ", 12, 4000));
        zoo.add(new Penguin("ピコ", 3, 15));
        zoo.add(new Parrot("ソラ", 2, 1.0));

        IO.println();
        IO.println("=== 1日の給餌の予定 ===");
        zoo.showFeedingPlan();

        IO.println();
        IO.println("=== 健康チェック ===");
        zoo.checkHealth(new double[] {38.5, 36.2, 43.0, 41.5});

        IO.println();
        IO.println("=== 集計 ===");
        zoo.showStatistics();
    }
}
