import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;

/**
 * StateMachineクラスのテストクラス
 */
public class StateMachineTest {
    
    private StateMachine stateMachine;
    
    @BeforeEach
    public void setUp() {
        stateMachine = new StateMachine();
    }
    
    @Test
    public void testInitialState() {
        assertEquals(StateMachine.State.INITIAL, stateMachine.getCurrentState());
        assertFalse(stateMachine.isTerminalState());
        assertFalse(stateMachine.isErrorState());
    }
    
    @Test
    public void testValidTransitions() {
        // INITIAL -> PREPARING
        assertTrue(stateMachine.transition(StateMachine.Event.START));
        assertEquals(StateMachine.State.PREPARING, stateMachine.getCurrentState());
        
        // PREPARING -> EXECUTING
        assertTrue(stateMachine.transition(StateMachine.Event.EXECUTE));
        assertEquals(StateMachine.State.EXECUTING, stateMachine.getCurrentState());
        
        // EXECUTING -> COMPLETED
        assertTrue(stateMachine.transition(StateMachine.Event.COMPLETE));
        assertEquals(StateMachine.State.COMPLETED, stateMachine.getCurrentState());
    }
    
    @Test
    public void testInvalidTransitions() {
        // INITIAL状態でEXECUTEは無効
        assertFalse(stateMachine.transition(StateMachine.Event.EXECUTE));
        assertEquals(StateMachine.State.INITIAL, stateMachine.getCurrentState());
        
        // COMPLETED状態でPAUSEは無効
        stateMachine.transition(StateMachine.Event.START);
        stateMachine.transition(StateMachine.Event.EXECUTE);
        stateMachine.transition(StateMachine.Event.COMPLETE);
        assertFalse(stateMachine.transition(StateMachine.Event.PAUSE));
        assertEquals(StateMachine.State.COMPLETED, stateMachine.getCurrentState());
    }
    
    @Test
    public void testPauseAndResume() {
        // 実行中状態まで遷移
        stateMachine.transition(StateMachine.Event.START);
        stateMachine.transition(StateMachine.Event.EXECUTE);
        
        // 一時停止
        assertTrue(stateMachine.transition(StateMachine.Event.PAUSE));
        assertEquals(StateMachine.State.PAUSED, stateMachine.getCurrentState());
        
        // 再開
        assertTrue(stateMachine.transition(StateMachine.Event.RESUME));
        assertEquals(StateMachine.State.EXECUTING, stateMachine.getCurrentState());
    }
    
    @Test
    public void testErrorHandling() {
        // 準備中状態でエラー発生
        stateMachine.transition(StateMachine.Event.START);
        assertTrue(stateMachine.transition(StateMachine.Event.ERROR));
        assertEquals(StateMachine.State.ERROR, stateMachine.getCurrentState());
        assertTrue(stateMachine.isErrorState());
        
        // エラー状態からリセット
        assertTrue(stateMachine.transition(StateMachine.Event.RESET));
        assertEquals(StateMachine.State.INITIAL, stateMachine.getCurrentState());
    }
    
    @Test
    public void testTerminalState() {
        // 終了状態に遷移
        assertTrue(stateMachine.transition(StateMachine.Event.TERMINATE));
        assertEquals(StateMachine.State.TERMINATED, stateMachine.getCurrentState());
        assertTrue(stateMachine.isTerminalState());
        
        // 終了状態からは遷移できない
        assertFalse(stateMachine.transition(StateMachine.Event.START));
        assertFalse(stateMachine.transition(StateMachine.Event.RESET));
    }
    
