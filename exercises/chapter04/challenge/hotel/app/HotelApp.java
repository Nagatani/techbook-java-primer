package hotel.app;

import hotel.domain.Guest;
import hotel.domain.Hotel;
import hotel.domain.Reservation;
import hotel.domain.Room;

/**
 * 第4章 チャレンジ課題: ホテルの客室予約システム（解答例）
 *
 * hotel.domainパッケージのクラスを、別のパッケージから使うプログラムです。
 * 予約の作成・取り消しはHotelのpublicメソッドを通してしか行えません。
 * 次の2行はどちらもコンパイルエラーになります（パッケージプライベートのため）。
 *   new Reservation(guest, room, 1, 2);
 *   room.occupy(1, 2);
 *
 * 学習ポイント:
 * - パッケージによるクラスの整理と、import文
 * - public・private・パッケージプライベートの使い分け
 * - this(...)によるコンストラクターの呼び出しと、オーバーロード
 * - コンストラクターでの値の検証
 * - 責任を1つのクラスに集めすぎない（客室・宿泊客・予約・ホテルに分ける）
 */
public class HotelApp {
    public static void main(String[] args) {
        Hotel hotel = new Hotel("ホテル青葉");
        hotel.addRoom(new Room(101, "シングル"));
        hotel.addRoom(new Room(102, "シングル", 7000));
        hotel.addRoom(new Room(201, "ダブル"));
        hotel.addRoom(new Room(301, "スイート"));
        IO.println("=== " + hotel.getName() + " ===");

        IO.println("10日から2泊で空いている部屋:");
        hotel.showAvailableRooms(10, 2);

        Guest tanaka = new Guest("田中太郎", "090-1234-5678");
        Guest suzuki = new Guest("鈴木花子");

        IO.println();
        printResult(hotel.reserve(tanaka, 102, 10, 2));
        printResult(hotel.reserve(suzuki, 102, 11, 1));  // 11日の夜は田中様の予約で埋まっている
        printResult(hotel.reserve(suzuki, 101, 11, 3));
        printResult(hotel.reserve(suzuki, 201, 30, 3));  // 31日を超える

        IO.println();
        IO.println("11日から1泊で空いているシングル:");
        hotel.showAvailableRooms(11, 1, "シングル");
        IO.println("11日から1泊で1泊15000円以下の空室:");
        hotel.showAvailableRooms(11, 1, null, 15000);

        IO.println();
        IO.println("R001の取り消し: " + (hotel.cancel("R001") ? "取り消しました" : "取り消せません"));
        IO.println("R001の取り消し: " + (hotel.cancel("R001") ? "取り消しました" : "取り消せません"));
        printResult(hotel.reserve(suzuki, 102, 11, 1));  // 空いたので予約できる

        IO.println();
        IO.println("予約の一覧:");
        hotel.showReservations();
        IO.println("売上: " + hotel.getTotalSales() + "円");
    }

    static void printResult(Reservation reservation) {
        if (reservation == null) {
            IO.println("予約できませんでした");
        } else {
            IO.println("予約しました: " + reservation.describe());
        }
    }
}
