/**
 * リストAB-2
 * コードスニペット
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (44行目)
 */

// 構造化プログラミング：制御構造の明確化
public int calculateSum(int n) {
    if (n <= 0) {
        return 0;
    }
    int sum = 0;
    for (int i = 1; i <= n; i++) {
        sum += i;
    }
    return sum;
}