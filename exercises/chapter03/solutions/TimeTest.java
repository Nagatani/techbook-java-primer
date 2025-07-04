/**
 * 第3章 演習問題4: TimeTestクラスの解答例
 * 
 * 【テストの目的】
 * - 時刻の正規化処理の動作確認
 * - オーバーロードメソッドの動作確認
 * - 24時間制での時刻計算の正確性確認
 * - 境界値での動作確認
 * 
 * 【デバッグのコツ】
 * 1. 正規化処理の前後で値を確認する
 * 2. 24時間制での時刻の扱いに注意する
 * 3. 境界値（23:59:59 → 00:00:00）をテストする
 * 4. 負の値の処理を確認する
 */
public class TimeTest {
    public static void main(String[] args) {
        System.out.println("=== 第3章 演習問題4: Time クラスのテスト ===");
        
        // 【基本テスト】コンストラクタのテスト
        testConstructors();
        
        // 【基本テスト】addTimeメソッドのテスト
        testAddTimeMethods();
        
        // 【応用テスト】正規化処理のテスト
        testNormalization();
        
        // 【発展テスト】時刻の比較・判定メソッドのテスト
        testComparisonMethods();
        
        // 【実践テスト】実際の使用例
        testPracticalUsage();
        
        // 【エラーテスト】エラーケースの処理
        testErrorCases();
        
        System.out.println("\n=== テスト完了 ===");
    }
    
    /**
     * コンストラクタのテスト
     */
    private static void testConstructors() {
        System.out.println("\n--- コンストラクタのテスト ---");
        
        // 時のみのコンストラクタ
        Time time1 = new Time(10);
        System.out.print("時のみ(10): ");
        time1.displayTime();
        
        // 時分のコンストラクタ
        Time time2 = new Time(14, 30);
        System.out.print("時分(14:30): ");
        time2.displayTime();
        
        // 時分秒のコンストラクタ
        Time time3 = new Time(23, 45, 30);
        System.out.print("時分秒(23:45:30): ");
        time3.displayTime();
        
        // デフォルトコンストラクタ
        Time time4 = new Time();
        System.out.print("デフォルト: ");
        time4.displayTime();
        
        // コピーコンストラクタ
        Time time5 = new Time(time3);
        System.out.print("コピー: ");
        time5.displayTime();
        
        // 秒数からの変換コンストラクタ
        Time time6 = new Time(3661);  // 1時間1分1秒
        System.out.print("秒数から(3661秒): ");
        time6.displayTime();
        
        System.out.println("コンストラクタが正常に動作しています。");
    }
    
    /**
     * addTimeメソッドのテスト
     */
    private static void testAddTimeMethods() {
        System.out.println("\n--- addTimeメソッドのテスト ---");
        
        // 基本的な時刻を作成
        Time time1 = new Time(10, 0, 0);
        Time time2 = new Time(14, 30, 0);
        Time time3 = new Time(23, 45, 30);
        
        System.out.println("初期時刻:");
        System.out.print("時刻1: ");
        time1.displayTime();
        System.out.print("時刻2: ");
        time2.displayTime();
        System.out.print("時刻3: ");
        time3.displayTime();
        
        // 秒追加のテスト
        System.out.println("\n--- 秒追加のテスト ---");
        time1.addTime(30);
        System.out.print("時刻1に30秒追加: ");
        time1.displayTime();
        
        // 分追加のテスト
        System.out.println("\n--- 分追加のテスト ---");
        time2.addTime(15, 0);
        System.out.print("時刻2に15分追加: ");
        time2.displayTime();
        
        // 時分秒追加のテスト
        System.out.println("\n--- 時分秒追加のテスト ---");
        time3.addTime(1, 30, 45);
        System.out.print("時刻3に1時間30分45秒追加: ");
        time3.displayTime();
        
        // Timeオブジェクトの加算
        System.out.println("\n--- Timeオブジェクトの加算 ---");
        Time addTime = new Time(2, 15, 30);
        Time baseTime = new Time(10, 30, 15);
        System.out.print("ベース時刻: ");
        baseTime.displayTime();
        System.out.print("追加時刻: ");
        addTime.displayTime();
        
        baseTime.addTime(addTime);
        System.out.print("加算後: ");
        baseTime.displayTime();
    }
    
