/**
 * 第1章 応用課題3: 時間計算プログラム
 * 
 * 時間に関する様々な計算を行うプログラム。
 * 秒数変換、時刻の差、年齢計算など実用的な時間処理を実装します。
 * 
 * 学習ポイント:
 * - 整数の除算と余りの活用
 * - 複雑な計算ロジックの実装
 * - 時間の単位変換
 * - 実用的なプログラム設計
 * - 数値フォーマットの工夫
 */

public class TimeCalculation {
    public static void main(String[] args) {
        // プログラムのタイトル表示
        System.out.println("=== 時間計算プログラム ===");
        System.out.println();
        
        // 各種時間計算のデモンストレーション
        demonstrateSecondConversion();
        demonstrateTimeDifference();
        demonstrateAgeCalculation();
        demonstrateTimeFormatting();
        demonstrateAdvancedTimeCalculations();
    }
    
    /**
     * 秒数変換のデモンストレーション
     */
    private static void demonstrateSecondConversion() {
        System.out.println("秒数変換:");
        
        // 様々な秒数での変換例
        int[] secondsExamples = {7265, 3661, 86400, 90061, 172800};
        
        for (int seconds : secondsExamples) {
            String timeString = convertSecondsToTimeString(seconds);
            System.out.println(seconds + "秒 = " + timeString);
        }
        
        System.out.println();
    }
    
    /**
     * 時刻の差計算のデモンストレーション
     */
    private static void demonstrateTimeDifference() {
        System.out.println("時刻の差:");
        
        // 勤務時間の例
        int startHour = 9, startMinute = 30;
        int endHour = 17, endMinute = 45;
        
        System.out.println("開始時刻: " + startHour + "時" + startMinute + "分");
        System.out.println("終了時刻: " + endHour + "時" + endMinute + "分");
        
        int[] workTime = calculateTimeDifference(startHour, startMinute, endHour, endMinute);
        int workHours = workTime[0];
        int workMinutes = workTime[1];
        int totalMinutes = workHours * 60 + workMinutes;
        
        System.out.println("作業時間: " + workHours + "時間" + workMinutes + "分 (" + totalMinutes + "分)");
        
        // 他の時刻例も計算
        System.out.println();
        System.out.println("その他の時刻計算例:");
        
        // 深夜勤務の例
        calculateAndDisplayTimeDiff("深夜勤務", 22, 0, 6, 0);
        
        // 短時間勤務の例
        calculateAndDisplayTimeDiff("パート勤務", 10, 0, 14, 30);
        
        // 長時間の例
        calculateAndDisplayTimeDiff("長時間作業", 8, 30, 20, 15);
        
        System.out.println();
    }
    
    /**
     * 年齢計算のデモンストレーション
     */
    private static void demonstrateAgeCalculation() {
        System.out.println("年齢計算:");
        
        int[] ages = {22, 35, 50, 65, 80};
        
        for (int age : ages) {
            displayAgeInDifferentUnits(age);
            System.out.println();
        }
    }
    
    /**
     * 時間フォーマットのデモンストレーション
     */
    private static void demonstrateTimeFormatting() {
        System.out.println("時間フォーマット変換:");
        
        // 24時間制と12時間制の変換
        int[] hours24 = {0, 6, 12, 15, 23};
        
        for (int hour : hours24) {
            String time12 = convertTo12HourFormat(hour, 30);
            System.out.println(hour + ":30 (24時間制) = " + time12 + " (12時間制)");
        }
        
        System.out.println();
        
        // 経過時間の表示
        System.out.println("経過時間の表現:");
        int[] elapsedSeconds = {45, 125, 3725, 86500, 90061};
        
        for (int elapsed : elapsedSeconds) {
            String humanReadable = formatElapsedTime(elapsed);
            System.out.println(elapsed + "秒経過 = " + humanReadable);
        }
        
        System.out.println();
    }
    
