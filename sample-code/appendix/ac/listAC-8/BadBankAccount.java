/**
 * リストAC-8
 * BadBankAccountクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (223行目)
 */

public class BadBankAccount {
    public double[] transactionHistory; // 実装詳細の露出
    public int transactionCount;        // 不変条件の破綻可能性
    
    public void addTransaction(double amount) {
        // 配列サイズチェックなし - バグの温床
        transactionHistory[transactionCount++] = amount;
    }
}