/**
 * Timeを試すプログラム（課題7の解答例）
 */
public class TimeTest {
    public static void main(String[] args) {
        IO.println("=== 時刻管理システム ===");
        
        // 時のみを設定してTimeオブジェクトを作成する
        Time time1 = new Time(10);
        IO.print("時刻1: ");
        time1.showTime();
        
        // 時分を設定してTimeオブジェクトを作成する
        Time time2 = new Time(14, 30);
        IO.print("時刻2: ");
        time2.showTime();
        
        // 時分秒を設定してTimeオブジェクトを作成する
        Time time3 = new Time(23, 45, 30);
        IO.print("時刻3: ");
        time3.showTime();
        IO.println();
        
        // 30秒を追加する
        time1.addTime(30);
        IO.println("30秒追加後:");
        IO.print("時刻1: ");
        time1.showTime();
        IO.println();
        
        // 15分を追加する
        time2.addTime(15, true);
        IO.println("15分追加後:");
        IO.print("時刻2: ");
        time2.showTime();
        IO.println();
        
        // 1時間30分45秒を追加する
        time3.addTime(1, 30, 45);
        IO.println("1時間30分45秒追加後:");
        IO.print("時刻3: ");
        time3.showTime();
    }
}