package chapter07.solutions;

/**
 * 請求書（課題2の解答例）
 */
public class Invoice implements Payable {
    private String invoiceNumber;
    private String productDescription;
    private int quantity;
    private double pricePerItem;

    // 負の数量・単価は0にする
    public Invoice(String invoiceNumber, String productDescription, int quantity, double pricePerItem) {
        this.invoiceNumber = invoiceNumber;
        this.productDescription = productDescription;
        this.quantity = quantity < 0 ? 0 : quantity;
        this.pricePerItem = pricePerItem < 0 ? 0.0 : pricePerItem;
    }

    // 数量 × 単価
    @Override
    public double calculatePayment() {
        return quantity * pricePerItem;
    }

    public void displayInvoiceInfo() {
        IO.println("請求書情報:");
        IO.println("  番号: " + invoiceNumber);
        IO.println("  商品: " + productDescription);
        IO.println("  数量: " + quantity + "個、単価: " + "%,.0f".formatted(pricePerItem) + "円");
        IO.println("  合計: " + "%,.0f".formatted(calculatePayment()) + "円");
    }

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