    @Test
    public void testGetAvailableEvents() {
        // 初期状態で利用可能なイベント
        Set<StateMachine.Event> initialEvents = stateMachine.getAvailableEvents();
        assertTrue(initialEvents.contains(StateMachine.Event.START));
        assertTrue(initialEvents.contains(StateMachine.Event.TERMINATE));
        assertFalse(initialEvents.contains(StateMachine.Event.EXECUTE));
        
        // 実行中状態で利用可能なイベント
        stateMachine.transition(StateMachine.Event.START);
        stateMachine.transition(StateMachine.Event.EXECUTE);
        Set<StateMachine.Event> executingEvents = stateMachine.getAvailableEvents();
        assertTrue(executingEvents.contains(StateMachine.Event.PAUSE));
        assertTrue(executingEvents.contains(StateMachine.Event.COMPLETE));
        assertTrue(executingEvents.contains(StateMachine.Event.ERROR));
        assertTrue(executingEvents.contains(StateMachine.Event.RESET));
        assertFalse(executingEvents.contains(StateMachine.Event.START));
    }
    
    @Test
    public void testIsValidTransition() {
        // 初期状態での有効性チェック
        assertTrue(stateMachine.isValidTransition(StateMachine.Event.START));
        assertTrue(stateMachine.isValidTransition(StateMachine.Event.TERMINATE));
        assertFalse(stateMachine.isValidTransition(StateMachine.Event.EXECUTE));
        assertFalse(stateMachine.isValidTransition(StateMachine.Event.PAUSE));
    }
    
    @Test
    public void testTransitionHistory() {
        // 履歴の確認
        String history = stateMachine.getTransitionHistory();
        assertNotNull(history);
        assertTrue(history.contains("INITIAL"));
        
        // 遷移後の履歴
        stateMachine.transition(StateMachine.Event.START);
        history = stateMachine.getTransitionHistory();
        assertTrue(history.contains("PREPARING"));
    }
    
    @Test
    public void testReset() {
        // 状態を変更
        stateMachine.transition(StateMachine.Event.START);
        stateMachine.transition(StateMachine.Event.EXECUTE);
        assertEquals(StateMachine.State.EXECUTING, stateMachine.getCurrentState());
        
        // リセット
        stateMachine.reset();
        assertEquals(StateMachine.State.INITIAL, stateMachine.getCurrentState());
    }
    
    @Test
    public void testDisplayNames() {
        // 状態の表示名
        assertEquals("初期状態", StateMachine.getStateDisplayName(StateMachine.State.INITIAL));
        assertEquals("実行中", StateMachine.getStateDisplayName(StateMachine.State.EXECUTING));
        assertEquals("完了", StateMachine.getStateDisplayName(StateMachine.State.COMPLETED));
        
        // イベントの表示名
        assertEquals("開始", StateMachine.getEventDisplayName(StateMachine.Event.START));
        assertEquals("実行", StateMachine.getEventDisplayName(StateMachine.Event.EXECUTE));
        assertEquals("完了", StateMachine.getEventDisplayName(StateMachine.Event.COMPLETE));
    }
    
    @Test
    public void testCompleteWorkflow() {
        // 完全なワークフローのテスト
        assertEquals(StateMachine.State.INITIAL, stateMachine.getCurrentState());
        
        // 開始 -> 準備中
        assertTrue(stateMachine.transition(StateMachine.Event.START));
        assertEquals(StateMachine.State.PREPARING, stateMachine.getCurrentState());
        
        // 実行 -> 実行中
        assertTrue(stateMachine.transition(StateMachine.Event.EXECUTE));
        assertEquals(StateMachine.State.EXECUTING, stateMachine.getCurrentState());
        
        // 一時停止 -> 一時停止中
        assertTrue(stateMachine.transition(StateMachine.Event.PAUSE));
        assertEquals(StateMachine.State.PAUSED, stateMachine.getCurrentState());
        
        // 再開 -> 実行中
        assertTrue(stateMachine.transition(StateMachine.Event.RESUME));
        assertEquals(StateMachine.State.EXECUTING, stateMachine.getCurrentState());
        
        // 完了 -> 完了
        assertTrue(stateMachine.transition(StateMachine.Event.COMPLETE));
        assertEquals(StateMachine.State.COMPLETED, stateMachine.getCurrentState());
        
        // 終了 -> 終了
        assertTrue(stateMachine.transition(StateMachine.Event.TERMINATE));
        assertEquals(StateMachine.State.TERMINATED, stateMachine.getCurrentState());
        assertTrue(stateMachine.isTerminalState());
    }
}