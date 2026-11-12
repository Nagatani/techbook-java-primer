public class CommuteApp {
    public static void main(String[] args) {
        Transport[] transports = {
            new Walk(),
            new Bicycle(100),
            new Bus(),
            new Train(8),
        };

        IO.println("=== 通学の手段 ===");
        for (Transport t : transports) {
            IO.println("  " + t);
        }

        CommutePlanner planner = new CommutePlanner(transports);
        IO.println();
        IO.println("=== 比較 ===");
        planner.compare(1.5, 20);
        IO.println();
        planner.compare(6.0, 20);
        IO.println();
        planner.compare(15.0, 20);

        IO.println();
        IO.println("=== 公共交通の待ち時間 ===");
        planner.showWaitTimes();
    }
}
