import java.util.List;
import java.util.ArrayList;

public class EventProcessor {
    private String name;
    
    public EventProcessor(String name) {
        this.name = name;
    }
    
    public void startProcessing() {
        // 自分自身をイベントハンドラーに登録
        EventManager.register(this);
        IO.println(name + " が処理を開始しました");
    }
    
    public void handleEvent(String event) {
        IO.println(name + " がイベントを処理: " + event);
    }
}
class EventManager {
    private static List<EventProcessor> processors = new ArrayList<>();
    
    public static void register(EventProcessor processor) {
        processors.add(processor);
    }
    
    public static void fireEvent(String event) {
        for (EventProcessor processor : processors) {
            processor.handleEvent(event);
        }
    }
}
