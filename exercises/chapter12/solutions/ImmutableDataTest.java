package chapter12.solutions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.Map;

/**
 * ImmutableDataクラスのテストクラス
 */
public class ImmutableDataTest {
    
    @Test
    void testPoint() {
        ImmutableData.Point point = new ImmutableData.Point(3.0, 4.0);
        
        assertEquals(3.0, point.x());
        assertEquals(4.0, point.y());
        assertEquals(5.0, point.distanceFromOrigin(), 0.001);
        
        // 点の移動
        ImmutableData.Point translated = point.translate(1.0, 2.0);
        assertEquals(4.0, translated.x());
        assertEquals(6.0, translated.y());
        
        // 元の点は変更されていない
        assertEquals(3.0, point.x());
        assertEquals(4.0, point.y());
    }
    
    @Test
    void testPointValidation() {
        assertThrows(IllegalArgumentException.class, () -> 
            new ImmutableData.Point(Double.NaN, 0.0));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new ImmutableData.Point(0.0, Double.POSITIVE_INFINITY));
    }
    
    @Test
    void testPointOperations() {
        ImmutableData.Point point = new ImmutableData.Point(2.0, 3.0);
        ImmutableData.Point other = new ImmutableData.Point(5.0, 7.0);
        
        assertEquals(5.0, point.distanceFrom(other), 0.001);
        
        // スケーリング
        ImmutableData.Point scaled = point.scale(2.0);
        assertEquals(4.0, scaled.x());
        assertEquals(6.0, scaled.y());
        
        // 回転（90度）
        ImmutableData.Point rotated = point.rotate(Math.PI / 2);
        assertEquals(-3.0, rotated.x(), 0.001);
        assertEquals(2.0, rotated.y(), 0.001);
    }
    
    @Test
    void testPointConstants() {
        assertEquals(0.0, ImmutableData.Point.ORIGIN.x());
        assertEquals(0.0, ImmutableData.Point.ORIGIN.y());
        
        assertEquals(1.0, ImmutableData.Point.UNIT_X.x());
        assertEquals(0.0, ImmutableData.Point.UNIT_X.y());
        
        assertEquals(0.0, ImmutableData.Point.UNIT_Y.x());
        assertEquals(1.0, ImmutableData.Point.UNIT_Y.y());
    }
    
    @Test
    void testRectangle() {
        ImmutableData.Rectangle rectangle = new ImmutableData.Rectangle(1.0, 2.0, 5.0, 6.0);
        
        assertEquals(4.0, rectangle.width());
        assertEquals(4.0, rectangle.height());
        assertEquals(16.0, rectangle.area());
        assertEquals(16.0, rectangle.perimeter());
        
        ImmutableData.Point center = rectangle.center();
        assertEquals(3.0, center.x());
        assertEquals(4.0, center.y());
    }
    
    @Test
    void testRectangleValidation() {
        assertThrows(IllegalArgumentException.class, () -> 
            new ImmutableData.Rectangle(5.0, 2.0, 1.0, 6.0)); // 無効な座標
        
        assertThrows(IllegalArgumentException.class, () -> 
            new ImmutableData.Rectangle(1.0, 6.0, 5.0, 2.0)); // 無効な座標
    }
    
    @Test
    void testRectangleContains() {
        ImmutableData.Rectangle rectangle = new ImmutableData.Rectangle(0.0, 0.0, 10.0, 10.0);
        
        assertTrue(rectangle.contains(new ImmutableData.Point(5.0, 5.0)));
        assertTrue(rectangle.contains(new ImmutableData.Point(0.0, 0.0))); // 境界
        assertTrue(rectangle.contains(new ImmutableData.Point(10.0, 10.0))); // 境界
        
        assertFalse(rectangle.contains(new ImmutableData.Point(-1.0, 5.0)));
        assertFalse(rectangle.contains(new ImmutableData.Point(5.0, 11.0)));
    }
    
    @Test
    void testRectangleOperations() {
        ImmutableData.Rectangle rectangle = new ImmutableData.Rectangle(0.0, 0.0, 4.0, 4.0);
        
        // 移動
        ImmutableData.Rectangle translated = rectangle.translate(2.0, 3.0);
        assertEquals(2.0, translated.topLeft().x());
        assertEquals(3.0, translated.topLeft().y());
        assertEquals(6.0, translated.bottomRight().x());
        assertEquals(7.0, translated.bottomRight().y());
        
        // スケーリング
        ImmutableData.Rectangle scaled = rectangle.scale(2.0);
        assertEquals(8.0, scaled.width());
        assertEquals(8.0, scaled.height());
    }
    
    @Test
    void testProduct() {
        ImmutableData.Product product = new ImmutableData.Product(
            "PROD001", "Laptop", "Electronics", 999.99,
            Set.of("portable", "computer"), 
            Map.of("brand", "TechCorp", "warranty", "2 years")
        );
        
        assertEquals("PROD001", product.id());
        assertEquals("Laptop", product.name());
        assertEquals("Electronics", product.category());
        assertEquals(999.99, product.price());
        assertTrue(product.hasTag("portable"));
        assertTrue(product.hasAttribute("brand"));
        assertEquals("TechCorp", product.getAttribute("brand"));
    }
    
    @Test
    void testProductValidation() {
        assertThrows(NullPointerException.class, () -> 
            new ImmutableData.Product(null, "Name", "Category", 100.0));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new ImmutableData.Product("", "Name", "Category", 100.0));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new ImmutableData.Product("ID", "Name", "Category", -10.0));
    }
    
    @Test
    void testProductOperations() {
        ImmutableData.Product product = new ImmutableData.Product("PROD001", "Laptop", "Electronics", 999.99);
        
        // タグの追加
        ImmutableData.Product withTag = product.addTag("premium");
        assertTrue(withTag.hasTag("premium"));
        assertFalse(product.hasTag("premium")); // 元のオブジェクトは変更されていない
        
        // 属性の追加
        ImmutableData.Product withAttribute = product.addAttribute("color", "silver");
        assertEquals("silver", withAttribute.getAttribute("color"));
        assertNull(product.getAttribute("color"));
        
        // 価格の更新
        ImmutableData.Product withNewPrice = product.withPrice(799.99);
        assertEquals(799.99, withNewPrice.price());
        assertEquals(999.99, product.price());
        
        // 割引価格の計算
        assertEquals(799.992, product.getDiscountedPrice(0.2), 0.001);
    }
    
    @Test
    void testProductImmutability() {
        Set<String> tags = new java.util.HashSet<>();
        tags.add("tag1");
        Map<String, String> attributes = new java.util.HashMap<>();
        attributes.put("key1", "value1");
        
        ImmutableData.Product product = new ImmutableData.Product(
            "PROD001", "Test", "Category", 100.0, tags, attributes
        );
        
        // 元のコレクションを変更しても、Productは影響を受けない
        tags.add("tag2");
        attributes.put("key2", "value2");
        
        assertEquals(1, product.tags().size());
        assertEquals(1, product.attributes().size());
        
        // 返されたコレクションは変更できない
        assertThrows(UnsupportedOperationException.class, () -> 
            product.tags().add("new tag"));
        assertThrows(UnsupportedOperationException.class, () -> 
            product.attributes().put("new key", "new value"));
    }
    
    @Test
    void testOrderItem() {
        ImmutableData.Product product = new ImmutableData.Product("PROD001", "Test", "Category", 100.0);
        ImmutableData.OrderItem item = new ImmutableData.OrderItem(product, 2, 95.0);
        
        assertEquals(product, item.product());
        assertEquals(2, item.quantity());
        assertEquals(95.0, item.unitPrice());
        assertEquals(190.0, item.totalPrice());
        
        // 便利なコンストラクタ
        ImmutableData.OrderItem item2 = new ImmutableData.OrderItem(product, 3);
        assertEquals(100.0, item2.unitPrice()); // 商品価格を使用
        assertEquals(300.0, item2.totalPrice());
    }
    
    @Test
    void testOrderItemValidation() {
        ImmutableData.Product product = new ImmutableData.Product("PROD001", "Test", "Category", 100.0);
        
        assertThrows(NullPointerException.class, () -> 
            new ImmutableData.OrderItem(null, 1, 100.0));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new ImmutableData.OrderItem(product, 0, 100.0));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new ImmutableData.OrderItem(product, 1, -10.0));
    }
    
    @Test
    void testOrderItemOperations() {
        ImmutableData.Product product = new ImmutableData.Product("PROD001", "Test", "Category", 100.0);
        ImmutableData.OrderItem item = new ImmutableData.OrderItem(product, 2, 95.0);
        
        // 数量の変更
        ImmutableData.OrderItem newQuantity = item.withQuantity(5);
        assertEquals(5, newQuantity.quantity());
        assertEquals(2, item.quantity()); // 元のオブジェクトは変更されていない
        
        // 単価の変更
        ImmutableData.OrderItem newPrice = item.withUnitPrice(90.0);
        assertEquals(90.0, newPrice.unitPrice());
        assertEquals(95.0, item.unitPrice());
        
        // 割引の適用
        ImmutableData.OrderItem discounted = item.applyDiscount(0.1);
        assertEquals(85.5, discounted.unitPrice(), 0.001);
        assertEquals(171.0, discounted.totalPrice(), 0.001);
    }
    
    @Test
    void testOrder() {
        ImmutableData.Product product1 = new ImmutableData.Product("PROD001", "Laptop", "Electronics", 999.99);
        ImmutableData.Product product2 = new ImmutableData.Product("PROD002", "Mouse", "Electronics", 29.99);
        
        ImmutableData.OrderItem item1 = new ImmutableData.OrderItem(product1, 1);
        ImmutableData.OrderItem item2 = new ImmutableData.OrderItem(product2, 2);
        
        LocalDateTime orderDate = LocalDateTime.now();
        ImmutableData.Order order = new ImmutableData.Order(
            "ORDER001", "CUSTOMER001", orderDate, List.of(item1, item2), "PENDING"
        );
        
        assertEquals("ORDER001", order.orderId());
        assertEquals("CUSTOMER001", order.customerId());
        assertEquals(orderDate, order.orderDate());
        assertEquals(2, order.itemCount());
        assertEquals(3, order.totalQuantity());
        assertEquals(1059.97, order.totalAmount(), 0.001);
        assertTrue(order.containsProduct(product1));
        assertTrue(order.containsCategory("Electronics"));
    }
    
    @Test
    void testOrderValidation() {
        ImmutableData.Product product = new ImmutableData.Product("PROD001", "Test", "Category", 100.0);
        ImmutableData.OrderItem item = new ImmutableData.OrderItem(product, 1);
        
        assertThrows(NullPointerException.class, () -> 
            new ImmutableData.Order(null, "CUSTOMER001", LocalDateTime.now(), List.of(item), "PENDING"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new ImmutableData.Order("", "CUSTOMER001", LocalDateTime.now(), List.of(item), "PENDING"));
        
        assertThrows(IllegalArgumentException.class, () -> 
            new ImmutableData.Order("ORDER001", "CUSTOMER001", LocalDateTime.now(), List.of(), "PENDING"));
    }
    
    @Test
    void testOrderOperations() {
        ImmutableData.Product product1 = new ImmutableData.Product("PROD001", "Test1", "Category", 100.0);
        ImmutableData.Product product2 = new ImmutableData.Product("PROD002", "Test2", "Category", 200.0);
        
        ImmutableData.OrderItem item1 = new ImmutableData.OrderItem(product1, 1);
        ImmutableData.OrderItem item2 = new ImmutableData.OrderItem(product2, 2);
        
        ImmutableData.Order order = new ImmutableData.Order("ORDER001", "CUSTOMER001", List.of(item1));
        
        // 項目の追加
        ImmutableData.Order withItem = order.addItem(item2);
        assertEquals(2, withItem.itemCount());
        assertEquals(1, order.itemCount()); // 元のオブジェクトは変更されていない
        
        // 項目の削除
        ImmutableData.Order withoutItem = withItem.removeItem(item1);
        assertEquals(1, withoutItem.itemCount());
        
        // ステータスの変更
        ImmutableData.Order confirmed = order.confirm();
        assertEquals("CONFIRMED", confirmed.status());
        assertEquals("PENDING", order.status());
        
        ImmutableData.Order completed = order.complete();
        assertEquals("COMPLETED", completed.status());
        assertTrue(completed.isCompleted());
        
        ImmutableData.Order cancelled = order.cancel();
        assertEquals("CANCELLED", cancelled.status());
        assertTrue(cancelled.isCancelled());
    }
    
    @Test
    void testOrderImmutability() {
        ImmutableData.Product product = new ImmutableData.Product("PROD001", "Test", "Category", 100.0);
        ImmutableData.OrderItem item = new ImmutableData.OrderItem(product, 1);
        
        List<ImmutableData.OrderItem> items = new java.util.ArrayList<>();
        items.add(item);
        
        ImmutableData.Order order = new ImmutableData.Order("ORDER001", "CUSTOMER001", items);
        
        // 元のリストを変更しても、Orderは影響を受けない
        items.add(new ImmutableData.OrderItem(product, 2));
        assertEquals(1, order.itemCount());
        
        // 返されたリストは変更できない
        assertThrows(UnsupportedOperationException.class, () -> 
            order.items().add(new ImmutableData.OrderItem(product, 3)));
    }
    
    @Test
    void testOrderBuilder() {
        ImmutableData.Product product1 = new ImmutableData.Product("PROD001", "Test1", "Category", 100.0);
        ImmutableData.Product product2 = new ImmutableData.Product("PROD002", "Test2", "Category", 200.0);
        
        ImmutableData.Order order = ImmutableData.orderBuilder()
            .orderId("ORDER001")
            .customerId("CUSTOMER001")
            .addItem(product1, 1)
            .addItem(product2, 2, 180.0)
            .status("CONFIRMED")
            .build();
        
        assertEquals("ORDER001", order.orderId());
        assertEquals("CUSTOMER001", order.customerId());
        assertEquals(2, order.itemCount());
        assertEquals("CONFIRMED", order.status());
        assertEquals(460.0, order.totalAmount(), 0.001); // 100 + 180*2
    }
    
    @Test
    void testOrderWithWeightAttribute() {
        ImmutableData.Product product = new ImmutableData.Product("PROD001", "Test", "Category", 100.0)
            .addAttribute("weight", "2.5");
        
        ImmutableData.OrderItem item = new ImmutableData.OrderItem(product, 3);
        ImmutableData.Order order = new ImmutableData.Order("ORDER001", "CUSTOMER001", List.of(item));
        
        assertEquals(7.5, order.getTotalWeight(), 0.001);
    }
}