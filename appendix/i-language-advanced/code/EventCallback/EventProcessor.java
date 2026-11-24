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
    private static EventProcessor[] processors = new EventProcessor[10];
    private static int count = 0;  // 登録済みの数
    
    public static void register(EventProcessor processor) {
        if (count < processors.length) {
            processors[count] = processor;
            count++;
        }
    }
    
    public static void fireEvent(String event) {
        for (int i = 0; i < count; i++) {
            processors[i].handleEvent(event);
        }
    }
}
