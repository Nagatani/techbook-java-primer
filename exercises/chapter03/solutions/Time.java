/**
 * 時刻を表すクラス（課題7の解答例）
 */
public class Time {
    // インスタンス変数を宣言する
    int hour;
    int minute;
    int second;
    
    // 時のみを設定するコンストラクタ
    public Time(int hour) {
        this(hour, 0, 0);
    }
    
    // 時分を設定するコンストラクタ
    public Time(int hour, int minute) {
        this(hour, minute, 0);
    }
    
    // 時分秒を設定するコンストラクタ
    public Time(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        normalize();
    }
    
    // 秒を追加するメソッド
    public void addTime(int seconds) {
        this.second += seconds;
        normalize();
    }
    
    // 分を追加するメソッド（オーバーロード）
    public void addTime(int minutes, boolean isMinute) {
        if (isMinute) {
            this.minute += minutes;
        } else {
            this.second += minutes;
        }
        normalize();
    }
    
    // 時分秒を追加するメソッド（オーバーロード）
    public void addTime(int hours, int minutes, int seconds) {
        this.hour += hours;
        this.minute += minutes;
        this.second += seconds;
        normalize();
    }
    
    // 時刻の正規化メソッド
    private void normalize() {
        // 秒が60以上の場合、分に繰り上げ
        if (this.second >= 60) {
            this.minute += this.second / 60;
            this.second = this.second % 60;
        }
        
        // 分が60以上の場合、時に繰り上げ
        if (this.minute >= 60) {
            this.hour += this.minute / 60;
            this.minute = this.minute % 60;
        }
        
        // 時が24以上の場合、24時間制に正規化
        if (this.hour >= 24) {
            this.hour = this.hour % 24;
        }
    }
    
    // 時刻表示メソッド
    public void showTime() {
        IO.println("%02d:%02d:%02d".formatted(hour, minute, second));
    }
}