    /**
     * 正規化処理のテスト
     */
    private static void testNormalization() {
        System.out.println("\n--- 正規化処理のテスト ---");
        
        // 秒のオーバーフロー
        System.out.println("秒のオーバーフロー:");
        Time time1 = new Time(10, 30, 75);  // 75秒
        System.out.print("10:30:75 → ");
        time1.displayTime();
        
        // 分のオーバーフロー
        System.out.println("\n分のオーバーフロー:");
        Time time2 = new Time(10, 75, 30);  // 75分
        System.out.print("10:75:30 → ");
        time2.displayTime();
        
        // 時のオーバーフロー
        System.out.println("\n時のオーバーフロー:");
        Time time3 = new Time(25, 30, 45);  // 25時
        System.out.print("25:30:45 → ");
        time3.displayTime();
        
        // 複数のオーバーフロー
        System.out.println("\n複数のオーバーフロー:");
        Time time4 = new Time(23, 59, 75);  // 23:59:75
        System.out.print("23:59:75 → ");
        time4.displayTime();
        
        // 日をまたぐケース
        System.out.println("\n日をまたぐケース:");
        Time time5 = new Time(23, 45, 30);
        System.out.print("23:45:30 に1時間30分追加: ");
        time5.addTime(1, 30, 0);
        time5.displayTime();
        
        // 負の値の処理
        System.out.println("\n負の値の処理:");
        Time time6 = new Time(1, 0, 0);
        System.out.print("01:00:00 から2時間減算: ");
        time6.subtractTime(2, 0, 0);
        time6.displayTime();
    }
    
    /**
     * 時刻の比較・判定メソッドのテスト
     */
    private static void testComparisonMethods() {
        System.out.println("\n--- 時刻の比較・判定メソッドのテスト ---");
        
        Time morning = new Time(9, 30, 0);
        Time afternoon = new Time(15, 45, 30);
        Time evening = new Time(20, 15, 45);
        
        System.out.println("時刻の比較:");
        System.out.print("朝の時刻: ");
        morning.displayTime();
        System.out.print("午後の時刻: ");
        afternoon.displayTime();
        System.out.print("夜の時刻: ");
        evening.displayTime();
        
        System.out.println("\n比較結果:");
        System.out.println("朝 < 午後: " + morning.isEarlierThan(afternoon));
        System.out.println("午後 > 朝: " + afternoon.isLaterThan(morning));
        System.out.println("夜 > 午後: " + evening.isLaterThan(afternoon));
        
        // 時刻の判定
        System.out.println("\n時刻の判定:");
        Time[] times = {
            new Time(6, 0, 0),   // 朝
            new Time(12, 0, 0),  // 正午
            new Time(18, 30, 0), // 夕方
            new Time(0, 0, 0)    // 真夜中
        };
        
        for (Time time : times) {
            System.out.print(time.toString() + " - ");
            System.out.print(time.isAM() ? "午前" : "午後");
            if (time.isNoon()) System.out.print(" (正午)");
            if (time.isMidnight()) System.out.print(" (真夜中)");
            System.out.println();
        }
        
        // 12時間制での表示
        System.out.println("\n12時間制での表示:");
        for (Time time : times) {
            System.out.println(time.toString() + " → " + time.to12HourFormat());
        }
    }
    
    /**
     * 実践的な使用例のテスト
     */
    private static void testPracticalUsage() {
        System.out.println("\n--- 実践的な使用例のテスト ---");
        
        System.out.println("=== 時刻管理システム ===");
        
        // 勤務時間の管理
        Time workStart = new Time(9, 0, 0);
        Time workEnd = new Time(17, 30, 0);
        
        System.out.println("勤務時間管理:");
        System.out.print("開始時刻: ");
        workStart.displayDetailedTime();
        System.out.print("終了時刻: ");
        workEnd.displayDetailedTime();
        
        // 勤務時間の計算
        Time workDuration = workEnd.getElapsedTime(workStart);
        System.out.print("勤務時間: ");
        workDuration.displayTime();
        
        // 休憩時間の管理
        System.out.println("\n休憩時間の管理:");
        Time[] breakTimes = {
            new Time(10, 15, 0),  // 午前の休憩
            new Time(12, 0, 0),   // 昼休み開始
            new Time(13, 0, 0),   // 昼休み終了
            new Time(15, 0, 0)    // 午後の休憩
        };
        
        for (int i = 0; i < breakTimes.length; i++) {
            System.out.print("休憩" + (i + 1) + ": ");
            breakTimes[i].displayTime();
            System.out.println("  12時間制: " + breakTimes[i].to12HourFormat());
        }
        
        // 会議時間の計算
        System.out.println("\n会議時間の計算:");
        Time meetingStart = new Time(14, 0, 0);
        Time meetingEnd = new Time(15, 30, 0);
        
        System.out.print("会議開始: ");
        meetingStart.displayTime();
        System.out.print("会議終了: ");
        meetingEnd.displayTime();
        
        Time meetingDuration = meetingEnd.getElapsedTime(meetingStart);
        System.out.print("会議時間: ");
        meetingDuration.displayTime();
        
        // 時刻の丸め処理
        System.out.println("\n時刻の丸め処理:");
        Time impreciseTime = new Time(14, 27, 45);
        System.out.print("元の時刻: ");
        impreciseTime.displayTime();
        
        Time roundedToMinute = new Time(impreciseTime);
        roundedToMinute.roundToNearestMinute();
        System.out.print("分単位に丸め: ");
        roundedToMinute.displayTime();
        
        Time roundedToHour = new Time(impreciseTime);
        roundedToHour.roundToNearestHour();
        System.out.print("時単位に丸め: ");
        roundedToHour.displayTime();
    }
    
