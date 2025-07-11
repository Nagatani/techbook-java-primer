/**
 * リストAC-13
 * コードスニペット
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (373行目)
 */

// データ構造を渡すが、全体を使用
public double calculateTax(TaxInfo taxInfo) {
    return taxInfo.getIncome() * taxInfo.getRate();
}