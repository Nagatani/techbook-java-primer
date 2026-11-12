package hotel.domain;

/**
 * ホテルを表すクラス。客室と予約をまとめて管理する
 *
 * 予約を作る・取り消す処理はこのクラスに集める。
 * 客室を埋める処理（Room.occupy）や予約のコンストラクターはパッケージプライベートなので、
 * ほかのパッケージからはこのクラスのpublicメソッドを通してしか予約を操作できない。
 */
public class Hotel {
    private String name;
    private Room[] rooms;
    private int roomCount;
    private Reservation[] reservations;
    private int reservationCount;

    public Hotel(String name, int maxRooms, int maxReservations) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("ホテルの名前を指定してください");
        }
        if (maxRooms <= 0 || maxReservations <= 0) {
            throw new IllegalArgumentException("客室数と予約数の上限は1以上にしてください");
        }
        this.name = name;
        this.rooms = new Room[maxRooms];
        this.reservations = new Reservation[maxReservations];
    }

    // 上限を省いたときは、客室20室・予約100件にする
    public Hotel(String name) {
        this(name, 20, 100);
    }

    public String getName() {
        return name;
    }

    // 客室を加える。満室（配列がいっぱい）か、同じ番号の部屋があればfalse
    public boolean addRoom(Room room) {
        if (roomCount >= rooms.length || findRoom(room.getNumber()) != null) {
            return false;
        }
        rooms[roomCount] = room;
        roomCount++;
        return true;
    }

    // 空いている客室を表示する（3つのオーバーロード。条件の少ないものは多いものに処理を任せる）
    public void showAvailableRooms(int checkIn, int nights) {
        showAvailableRooms(checkIn, nights, null, Integer.MAX_VALUE);
    }

    public void showAvailableRooms(int checkIn, int nights, String type) {
        showAvailableRooms(checkIn, nights, type, Integer.MAX_VALUE);
    }

    public void showAvailableRooms(int checkIn, int nights, String type, int maxPrice) {
        int found = 0;
        for (int i = 0; i < roomCount; i++) {
            Room room = rooms[i];
            boolean typeMatches = type == null || type.equals(room.getType());
            if (typeMatches && room.getPricePerNight() <= maxPrice && room.isAvailable(checkIn, nights)) {
                IO.println("  " + room.describe());
                found++;
            }
        }
        if (found == 0) {
            IO.println("  条件に合う空室はありません");
        }
    }

    // 予約する。部屋がない・期間が範囲外・埋まっている・予約がいっぱいのときはnullを返す
    public Reservation reserve(Guest guest, int roomNumber, int checkIn, int nights) {
        Room room = findRoom(roomNumber);
        if (room == null || !room.isAvailable(checkIn, nights) || reservationCount >= reservations.length) {
            return null;
        }
        Reservation reservation = new Reservation(guest, room, checkIn, nights);
        room.occupy(checkIn, nights);
        reservations[reservationCount] = reservation;
        reservationCount++;
        return reservation;
    }

    // 予約を取り消す。見つからない・取り消し済みならfalse
    public boolean cancel(String reservationId) {
        Reservation reservation = findReservation(reservationId);
        if (reservation == null || reservation.isCancelled()) {
            return false;
        }
        reservation.getRoom().release(reservation.getCheckIn(), reservation.getNights());
        reservation.markCancelled();
        return true;
    }

    // 取り消していない予約の料金の合計
    public int getTotalSales() {
        int total = 0;
        for (int i = 0; i < reservationCount; i++) {
            if (!reservations[i].isCancelled()) {
                total += reservations[i].getTotalPrice();
            }
        }
        return total;
    }

    public void showReservations() {
        for (int i = 0; i < reservationCount; i++) {
            IO.println("  " + reservations[i].describe());
        }
    }

    private Room findRoom(int number) {
        for (int i = 0; i < roomCount; i++) {
            if (rooms[i].getNumber() == number) {
                return rooms[i];
            }
        }
        return null;
    }

    private Reservation findReservation(String id) {
        for (int i = 0; i < reservationCount; i++) {
            if (reservations[i].getId().equals(id)) {
                return reservations[i];
            }
        }
        return null;
    }
}