    /**
     * エラーケースのテスト
     */
    private static void testErrorCases() {
        System.out.println("\n--- エラーケースのテスト ---");
        
        // 極端な値での作成
        System.out.println("極端な値での作成:");
        Time extremeTime = new Time(100, 200, 300);
        System.out.print("100:200:300 → ");
        extremeTime.displayTime();
        
        // 負の値での作成
        System.out.println("\n負の値での作成:");
        Time negativeTime = new Time(-5, -10, -15);
        System.out.print("-5:-10:-15 → ");
        negativeTime.displayTime();
        
        // 大きな秒数での作成
        System.out.println("\n大きな秒数での作成:");
        Time bigSecondsTime = new Time(100000);  // 約27時間
        System.out.print("100000秒 → ");
        bigSecondsTime.displayTime();
        
        // 境界値のテスト
        System.out.println("\n境界値のテスト:");
        Time boundary1 = new Time(23, 59, 59);
        System.out.print("23:59:59 に1秒追加: ");
        boundary1.addTime(1);
        boundary1.displayTime();
        
        Time boundary2 = new Time(0, 0, 0);
        System.out.print("00:00:00 から1秒減算: ");
        boundary2.subtractTime(1);
        boundary2.displayTime();
        
        // 無効な時刻の検証
        System.out.println("\n時刻の妥当性チェック:");
        int[][] testCases = {
            {10, 30, 45},  // 正常
            {25, 30, 45},  // 無効な時
            {10, 65, 45},  // 無効な分
            {10, 30, 75},  // 無効な秒
            {-1, 30, 45}   // 負の時
        };
        
        for (int[] testCase : testCases) {
            boolean valid = Time.isValidTime(testCase[0], testCase[1], testCase[2]);
            System.out.printf("%02d:%02d:%02d → %s%n", 
                            testCase[0], testCase[1], testCase[2], 
                            valid ? "有効" : "無効");
        }
    }
    
    /**
     * 静的メソッドのテスト
     */
    private static void testStaticMethods() {
        System.out.println("\n--- 静的メソッドのテスト ---");
        
        // 文字列からのパース
        System.out.println("文字列からのパース:");
        String[] timeStrings = {
            "12:30:45",
            "09:15:30",
            "23:59:59",
            "invalid",
            "25:70:80"
        };
        
        for (String timeString : timeStrings) {
            Time parsed = Time.parse(timeString);
            System.out.println("'" + timeString + "' → " + parsed.toString());
        }
        
        // 中間時刻の計算
        System.out.println("\n中間時刻の計算:");
        Time time1 = new Time(10, 0, 0);
        Time time2 = new Time(14, 0, 0);
        Time middle = Time.getMiddleTime(time1, time2);
        
        System.out.print("時刻1: ");
        time1.displayTime();
        System.out.print("時刻2: ");
        time2.displayTime();
        System.out.print("中間時刻: ");
        middle.displayTime();
        
        // 現在時刻の取得（シミュレーション）
        System.out.println("\n現在時刻の取得:");
        Time currentTime = Time.getCurrentTime();
        System.out.print("現在時刻: ");
        currentTime.displayTime();
    }
    
    /**
     * 時刻の差分計算のテスト
     */
    private static void testTimeDifference() {
        System.out.println("\n--- 時刻の差分計算のテスト ---");
        
        Time time1 = new Time(10, 30, 0);
        Time time2 = new Time(14, 45, 30);
        
        System.out.print("時刻1: ");
        time1.displayTime();
        System.out.print("時刻2: ");
        time2.displayTime();
        
        int diffSeconds = time1.getDifferenceInSeconds(time2);
        System.out.println("秒単位の差分: " + diffSeconds + "秒");
        
        Time diffTime = time1.getDifferenceAsTime(time2);
        System.out.print("時刻形式の差分: ");
        diffTime.displayTime();
        
        Time elapsed = time2.getElapsedTime(time1);
        System.out.print("経過時間: ");
        elapsed.displayTime();
    }
}