/**
 * リストAC-16
 * PrimeCheckerクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (428行目)
 */

// 単一の機能に特化
public class PrimeChecker {
    public boolean isPrime(int number) {
        if (number < 2) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
}