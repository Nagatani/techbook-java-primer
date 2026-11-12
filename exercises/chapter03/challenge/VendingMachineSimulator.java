/**
 * 第3章 チャレンジ課題: 自動販売機シミュレーター（解答例）
 *
 * お金を入れて飲み物を買い、おつりを硬貨と紙幣で受け取る自動販売機を、
 * 飲み物を表すDrinkクラスと、自動販売機を表すVendingMachineクラスで作ります。
 * mainメソッドはメニューを表示して入力を受け取り、操作をVendingMachineに任せるだけにします。
 *
 * 学習ポイント:
 * - privateフィールドと、状態を変える処理をメソッドに閉じ込めるカプセル化
 * - コンストラクターでの初期化と、不正な値の扱い
 * - オブジェクトの配列と、実際に入っている数を数える変数
 * - static finalの定数とstaticメソッド（オブジェクトごとに変わらないもの）
 * - あるクラスのメソッドから、別のクラスのオブジェクトに処理を頼む
 */
public class VendingMachineSimulator {
    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine(5);
        machine.addDrink(new Drink("お茶", 130, 3));
        machine.addDrink(new Drink("水", 100, 5));
        machine.addDrink(new Drink("コーヒー", 150, 2));
        machine.addDrink(new Drink("オレンジジュース", 160, 1));

        IO.println("=== 自動販売機 ===");
        boolean running = true;
        while (running) {
            IO.println();
            machine.showDrinks();
            IO.println("投入金額: " + machine.getInsertedAmount() + "円");
            IO.println("1. お金を入れる  2. 飲み物を買う  3. おつりを受け取る  4. 管理メニュー  0. 終了");
            int choice = readNumber("番号: ");

            if (choice == 1) {
                insertMoney(machine);
            } else if (choice == 2) {
                buyDrink(machine);
            } else if (choice == 3) {
                IO.println(machine.returnChange());
            } else if (choice == 4) {
                manage(machine);
            } else if (choice == 0) {
                if (machine.getInsertedAmount() > 0) {
                    IO.println(machine.returnChange());
                }
                running = false;
            } else {
                IO.println("0〜4の番号を入力してください");
            }
        }
        IO.println("ご利用ありがとうございました");
    }

    // お金を入れる操作。使えないお金は受け付けない
    static void insertMoney(VendingMachine machine) {
        int money = readNumber("入れるお金（10, 50, 100, 500, 1000）: ");
        if (machine.insert(money)) {
            IO.println(money + "円を入れました");
        } else {
            IO.println(money + "円は使えません");
        }
    }

    // 飲み物を買う操作。買えなかった理由はVendingMachineが返す文字列で知らせる
    static void buyDrink(VendingMachine machine) {
        int number = readNumber("飲み物の番号: ");
        IO.println(machine.buy(number));
    }

    // 管理メニュー。補充と売上の確認
    static void manage(VendingMachine machine) {
        IO.println("--- 管理メニュー ---");
        IO.println("売上: " + machine.getSales() + "円（" + machine.getSoldCount() + "本）");
        int number = readNumber("補充する飲み物の番号（0で戻る）: ");
        if (number == 0) {
            return;
        }
        int amount = readNumber("補充する本数: ");
        if (machine.refill(number, amount)) {
            IO.println(amount + "本を補充しました");
        } else {
            IO.println("補充できませんでした");
        }
    }

    // 0以上の整数を読み取る。数字以外が入力されたら読み直す
    // 入力が終わったとき（IO.readlnがnullを返したとき）は0を返して、メニューを終わらせる
    static int readNumber(String message) {
        while (true) {
            String line = IO.readln(message);
            if (line == null) {
                return 0;
            }
            line = line.trim();
            if (isDigits(line)) {
                return Integer.parseInt(line);
            }
            IO.println("0以上の整数を入力してください");
        }
    }

    // 文字列が1〜9桁の数字だけでできているかを調べる（10桁以上はintに収まらないことがある）
    static boolean isDigits(String text) {
        if (text.isEmpty() || text.length() > 9) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}

/**
 * 飲み物を表すクラス
 * 在庫は、売る（sellOne）と補充する（refill）ときにだけ変わる
 */
class Drink {
    private String name;
    private int price;
    private int stock;

