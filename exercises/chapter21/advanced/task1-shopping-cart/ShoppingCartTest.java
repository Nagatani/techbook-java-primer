import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ShoppingCartのテスト。上から順に、TDDで1つずつ書いたテストを並べている
 */
@DisplayName("買い物かご")
class ShoppingCartTest {
    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    @Nested
    @DisplayName("小計")
    class Subtotal {
        @Test
        @DisplayName("1. 空のかごの小計は0円")
        void emptyCart() {
            assertEquals(0, cart.subtotal());
        }

        @Test
        @DisplayName("2. 1つ入れると、その単価が小計になる")
        void oneItem() {
            cart.add("ノート", 150, 1);
            assertEquals(150, cart.subtotal());
        }

        @Test
        @DisplayName("3. 数量を指定すると、単価×数量が小計になる")
        void quantity() {
            cart.add("ノート", 150, 3);
            assertEquals(450, cart.subtotal());
        }

        @Test
        @DisplayName("4. 種類の違う商品は小計に足される")
        void differentItems() {
            cart.add("ノート", 150, 3);
            cart.add("ボールペン", 120, 2);
            assertEquals(690, cart.subtotal());
        }

        @Test
        @DisplayName("5. 同じ商品を2回入れると数量がまとまる")
        void sameItemTwice() {
            // Arrange
            cart.add("ノート", 150, 1);
            // Act
            cart.add("ノート", 150, 2);
            // Assert
            assertAll(
                () -> assertEquals(3, cart.itemCount()),
                () -> assertEquals(450, cart.subtotal()));
        }
    }

    @Nested
    @DisplayName("割引")
    class Discount {
        @Test
        @DisplayName("6. 小計が4999円なら割引はない")
        void justBelowThreshold() {
            cart.add("参考書", 4999, 1);
            assertEquals(0, cart.discount());
            assertEquals(4999, cart.total());
        }

        @Test
        @DisplayName("7. 小計が5000円ちょうどなら10%引き")
        void atThreshold() {
            cart.add("参考書", 2500, 2);
            assertEquals(500, cart.discount());
            assertEquals(4500, cart.total());
        }

        @Test
        @DisplayName("8. 割引の1円未満は切り捨てる")
        void roundDown() {
            cart.add("電卓", 5005, 1);
            assertEquals(500, cart.discount());
            assertEquals(4505, cart.total());
        }
    }

    @Nested
    @DisplayName("取り除く")
    class Remove {
        @Test
        @DisplayName("9. 取り除いた商品は小計に含まれない")
        void removeItem() {
            cart.add("ノート", 150, 3);
            cart.add("ボールペン", 120, 2);
            assertTrue(cart.remove("ノート"));
            assertEquals(240, cart.subtotal());
        }

        @Test
        @DisplayName("10. かごにない商品を取り除こうとするとfalse")
        void removeMissing() {
            assertFalse(cart.remove("消しゴム"));
        }
    }

    @Nested
    @DisplayName("不正な入力")
    class InvalidInput {
        @Test
        @DisplayName("11. 数量が0なら例外")
        void zeroQuantity() {
            assertThrows(IllegalArgumentException.class, () -> cart.add("ノート", 150, 0));
        }

        @Test
        @DisplayName("12. 単価が負なら例外")
        void negativePrice() {
            assertThrows(IllegalArgumentException.class, () -> cart.add("ノート", -1, 1));
        }

        @Test
        @DisplayName("13. 商品名が空なら例外で、かごは変わらない")
        void blankName() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> cart.add(" ", 150, 1));
            assertEquals("商品名が空です", e.getMessage());
            assertEquals(0, cart.itemCount());
        }
    }
}
