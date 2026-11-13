/**
 * 素数かどうかを調べるクラス（画面に依存しないロジック）
 */
public class PrimeMath {

    private PrimeMath() {
    }

    /** nが素数ならtrue。2から√nまでの数で割り切れるかを調べる */
    public static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int d = 2; (long) d * d <= n; d++) {
            if (n % d == 0) {
                return false;
            }
        }
        return true;
    }

    /** 2からlimitまでの素数の個数 */
    public static int countPrimes(int limit) {
        int count = 0;
        for (int n = 2; n <= limit; n++) {
            if (isPrime(n)) {
                count++;
            }
        }
        return count;
    }
}
