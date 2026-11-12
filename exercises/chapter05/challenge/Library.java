/**
 * 図書館。資料をLibraryItem型の配列でまとめて管理する
 *
 * 貸出・返却の処理では資料の種類を調べない。
 * 貸出期間や延滞料金の違いは、各資料のオーバーライドしたメソッドが受け持つ（ポリモーフィズム）。
 * 種類に固有の情報（DVDの再生時間）が必要な集計だけ、instanceofで型を調べる。
 */
public class Library {
    private LibraryItem[] items;
    private int itemCount;
    private int totalLateFees;  // 受け取った延滞料金の合計

    public Library(int capacity) {
        this.items = new LibraryItem[capacity];
    }

    // 資料を登録する。同じ資料（equalsで等しいもの）がすでにあれば登録しない
    public String add(LibraryItem item) {
        if (itemCount >= items.length) {
            return "これ以上登録できません";
        }
        for (int i = 0; i < itemCount; i++) {
            if (items[i].equals(item)) {
                return item.getId() + "はすでに登録されています";
            }
        }
        items[itemCount] = item;
        itemCount++;
        return item.getId() + "「" + item.getTitle() + "」を登録しました";
    }

    public String borrow(String id, int day) {
        LibraryItem item = find(id);
        if (item == null) {
            return id + "という資料はありません";
        }
        if (!item.borrow(day)) {
            return "「" + item.getTitle() + "」は貸し出せません";
        }
        return day + "日: 「" + item.getTitle() + "」を貸し出しました（返却期限 " + item.getDueDay() + "日）";
    }

    public String giveBack(String id, int day) {
        LibraryItem item = find(id);
        if (item == null) {
            return id + "という資料はありません";
        }
        int fee = item.giveBack(day);
        if (fee < 0) {
            return "「" + item.getTitle() + "」は貸し出していません";
        }
        totalLateFees += fee;
        if (fee == 0) {
            return day + "日: 「" + item.getTitle() + "」が返却されました";
        }
        return day + "日: 「" + item.getTitle() + "」が返却されました（延滞料金 " + fee + "円）";
    }

    // すべての資料を表示する。IO.printlnに渡すと、各資料のtoString()が呼ばれる
    public void showItems() {
        for (int i = 0; i < itemCount; i++) {
            IO.println("  " + items[i]);
        }
    }

    // day日の時点で返却期限を過ぎている資料と、その日に返却した場合の延滞料金を表示する
    public void showOverdue(int day) {
        int found = 0;
        for (int i = 0; i < itemCount; i++) {
            LibraryItem item = items[i];
            if (item.isOnLoan() && item.calculateLateFee(day) > 0) {
                IO.println("  「" + item.getTitle() + "」 " + item.calculateLateFee(day) + "円");
                found++;
            }
        }
        if (found == 0) {
            IO.println("  延滞している資料はありません");
        }
    }

    // 種類ごとの数と、DVDの再生時間の合計を表示する
    public void showReport() {
        int books = 0;
        int magazines = 0;
        int dvds = 0;
        int dvdMinutes = 0;
        for (int i = 0; i < itemCount; i++) {
            LibraryItem item = items[i];
            if (item instanceof Book) {
                books++;
            } else if (item instanceof Magazine) {
                magazines++;
            } else if (item instanceof Dvd dvd) {  // パターンマッチング: 型を調べてDvd型の変数に入れる
                dvds++;
                dvdMinutes += dvd.getMinutes();
            }
        }
        IO.println("  本: " + books + "冊、雑誌: " + magazines + "冊、DVD: " + dvds + "枚（合計 " + dvdMinutes + "分）");
        IO.println("  受け取った延滞料金: " + totalLateFees + "円");
    }

    private LibraryItem find(String id) {
        for (int i = 0; i < itemCount; i++) {
            if (items[i].getId().equals(id)) {
                return items[i];
            }
        }
        return null;
    }
}
