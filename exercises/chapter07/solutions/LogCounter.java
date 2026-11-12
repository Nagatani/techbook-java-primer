package chapter07.solutions;

/**
 * ログの件数を数える（課題4の解答例）
 */
class LogCounter {
    private static int count = 0;

    private LogCounter() {
    }

    static void increment() {
        count++;
    }

    static int count() {
        return count;
    }
}