    /**
     * 高度な時間計算のデモンストレーション
     */
    private static void demonstrateAdvancedTimeCalculations() {
        System.out.println("高度な時間計算:");
        
        // うるう年の計算
        int[] years = {2020, 2021, 2024, 2100, 2000};
        System.out.println("うるう年判定:");
        for (int year : years) {
            boolean isLeap = isLeapYear(year);
            System.out.println(year + "年: " + (isLeap ? "うるう年" : "平年"));
        }
        System.out.println();
        
        // 月の日数計算
        System.out.println("月の日数:");
        for (int month = 1; month <= 12; month++) {
            int days2024 = getDaysInMonth(month, 2024); // うるう年
            int days2023 = getDaysInMonth(month, 2023); // 平年
            System.out.println(month + "月: " + days2023 + "日 (平年), " + days2024 + "日 (うるう年)");
        }
        System.out.println();
        
        // 生産性計算の例
        System.out.println("作業効率計算:");
        calculateProductivity(8, 30, 150); // 8.5時間で150タスク
        calculateProductivity(6, 0, 90);   // 6時間で90タスク
        calculateProductivity(10, 45, 200); // 10.75時間で200タスク
        
        System.out.println();
    }
    
    // ===== ユーティリティメソッド =====
    
    /**
     * 秒数を時分秒の文字列に変換します
     */
    private static String convertSecondsToTimeString(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return hours + "時間" + minutes + "分" + seconds + "秒";
        } else if (minutes > 0) {
            return minutes + "分" + seconds + "秒";
        } else {
            return seconds + "秒";
        }
    }
    
    /**
     * 2つの時刻の差を計算します
     * @return [時間, 分] の配列
     */
    private static int[] calculateTimeDifference(int startHour, int startMinute, 
                                               int endHour, int endMinute) {
        int startTotalMinutes = startHour * 60 + startMinute;
        int endTotalMinutes = endHour * 60 + endMinute;
        
        // 日をまたぐ場合の処理
        if (endTotalMinutes < startTotalMinutes) {
            endTotalMinutes += 24 * 60; // 24時間を加算
        }
        
        int diffMinutes = endTotalMinutes - startTotalMinutes;
        int hours = diffMinutes / 60;
        int minutes = diffMinutes % 60;
        
        return new int[]{hours, minutes};
    }
    
    /**
     * 時刻の差を計算して表示します
     */
    private static void calculateAndDisplayTimeDiff(String label, int startH, int startM, 
                                                   int endH, int endM) {
        int[] diff = calculateTimeDifference(startH, startM, endH, endM);
        System.out.println(label + ": " + startH + ":" + String.format("%02d", startM) + 
                          " ～ " + endH + ":" + String.format("%02d", endM) + 
                          " = " + diff[0] + "時間" + diff[1] + "分");
    }
    
    /**
     * 年齢を様々な単位で表示します
     */
    private static void displayAgeInDifferentUnits(int age) {
        // 日数計算（概算）
        int daysApprox = age * 365;
        int daysAccurate = age * 365 + age / 4; // うるう年を考慮
        
        // 時間計算
        long hours = daysAccurate * 24L;
        
        // 分計算
        long minutes = hours * 60L;
        
        // 秒計算
        long seconds = minutes * 60L;
        
        System.out.println(age + "歳の詳細:");
        System.out.println("  約" + String.format("%,d", daysApprox) + "日 (365日/年)");
        System.out.println("  約" + String.format("%,d", daysAccurate) + "日 (うるう年考慮)");
        System.out.println("  約" + String.format("%,d", hours) + "時間");
        System.out.println("  約" + String.format("%,d", minutes) + "分");
        System.out.println("  約" + String.format("%,d", seconds) + "秒");
        
        // 人生の残り時間（平均寿命を80歳として）
        if (age < 80) {
            int remainingYears = 80 - age;
            int remainingDays = remainingYears * 365;
            System.out.println("  残り約" + remainingYears + "年 (" + 
                              String.format("%,d", remainingDays) + "日)");
        }
    }
    
    /**
     * 24時間制を12時間制に変換します
     */
    private static String convertTo12HourFormat(int hour24, int minute) {
        String period;
        int hour12;
        
        if (hour24 == 0) {
            hour12 = 12;
            period = "AM";
        } else if (hour24 < 12) {
            hour12 = hour24;
            period = "AM";
        } else if (hour24 == 12) {
            hour12 = 12;
            period = "PM";
        } else {
            hour12 = hour24 - 12;
            period = "PM";
        }
        
        return hour12 + ":" + String.format("%02d", minute) + " " + period;
    }
    
    /**
     * 経過時間を人間が読みやすい形式にフォーマットします
     */
    private static String formatElapsedTime(int totalSeconds) {
        if (totalSeconds < 60) {
            return totalSeconds + "秒";
        } else if (totalSeconds < 3600) {
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            return minutes + "分" + (seconds > 0 ? seconds + "秒" : "");
        } else if (totalSeconds < 86400) {
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            return hours + "時間" + (minutes > 0 ? minutes + "分" : "");
        } else {
            int days = totalSeconds / 86400;
            int hours = (totalSeconds % 86400) / 3600;
            return days + "日" + (hours > 0 ? hours + "時間" : "");
        }
    }
    
    /**
     * うるう年かどうかを判定します
     */
    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
    
    /**
     * 指定した年月の日数を取得します
     */
    private static int getDaysInMonth(int month, int year) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return isLeapYear(year) ? 29 : 28;
            default:
                return 0; // 無効な月
        }
    }
    
    /**
     * 作業効率を計算します
     */
    private static void calculateProductivity(int hours, int minutes, int tasks) {
        double totalHours = hours + minutes / 60.0;
        double tasksPerHour = tasks / totalHours;
        double minutesPerTask = (totalHours * 60) / tasks;
        
        System.out.println(hours + "時間" + minutes + "分で" + tasks + "タスク:");
        System.out.println("  時間あたり: " + String.format("%.1f", tasksPerHour) + "タスク");
        System.out.println("  1タスクあたり: " + String.format("%.1f", minutesPerTask) + "分");
    }
}

