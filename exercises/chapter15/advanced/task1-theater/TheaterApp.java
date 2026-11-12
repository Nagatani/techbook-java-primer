import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TheaterApp {
    public static void main(String[] args) throws InterruptedException {
        Theater theater = new Theater(12);
        List<Request> requests = createRequests();

        IO.println("=== 映画館の座席予約 ===");
        IO.println("座席: " + theater.getSeatCount() + "席、窓口: 4か所、申し込み: " + requests.size() + "件");

        AtomicInteger succeeded = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        AtomicInteger reservedSeats = new AtomicInteger();  // 予約できた申し込みの席数の合計

        // 4つの窓口で、申し込みを同時に処理する
        ExecutorService windows = Executors.newFixedThreadPool(4);
        for (Request request : requests) {
            windows.submit(() -> {
                if (theater.reserve(request.customer(), request.seats())) {
                    succeeded.incrementAndGet();
                    reservedSeats.addAndGet(request.seats().size());
                } else {
                    failed.incrementAndGet();
                }
            });
        }
        windows.shutdown();
        if (!windows.awaitTermination(10, TimeUnit.SECONDS)) {
            IO.println("時間内に終わりませんでした");
            windows.shutdownNow();
            return;
        }

        IO.println("");
        IO.println("=== 結果 ===");
        IO.println("予約できた申し込み: " + succeeded.get() + "件");
        IO.println("予約できなかった申し込み: " + failed.get() + "件");
        IO.println("座席表: " + theater.seatMap());

        // 予約できた申し込みの席数の合計と、座席表の予約済みの数は一致するはず
        int doubleBooked = reservedSeats.get() - theater.countReserved();
        IO.println("予約できた申し込みの席数: " + reservedSeats.get() + "席");
        IO.println("座席表の予約済みの席: " + theater.countReserved() + "席");
        IO.println("二重に予約された席: " + doubleBooked + "席");
    }

    /** 人気の4組の座席（2席ずつ）に3人ずつ、9番と10番に1人ずつ、存在しない13番に1人が申し込む */
    private static List<Request> createRequests() {
        List<Request> requests = new ArrayList<>();
        String[] names = {
            "佐藤", "鈴木", "高橋", "田中", "伊藤", "渡辺",
            "山本", "中村", "小林", "加藤", "吉田", "山田"
        };
        int index = 0;
        for (int round = 0; round < 3; round++) {
            for (int first = 1; first <= 7; first += 2) {
                requests.add(new Request(names[index++], List.of(first, first + 1)));
            }
        }
        requests.add(new Request("佐々木", List.of(9)));
        requests.add(new Request("山口", List.of(10)));
        requests.add(new Request("松本", List.of(13)));
        return requests;
    }
}
