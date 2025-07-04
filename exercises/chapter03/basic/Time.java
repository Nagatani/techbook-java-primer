/**
 * 第3章 基本課題4: Timeクラスの総合実装
 * 
 * 課題: 時刻を表すTimeクラスを作成し、コンストラクタとオーバーロードを総合的に実装してください。
 * 
 * 要求仕様:
 * - インスタンス変数: hour（int）, minute（int）, second（int）
 * - 複数のコンストラクタ（時のみ、時分、時分秒）
 * - addTime()メソッドのオーバーロード（秒追加、分追加、時分秒追加）
 * - 時刻の正規化処理（60秒=1分、60分=1時間）
 * 
 * 評価ポイント:
 * - 複雑な状態管理の実装
 * - オーバーロードの適切な使い分け
 * - 時刻の正規化アルゴリズム
 */
public class Time {
    // TODO: インスタンス変数を宣言してください
    // int hour;
    // int minute;
    // int second;
    
    // TODO: 時のみを設定するコンストラクタを実装してください
    // public Time(int hour) {
    //     this(hour, 0, 0);
    // }
    
    // TODO: 時分を設定するコンストラクタを実装してください
    // public Time(int hour, int minute) {
    //     this(hour, minute, 0);
    // }
    
    // TODO: 時分秒を設定するコンストラクタを実装してください
    // public Time(int hour, int minute, int second) {
    //     this.hour = hour;
    //     this.minute = minute;
    //     this.second = second;
    //     normalize();
    // }
    
    // TODO: 秒を追加するメソッドを実装してください
    // public void addTime(int seconds) {
    //     this.second += seconds;
    //     normalize();
    // }
    
    // TODO: 分を追加するメソッドを実装してください（オーバーロード）
    // public void addTime(int minutes, boolean isMinute) {
    //     if (isMinute) {
    //         this.minute += minutes;
    //     } else {
    //         this.second += minutes;
    //     }
    //     normalize();
    // }
    
    // TODO: 時分秒を追加するメソッドを実装してください（オーバーロード）
    // public void addTime(int hours, int minutes, int seconds) {
    //     this.hour += hours;
    //     this.minute += minutes;
    //     this.second += seconds;
    //     normalize();
    // }
    
    // TODO: 時刻の正規化メソッドを実装してください
    // private void normalize() {
    //     // 秒が60以上の場合、分に繰り上げ
    //     if (this.second >= 60) {
    //         this.minute += this.second / 60;
    //         this.second = this.second % 60;
    //     }
    //     
    //     // 分が60以上の場合、時に繰り上げ
    //     if (this.minute >= 60) {
    //         this.hour += this.minute / 60;
    //         this.minute = this.minute % 60;
    //     }
    //     
    //     // 時が24以上の場合、24時間制に正規化
    //     if (this.hour >= 24) {
    //         this.hour = this.hour % 24;
    //     }
    // }
    
    // TODO: 時刻表示メソッドを実装してください
    // public void showTime() {
    //     System.out.printf("%02d:%02d:%02d%n", hour, minute, second);
    // }
}