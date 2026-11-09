import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Statusクラスのテストクラス
 */
public class StatusTest {
    
    @Test
    public void testCanTransitionTo() {
        // PENDINGからの遷移
        assertTrue(Status.PENDING.canTransitionTo(Status.RUNNING));
        assertTrue(Status.PENDING.canTransitionTo(Status.CANCELLED));
        assertFalse(Status.PENDING.canTransitionTo(Status.COMPLETED));
        assertFalse(Status.PENDING.canTransitionTo(Status.FAILED));
        
        // RUNNINGからの遷移
        assertTrue(Status.RUNNING.canTransitionTo(Status.COMPLETED));
        assertTrue(Status.RUNNING.canTransitionTo(Status.FAILED));
        assertTrue(Status.RUNNING.canTransitionTo(Status.CANCELLED));
        assertFalse(Status.RUNNING.canTransitionTo(Status.PENDING));
        
        // 終了状態からの遷移は不可
        assertFalse(Status.COMPLETED.canTransitionTo(Status.RUNNING));
        assertFalse(Status.FAILED.canTransitionTo(Status.RUNNING));
        assertFalse(Status.CANCELLED.canTransitionTo(Status.RUNNING));
    }
    
    @Test
    public void testIsTerminal() {
        assertFalse(Status.PENDING.isTerminal());
        assertFalse(Status.RUNNING.isTerminal());
        assertTrue(Status.COMPLETED.isTerminal());
        assertTrue(Status.FAILED.isTerminal());
        assertTrue(Status.CANCELLED.isTerminal());
    }
    
    @Test
    public void testGetDisplayName() {
        assertEquals("待機中", Status.PENDING.getDisplayName());
        assertEquals("実行中", Status.RUNNING.getDisplayName());
        assertEquals("完了", Status.COMPLETED.getDisplayName());
        assertEquals("失敗", Status.FAILED.getDisplayName());
        assertEquals("キャンセル", Status.CANCELLED.getDisplayName());
    }
    
    @Test
    public void testEnumValues() {
        Status[] values = Status.values();
        assertEquals(5, values.length);
        
        assertEquals(Status.PENDING, Status.valueOf("PENDING"));
        assertEquals(Status.RUNNING, Status.valueOf("RUNNING"));
        assertEquals(Status.COMPLETED, Status.valueOf("COMPLETED"));
        assertEquals(Status.FAILED, Status.valueOf("FAILED"));
        assertEquals(Status.CANCELLED, Status.valueOf("CANCELLED"));
    }
}