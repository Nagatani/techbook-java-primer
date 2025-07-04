/**
 * 第3章 演習問題4: Timeクラスの解答例
 * 
 * 【学習ポイント】
 * - 複雑な状態管理（時刻の正規化）
 * - メソッドオーバーロードの応用
 * - 計算アルゴリズムの実装
 * - 24時間制での時刻処理
 * 
 * 【よくある間違いと対策】
 * 1. 時刻の正規化処理を忘れる（60秒=1分、60分=1時間）
 * 2. 24時間制での時刻計算を間違える
 * 3. 負の値の処理を考慮しない
 * 4. オーバーロードメソッドの引数順序を間違える
 */
public class Time {
    // インスタンス変数
    private int hour;
    private int minute;
    private int second;
    
    // 【基本解答】時のみのコンストラクタ
    public Time(int hour) {
        this(hour, 0, 0);
    }
    
    // 【基本解答】時分のコンストラクタ
    public Time(int hour, int minute) {
        this(hour, minute, 0);
    }
    
    // 【基本解答】時分秒のコンストラクタ
    public Time(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        normalize();  // 正規化処理
    }
    
    // 【応用解答】デフォルトコンストラクタ（00:00:00）
    public Time() {
        this(0, 0, 0);
    }
    
    // 【応用解答】コピーコンストラクタ
    public Time(Time other) {
        this(other.hour, other.minute, other.second);
    }
    
    // 【応用解答】秒数からの変換コンストラクタ
    public Time(int totalSeconds) {
        this();
        setFromTotalSeconds(totalSeconds);
    }
    
    // 【基本解答】秒追加メソッド
    public void addTime(int seconds) {
        this.second += seconds;
        normalize();
    }
    
    // 【基本解答】分追加メソッド
    public void addTime(int minutes, int seconds) {
        this.minute += minutes;
        this.second += seconds;
        normalize();
    }
    
    // 【基本解答】時分秒追加メソッド
    public void addTime(int hours, int minutes, int seconds) {
        this.hour += hours;
        this.minute += minutes;
        this.second += seconds;
        normalize();
    }
    
    // 【応用解答】Timeオブジェクトの加算
    public void addTime(Time other) {
        this.hour += other.hour;
        this.minute += other.minute;
        this.second += other.second;
        normalize();
    }
    
    // 【発展解答】時刻の減算メソッド
    public void subtractTime(int seconds) {
        this.second -= seconds;
        normalize();
    }
    
    public void subtractTime(int minutes, int seconds) {
        this.minute -= minutes;
        this.second -= seconds;
        normalize();
    }
    
    public void subtractTime(int hours, int minutes, int seconds) {
        this.hour -= hours;
        this.minute -= minutes;
        this.second -= seconds;
        normalize();
    }
    
    // 【発展解答】時刻の正規化処理
    private void normalize() {
        // 秒の正規化
        while (second >= 60) {
            second -= 60;
            minute++;
        }
        while (second < 0) {
            second += 60;
            minute--;
        }
        
        // 分の正規化
        while (minute >= 60) {
            minute -= 60;
            hour++;
        }
        while (minute < 0) {
            minute += 60;
            hour--;
        }
        
        // 時の正規化（24時間制）
        while (hour >= 24) {
            hour -= 24;
        }
        while (hour < 0) {
            hour += 24;
        }
    }
    
    // 【発展解答】総秒数での設定
    private void setFromTotalSeconds(int totalSeconds) {
        // 負の値の処理
        if (totalSeconds < 0) {
            totalSeconds = 0;
        }
        
        // 24時間以上の場合は24時間で割った余りを使用
        totalSeconds = totalSeconds % (24 * 60 * 60);
        
        this.hour = totalSeconds / 3600;
        this.minute = (totalSeconds % 3600) / 60;
        this.second = totalSeconds % 60;
    }
    
    // 【発展解答】総秒数の取得
    public int getTotalSeconds() {
        return hour * 3600 + minute * 60 + second;
    }
    
