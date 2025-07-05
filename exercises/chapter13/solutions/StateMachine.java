import java.util.EnumSet;
import java.util.EnumMap;
import java.util.Set;
import java.util.Map;

/**
 * 状態遷移システム
 * EnumSetとEnumMapを活用した高性能な状態管理
 */
public class StateMachine {
    
    /**
     * 状態を表すEnum
     */
    public enum State {
        /** 初期状態 */
        INITIAL,
        /** 準備中 */
        PREPARING,
        /** 実行中 */
        EXECUTING,
        /** 一時停止中 */
        PAUSED,
        /** 完了 */
        COMPLETED,
        /** エラー */
        ERROR,
        /** 終了 */
        TERMINATED
    }
    
    /**
     * イベントを表すEnum
     */
    public enum Event {
        /** 開始 */
        START,
        /** 実行 */
        EXECUTE,
        /** 一時停止 */
        PAUSE,
        /** 再開 */
        RESUME,
        /** 完了 */
        COMPLETE,
        /** エラー発生 */
        ERROR,
        /** リセット */
        RESET,
        /** 終了 */
        TERMINATE
    }
    
    // 状態遷移テーブル（EnumMapを使用）
    private static final Map<State, EnumSet<Event>> ALLOWED_TRANSITIONS = new EnumMap<>(State.class);
    
    // 状態とイベントから次の状態を決定するマップ
    private static final Map<State, EnumMap<Event, State>> TRANSITION_TABLE = new EnumMap<>(State.class);
    
    static {
        // 許可される遷移を定義
        ALLOWED_TRANSITIONS.put(State.INITIAL, EnumSet.of(Event.START, Event.TERMINATE));
        ALLOWED_TRANSITIONS.put(State.PREPARING, EnumSet.of(Event.EXECUTE, Event.ERROR, Event.RESET));
        ALLOWED_TRANSITIONS.put(State.EXECUTING, EnumSet.of(Event.PAUSE, Event.COMPLETE, Event.ERROR, Event.RESET));
        ALLOWED_TRANSITIONS.put(State.PAUSED, EnumSet.of(Event.RESUME, Event.RESET, Event.TERMINATE));
        ALLOWED_TRANSITIONS.put(State.COMPLETED, EnumSet.of(Event.RESET, Event.TERMINATE));
        ALLOWED_TRANSITIONS.put(State.ERROR, EnumSet.of(Event.RESET, Event.TERMINATE));
        ALLOWED_TRANSITIONS.put(State.TERMINATED, EnumSet.noneOf(Event.class));
        
        // 状態遷移テーブルを定義
        initializeTransitionTable();
    }
    
    private static void initializeTransitionTable() {
        // INITIAL状態からの遷移
        EnumMap<Event, State> initialTransitions = new EnumMap<>(Event.class);
        initialTransitions.put(Event.START, State.PREPARING);
        initialTransitions.put(Event.TERMINATE, State.TERMINATED);
        TRANSITION_TABLE.put(State.INITIAL, initialTransitions);
        
        // PREPARING状態からの遷移
        EnumMap<Event, State> preparingTransitions = new EnumMap<>(Event.class);
        preparingTransitions.put(Event.EXECUTE, State.EXECUTING);
        preparingTransitions.put(Event.ERROR, State.ERROR);
        preparingTransitions.put(Event.RESET, State.INITIAL);
        TRANSITION_TABLE.put(State.PREPARING, preparingTransitions);
        
        // EXECUTING状態からの遷移
        EnumMap<Event, State> executingTransitions = new EnumMap<>(Event.class);
        executingTransitions.put(Event.PAUSE, State.PAUSED);
        executingTransitions.put(Event.COMPLETE, State.COMPLETED);
        executingTransitions.put(Event.ERROR, State.ERROR);
        executingTransitions.put(Event.RESET, State.INITIAL);
        TRANSITION_TABLE.put(State.EXECUTING, executingTransitions);
        
        // PAUSED状態からの遷移
        EnumMap<Event, State> pausedTransitions = new EnumMap<>(Event.class);
        pausedTransitions.put(Event.RESUME, State.EXECUTING);
        pausedTransitions.put(Event.RESET, State.INITIAL);
        pausedTransitions.put(Event.TERMINATE, State.TERMINATED);
        TRANSITION_TABLE.put(State.PAUSED, pausedTransitions);
        
        // COMPLETED状態からの遷移
        EnumMap<Event, State> completedTransitions = new EnumMap<>(Event.class);
        completedTransitions.put(Event.RESET, State.INITIAL);
        completedTransitions.put(Event.TERMINATE, State.TERMINATED);
        TRANSITION_TABLE.put(State.COMPLETED, completedTransitions);
        
        // ERROR状態からの遷移
        EnumMap<Event, State> errorTransitions = new EnumMap<>(Event.class);
        errorTransitions.put(Event.RESET, State.INITIAL);
        errorTransitions.put(Event.TERMINATE, State.TERMINATED);
        TRANSITION_TABLE.put(State.ERROR, errorTransitions);
        
        // TERMINATED状態からの遷移（なし）
        TRANSITION_TABLE.put(State.TERMINATED, new EnumMap<>(Event.class));
    }
    
