package chapter09.solutions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShoppingCartTest {
    private Product laptop;
    private Product mouse;
    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        laptop = new Product("P001", "ノートパソコン", 80000, 5);
        mouse = new Product("P002", "マウス", 2000, 10);
        cart = new ShoppingCart("山田太郎");
    }

    @Test
    void 在庫を減らす() {
        assertTrue(laptop.reduceStock(2));
        assertEquals(3, laptop.getStock());
        assertFalse(laptop.reduceStock(4));
        assertEquals(3, laptop.getStock());
    }

    @Test
    void 小計は単価と数量の積() {
        assertEquals(6000.0, new CartItem(mouse, 3).getSubtotal(), 0.001);
    }

    @Test
    void 同じ商品を追加すると数量が増える() {
        cart.addItem(mouse, 2);
        cart.addItem(mouse, 3);
        assertEquals(5, cart.getTotalItemCount());
        assertEquals(10000.0, cart.getTotalAmount(), 0.001);
    }

    @Test
    void 在庫を超える追加は断る() {
        assertTrue(cart.addItem(laptop, 4));
        assertFalse(cart.addItem(laptop, 2));
        assertEquals(4, cart.getTotalItemCount());
    }

    @Test
    void 数量を0にすると商品が除かれる() {
        cart.addItem(mouse, 2);
        assertTrue(cart.updateQuantity("P002", 0));
        assertFalse(cart.containsProduct("P002"));
        assertFalse(cart.updateQuantity("P009", 1));
    }

    @Test
    void 総アイテム数は数量の合計() {
        cart.addItem(laptop, 1);
        cart.addItem(mouse, 3);
        assertEquals(4, cart.getTotalItemCount());
    }

    @Test
    void 削除とクリア() {
        cart.addItem(laptop, 1);
        assertTrue(cart.removeItem("P001"));
        assertFalse(cart.removeItem("P001"));
        cart.addItem(mouse, 1);
        cart.clearCart();
        assertTrue(cart.isEmpty());
    }
}
