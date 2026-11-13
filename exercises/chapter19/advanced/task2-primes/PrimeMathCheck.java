/**
 * PrimeMathの動作確認
 */
public class PrimeMathCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== PrimeMathの動作確認 ===");
        check("1は素数でない", false, PrimeMath.isPrime(1));
        check("2は素数", true, PrimeMath.isPrime(2));
        check("9は素数でない", false, PrimeMath.isPrime(9));
        check("97は素数", true, PrimeMath.isPrime(97));
        check("2147483647（intの最大値）は素数", true, PrimeMath.isPrime(Integer.MAX_VALUE));
        check("100までの素数は25個", 25, PrimeMath.countPrimes(100));
        check("10000までの素数は1229個", 1229, PrimeMath.countPrimes(10_000));
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, Object expected, Object actual) {
        total++;
        if (expected.equals(actual)) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