/*
 * 実行結果例:
 * 
 * === 時間計算プログラム ===
 * 
 * 秒数変換:
 * 7265秒 = 2時間1分5秒
 * 3661秒 = 1時間1分1秒
 * 86400秒 = 24時間0分0秒
 * 90061秒 = 25時間1分1秒
 * 172800秒 = 48時間0分0秒
 * 
 * 時刻の差:
 * 開始時刻: 9時30分
 * 終了時刻: 17時45分
 * 作業時間: 8時間15分 (495分)
 * 
 * その他の時刻計算例:
 * 深夜勤務: 22:00 ～ 6:00 = 8時間0分
 * パート勤務: 10:00 ～ 14:30 = 4時間30分
 * 長時間作業: 8:30 ～ 20:15 = 11時間45分
 * 
 * 年齢計算:
 * 22歳の詳細:
 *   約8,030日 (365日/年)
 *   約8,035日 (うるう年考慮)
 *   約192,840時間
 *   約11,570,400分
 *   約694,224,000秒
 *   残り約58年 (21,170日)
 * 
 * 35歳の詳細:
 *   約12,775日 (365日/年)
 *   約12,783日 (うるう年考慮)
 *   約306,792時間
 *   約18,407,520分
 *   約1,104,451,200秒
 *   残り約45年 (16,425日)
 * 
 * ...
 * 
 * 時間フォーマット変換:
 * 0:30 (24時間制) = 12:30 AM (12時間制)
 * 6:30 (24時間制) = 6:30 AM (12時間制)
 * 12:30 (24時間制) = 12:30 PM (12時間制)
 * 15:30 (24時間制) = 3:30 PM (12時間制)
 * 23:30 (24時間制) = 11:30 PM (12時間制)
 * 
 * 経過時間の表現:
 * 45秒経過 = 45秒
 * 125秒経過 = 2分5秒
 * 3725秒経過 = 1時間2分
 * 86500秒経過 = 1日1時間
 * 90061秒経過 = 1日1時間
 * 
 * 高度な時間計算:
 * うるう年判定:
 * 2020年: うるう年
 * 2021年: 平年
 * 2024年: うるう年
 * 2100年: 平年
 * 2000年: うるう年
 * 
 * 月の日数:
 * 1月: 31日 (平年), 31日 (うるう年)
 * 2月: 28日 (平年), 29日 (うるう年)
 * 3月: 31日 (平年), 31日 (うるう年)
 * ...
 * 
 * 作業効率計算:
 * 8時間30分で150タスク:
 *   時間あたり: 17.6タスク
 *   1タスクあたり: 3.4分
 * 6時間0分で90タスク:
 *   時間あたり: 15.0タスク
 *   1タスクあたり: 4.0分
 * 10時間45分で200タスク:
 *   時間あたり: 18.6タスク
 *   1タスクあたり: 3.2分
 * 
 * 学習のポイント:
 * 1. 整数の除算と余り：時分秒変換で / と % 演算子を活用
 * 2. 単位変換：秒→分→時間→日の変換計算
 * 3. 条件分岐：うるう年判定、12時間制変換などの複雑な条件処理
 * 4. 配列の活用：時刻の差分計算結果を配列で返す
 * 5. 実用的な計算：実際の時間管理に使える計算式の実装
 */