    // 【発展解答】時刻の設定メソッド
    public void setTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        normalize();
    }
    
    // 時刻表示メソッド
    public void displayTime() {
        System.out.printf("%02d:%02d:%02d%n", hour, minute, second);
    }
    
    // 【発展解答】詳細表示メソッド
    public void displayDetailedTime() {
        System.out.printf("時刻: %02d:%02d:%02d%n", hour, minute, second);
        System.out.printf("総秒数: %d秒%n", getTotalSeconds());
        System.out.printf("午前/午後: %s%n", isAM() ? "午前" : "午後");
        System.out.printf("12時間制: %s%n", to12HourFormat());
    }
    
    // getter/setterメソッド
    public int getHour() {
        return hour;
    }
    
    public void setHour(int hour) {
        this.hour = hour;
        normalize();
    }
    
    public int getMinute() {
        return minute;
    }
    
    public void setMinute(int minute) {
        this.minute = minute;
        normalize();
    }
    
    public int getSecond() {
        return second;
    }
    
    public void setSecond(int second) {
        this.second = second;
        normalize();
    }
    
    // 【発展解答】時刻の比較メソッド
    public boolean isEarlierThan(Time other) {
        return this.getTotalSeconds() < other.getTotalSeconds();
    }
    
    public boolean isLaterThan(Time other) {
        return this.getTotalSeconds() > other.getTotalSeconds();
    }
    
    public boolean isSameTime(Time other) {
        return this.getTotalSeconds() == other.getTotalSeconds();
    }
    
    // 【発展解答】時刻の判定メソッド
    public boolean isAM() {
        return hour < 12;
    }
    
    public boolean isPM() {
        return hour >= 12;
    }
    
    public boolean isMidnight() {
        return hour == 0 && minute == 0 && second == 0;
    }
    
    public boolean isNoon() {
        return hour == 12 && minute == 0 && second == 0;
    }
    
    // 【発展解答】12時間制での表示
    public String to12HourFormat() {
        int displayHour = hour;
        String period = "AM";
        
        if (hour == 0) {
            displayHour = 12;
        } else if (hour == 12) {
            period = "PM";
        } else if (hour > 12) {
            displayHour = hour - 12;
            period = "PM";
        }
        
        return String.format("%02d:%02d:%02d %s", displayHour, minute, second, period);
    }
    
    // 【発展解答】時刻の差分計算
    public int getDifferenceInSeconds(Time other) {
        return Math.abs(this.getTotalSeconds() - other.getTotalSeconds());
    }
    
    public Time getDifferenceAsTime(Time other) {
        int diffSeconds = getDifferenceInSeconds(other);
        return new Time(diffSeconds);
    }
    
    // 【発展解答】時刻の経過時間計算
    public Time getElapsedTime(Time startTime) {
        int elapsed = this.getTotalSeconds() - startTime.getTotalSeconds();
        if (elapsed < 0) {
            elapsed += 24 * 60 * 60;  // 日をまたいだ場合
        }
        return new Time(elapsed);
    }
    
    // 【発展解答】時刻の丸め処理
    public void roundToNearestMinute() {
        if (second >= 30) {
            minute++;
            second = 0;
        } else {
            second = 0;
        }
        normalize();
    }
    
    public void roundToNearestHour() {
        if (minute >= 30) {
            hour++;
            minute = 0;
            second = 0;
        } else {
            minute = 0;
            second = 0;
        }
        normalize();
    }
    
    // toString()メソッドのオーバーライド
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
    
    // 【発展解答】equals()メソッドのオーバーライド
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Time time = (Time) obj;
        return hour == time.hour && 
               minute == time.minute && 
               second == time.second;
    }
    
    // 【発展解答】hashCode()メソッドのオーバーライド
    @Override
    public int hashCode() {
        return hour * 3600 + minute * 60 + second;
    }
    
    // 【発展解答】静的メソッド - 現在時刻の取得（シミュレーション）
    public static Time getCurrentTime() {
        // 実際の実装では System.currentTimeMillis() を使用
        return new Time(12, 0, 0);  // デモ用
    }
    
    // 【発展解答】静的メソッド - 文字列からのパース
    public static Time parse(String timeString) {
        try {
            String[] parts = timeString.split(":");
            if (parts.length == 3) {
                int h = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                int s = Integer.parseInt(parts[2]);
                return new Time(h, m, s);
            }
        } catch (NumberFormatException e) {
            System.out.println("時刻の形式が正しくありません: " + timeString);
        }
        return new Time();  // デフォルト値
    }
    
    // 【発展解答】静的メソッド - 時刻の妥当性チェック
    public static boolean isValidTime(int hour, int minute, int second) {
        return hour >= 0 && hour < 24 && 
               minute >= 0 && minute < 60 && 
               second >= 0 && second < 60;
    }
    
    // 【発展解答】静的メソッド - 二つの時刻の中間時刻
    public static Time getMiddleTime(Time time1, Time time2) {
        int total1 = time1.getTotalSeconds();
        int total2 = time2.getTotalSeconds();
        int middle = (total1 + total2) / 2;
        return new Time(middle);
    }
}