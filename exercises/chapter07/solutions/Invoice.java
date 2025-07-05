package chapter07.solutions;

/**
 * 請求書を表すクラス
 * 
 * Payableインターフェースを実装し、給与計算システムで
 * 統一的に扱えるようにしています。
 */
public class Invoice implements Payable {
    
    private String partNumber;
    private String partDescription;
    private int quantity;
    private double pricePerItem;
    
    /**
     * 請求書のコンストラクタ
     * 
     * @param partNumber 部品番号
     * @param partDescription 部品説明
     * @param quantity 数量
     * @param pricePerItem 単価
     */
    public Invoice(String partNumber, String partDescription, int quantity, double pricePerItem) {
        this.partNumber = partNumber;
        this.partDescription = partDescription;
        this.quantity = Math.max(0, quantity);
        this.pricePerItem = Math.max(0.0, pricePerItem);
    }
    
    /**
     * 支払い金額（請求額）を計算する
     * 数量 × 単価を返す
     * 
     * @return 支払い金額
     */
    @Override
    public double getPaymentAmount() {
        return quantity * pricePerItem;
    }
    
    /**
     * 支払い対象の名前を取得する
     * 
     * @return 部品番号
     */
    @Override
    public String getPaymentName() {
        return partNumber;
    }
    
    /**
     * 請求書の詳細情報を取得する
     * 
     * @return 請求書の詳細情報
     */
    @Override
    public String getPaymentDescription() {
        return String.format(
            "請求書: %s\n" +
            "部品説明: %s\n" +
            "数量: %d個\n" +
            "単価: %.2f円\n" +
            "総額: %.2f円",
            partNumber, partDescription, quantity, pricePerItem, getPaymentAmount()
        );
    }
    
    // Getters
    public String getPartNumber() { return partNumber; }
    public String getPartDescription() { return partDescription; }
    public int getQuantity() { return quantity; }
    public double getPricePerItem() { return pricePerItem; }
    
    // Setters
    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }
    
    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = Math.max(0.0, pricePerItem);
    }
}