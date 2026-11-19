// 第15章：count++（インスタンスのフィールド）のバイトコード。読む・足す・書くの別々の命令になる
class UnsafeCounter {
    private int count = 0;

    public void increment() {
        count++;
    }
}
