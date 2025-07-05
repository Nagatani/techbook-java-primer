import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * OperationEnumクラスのテストクラス
 */
public class OperationEnumTest {
    
    @Test
    public void testBasicOperations() {
        assertEquals(10.0, OperationEnum.PLUS.apply(6.0, 4.0));
        assertEquals(2.0, OperationEnum.MINUS.apply(6.0, 4.0));
        assertEquals(24.0, OperationEnum.TIMES.apply(6.0, 4.0));
        assertEquals(1.5, OperationEnum.DIVIDE.apply(6.0, 4.0));
        assertEquals(2.0, OperationEnum.MODULO.apply(6.0, 4.0));
        assertEquals(1296.0, OperationEnum.POWER.apply(6.0, 4.0));
    }
    
    @Test
    public void testDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> {
            OperationEnum.DIVIDE.apply(10.0, 0.0);
        });
        
        assertThrows(ArithmeticException.class, () -> {
            OperationEnum.MODULO.apply(10.0, 0.0);
        });
    }
    
    @Test
    public void testFromSymbol() {
        assertEquals(OperationEnum.PLUS, OperationEnum.fromSymbol("+"));
        assertEquals(OperationEnum.MINUS, OperationEnum.fromSymbol("-"));
        assertEquals(OperationEnum.TIMES, OperationEnum.fromSymbol("*"));
        assertEquals(OperationEnum.DIVIDE, OperationEnum.fromSymbol("/"));
        assertEquals(OperationEnum.MODULO, OperationEnum.fromSymbol("%"));
        assertEquals(OperationEnum.POWER, OperationEnum.fromSymbol("^"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            OperationEnum.fromSymbol("?");
        });
    }
    
    @Test
    public void testGetSymbol() {
        assertEquals("+", OperationEnum.PLUS.getSymbol());
        assertEquals("-", OperationEnum.MINUS.getSymbol());
        assertEquals("*", OperationEnum.TIMES.getSymbol());
        assertEquals("/", OperationEnum.DIVIDE.getSymbol());
        assertEquals("%", OperationEnum.MODULO.getSymbol());
        assertEquals("^", OperationEnum.POWER.getSymbol());
    }
    
    @Test
    public void testGetPriority() {
        assertEquals(1, OperationEnum.PLUS.getPriority());
        assertEquals(1, OperationEnum.MINUS.getPriority());
        assertEquals(2, OperationEnum.TIMES.getPriority());
        assertEquals(2, OperationEnum.DIVIDE.getPriority());
        assertEquals(2, OperationEnum.MODULO.getPriority());
        assertEquals(3, OperationEnum.POWER.getPriority());
    }
    
    @Test
    public void testIsRightAssociative() {
        assertFalse(OperationEnum.PLUS.isRightAssociative());
        assertFalse(OperationEnum.MINUS.isRightAssociative());
        assertFalse(OperationEnum.TIMES.isRightAssociative());
        assertFalse(OperationEnum.DIVIDE.isRightAssociative());
        assertFalse(OperationEnum.MODULO.isRightAssociative());
        assertTrue(OperationEnum.POWER.isRightAssociative());
    }
    
    @Test
    public void testGetJapaneseName() {
        assertEquals("加算", OperationEnum.PLUS.getJapaneseName());
        assertEquals("減算", OperationEnum.MINUS.getJapaneseName());
        assertEquals("乗算", OperationEnum.TIMES.getJapaneseName());
        assertEquals("除算", OperationEnum.DIVIDE.getJapaneseName());
        assertEquals("剰余", OperationEnum.MODULO.getJapaneseName());
        assertEquals("冪乗", OperationEnum.POWER.getJapaneseName());
    }
    
    @Test
    public void testToString() {
        assertEquals("+", OperationEnum.PLUS.toString());
        assertEquals("-", OperationEnum.MINUS.toString());
        assertEquals("*", OperationEnum.TIMES.toString());
        assertEquals("/", OperationEnum.DIVIDE.toString());
        assertEquals("%", OperationEnum.MODULO.toString());
        assertEquals("^", OperationEnum.POWER.toString());
    }
}