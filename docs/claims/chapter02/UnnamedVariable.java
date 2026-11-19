// 第2章：使わない変数を _ で表す（Java 22で正式、JEP 456）
public class UnnamedVariable {
    static int count(Iterable<String> items) {
        int n = 0;
        for (String _ : items) {
            n++;
        }
        return n;
    }
}
