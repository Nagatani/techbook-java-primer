package chapter07.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 給与計算システムのテストクラス
 */
class PayrollTest {
    
    private Employee employee;
    private Invoice invoice;
    
    @BeforeEach
    void setUp() {
        employee = new Employee("田中太郎", 300000);
        invoice = new Invoice("P001", "プリンタ", 2, 50000);
    }
    
    @Test
    void testEmployeeBasicPayment() {
        // 残業なしの場合の基本給
        assertEquals(300000, employee.getPaymentAmount(), 0.01);
        assertEquals("田中太郎", employee.getPaymentName());
    }
    
    @Test
    void testEmployeeWithOvertime() {
        // 残業ありの場合の給与計算
        employee.setOvertimeHours(10);
        
        // 基本給: 300000円, 時給: 1875円, 残業代: 1875 * 10 * 1.25 = 23437.5円
        double expectedPayment = 300000 + (300000 / 160.0) * 10 * 1.25;
        assertEquals(expectedPayment, employee.getPaymentAmount(), 0.01);
    }
    
    @Test
    void testEmployeeOvertimeRate() {
        employee.setOvertimeHours(8);
        employee.setOvertimeRate(1.5); // 50%増し
        
        double expectedPayment = 300000 + (300000 / 160.0) * 8 * 1.5;
        assertEquals(expectedPayment, employee.getPaymentAmount(), 0.01);
    }
    
    @Test
    void testEmployeeNegativeOvertimeHours() {
        employee.setOvertimeHours(-5);
        assertEquals(0.0, employee.getOvertimeHours());
        assertEquals(300000, employee.getPaymentAmount(), 0.01);
    }
    
    @Test
    void testInvoiceBasicPayment() {
        // 請求書の基本計算
        assertEquals(100000, invoice.getPaymentAmount(), 0.01);
        assertEquals("P001", invoice.getPaymentName());
    }
    
    @Test
    void testInvoiceQuantityUpdate() {
        invoice.setQuantity(5);
        assertEquals(250000, invoice.getPaymentAmount(), 0.01);
    }
    
    @Test
    void testInvoicePriceUpdate() {
        invoice.setPricePerItem(75000);
        assertEquals(150000, invoice.getPaymentAmount(), 0.01);
    }
    
    @Test
    void testInvoiceNegativeValues() {
        invoice.setQuantity(-3);
        invoice.setPricePerItem(-1000);
        
        assertEquals(0, invoice.getQuantity());
        assertEquals(0.0, invoice.getPricePerItem());
        assertEquals(0.0, invoice.getPaymentAmount());
    }
    
    @Test
    void testPayablePolymorphism() {
        // Payableインターフェースを通じた多態性のテスト
        Payable[] payables = {employee, invoice};
        
        double totalPayment = 0;
        for (Payable payable : payables) {
            totalPayment += payable.getPaymentAmount();
            assertNotNull(payable.getPaymentName());
            assertNotNull(payable.getPaymentDescription());
        }
        
        assertEquals(400000, totalPayment, 0.01);
    }
    
    @Test
    void testPaymentDescriptions() {
        // 支払い説明の詳細テスト
        String employeeDesc = employee.getPaymentDescription();
        assertTrue(employeeDesc.contains("従業員"));
        assertTrue(employeeDesc.contains("田中太郎"));
        assertTrue(employeeDesc.contains("基本給"));
        
        String invoiceDesc = invoice.getPaymentDescription();
        assertTrue(invoiceDesc.contains("請求書"));
        assertTrue(invoiceDesc.contains("P001"));
        assertTrue(invoiceDesc.contains("プリンタ"));
    }
    
    @Test
    void testDefaultMethod() {
        // デフォルトメソッドのテスト
        Payable payable = new Employee("テスト", 100000);
        String description = payable.getPaymentDescription();
        
        // デフォルトメソッドがオーバーライドされていることを確認
        assertTrue(description.contains("従業員"));
        assertFalse(description.contains("支払い対象:")); // デフォルトの形式ではない
    }
}