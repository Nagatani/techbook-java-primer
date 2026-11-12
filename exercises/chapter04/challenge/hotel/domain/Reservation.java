package hotel.domain;

/**
 * 1件の予約を表すクラス
 *
 * コンストラクターをパッケージプライベートにして、予約はHotelのreserveメソッドを通してしか作れないようにする。
 * hotel.appパッケージで new Reservation(...) と書くと、コンパイルエラーになる。
 */
public class Reservation {
    private static int nextNumber = 1;  // 予約番号の連番。すべての予約で共有する

    private String id;
    private Guest guest;
    private Room room;
    private int checkIn;
    private int nights;
    private boolean cancelled;

    Reservation(Guest guest, Room room, int checkIn, int nights) {
        this.id = "R" + "%03d".formatted(nextNumber);
        nextNumber++;
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.nights = nights;
        this.cancelled = false;
    }

    public String getId() {
        return id;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }

    public int getCheckIn() {
        return checkIn;
    }

    public int getNights() {
        return nights;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public int getTotalPrice() {
        return room.getPricePerNight() * nights;
    }

    // 取り消しの印を付ける（同じパッケージのHotelだけが呼ぶ）
    void markCancelled() {
        this.cancelled = true;
    }

    public String describe() {
        String status = cancelled ? "（取り消し済み）" : "";
        return id + " " + guest.getName() + "様 " + room.getNumber() + "号室 "
                + checkIn + "日から" + nights + "泊 " + getTotalPrice() + "円" + status;
    }
}