    public Drink(String name, int price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public boolean isSoldOut() {
        return stock == 0;
    }

    // 1本売る。売り切れなら売らずにfalseを返す
    public boolean sellOne() {
        if (isSoldOut()) {
            return false;
        }
        stock--;
        return true;
    }

    // 補充する。1本未満の補充は受け付けない
    public boolean refill(int amount) {
        if (amount <= 0) {
            return false;
        }
        stock += amount;
        return true;
    }
}

/**
 * 自動販売機を表すクラス
 * 投入金額と売上はこのクラスの中だけで変え、外からは値を読むことしかできない
 */
class VendingMachine {
    // 使えるお金の種類。おつりを出すときは大きい順に使う
    // どの自動販売機でも同じなので、static finalの定数にする
    private static final int[] MONEY_UNITS = {1000, 500, 100, 50, 10};
    private static final int MAX_INSERTED = 5000;  // 一度に入れられる金額の上限

    private Drink[] drinks;
    private int drinkCount;      // 実際に並んでいる飲み物の種類の数
    private int insertedAmount;  // 投入金額
    private int sales;           // 売上金額
    private int soldCount;       // 売れた本数

    public VendingMachine(int slots) {
        this.drinks = new Drink[slots];
        this.drinkCount = 0;
        this.insertedAmount = 0;
        this.sales = 0;
        this.soldCount = 0;
    }

    // 飲み物を並べる。棚がいっぱいならfalseを返す
    public boolean addDrink(Drink drink) {
        if (drinkCount >= drinks.length) {
            return false;
        }
        drinks[drinkCount] = drink;
        drinkCount++;
        return true;
    }

    // 使えるお金かどうか。オブジェクトの状態を使わないのでstaticメソッドにする
    public static boolean isAcceptable(int money) {
        for (int unit : MONEY_UNITS) {
            if (money == unit) {
                return true;
            }
        }
        return false;
    }

    // お金を入れる。使えないお金や、上限を超える投入は受け付けない
    public boolean insert(int money) {
        if (!isAcceptable(money) || insertedAmount + money > MAX_INSERTED) {
            return false;
        }
        insertedAmount += money;
        return true;
    }

    // 番号（1から）で飲み物を買い、結果を文字列で返す
    public String buy(int number) {
        Drink drink = findDrink(number);
        if (drink == null) {
            return "その番号の飲み物はありません";
        }
        if (drink.isSoldOut()) {
            return drink.getName() + "は売り切れです";
        }
        if (insertedAmount < drink.getPrice()) {
            return "お金が足りません（あと" + (drink.getPrice() - insertedAmount) + "円）";
        }
        drink.sellOne();
        insertedAmount -= drink.getPrice();
        sales += drink.getPrice();
        soldCount++;
        return drink.getName() + "が出てきました（残りの投入金額: " + insertedAmount + "円）";
    }

    // おつりを返す。大きいお金から順に、何枚ずつ返すかを文字列にする
    public String returnChange() {
        if (insertedAmount == 0) {
            return "おつりはありません";
        }
        String result = "おつり " + insertedAmount + "円:";
        int rest = insertedAmount;
        for (int unit : MONEY_UNITS) {
            int count = rest / unit;
            if (count > 0) {
                result += " " + unit + "円×" + count;
                rest -= unit * count;
            }
        }
        insertedAmount = 0;
        return result;
    }

    // 番号（1から）の飲み物に補充する
    public boolean refill(int number, int amount) {
        Drink drink = findDrink(number);
        if (drink == null) {
            return false;
        }
        return drink.refill(amount);
    }

    // 並んでいる飲み物を、買えるかどうかの印を付けて表示する
    public void showDrinks() {
        for (int i = 0; i < drinkCount; i++) {
            Drink drink = drinks[i];
            String mark = "";
            if (drink.isSoldOut()) {
                mark = " [売切]";
            } else if (insertedAmount >= drink.getPrice()) {
                mark = " [購入可]";
            }
            IO.println((i + 1) + ". " + drink.getName() + " " + drink.getPrice() + "円" + mark);
        }
    }

    public int getInsertedAmount() {
        return insertedAmount;
    }

    public int getSales() {
        return sales;
    }

    public int getSoldCount() {
        return soldCount;
    }

    // 番号（1から）で飲み物を探す。範囲外ならnullを返す
    private Drink findDrink(int number) {
        if (number < 1 || number > drinkCount) {
            return null;
        }
        return drinks[number - 1];
    }
}
