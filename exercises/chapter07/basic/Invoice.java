package chapter07.basic;

/**
 * 請求書を表すクラス
 * 
 * Payableインターフェイスを実装して、請求金額計算機能を提供します。
 */
public class Invoice implements Payable {
    // 請求書番号
    private String invoiceNumber;
    // 商品説明
    private String productDescription;
    // 数量
    private int quantity;
    // 単価
    private double pricePerItem;
    
    /**
     * コンストラクタ
     * @param invoiceNumber 請求書番号
     * @param productDescription 商品説明
     * @param quantity 数量
     * @param pricePerItem 単価
     */
    public Invoice(String invoiceNumber, String productDescription, 
                   int quantity, double pricePerItem) {
        // TODO: 各フィールドを初期化してください
        // 注意: quantityが負の場合は0に、pricePerItemが負の場合は0.0に設定してください
        
    }
    
    /**
     * 請求金額（支払い金額）を計算する
     * TODO: 数量 × 単価 で計算してください
     * @return 請求金額
     */
    @Override
    public double calculatePayment() {
        // TODO: 実装してください
        return 0.0;
    }
    
    /**
     * 請求書情報を表示する
     * TODO: 請求書番号、商品説明、数量、単価、合計金額を表示してください
     */
    public void displayInvoiceInfo() {
        // TODO: 実装してください
        System.out.println("請求書情報:");
        // 請求書番号、商品説明、数量、単価、合計金額を表示
    }
    
    // ゲッターメソッド
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    
    public String getProductDescription() {
        return productDescription;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public double getPricePerItem() {
        return pricePerItem;
    }
}