    private State currentState;
    private final StringBuilder transitionHistory;
    
    /**
     * 状態マシンを初期化
     */
    public StateMachine() {
        this.currentState = State.INITIAL;
        this.transitionHistory = new StringBuilder();
        logTransition(null, Event.START, State.INITIAL);
    }
    
    /**
     * 現在の状態を取得
     * @return 現在の状態
     */
    public State getCurrentState() {
        return currentState;
    }
    
    /**
     * 指定されたイベントで状態遷移を実行
     * @param event 発生したイベント
     * @return 遷移が成功した場合true
     */
    public boolean transition(Event event) {
        if (!isValidTransition(event)) {
            return false;
        }
        
        State previousState = currentState;
        State nextState = TRANSITION_TABLE.get(currentState).get(event);
        
        if (nextState != null) {
            currentState = nextState;
            logTransition(previousState, event, nextState);
            return true;
        }
        
        return false;
    }
    
    /**
     * 指定されたイベントでの遷移が有効かチェック
     * @param event チェックするイベント
     * @return 有効な場合true
     */
    public boolean isValidTransition(Event event) {
        Set<Event> allowedEvents = ALLOWED_TRANSITIONS.get(currentState);
        return allowedEvents != null && allowedEvents.contains(event);
    }
    
    /**
     * 現在の状態で可能なイベントを取得
     * @return 可能なイベントのセット
     */
    public Set<Event> getAvailableEvents() {
        Set<Event> available = ALLOWED_TRANSITIONS.get(currentState);
        return available != null ? EnumSet.copyOf(available) : EnumSet.noneOf(Event.class);
    }
    
    /**
     * 終了状態かどうかを判定
     * @return 終了状態の場合true
     */
    public boolean isTerminalState() {
        return currentState == State.TERMINATED;
    }
    
    /**
     * エラー状態かどうかを判定
     * @return エラー状態の場合true
     */
    public boolean isErrorState() {
        return currentState == State.ERROR;
    }
    
    /**
     * 遷移履歴を取得
     * @return 遷移履歴
     */
    public String getTransitionHistory() {
        return transitionHistory.toString();
    }
    
    /**
     * 状態マシンをリセット
     */
    public void reset() {
        currentState = State.INITIAL;
        transitionHistory.setLength(0);
        logTransition(null, Event.RESET, State.INITIAL);
    }
    
    private void logTransition(State from, Event event, State to) {
        if (transitionHistory.length() > 0) {
            transitionHistory.append(" -> ");
        }
        transitionHistory.append(String.format("%s(%s)%s", 
            from != null ? from : "START", 
            event, 
            to));
    }
    
    /**
     * 状態の日本語名を取得
     * @param state 状態
     * @return 日本語名
     */
    public static String getStateDisplayName(State state) {
        return switch (state) {
            case INITIAL -> "初期状態";
            case PREPARING -> "準備中";
            case EXECUTING -> "実行中";
            case PAUSED -> "一時停止中";
            case COMPLETED -> "完了";
            case ERROR -> "エラー";
            case TERMINATED -> "終了";
        };
    }
    
    /**
     * イベントの日本語名を取得
     * @param event イベント
     * @return 日本語名
     */
    public static String getEventDisplayName(Event event) {
        return switch (event) {
            case START -> "開始";
            case EXECUTE -> "実行";
            case PAUSE -> "一時停止";
            case RESUME -> "再開";
            case COMPLETE -> "完了";
            case ERROR -> "エラー発生";
            case RESET -> "リセット";
            case TERMINATE -> "終了";
        };
    }
}