package chapter08.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * ShoppingCartクラスのテストクラス
 */
class ShoppingCartTest {
    
    private ShoppingCart cart;
    private ShoppingCart.Product product1;
    private ShoppingCart.Product product2;
    private ShoppingCart.Product product3;
    private ShoppingCart.Discount discount1;
    private ShoppingCart.Discount discount2;
    
    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
        
        product1 = new ShoppingCart.Product("P001", "ノートPC", new BigDecimal("80000"), "電子機器", 10);
        product2 = new ShoppingCart.Product("P002", "マウス", new BigDecimal("2000"), "電子機器", 50);
        product3 = new ShoppingCart.Product("P003", "本", new BigDecimal("1500"), "書籍", 20);
        
        discount1 = new ShoppingCart.Discount("SAVE10", "10%割引", ShoppingCart.Discount.DiscountType.PERCENTAGE, new BigDecimal("10"), new BigDecimal("5000"));
        discount2 = new ShoppingCart.Discount("FLAT1000", "1000円割引", ShoppingCart.Discount.DiscountType.FIXED_AMOUNT, new BigDecimal("1000"), new BigDecimal("10000"));
        
        cart.addProduct(product1);
        cart.addProduct(product2);
        cart.addProduct(product3);
        cart.addDiscount(discount1);
        cart.addDiscount(discount2);
    }
    
    @Test
    void testProductBasicProperties() {
        assertEquals("P001", product1.getId());
        assertEquals("ノートPC", product1.getName());
        assertEquals(new BigDecimal("80000"), product1.getPrice());
        assertEquals("電子機器", product1.getCategory());
        assertEquals(10, product1.getStockQuantity());
        assertTrue(product1.isAvailable());
    }
    
    @Test
    void testProductStockManagement() {
        assertTrue(product1.isInStock(5));
        assertTrue(product1.isInStock(10));
        assertFalse(product1.isInStock(15));
        
        product1.reduceStock(3);
        assertEquals(7, product1.getStockQuantity());
        
        product1.setStockQuantity(-5);
        assertEquals(0, product1.getStockQuantity());
    }
    
    @Test
    void testDiscountCalculation() {
        // パーセンテージ割引
        assertEquals(new BigDecimal("800.00"), discount1.calculateDiscount(new BigDecimal("8000")));
        assertEquals(BigDecimal.ZERO, discount1.calculateDiscount(new BigDecimal("1000"))); // 最低金額未満
        
        // 固定金額割引
        assertEquals(new BigDecimal("1000"), discount2.calculateDiscount(new BigDecimal("15000")));
        assertEquals(new BigDecimal("500"), discount2.calculateDiscount(new BigDecimal("500"))); // 割引額より小さい場合
        assertEquals(BigDecimal.ZERO, discount2.calculateDiscount(new BigDecimal("5000"))); // 最低金額未満
    }
    
    @Test
    void testAddToCart() {
        assertTrue(cart.addToCart("P001", 1));
        assertEquals(1, cart.getUniqueItemCount());
        assertEquals(1, cart.getTotalItemCount());
        
        // 同じ商品を再度追加
        assertTrue(cart.addToCart("P001", 2));
        assertEquals(1, cart.getUniqueItemCount());
        assertEquals(3, cart.getTotalItemCount());
        
        // 在庫を超える数量は追加できない
        assertFalse(cart.addToCart("P001", 20));
        
        // 存在しない商品は追加できない
        assertFalse(cart.addToCart("P999", 1));
    }
    
    @Test
    void testRemoveFromCart() {
        cart.addToCart("P001", 1);
        cart.addToCart("P002", 2);
        
        assertTrue(cart.removeFromCart("P001"));
        assertEquals(1, cart.getUniqueItemCount());
        assertEquals(2, cart.getTotalItemCount());
        
        // 存在しない商品は削除できない
        assertFalse(cart.removeFromCart("P999"));
    }
    
    @Test
    void testUpdateQuantity() {
        cart.addToCart("P001", 1);
        
        assertTrue(cart.updateQuantity("P001", 3));
        assertEquals(3, cart.getCartItem("P001").getQuantity());
        
        // 0以下の数量は削除
        assertTrue(cart.updateQuantity("P001", 0));
        assertNull(cart.getCartItem("P001"));
        
        // 在庫を超える数量は更新できない
        cart.addToCart("P001", 1);
        assertFalse(cart.updateQuantity("P001", 20));
    }
    
    @Test
    void testSubtotalCalculation() {
        cart.addToCart("P001", 1); // 80000
        cart.addToCart("P002", 2); // 4000
        cart.addToCart("P003", 3); // 4500
        
        assertEquals(new BigDecimal("88500"), cart.getSubtotal());
    }
    
    @Test
    void testDiscountApplication() {
        cart.addToCart("P001", 1); // 80000
        
        assertTrue(cart.applyDiscount("SAVE10"));
        assertTrue(cart.applyDiscount("FLAT1000"));
        
        // 最低金額未満の割引は適用できない
        cart.clear();
        cart.addToCart("P003", 1); // 1500
        assertFalse(cart.applyDiscount("SAVE10")); // 最低5000円
        assertFalse(cart.applyDiscount("FLAT1000")); // 最低10000円
    }
    
    @Test
    void testTotalCalculation() {
        cart.addToCart("P001", 1); // 80000
        cart.applyDiscount("SAVE10"); // 8000円割引
        
        assertEquals(new BigDecimal("72000"), cart.getTotal());
    }
    
    @Test
    void testCartOperations() {
        assertTrue(cart.isEmpty());
        
        cart.addToCart("P001", 1);
        cart.addToCart("P002", 2);
        
        assertFalse(cart.isEmpty());
        assertEquals(2, cart.getUniqueItemCount());
        assertEquals(3, cart.getTotalItemCount());
        
        List<ShoppingCart.CartItem> items = cart.getCartItems();
        assertEquals(2, items.size());
        
        cart.clear();
        assertTrue(cart.isEmpty());
    }
    
    @Test
    void testCartItemBasicProperties() {
        cart.addToCart("P001", 2);
        ShoppingCart.CartItem item = cart.getCartItem("P001");
        
        assertEquals(product1, item.getProduct());
        assertEquals(2, item.getQuantity());
        assertEquals(new BigDecimal("80000"), item.getUnitPrice());
        assertEquals(new BigDecimal("160000"), item.getTotalPrice());
    }
    
    @Test
    void testGetProductsByCategory() {
        List<ShoppingCart.Product> electronics = cart.getProductsByCategory("電子機器");
        assertEquals(2, electronics.size());
        
        List<ShoppingCart.Product> books = cart.getProductsByCategory("書籍");
        assertEquals(1, books.size());
        
        List<ShoppingCart.Product> none = cart.getProductsByCategory("存在しないカテゴリ");
        assertEquals(0, none.size());
    }
    
    @Test
    void testGetAppliedDiscounts() {
        cart.addToCart("P001", 1);
        cart.applyDiscount("SAVE10");
        cart.applyDiscount("FLAT1000");
        
        List<ShoppingCart.Discount> applied = cart.getAppliedDiscounts();
        assertEquals(2, applied.size());
        
        assertTrue(cart.removeDiscount("SAVE10"));
        applied = cart.getAppliedDiscounts();
        assertEquals(1, applied.size());
    }
    
    @Test
    void testGetInvoice() {
        String invoice = cart.getInvoice();
        assertTrue(invoice.contains("カートは空です"));
        
        cart.addToCart("P001", 1);
        cart.addToCart("P002", 2);
        cart.applyDiscount("SAVE10");
        
        invoice = cart.getInvoice();
        assertTrue(invoice.contains("ノートPC"));
        assertTrue(invoice.contains("マウス"));
        assertTrue(invoice.contains("小計"));
        assertTrue(invoice.contains("割引"));
        assertTrue(invoice.contains("合計"));
    }
    
    @Test
    void testProductEquality() {
        ShoppingCart.Product product1Copy = new ShoppingCart.Product("P001", "ノートPC", new BigDecimal("80000"), "電子機器", 10);
        ShoppingCart.Product differentProduct = new ShoppingCart.Product("P002", "ノートPC", new BigDecimal("80000"), "電子機器", 10);
        
        assertEquals(product1, product1Copy);
        assertNotEquals(product1, differentProduct);
        assertEquals(product1.hashCode(), product1Copy.hashCode());
    }
    
    @Test
    void testCartItemEquality() {
        ShoppingCart.CartItem item1 = new ShoppingCart.CartItem(product1, 1);
        ShoppingCart.CartItem item2 = new ShoppingCart.CartItem(product1, 2);
        ShoppingCart.CartItem item3 = new ShoppingCart.CartItem(product2, 1);
        
        assertEquals(item1, item2); // 同じ商品なら数量が違っても等しい
        assertNotEquals(item1, item3);
        assertEquals(item1.hashCode(), item2.hashCode());
    }
    
    @Test
    void testToStringMethods() {
        String productStr = product1.toString();
        assertTrue(productStr.contains("P001"));
        assertTrue(productStr.contains("ノートPC"));
        assertTrue(productStr.contains("80000"));
        
        String discountStr = discount1.toString();
        assertTrue(discountStr.contains("SAVE10"));
        assertTrue(discountStr.contains("10%割引"));
        
        ShoppingCart.CartItem item = new ShoppingCart.CartItem(product1, 2);
        String itemStr = item.toString();
        assertTrue(itemStr.contains("ノートPC"));
        assertTrue(itemStr.contains("2"));
    }
    
    @Test
    void testCategorySpecificDiscount() {
        ShoppingCart.Discount categoryDiscount = new ShoppingCart.Discount(
            "ELECTRONICS20", "電子機器20%割引", 
            ShoppingCart.Discount.DiscountType.PERCENTAGE, 
            new BigDecimal("20"), 
            BigDecimal.ZERO, 
            "電子機器"
        );
        
        cart.addDiscount(categoryDiscount);
        cart.addToCart("P001", 1); // 電子機器
        cart.addToCart("P003", 1); // 書籍
        cart.applyDiscount("ELECTRONICS20");
        
        // 電子機器のみに割引が適用されることを確認
        BigDecimal totalDiscount = cart.getTotalDiscount();
        assertEquals(new BigDecimal("16000.00"), totalDiscount); // 80000 * 0.2
    }
    
    @Test
    void testComplexScenario() {
        // 複合的なシナリオのテスト
        cart.addToCart("P001", 1); // 80000
        cart.addToCart("P002", 3); // 6000
        cart.addToCart("P003", 2); // 3000
        
        cart.applyDiscount("SAVE10"); // 10%割引
        
        BigDecimal expectedSubtotal = new BigDecimal("89000");
        BigDecimal expectedDiscount = new BigDecimal("8900.00");
        BigDecimal expectedTotal = new BigDecimal("80100.00");
        
        assertEquals(expectedSubtotal, cart.getSubtotal());
        assertEquals(expectedDiscount, cart.getTotalDiscount());
        assertEquals(expectedTotal, cart.getTotal());
    }
}